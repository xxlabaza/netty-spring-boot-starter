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

package com.xxlabaza.utils.netty.handler;

import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;

import com.xxlabaza.utils.netty.config.client.builder.NettyClientChannelInitializer;
import com.xxlabaza.utils.netty.config.server.builder.NettyServerChannelInitializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChannelHandlerInitializerPipeline extends ChannelInitializer<SocketChannel> {

  public static ChannelInitializer<SocketChannel> pipelineOf (ChannelHandler... handlers) {
    val collection = asList(handlers);
    return pipelineOf(collection);
  }

  public static ChannelInitializer<SocketChannel> pipelineOf (Collection<ChannelHandler> handlers) {
    return new ChannelHandlerInitializerPipeline(handlers);
  }

  public static NettyClientChannelInitializer clientPipeline (ChannelHandler... handlers) {
    val collection = asList(handlers);
    return clientPipeline(collection);
  }

  public static NettyClientChannelInitializer clientPipeline (Collection<ChannelHandler> handlers) {
    val delegate = pipelineOf(handlers);
    return new NettyClientChannelInitializer(delegate);
  }

  public static NettyServerChannelInitializer serverPipeline (ChannelHandler... handlers) {
    val collection = asList(handlers);
    return serverPipeline(collection);
  }

  public static NettyServerChannelInitializer serverPipeline (Collection<ChannelHandler> handlers) {
    val delegate = pipelineOf(handlers);
    return new NettyServerChannelInitializer(delegate);
  }

  @NonNull
  Collection<ChannelHandler> handlers;

  @Override
  protected void initChannel (SocketChannel socketChannel) throws Exception {
    val pipeline = socketChannel.pipeline();
    for (val handler : handlers) {
      if (handler instanceof ChannelHandlerProvider) {
        val newHandler = ((ChannelHandlerProvider) handler).create();
        pipeline.addLast(newHandler);
      } else {
        pipeline.addLast(handler);
      }
    }
  }
}
