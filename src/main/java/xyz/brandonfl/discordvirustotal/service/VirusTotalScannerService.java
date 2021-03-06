/*
 * MIT License
 *
 * Copyright (c) 2021 Fontany--Legall Brandon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package xyz.brandonfl.discordvirustotal.service;

import static xyz.brandonfl.discordvirustotal.utils.UrlDetectorUtil.getUrlsFromString;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import xyz.brandonfl.discordvirustotal.DTO.ScannedResource;
import xyz.brandonfl.discordvirustotal.config.BotProperties;
import xyz.brandonfl.discordvirustotal.db.entity.ScanEntity;
import xyz.brandonfl.discordvirustotal.db.repository.RepositoryContainer;

@Slf4j
@Service
public class VirusTotalScannerService {

  private final BotProperties botProperties;
  private final RepositoryContainer repositoryContainer;
  private final VirustotalPublicV2 virusTotalRef;

  @Autowired
  public VirusTotalScannerService(BotProperties botProperties, RepositoryContainer repositoryContainer) throws APIKeyNotFoundException {
    this.botProperties = botProperties;
    this.repositoryContainer = repositoryContainer;

    VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey(botProperties.getVirusTotal().getToken());
    this.virusTotalRef = new VirustotalPublicV2Impl();
  }

  public List<ScannedResource> scanUrlsFromReceivedMessage(String receivedMessage) {
    return scanUrls(getUrlsFromString(receivedMessage));
  }

  public List<ScannedResource> scanUrls(List<URL> urls) {
    List<ScannedResource> scannedResources = new ArrayList<>();
    if (urls == null || urls.isEmpty()) {
      return scannedResources;
    }

    List<String> urlsToScan = urls.stream().map(URL::toString).collect(Collectors.toList());

    scannedResources = repositoryContainer.getScanRepository()
        .getAllByResourceIn(urlsToScan)
        .stream()
        .map(scanEntity -> scanEntity.getAsScannedUrl(botProperties.getVirusTotal().getMaxPositiveScoreForBlacklist()))
        .collect(Collectors.toList());

    if (!scannedResources.isEmpty()) {
      if (ScannedResource.asMaliciousResource(scannedResources)) {
        List<ScannedResource> alreadyScannedMaliciousResources = ScannedResource.getAllMaliciousResource(scannedResources);
        log.warn("Malicious url detected (already scanned): {}", alreadyScannedMaliciousResources);
        return alreadyScannedMaliciousResources;
      } else {
        final List<String> alreadyScannedNonMaliciousUrls = scannedResources.stream()
            .map(ScannedResource::getResource)
            .collect(Collectors.toUnmodifiableList());

        log.info("Already scanned non malicious resources: {}", alreadyScannedNonMaliciousUrls);

        urlsToScan = urlsToScan.stream()
            .filter(url -> !alreadyScannedNonMaliciousUrls.contains(url))
            .collect(Collectors.toList());
      }

    }

    if (!urlsToScan.isEmpty()) {
      try {
        List<ScannedResource> notAlreadyScannedResources = new ArrayList<>();

        FileScanReport[] reports = virusTotalRef.getUrlScanReport(urlsToScan.toArray(String[]::new), false);

        for (FileScanReport report : reports) {
          if (report.getResponseCode() == 1) {
            ScannedResource scannedResource = ScannedResource.builder()
                .resource(report.getResource())
                .virusTotalPermaLink(report.getPermalink())
                .positiveScore(report.getPositives())
                .isMalicious(report.getPositives() >= botProperties.getVirusTotal().getMaxPositiveScoreForBlacklist())
                .build();

            notAlreadyScannedResources.add(scannedResource);

            if (scannedResource.isMalicious()) {
              log.warn("Malicious url detected: {}", scannedResource);
            } else {
              log.info("Scanned resource {}: {}", scannedResource.getResource(), scannedResource);
            }
          }
        }

        storeScannedResources(notAlreadyScannedResources);
        scannedResources.addAll(notAlreadyScannedResources);

      } catch (UnauthorizedAccessException e) {
        log.error("Unauthorized access to API, please verify your token", e);
      } catch (QuotaExceededException e) {
        log.warn("Quota exceeded");
      } catch (Exception e) {
        log.error("Error during scan", e);
      }
    }

    return scannedResources;
  }

  @Async
  public void storeScannedResources(List<ScannedResource> resources) {
    if (resources != null && !resources.isEmpty()) {
      repositoryContainer.getScanRepository().saveAll(
          resources.stream().map(ScanEntity::getFromScannedUrl).collect(Collectors.toList())
      );
    }
  }

}
