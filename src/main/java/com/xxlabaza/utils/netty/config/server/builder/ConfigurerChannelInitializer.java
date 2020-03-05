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

import static com.xxlabaza.utils.netty.handler.ChannelHandlerInitializerPipeline.pipelineOf;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.util.Collections;
import java.util.Optional;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.val;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

class ConfigurerChannelInitializer implements BuildContextConfigurer<NettyServerBuildContext> {

  @Override
  @SuppressWarnings("unchecked")
  public void configure (NettyServerBuildContext context) {
    val suffix = "NettyServerChannelInitializer";
    if (context.containsBean(suffix)) {
      context.channelInitializer = (ChannelInitializer<SocketChannel>) context.getBean(suffix, ChannelInitializer.class);
      return;
    }

    context.channelInitializer = fromConfig(context)
        .orElseGet(() -> fromContext(context)
            .orElseGet(() -> create(context)));

    val definition = new GenericBeanDefinition();
    definition.setBeanClass(ChannelInitializer.class);
    definition.setInstanceSupplier(() -> context.channelInitializer);

    context.register(suffix, definition);
  }

  private Optional<ChannelInitializer<SocketChannel>> fromConfig (NettyServerBuildContext context) {
    return ofNullable(context.config.getChannelInitializer());
  }

  @SuppressWarnings("unchecked")
  private Optional<ChannelInitializer<SocketChannel>> fromContext (NettyServerBuildContext context) {
    val serverChannelInitializer = context.getBean(NettyServerChannelInitializer.class);
    if (serverChannelInitializer != null) {
      return of(serverChannelInitializer);
    }

    val channelInitializer = (ChannelInitializer<SocketChannel>) context.getBean(ChannelInitializer.class);
    return ofNullable(channelInitializer);
  }

  private ChannelInitializer<SocketChannel> create (NettyServerBuildContext context) {
    val handlers = context.getBeansOfType(ChannelHandler.class);
    Collections.sort(handlers, AnnotationAwareOrderComparator.INSTANCE);
    return pipelineOf(handlers);
  }
}
