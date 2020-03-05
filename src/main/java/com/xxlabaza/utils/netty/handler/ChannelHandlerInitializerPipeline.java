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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

@AllArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChannelHandlerInitializerPipeline extends ChannelInitializer<SocketChannel> {

  public static ChannelInitializer<SocketChannel> of (ChannelHandler... handlers) {
    val collection = asList(handlers);
    return new ChannelHandlerInitializerPipeline(collection);
  }

  public static ChannelInitializer<SocketChannel> of (Collection<ChannelHandler> handlers) {
    return new ChannelHandlerInitializerPipeline(handlers);
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
