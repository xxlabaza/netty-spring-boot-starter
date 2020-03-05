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

package com.xxlabaza.utils.netty.config.server;

import static lombok.AccessLevel.PRIVATE;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.val;

@With
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class NettyServerConfig {

  public static NettyServerConfigBuilder bind (int port) {
    val bindAddress = new InetSocketAddress(port);
    return bind(bindAddress);
  }

  public static NettyServerConfigBuilder bind (@NonNull String host, int port) {
    val bindAddress = new InetSocketAddress(host, port);
    return bind(bindAddress);
  }

  public static NettyServerConfigBuilder bind (@NonNull InetAddress address, int port) {
    val bindAddress = new InetSocketAddress(address, port);
    return bind(bindAddress);
  }

  public static NettyServerConfigBuilder bind (@NonNull InetSocketAddress address) {
    return builder().bind(address);
  }

  InetSocketAddress bind;

  @NonNull
  @Builder.Default
  String namePrefix = UUID.randomUUID().toString();

  NettyServerProperties properties;

  String propertiesPrefix;

  ServerBootstrap serverBootstrap;

  EventLoopGroup bossGroup;

  EventLoopGroup workerGroup;

  ChannelInitializer<SocketChannel> channelInitializer;

  public static class NettyServerConfigBuilder {

  }
}
