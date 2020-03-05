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

package com.xxlabaza.utils.netty.client.multiple;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.xxlabaza.utils.netty.ApplicationContextHolder;
import com.xxlabaza.utils.netty.NettyClient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("client-multiple-instances")
@SpringBootTest(classes = TestConfiguration.class)
class MultipleClientsTests {

  @Autowired(required = false)
  NettyClient defaultNettyClient;

  @Qualifier("secondNettyClient")
  @Autowired(required = false)
  NettyClient secondNettyClient;

  @Qualifier("thirdNettyClient")
  @Autowired(required = false)
  NettyClient thirdNettyClient;

  @Autowired
  List<NettyClient> clients;

  @Autowired
  ApplicationContext context;

  @AfterAll
  static void afterAll () {
    ApplicationContextHolder.getBeans(NettyClient.class)
        .values()
        .forEach(NettyClient::close);
  }

  @Test
  void checkAutowires () {
    assertThat(defaultNettyClient).isNotNull();
    assertThat(secondNettyClient).isNotNull();
    assertThat(thirdNettyClient).isNotNull();

    assertThat(clients).hasSize(3);
    assertThat(clients).containsExactlyInAnyOrder(
        defaultNettyClient, secondNettyClient, thirdNettyClient
    );
  }

  @Test
  void checkContext () {
    asList(
        "defaultNettyClientProperties",
        "secondNettyClientProperties",
        "thirdNettyClientProperties",

        "defaultNettyClientBootstrap",
        "secondNettyClientBootstrap",
        "thirdNettyClientBootstrap",

        "defaultNettyClient",
        "secondNettyClient",
        "thirdNettyClient",

        "defaultNettyClientChannelInitializer",
        "secondNettyClientChannelInitializer",
        "thirdNettyClientChannelInitializer"
    ).forEach(it -> {
      assertThat(context.containsBean(it))
          .as("check that bean %s exists", it)
          .isTrue();
    });
  }
}
