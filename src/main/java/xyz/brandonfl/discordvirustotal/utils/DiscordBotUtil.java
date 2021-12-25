package xyz.brandonfl.discordvirustotal.utils;

import java.awt.Color;
import java.time.Instant;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscordBotUtil {
  public static final Color COLOR = new Color(108, 135, 202);

  public static EmbedBuilder getGenericEmbed(JDA jda) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder
        .setColor(COLOR)
        .setTimestamp(Instant.from(ZonedDateTime.now()))
        .setFooter("VirusTotal", jda.getSelfUser().getEffectiveAvatarUrl());
    return embedBuilder;
  }
}
