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

import io.appulse.utils.Bytes;
import io.appulse.utils.HexUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@Sharable
class EchoChannelHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead (ChannelHandlerContext context, Object object) throws Exception {
    if ((object instanceof Bytes) == false) {
      log.error("invalid inbound message - {}", object);
      return;
    }
    val channel = context.channel();

    val bytes = (Bytes) object;
    val hex = HexUtil.prettyHexDump(bytes);
    log.debug("inbound message from {}\n{}\n", channel.remoteAddress(), hex);

    bytes.readerIndex(0);
    channel.writeAndFlush(bytes);
  }
}
