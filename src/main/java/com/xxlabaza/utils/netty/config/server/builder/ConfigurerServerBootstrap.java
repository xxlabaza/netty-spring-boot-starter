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

import static io.netty.channel.ChannelOption.ALLOCATOR;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.channel.ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP;
import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.channel.ChannelOption.SO_REUSEADDR;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;
import com.xxlabaza.utils.netty.config.server.NettyServerProperties.ChannelOptionsProperties;
import com.xxlabaza.utils.netty.config.server.NettyServerProperties.ChildChannelOptionsProperties;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.val;
import org.springframework.beans.factory.support.GenericBeanDefinition;

class ConfigurerServerBootstrap implements BuildContextConfigurer<NettyServerBuildContext> {

  @Override
  public void configure (NettyServerBuildContext context) {
    val suffix = "NettyServerBootstrap";
    if (context.containsBean(suffix)) {
      context.serverBootstrap = context.getBean(suffix, ServerBootstrap.class);
      configureBootstrap(context);
      return;
    }

    context.serverBootstrap = fromConfig(context)
        .orElseGet(() -> fromContext(context)
            .orElseGet(() -> create(context)));

    configureBootstrap(context);

    val definition = new GenericBeanDefinition();
    definition.setBeanClass(ServerBootstrap.class);
    definition.setInstanceSupplier(() -> context.serverBootstrap);

    context.register(suffix, definition);
  }

  private Optional<ServerBootstrap> fromConfig (NettyServerBuildContext context) {
    return ofNullable(context.serverBootstrap);
  }

  private Optional<ServerBootstrap> fromContext (NettyServerBuildContext context) {
    val result = context.getBean(ServerBootstrap.class);
    return ofNullable(result);
  }

  private ServerBootstrap create (NettyServerBuildContext context) {
    Class<? extends ServerChannel> serverChannelClass;
    if (Epoll.isAvailable() && context.properties.isUseEpollIfAvailable()) {
      serverChannelClass = EpollServerSocketChannel.class;
    } else {
      serverChannelClass = NioServerSocketChannel.class;
    }

    ServerBootstrap serverBootstrap = new ServerBootstrap()
        .group(context.workerGroup, context.workerGroup)
        .channel(serverChannelClass)
        .childHandler(context.channelInitializer);

    makeChannelOptions(serverBootstrap, context.properties.getChannelOptions());
    makeChannelChildOptions(serverBootstrap, context.properties.getChildChannelOptions());

    return serverBootstrap;
  }

  private void makeChannelOptions (ServerBootstrap serverBootstrap, ChannelOptionsProperties options) {
    val allocatorProperties = options.getAllocator();
    val allocator = allocatorProperties.isPooled()
                    ? new PooledByteBufAllocator(allocatorProperties.isPreferDirect())
                    : new UnpooledByteBufAllocator(allocatorProperties.isPreferDirect());

    serverBootstrap
        .option(ALLOCATOR, allocator)
        .option(CONNECT_TIMEOUT_MILLIS, options.getConnectionTimeoutMilliseconds())
        .option(SINGLE_EVENTEXECUTOR_PER_GROUP, options.isSingleEventExecutorPerLoop())
        .option(SO_BACKLOG, options.getBacklog())
        .option(SO_REUSEADDR, options.isReuseAddress());
  }

  private void makeChannelChildOptions (ServerBootstrap serverBootstrap, ChildChannelOptionsProperties options) {
    val allocatorProperties = options.getAllocator();
    val allocator = allocatorProperties.isPooled()
                    ? new PooledByteBufAllocator(allocatorProperties.isPreferDirect())
                    : new UnpooledByteBufAllocator(allocatorProperties.isPreferDirect());

    serverBootstrap
        .childOption(ALLOCATOR, allocator)
        .childOption(CONNECT_TIMEOUT_MILLIS, options.getConnectionTimeoutMilliseconds())
        .childOption(SINGLE_EVENTEXECUTOR_PER_GROUP, options.isSingleEventExecutorPerLoop())
        .childOption(SO_KEEPALIVE, options.isKeepAlive())
        .childOption(SO_REUSEADDR, options.isReuseAddress())
        .childOption(TCP_NODELAY, options.isTcpNoDelay());
  }

  private void configureBootstrap (NettyServerBuildContext context) {
    val bootstrap = context.serverBootstrap;
    val configurers = context.getBeansOfType(NettyServerBootstrapConfigurer.class);
    for (val configurer : configurers) {
      configurer.configure(bootstrap);
    }
  }
}
