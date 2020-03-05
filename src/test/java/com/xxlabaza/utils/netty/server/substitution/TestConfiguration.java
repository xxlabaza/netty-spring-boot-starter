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

package com.xxlabaza.utils.netty.server.substitution;

import static com.xxlabaza.utils.netty.handler.ChannelHandlerInitializerPipeline.pipelineOf;

import com.xxlabaza.utils.netty.ApplicationContextHolder;

import io.appulse.utils.Bytes;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.val;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
class TestConfiguration {

  @Bean
  ApplicationContextHolder applicationContextHolder () {
    return new ApplicationContextHolder();
  }

  @Bean
  ChannelInitializer<SocketChannel> myServerChannelInititalizer () {
    return pipelineOf(new HelloHandler());
  }

  @Sharable
  private class HelloHandler extends ChannelInboundHandlerAdapter {

    final byte[] message = Bytes.resizableArray()
        .write1B("Hello world".getBytes().length)
        .writeNB("Hello world".getBytes())
        .arrayCopy();

    @Override
    public void channelActive (ChannelHandlerContext context) throws Exception {
      context.fireChannelActive();

      val buffer = context.alloc()
          .buffer(message.length)
          .writeBytes(message);

      context.writeAndFlush(buffer);
    }
  }
}
