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
