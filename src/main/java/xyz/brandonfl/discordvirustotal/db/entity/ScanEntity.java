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
