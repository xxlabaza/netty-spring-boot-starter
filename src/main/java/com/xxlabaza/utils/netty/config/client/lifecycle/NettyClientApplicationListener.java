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

package com.xxlabaza.utils.netty.config.client.lifecycle;

import java.util.List;

import com.xxlabaza.utils.netty.NettyClient;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
class NettyClientApplicationListener {

  @Autowired(required = false)
  List<NettyClient> clients;

  @EventListener
  void handle (ContextRefreshedEvent event) {
    if (clients == null) {
      return;
    }

    for (val client : clients) {
      val properties = client.getProperties();
      if (client.isConnected() || properties.isAutoConnect() == false) {
        return;
      }

      log.info("starting {}", client);
      client.connect();
    }
  }

  @EventListener
  void handle (ContextClosedEvent event) {
    if (clients == null) {
      return;
    }

    for (val client : clients) {
      if (client.isDisconnected()) {
        continue;
      }

      log.info("closing {}", client);
      client.close();
    }
  }

  @EventListener
  void handle (ContextStartedEvent event) {
    handle((ContextRefreshedEvent) null);
  }

  @EventListener
  void handle (ContextStoppedEvent event) {
    handle((ContextClosedEvent) null);
  }
}
