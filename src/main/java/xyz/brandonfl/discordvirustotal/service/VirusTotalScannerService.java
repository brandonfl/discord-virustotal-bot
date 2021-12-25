package xyz.brandonfl.discordvirustotal.service;

import static xyz.brandonfl.discordvirustotal.utils.UrlDetectorUtil.getUrlsFromString;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.dto.ScanInfo;
import com.kanishka.virustotal.dto.VirusScanInfo;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.InvalidArgumentsException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.brandonfl.discordvirustotal.DTO.MaliciousUrl;
import xyz.brandonfl.discordvirustotal.config.BotProperties;

@Slf4j
@Service
public class VirusTotalScannerService {

  private final BotProperties botProperties;
  private final VirustotalPublicV2 virusTotalRef;

  @Autowired
  public VirusTotalScannerService(BotProperties botProperties) throws APIKeyNotFoundException {
    this.botProperties = botProperties;

    VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey(botProperties.getVirusTotal().getToken());
    this.virusTotalRef = new VirustotalPublicV2Impl();
  }

  public List<MaliciousUrl> isUrlsSafeFromReceivedMessage(String receivedMessage) {
    return isUrlsSafe(getUrlsFromString(receivedMessage));
  }

  @SneakyThrows
  public List<MaliciousUrl> isUrlsSafe(List<URL> urls) {
    List<MaliciousUrl> maliciousUrls = new ArrayList<>();

    try {

      FileScanReport[] reports = virusTotalRef.getUrlScanReport(urls.stream().map(URL::toString).toArray(String[]::new), false);

      for (FileScanReport report : reports) {
        if (report.getResponseCode() == 1 && report.getPositives() >= botProperties.getVirusTotal().getMaxPositiveScoreForBlacklist()) {
          MaliciousUrl maliciousUrl = MaliciousUrl.builder()
              .scannedLink(report.getResource())
              .virusTotalPermaLink(report.getPermalink())
              .positiveScore(report.getPositives())
              .build();

          maliciousUrls.add(maliciousUrl);
          log.warn("Malicious url detected: {}", maliciousUrl);
        }
      }

    } catch (UnauthorizedAccessException e) {
      log.error("Unauthorized access to API, please verify your token", e);
    } catch (QuotaExceededException e) {
      log.warn("Quota exceeded");
    } catch (Exception e) {
      log.error("Error during scan", e);
    }

    return maliciousUrls;
  }

}
