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

import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.val;
import org.springframework.beans.factory.support.GenericBeanDefinition;

class ConfigurerEventLoopGroup implements BuildContextConfigurer<NettyClientBuildContext> {

  @Override
  public void configure (NettyClientBuildContext context) {
    val suffix = "NettyClientGroup";
    if (context.containsBean(suffix)) {
      context.group = context.getBean(suffix, EventLoopGroup.class);
      return;
    }

    context.group = fromConfig(context)
        .orElseGet(() -> fromContext(context)
            .orElseGet(() -> create(context)));

    val definition = new GenericBeanDefinition();
    definition.setBeanClass(EventLoopGroup.class);
    definition.setInstanceSupplier(() -> context.group);
    definition.setDestroyMethodName("shutdownGracefully");

    context.register(suffix, definition);
  }

  private Optional<EventLoopGroup> fromConfig (NettyClientBuildContext context) {
    return ofNullable(context.config.getGroup());
  }

  private Optional<EventLoopGroup> fromContext (NettyClientBuildContext context) {
    val result = context.getBean(EventLoopGroup.class);
    return ofNullable(result);
  }

  private EventLoopGroup create (NettyClientBuildContext context) {
    val eventLoopProperties = context.properties.getEventLoop();
    val useEpollIfAvailable = context.properties.isUseEpollIfAvailable();
    val threadFactory = new DefaultThreadFactory(eventLoopProperties.getName());
    return Epoll.isAvailable() && useEpollIfAvailable
          ? new EpollEventLoopGroup(eventLoopProperties.getThreads(), threadFactory)
          : new NioEventLoopGroup(eventLoopProperties.getThreads(), threadFactory);
  }
}
