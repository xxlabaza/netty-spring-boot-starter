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

package com.xxlabaza.utils.netty.server.single;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.Socket;

import com.xxlabaza.utils.netty.ApplicationContextHolder;
import com.xxlabaza.utils.netty.NettyServer;

import io.appulse.utils.Bytes;
import io.appulse.utils.ReadBytesUtils;
import io.appulse.utils.WriteBytesUtils;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("server-single-instance")
@SpringBootTest(classes = {
    TestConfiguration.class,
    ConnectionsTrackingConfiguration.class
})
class SingleServerTests {

  @Autowired
  NettyServer server;

  @Autowired
  ConnectionsRepository repository;

  @AfterAll
  static void afterAll () {
    ApplicationContextHolder.getBeans(NettyServer.class)
        .values()
        .forEach(NettyServer::close);
  }

  @Test
  void configurationParsing () {
    // nothing
  }

  @Test
  void sendAndReceive () throws IOException {
    try (val socket = new Socket()) {
      val properties = server.getProperties();
      socket.connect(properties.getBind());

      val bytes = Bytes.allocate(Short.BYTES + Integer.BYTES)
          .write2B(4)
          .write4B(0xFFFF);

      val output = socket.getOutputStream();
      WriteBytesUtils.write(output, bytes);

      val input = socket.getInputStream();
      val responseLengthBytes = ReadBytesUtils.read(input, 2);
      assertThat(responseLengthBytes.readableBytes())
          .isEqualTo(2);

      val responseLength = responseLengthBytes.readShort();
      assertThat((int) responseLength)
          .isEqualTo(4);

      val response = ReadBytesUtils.read(input, responseLength);
      assertThat(response.readableBytes())
          .isEqualTo(responseLength);

      assertThat(response.readInt())
          .isEqualTo(0xFFFF);
    }
  }

  @Test
  void receive () throws Exception {
    assertThat(repository.size())
        .isEqualTo(0);

    try (val socket = new Socket()) {
      val properties = server.getProperties();
      socket.connect(properties.getBind());

      SECONDS.sleep(1);

      assertThat(repository.size())
          .isEqualTo(1);

      val bytes = Bytes.allocate(Integer.BYTES)
          .write4B(0xFFFF);

      repository.all()
          .values()
          .iterator()
          .next()
          .writeAndFlush(bytes);

      val input = socket.getInputStream();
      val responseLengthBytes = ReadBytesUtils.read(input, 2);
      assertThat(responseLengthBytes.readableBytes())
          .isEqualTo(2);

      val responseLength = responseLengthBytes.readShort();
      assertThat((int) responseLength)
          .isEqualTo(4);

      val response = ReadBytesUtils.read(input, responseLength);
      assertThat(response.readableBytes())
          .isEqualTo(responseLength);

      assertThat(response.readInt())
          .isEqualTo(0xFFFF);
    }

    SECONDS.sleep(1);

    assertThat(repository.size())
        .isEqualTo(0);
  }
}
