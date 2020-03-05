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

package com.xxlabaza.utils.netty.config.client;

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
public class NettyClientConfig {

  public static NettyClientConfigBuilder connect (int port) {
    val connectAddress = new InetSocketAddress(port);
    return connect(connectAddress);
  }

  public static NettyClientConfigBuilder connect (@NonNull String host, int port) {
    val connectAddress = new InetSocketAddress(host, port);
    return connect(connectAddress);
  }

  public static NettyClientConfigBuilder connect (@NonNull InetAddress address, int port) {
    val connectAddress = new InetSocketAddress(address, port);
    return connect(connectAddress);
  }

  public static NettyClientConfigBuilder connect (@NonNull InetSocketAddress address) {
    return builder().connect(address);
  }

  InetSocketAddress connect;

  @NonNull
  @Builder.Default
  String namePrefix = UUID.randomUUID().toString();

  NettyClientProperties properties;

  String propertiesPrefix;

  ServerBootstrap serverBootstrap;

  EventLoopGroup group;

  ChannelInitializer<SocketChannel> channelInitializer;

  public static class NettyClientConfigBuilder {

  }
}
