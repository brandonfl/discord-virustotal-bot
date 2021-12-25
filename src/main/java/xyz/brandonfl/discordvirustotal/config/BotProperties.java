package xyz.brandonfl.discordvirustotal.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot", ignoreInvalidFields = false)
@PropertySources({
    @PropertySource(value = "classpath:META-INF/additional-spring-configuration-metadata.json", ignoreResourceNotFound = true)
})
public class BotProperties {
  @Getter
  private final Setting setting = new Setting();

  @Getter
  private final VirusTotal virusTotal = new VirusTotal();

  @Data
  public static class Setting {
    private String version;
    private String token;
  }

  @Data
  public static class VirusTotal {
    private String token;
    private int maxPositiveScoreForBlacklist;
  }

}
