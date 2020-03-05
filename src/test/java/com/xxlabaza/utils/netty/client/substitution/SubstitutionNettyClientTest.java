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

package com.xxlabaza.utils.netty.client.substitution;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;

import com.xxlabaza.utils.netty.ApplicationContextHolder;
import com.xxlabaza.utils.netty.NettyClient;

import io.appulse.utils.ReadBytesUtils;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("client-substitution")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestConfiguration.class)
class SubstitutionNettyClientTest {

  @Autowired
  NettyClient client;

  @Autowired
  ApplicationContext context;

  @AfterAll
  static void afterAll () {
    ApplicationContextHolder.getBeans(NettyClient.class)
        .values()
        .forEach(NettyClient::close);
  }

  @Test
  void configurationParsing () {
    // nothing
  }

  @Test
  void sendAndReceive () throws IOException {
    try (val server = new ServerSocket(4557)) {
      client.connect();
      val socket = server.accept();

      val input = socket.getInputStream();
      val responseLengthBytes = ReadBytesUtils.read(input, 1);
      assertThat(responseLengthBytes.readableBytes())
          .isEqualTo(1);

      val responseLength = responseLengthBytes.readByte();
      assertThat((int) responseLength)
          .isEqualTo(11);

      val response = ReadBytesUtils.read(input, responseLength);
      assertThat(response.readableBytes())
          .isEqualTo(responseLength);

      assertThat(response.readString())
          .isEqualTo("Hello world");
    }
  }

  @Test
  void checkContext () {
    asList(
        "defaultNettyClientProperties",
        "defaultNettyClientBootstrap",
        "defaultNettyClient",
        "defaultNettyClientChannelInitializer",
        "myClientChannelInititalizer"
    ).forEach(it -> {
      assertThat(context.containsBean(it))
          .as("check that bean %s exists", it)
          .isTrue();
    });
  }
}
