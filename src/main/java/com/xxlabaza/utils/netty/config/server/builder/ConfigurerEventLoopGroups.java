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

import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;
import com.xxlabaza.utils.netty.config.server.NettyServerProperties.EventLoops.EventLoopProperties;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.val;
import org.springframework.beans.factory.support.GenericBeanDefinition;

class ConfigurerEventLoopGroups implements BuildContextConfigurer<NettyServerBuildContext> {

  @Override
  public void configure (NettyServerBuildContext context) {
    registerNettyServerBossGroup(context);
    registerNettyServerWorkerGroup(context);
  }

  private void registerNettyServerBossGroup (NettyServerBuildContext context) {
    val suffix = "NettyServerBossGroup";
    if (context.containsBean(suffix)) {
      context.bossGroup = context.getBean(suffix, EventLoopGroup.class);
      return;
    }

    val poolProperties = context.properties.getPools().getBoss();
    context.bossGroup = ofNullable(context.config.getBossGroup())
        .orElseGet(() -> fromContext(context)
            .orElseGet(() -> create(context, poolProperties)));

    registerLoopGroup(context, suffix, context.bossGroup);
  }

  private void registerNettyServerWorkerGroup (NettyServerBuildContext context) {
    String suffix = "NettyServerWorkerGroup";
    if (context.containsBean(suffix)) {
      context.workerGroup = context.getBean(suffix, EventLoopGroup.class);
      return;
    }

    EventLoopProperties poolProperties = context.properties.getPools().getWorkers();
    context.workerGroup = ofNullable(context.config.getWorkerGroup())
        .orElseGet(() -> fromContext(context)
            .orElseGet(() -> create(context, poolProperties)));

    registerLoopGroup(context, suffix, context.workerGroup);
  }

  private Optional<EventLoopGroup> fromContext (NettyServerBuildContext context) {
    val result = context.getBean(EventLoopGroup.class);
    return ofNullable(result);
  }

  private EventLoopGroup create (NettyServerBuildContext context, EventLoopProperties properties) {
    val useEpollIfAvailable = context.properties.isUseEpollIfAvailable();
    val threadFactory = new DefaultThreadFactory(properties.getName());
    return Epoll.isAvailable() && useEpollIfAvailable
          ? new EpollEventLoopGroup(properties.getThreads(), threadFactory)
          : new NioEventLoopGroup(properties.getThreads(), threadFactory);
  }

  private void registerLoopGroup (NettyServerBuildContext context, String suffix, EventLoopGroup group) {
    val definition = new GenericBeanDefinition();
    definition.setBeanClass(EventLoopGroup.class);
    definition.setInstanceSupplier(() -> group);
    definition.setDestroyMethodName("shutdownGracefully");
    context.register(suffix, definition);
  }
}
