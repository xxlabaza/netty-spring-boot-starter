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

import java.util.function.Supplier;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * A provider of the new {@link ChannelHandler} instances.
 * <p>
 * The wrapper implements {@link ChannelHandler} itself,
 * but throws the {@link UnsupportedOperationException}
 * each time the client tries to invoke any of the methods
 * except <b>create</b>.
 */
@RequiredArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChannelHandlerProvider implements ChannelHandler {

  /**
   * Creates a new {@link ChannelHandlerProvider} instance
   * with specified {@link ChannelHandler} supplier.
   *
   * @param supplier the {@link ChannelHandler} supplier.
   *
   * @return the new {@link ChannelHandlerProvider} instance.
   */
  public static ChannelHandler from (Supplier<ChannelHandler> supplier) {
    return new ChannelHandlerProvider(supplier);
  }

  @NonNull
  Supplier<ChannelHandler> supplier;

  /**
   * Creates a new {@link ChannelHandler} instance from the setted {@link Supplier}.
   *
   * @return the new {@link ChannelHandler} instance;
   */
  public ChannelHandler create () {
    return supplier.get();
  }

  @Override
  public void handlerAdded (ChannelHandlerContext context) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handlerRemoved (ChannelHandlerContext context) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void exceptionCaught (ChannelHandlerContext context, Throwable cause) throws Exception {
    throw new UnsupportedOperationException();
  }
}
