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

package com.xxlabaza.utils.netty.client.single;

import static io.netty.handler.logging.LogLevel.INFO;
import static java.lang.Integer.MAX_VALUE;

import com.xxlabaza.utils.netty.ApplicationContextHolder;
import com.xxlabaza.utils.netty.handler.ChannelHandlerProvider;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import lombok.val;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@EnableAutoConfiguration
class TestConfiguration {

  @Bean
  ApplicationContextHolder applicationContextHolder () {
    return new ApplicationContextHolder();
  }

  @Bean
  @Order(0)
  ChannelHandler loggingHandler () {
    return new LoggingHandler(INFO);
  }

  @Bean
  @Order(1)
  ChannelHandler lengthFieldPrepender () {
    return new LengthFieldPrepender(2, false);
  }

  @Bean
  @Order(2)
  ChannelHandler lengthFieldBasedFrameDecoder () {
    return ChannelHandlerProvider.from(() ->
        new LengthFieldBasedFrameDecoder(MAX_VALUE, 0, 2)
    );
  }

  @Bean
  @Order(3)
  ChannelHandler messageEncoder () {
    return new MessageEncoder();
  }

  @Bean
  @Order(4)
  ChannelHandler messageDecoder () {
    return new MessageDecoder();
  }

  @Bean
  @Order(5)
  MessageReceiverHandler messageReceiverHandler () {
    val receiver = messageReceiver();
    return new MessageReceiverHandler(receiver);
  }

  @Bean
  MessageReceiver messageReceiver () {
    return new MessageReceiver();
  }
}
