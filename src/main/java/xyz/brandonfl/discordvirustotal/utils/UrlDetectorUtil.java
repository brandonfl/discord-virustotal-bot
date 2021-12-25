package xyz.brandonfl.discordvirustotal.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlDetectorUtil {

  public static List<URL> getUrlsFromString(String stringWithPossibleUrls) {
    List<URL> urls = new ArrayList<>();

    String[] parts = stringWithPossibleUrls.split("\\s+");

    for(String possibleUrl : parts) {
      try {
        URL url = new URL(possibleUrl);
        urls.add(url);
      } catch (Exception ignore) {
      }
    }

    return urls;
  }
}
