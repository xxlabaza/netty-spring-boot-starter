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

import javax.annotation.PostConstruct;

import com.xxlabaza.utils.netty.NettyServer;
import com.xxlabaza.utils.netty.handler.ChannelHandlerInitializerDelegateWrapper;

import io.appulse.utils.ReflectionUtils;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
class ConnectionsTrackingConfiguration {

  @Autowired
  NettyServer server;

  @Autowired
  ConnectionsRepository repository;

  @PostConstruct
  @SuppressWarnings("unchecked")
  void postConstruct () {
    val serverBootstrap = server.getServerBootstrap();

    val previousInitializer = ReflectionUtils.getFieldValueFrom(serverBootstrap, "childHandler")
        .map(it -> (ChannelInitializer<SocketChannel>) it)
        .orElseThrow(IllegalArgumentException::new);

    val connectionsRegistrator = new ChannelHandlerConnectionsRepository(repository);

    val newInitializer = ChannelHandlerInitializerDelegateWrapper.builder()
        .delegate(previousInitializer)
        .addFirst(connectionsRegistrator)
        .build();

    serverBootstrap.childHandler(newInitializer);
  }
}
