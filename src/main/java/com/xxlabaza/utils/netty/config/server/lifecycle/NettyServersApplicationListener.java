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

package com.xxlabaza.utils.netty.config.server.lifecycle;

import java.util.List;

import com.xxlabaza.utils.netty.NettyServer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.PriorityOrdered;

@Slf4j
class NettyServersApplicationListener implements ApplicationListener<ApplicationContextEvent>, PriorityOrdered {

  @Autowired(required = false)
  List<NettyServer> servers;

  @Override
  public int getOrder () {
    return HIGHEST_PRECEDENCE;
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
    if (servers == null) {
      log.warn("didn't find any netty server bean");
      return;
    }

    for (val server : servers) {
      val properties = server.getProperties();
      if (server.isRunning() || properties.isAutoStart() == false) {
        continue;
      }

      log.info("starting {}", server);
      server.start();
    }
  }

  void stop () {
    if (servers == null) {
      return;
    }

    for (val server : servers) {
      if (server.isClosed()) {
        continue;
      }

      log.info("closing {}", server);
      server.close();
    }
  }
}
