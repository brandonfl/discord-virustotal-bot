package xyz.brandonfl.discordvirustotal.discordbot;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.brandonfl.discordvirustotal.config.BotProperties;
import xyz.brandonfl.discordvirustotal.discordbot.event.MessageEvent;
import xyz.brandonfl.discordvirustotal.service.VirusTotalScannerService;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DiscordBot {
  public final BotProperties botProperties;
  private final VirusTotalScannerService virusTotalScannerService;

  public static final Activity DEFAULT_ACTIVITY = Activity.playing("scanner with VirusTotal");

  @PostConstruct
  public void startBot() throws LoginException {

    JDABuilder.createDefault(botProperties.getSetting().getToken())
        .setAutoReconnect(true)
        .addEventListeners(
            new MessageEvent(virusTotalScannerService)
        )
        .setActivity(DEFAULT_ACTIVITY)
        .build();
  }
}
