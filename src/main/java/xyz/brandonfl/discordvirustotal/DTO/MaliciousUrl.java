package xyz.brandonfl.discordvirustotal.DTO;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class MaliciousUrl {
  private String scannedLink;
  private String virusTotalPermaLink;
  private int positiveScore;
}
