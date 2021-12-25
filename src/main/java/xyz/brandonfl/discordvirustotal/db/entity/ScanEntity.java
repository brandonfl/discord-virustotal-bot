package xyz.brandonfl.discordvirustotal.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import xyz.brandonfl.discordvirustotal.DTO.ScannedResource;

@Getter
@Setter
@Entity
@Table(name = "scan")
public class ScanEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "resource", nullable = false)
  private String resource;

  @Column(name = "perma_link", nullable = false)
  private String permaLink;

  @Column(name = "positive_score", nullable = false)
  private int positiveScore = 0;

  public ScannedResource getAsScannedUrl() {
    return getAsScannedUrl(0);
  }

  public ScannedResource getAsScannedUrl(final int maxPositiveScoreForBlacklist) {
    return ScannedResource.builder()
        .resource(resource)
        .virusTotalPermaLink(permaLink)
        .positiveScore(positiveScore)
        .isMalicious(positiveScore >= maxPositiveScoreForBlacklist)
        .build();
  }

  public static ScanEntity getFromScannedUrl(final ScannedResource scannedResource) {
    ScanEntity scanEntity = new ScanEntity();
    scanEntity.setResource(scannedResource.getResource());
    scanEntity.setPermaLink(scannedResource.getVirusTotalPermaLink());
    scanEntity.setPositiveScore(scannedResource.getPositiveScore());
    return scanEntity;
  }
}
