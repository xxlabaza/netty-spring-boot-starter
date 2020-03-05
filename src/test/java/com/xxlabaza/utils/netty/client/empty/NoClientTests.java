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

package com.xxlabaza.utils.netty.client.empty;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.xxlabaza.utils.netty.NettyClient;
import com.xxlabaza.utils.netty.config.client.NettyClientProperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("client-empty")
@SpringBootTest(classes = TestConfiguration.class)
class NoClientTests {

  @Autowired(required = false)
  NettyClientProperties properties;

  @Autowired(required = false)
  NettyClient client;

  @Autowired
  ApplicationContext context;

  @Test
  void checkAutowires () {
    assertThat(properties).isNull();
    assertThat(client).isNull();
  }

  @Test
  void checkContext () {
    asList(
        "defaultNettyClientProperties",
        "defaultNettyClientBootstrap",
        "defaultNettyClient",
        "defaultNettyClientChannelInitializer"
    ).forEach(it -> {
      assertThat(context.containsBean(it))
          .as("check that bean %s doesn't exist", it)
          .isFalse();
    });
  }
}
