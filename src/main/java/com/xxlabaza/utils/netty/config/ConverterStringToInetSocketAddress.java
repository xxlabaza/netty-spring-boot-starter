/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xxlabaza.utils.netty.config;

import static java.util.Locale.ENGLISH;
import static java.util.Optional.ofNullable;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.val;
import org.springframework.core.convert.converter.Converter;

class ConverterStringToInetSocketAddress implements Converter<String, InetSocketAddress> {

  private static final Pattern PATTERN = Pattern.compile("^(?<host>[\\w-\\.]+)(:(?<port>\\d+))?$");

  @Override
  public InetSocketAddress convert (@NonNull String string) {
    val matcher = PATTERN.matcher(string);
    if (matcher.matches() == false) {
      val msg = String.format(ENGLISH, "invalid host[:port] string - '%s'", string);
      throw new IllegalArgumentException(msg);
    }

    val host = matcher.group("host");
    val port = ofNullable(matcher.group("port"))
        .map(Integer::parseInt)
        .orElse(0);

    return new InetSocketAddress(host, port);
  }
}
