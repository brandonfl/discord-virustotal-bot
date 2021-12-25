package xyz.brandonfl.discordvirustotal.DTO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class ScannedResource {
  private String resource;
  private String virusTotalPermaLink;
  private int positiveScore;
  private boolean isMalicious;

  public static boolean asMaliciousResource(Iterable<ScannedResource> resources) {
    if (resources == null) {
      return false;
    } else {
      return StreamSupport.stream(resources.spliterator(), false).anyMatch(ScannedResource::isMalicious);
    }
  }

  public static List<ScannedResource> getAllMaliciousResource(Iterable<ScannedResource> resources) {
    if (resources == null) {
      return List.of();
    } else {
      return StreamSupport.stream(resources.spliterator(), false)
          .filter(ScannedResource::isMalicious)
          .collect(Collectors.toList());
    }
  }

  public static List<ScannedResource> getAllNonMaliciousResource(Iterable<ScannedResource> resources) {
    if (resources == null) {
      return List.of();
    } else {
      return StreamSupport.stream(resources.spliterator(), false)
          .filter(scannedResource -> !scannedResource.isMalicious)
          .collect(Collectors.toList());
    }
  }
}
