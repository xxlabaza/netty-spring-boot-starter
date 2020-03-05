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

import java.net.InetSocketAddress;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class ConverterIntegerToInetSocketAddressTests {

  Converter<Integer, InetSocketAddress> converter = new ConverterIntegerToInetSocketAddress();

  @Test
  void onlyPort () {
    val string = 8989;
    val address = converter.convert(string);

    assertThat(address.getAddress())
        .isNotNull();

    assertThat(address.getPort())
        .isEqualTo(8989);
  }

  @Test
  void nullPointerException () {
    assertThatThrownBy(() -> converter.convert(null))
        .isInstanceOf(NullPointerException.class);
  }
}
