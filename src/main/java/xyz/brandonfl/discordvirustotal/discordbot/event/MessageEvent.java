package xyz.brandonfl.discordvirustotal.discordbot.event;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.List;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.brandonfl.discordvirustotal.DTO.ScannedResource;
import xyz.brandonfl.discordvirustotal.service.VirusTotalScannerService;
import xyz.brandonfl.discordvirustotal.utils.DiscordBotUtil;

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
    if (!genericEvent.getJDA().getSelfUser().getId().equals(message.getAuthor().getId()) ) {
      List<ScannedResource> scannedResourceList = virusTotalScannerService.scanUrlsFromReceivedMessage(message.getContentStripped());
      if (ScannedResource.asMaliciousResource(scannedResourceList)){
        message.delete().queue();

        MessageEmbed userMaliciousWarning = DiscordBotUtil.getGenericEmbed(genericEvent.getJDA())
            .setColor(Color.red)
            .setTitle("Malicious url removed")
            .setDescription(MessageFormat
                .format("Oops {0}, it seems that you have sent a link detected as malicious. "
                        + "If you think this is a false positive, please contact an administrator.",
                    message.getAuthor().getAsMention()))
            .build();

        genericEvent.getChannel().sendMessage(userMaliciousWarning).queue();
      }
    }
  }
}
