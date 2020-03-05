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

package com.xxlabaza.utils.netty.config.client.builder;

import static io.netty.channel.ChannelOption.ALLOCATOR;
import static io.netty.channel.ChannelOption.AUTO_READ;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.channel.ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.channel.ChannelOption.SO_REUSEADDR;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.val;
import org.springframework.beans.factory.support.GenericBeanDefinition;

class ConfigurerBootstrap implements BuildContextConfigurer<NettyClientBuildContext> {

  @Override
  public void configure (NettyClientBuildContext context) {
    val suffix = "NettyClientBootstrap";
    if (context.containsBean(suffix)) {
      context.bootstrap = context.getBean(suffix, Bootstrap.class);
      configureBootstrap(context);
      return;
    }

    context.bootstrap = fromConfig(context)
        .orElseGet(() -> fromContext(context)
            .orElseGet(() -> create(context)));

    configureBootstrap(context);

    val definition = new GenericBeanDefinition();
    definition.setBeanClass(Bootstrap.class);
    definition.setInstanceSupplier(() -> context.bootstrap);

    context.register(suffix, definition);
  }

  private Optional<Bootstrap> fromConfig (NettyClientBuildContext context) {
    return ofNullable(context.bootstrap);
  }

  private Optional<Bootstrap> fromContext (NettyClientBuildContext context) {
    val result = context.getBean(Bootstrap.class);
    return ofNullable(result);
  }

  private Bootstrap create (NettyClientBuildContext context) {
    val options = context.properties.getChannelOptions();

    val allocatorProperties = options.getAllocator();
    val allocator = allocatorProperties.isPooled()
                    ? new PooledByteBufAllocator(allocatorProperties.isPreferDirect())
                    : new UnpooledByteBufAllocator(allocatorProperties.isPreferDirect());

    Class<? extends Channel> clientChannelClass;
    if (Epoll.isAvailable() && context.properties.isUseEpollIfAvailable()) {
      clientChannelClass = EpollSocketChannel.class;
    } else {
      clientChannelClass = NioSocketChannel.class;
    }

    return new Bootstrap()
        .group(context.group)
        .channel(clientChannelClass)
        .handler(context.channelInitializer)
        .option(ALLOCATOR, allocator)
        .option(AUTO_READ, options.isAutoRead())
        .option(CONNECT_TIMEOUT_MILLIS, options.getConnectionTimeoutMilliseconds())
        .option(SINGLE_EVENTEXECUTOR_PER_GROUP, options.isSingleEventExecutorPerLoop())
        .option(SO_KEEPALIVE, options.isKeepAlive())
        .option(SO_REUSEADDR, options.isReuseAddress())
        .option(TCP_NODELAY, options.isTcpNoDelay());
  }

  private void configureBootstrap (NettyClientBuildContext context) {
    val bootstrap = context.bootstrap;
    val configurers = context.getBeansOfType(NettyClientBootstrapConfigurer.class);
    for (val configurer : configurers) {
      configurer.configure(bootstrap);
    }
  }
}
