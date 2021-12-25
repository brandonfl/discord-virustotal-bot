package xyz.brandonfl.discordvirustotal.discordbot.event;

import static xyz.brandonfl.discordvirustotal.utils.UrlDetectorUtil.getUrlsFromString;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.brandonfl.discordvirustotal.service.VirusTotalScannerService;

@AllArgsConstructor
public class MessageEvent extends ListenerAdapter {
  private final VirusTotalScannerService virusTotalScannerService;

  @Override
  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    onMessage(event, event.getMessage());
  }

  @Override
  public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
    onMessage(event, event.getMessage());
  }

  private void onMessage(GenericGuildMessageEvent genericEvent, Message message) {
    virusTotalScannerService.isUrlsSafeFromReceivedMessage(message.getContentStripped());
  }
}
