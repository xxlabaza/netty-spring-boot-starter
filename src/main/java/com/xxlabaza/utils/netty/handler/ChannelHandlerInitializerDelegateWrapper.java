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

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.val;

@Builder
@Sharable
@RequiredArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChannelHandlerInitializerDelegateWrapper extends ChannelInitializer<SocketChannel> {

  @NonNull
  ChannelInitializer<SocketChannel> delegate;

  @Singular
  List<ChannelHandler> addFirsts;

  @Singular
  List<ChannelHandler> addLasts;

  @Override
  public void handlerAdded (ChannelHandlerContext context) throws Exception {
    delegate.handlerAdded(context);

    val pipeline = context.channel().pipeline();
    if (addFirsts != null) {
      addFirsts.forEach(pipeline::addFirst);
    }
    if (addLasts != null) {
      addLasts.forEach(pipeline::addLast);
    }
  }

  @Override
  public void handlerRemoved (ChannelHandlerContext context) throws Exception {
    delegate.handlerRemoved(context);
  }

  @Override
  public void exceptionCaught (ChannelHandlerContext context, Throwable cause) throws Exception {
    delegate.exceptionCaught(context, cause);
  }

  @Override
  public void channelUnregistered (ChannelHandlerContext context) throws Exception {
    delegate.channelUnregistered(context);
  }

  @Override
  public void channelActive (ChannelHandlerContext context) throws Exception {
    delegate.channelActive(context);
  }

  @Override
  public void channelInactive (ChannelHandlerContext context) throws Exception {
    delegate.channelInactive(context);
  }

  @Override
  public void channelRead (ChannelHandlerContext context, Object message) throws Exception {
    delegate.channelRead(context, message);
  }

  @Override
  public void channelReadComplete (ChannelHandlerContext context) throws Exception {
    delegate.channelReadComplete(context);
  }

  @Override
  public void userEventTriggered (ChannelHandlerContext context, Object event) throws Exception {
    delegate.userEventTriggered(context, event);
  }

  @Override
  public void channelWritabilityChanged (ChannelHandlerContext context) throws Exception {
    delegate.channelWritabilityChanged(context);
  }

  @Override
  public boolean isSharable () {
    return delegate.isSharable();
  }

  @Override
  protected void initChannel (SocketChannel socketChannel) throws Exception {
    // nothing
  }
}
