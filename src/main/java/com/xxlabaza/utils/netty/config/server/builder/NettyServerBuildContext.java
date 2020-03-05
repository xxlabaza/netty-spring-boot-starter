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

package com.xxlabaza.utils.netty.config.server.builder;

import com.xxlabaza.utils.netty.config.BuildContext;
import com.xxlabaza.utils.netty.config.server.NettyServerConfig;
import com.xxlabaza.utils.netty.config.server.NettyServerProperties;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.Builder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

/**
 * A context for building a {@link com.xxlabaza.utils.netty.NettyServer} and all attendant beans.
 */
final class NettyServerBuildContext extends BuildContext {

  final NettyServerConfig config;

  NettyServerProperties properties;

  EventLoopGroup bossGroup;

  EventLoopGroup workerGroup;

  ChannelInitializer<SocketChannel> channelInitializer;

  ServerBootstrap serverBootstrap;

  @Builder
  NettyServerBuildContext (DefaultListableBeanFactory beanFactory, Environment environment, NettyServerConfig config) {
    super(beanFactory, environment);
    this.config = config;
  }

  @Override
  public String nameWithSuffix (String suffix) {
    return config.getNamePrefix() + suffix;
  }
}
