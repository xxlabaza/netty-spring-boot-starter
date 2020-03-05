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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class ConverterStringToInetSocketAddressTests {

  Converter<String, InetSocketAddress> converter = new ConverterStringToInetSocketAddress();

  @Test
  void onlyHost () throws UnknownHostException {
    val string = "localhost";
    val address = converter.convert(string);

    assertThat(address.getAddress())
        .isEqualTo(InetAddress.getByName("localhost"));

    assertThat(address.getPort())
        .isEqualTo(0);
  }

  @Test
  void hostAndPort () throws UnknownHostException {
    val string = "localhost:8999";
    val address = converter.convert(string);

    assertThat(address.getAddress())
        .isEqualTo(InetAddress.getByName("localhost"));

    assertThat(address.getPort())
        .isEqualTo(8999);
  }

  @Test
  void invalid () {
    val string = "hello world";
    assertThatThrownBy(() -> converter.convert(string))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid host[:port] string - '%s'", string);
  }

  @Test
  void nullPointerException () {
    assertThatThrownBy(() -> converter.convert(null))
        .isInstanceOf(NullPointerException.class);
  }
}
