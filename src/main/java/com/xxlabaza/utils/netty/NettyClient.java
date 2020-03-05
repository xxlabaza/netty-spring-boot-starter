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
import java.util.concurrent.CompletableFuture;

import com.xxlabaza.utils.netty.config.client.NettyClientProperties;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@ToString(of = "remote")
public final class NettyClient implements AutoCloseable {

  @Getter
  final Bootstrap bootstrap;

  @Getter
  final NettyClientProperties properties;

  final InetSocketAddress remote;

  Channel channel;

  @Builder
  NettyClient (@NonNull Bootstrap bootstrap,
               @NonNull NettyClientProperties properties
  ) {
    this.bootstrap = bootstrap;
    this.properties = properties;
    remote = properties.getConnect();
  }

  @SuppressWarnings("PMD.UseDiamondOperator")
  public synchronized CompletableFuture<Void> send (Object message) {
    if (isDisconnected()) {
      connect();
    }

    val future = new CompletableFuture<Void>();
    channel.eventLoop().execute(() -> {
      channel.writeAndFlush(message).addListener(it -> {
        if (it.isSuccess()) {
          future.complete(null);
        } else {
          future.completeExceptionally(it.cause());
        }
      });
    });
    return future;
  }

  @SneakyThrows
  public synchronized void connect () {
    if (channel != null) {
      return;
    }
    val future = bootstrap.connect(remote).sync();
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

    bootstrap.config().group().shutdownGracefully();
    channel.closeFuture().sync();
    channel = null;

    log.info("closed {}", this);
  }

  public boolean isConnected () {
    return channel != null && channel.isWritable();
  }

  public synchronized boolean isDisconnected () {
    return isConnected() == false;
  }
}
