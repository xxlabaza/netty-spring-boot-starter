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

package com.xxlabaza.utils.netty.server.single;

import static lombok.AccessLevel.PRIVATE;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@Sharable
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChannelHandlerConnectionsRepository extends ChannelInboundHandlerAdapter {

  ConnectionsRepository repository;

  @Override
  public void channelActive (ChannelHandlerContext context) throws Exception {
    super.channelActive(context);

    val channel = context.channel();
    val socketAddress = channel.remoteAddress();
    if (socketAddress instanceof InetSocketAddress) {
      repository.add(channel);
      log.debug("connection {} registered", socketAddress);
    }
  }

  @Override
  public void channelInactive (ChannelHandlerContext context) throws Exception {
    super.channelInactive(context);

    removeConnection(context);
  }

  @Override
  public void exceptionCaught (ChannelHandlerContext context, Throwable cause) throws Exception {
    log.error("Error during channel connection with {}",
              context.channel().remoteAddress(), cause);

    context.fireExceptionCaught(cause);
    context.close();

    removeConnection(context);
  }

  private void removeConnection (ChannelHandlerContext context) {
    val channel = context.channel();
    val socketAddress = channel.remoteAddress();
    if (socketAddress instanceof InetSocketAddress) {
      repository.remove((InetSocketAddress) socketAddress);
      log.debug("connection {} deregistered", socketAddress);
    }
  }
}
