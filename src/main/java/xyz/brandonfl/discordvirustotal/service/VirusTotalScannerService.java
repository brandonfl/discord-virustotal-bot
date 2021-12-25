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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @SneakyThrows
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

    return scannedResources;
  }

  @Async
  @Transactional
  public void storeScannedResources(List<ScannedResource> resources) {
    if (resources != null && !resources.isEmpty()) {
      repositoryContainer.getScanRepository().saveAll(
          resources.stream().map(ScanEntity::getFromScannedUrl).collect(Collectors.toList())
      );
    }
  }

}
