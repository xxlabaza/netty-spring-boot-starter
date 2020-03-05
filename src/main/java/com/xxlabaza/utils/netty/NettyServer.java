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

package com.xxlabaza.utils.netty;

import java.net.InetSocketAddress;

import com.xxlabaza.utils.netty.config.server.NettyServerProperties;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@ToString(of = "bind")
public final class NettyServer implements AutoCloseable {

  @Getter
  final ServerBootstrap serverBootstrap;

  @Getter
  final NettyServerProperties properties;

  final InetSocketAddress bind;

  Channel channel;

  @Builder
  NettyServer (@NonNull ServerBootstrap serverBootstrap,
               @NonNull NettyServerProperties properties
  ) {
    this.serverBootstrap = serverBootstrap;
    this.properties = properties;
    bind = properties.getBind();
  }

  @SneakyThrows
  public synchronized void start () {
    if (channel != null) {
      return;
    }
    val future = serverBootstrap.bind(bind).sync();
    log.info("started {}", this);

    channel = future.channel();
  }

  public synchronized void pause () {
    channel.config().setAutoRead(false);
    log.info("paused {}", this);
  }

  public synchronized void resume () {
    channel.config().setAutoRead(true);
    log.info("resumed {}", this);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("PMD.NullAssignment")
  public synchronized void close () {
    if (channel == null) {
      return;
    }
    pause();

    serverBootstrap.config().group().shutdownGracefully();
    serverBootstrap.config().childGroup().shutdownGracefully();
    channel.closeFuture().sync();
    channel = null;

    log.info("closed {}", this);
  }

  public boolean isRunning () {
    return channel != null && channel.isWritable();
  }

  public synchronized boolean isClosed () {
    return isRunning() == false;
  }
}
