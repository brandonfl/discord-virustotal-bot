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
