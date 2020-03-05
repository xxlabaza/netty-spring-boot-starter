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
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.Ordered;

@Slf4j
class NettyClientApplicationListener implements ApplicationListener<ApplicationContextEvent>, Ordered {

  @Autowired(required = false)
  List<NettyClient> clients;

  @Override
  public int getOrder () {
    return LOWEST_PRECEDENCE;
  }

  @Override
  public void onApplicationEvent (ApplicationContextEvent event) {
    if (event instanceof ContextRefreshedEvent || event instanceof ContextStartedEvent) {
      start();
    } else if (event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent) {
      stop();
    }
  }

  void start () {
    if (clients == null) {
      log.warn("didn't find any netty client bean");
      return;
    }

    for (val client : clients) {
      val properties = client.getProperties();
      if (client.isConnected() || properties.isAutoConnect() == false) {
        continue;
      }

      log.info("starting {}", client);
      client.connect();
    }
  }

  void stop () {
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
}
