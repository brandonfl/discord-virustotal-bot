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
