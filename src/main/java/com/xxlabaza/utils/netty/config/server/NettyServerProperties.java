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

package com.xxlabaza.utils.netty.config.server;

import java.net.InetSocketAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NettyServerProperties {

  InetSocketAddress bind;

  @Builder.Default
  boolean autoStart = true;

  @Builder.Default
  boolean useEpollIfAvailable = true;

  @Builder.Default
  EventLoops pools = new EventLoops();

  @Builder.Default
  ChannelOptionsProperties channelOptions = new ChannelOptionsProperties();

  @Builder.Default
  ChildChannelOptionsProperties childChannelOptions = new ChildChannelOptionsProperties();

  @With
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EventLoops {

    @Builder.Default
    EventLoopProperties boss = new EventLoopProperties(1, "boss-pool");

    @Builder.Default
    EventLoopProperties workers = new EventLoopProperties(Runtime.getRuntime().availableProcessors(), "workers-pool");

    @With
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventLoopProperties {

      @Builder.Default
      int threads = 1;

      @Builder.Default
      String name = "pool";
    }
  }

  @With
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChildChannelOptionsProperties {

    @Builder.Default
    AllocatorProperties allocator = new AllocatorProperties();

    @Builder.Default
    int connectionTimeoutMilliseconds = 1_000;

    @Builder.Default
    boolean singleEventExecutorPerLoop = true;

    @Builder.Default
    boolean keepAlive = true;

    @Builder.Default
    boolean reuseAddress = true;

    @Builder.Default
    boolean tcpNoDelay = true;
  }

  @With
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChannelOptionsProperties {

    @Builder.Default
    AllocatorProperties allocator = new AllocatorProperties();

    @Builder.Default
    int connectionTimeoutMilliseconds = 1_000;

    @Builder.Default
    boolean singleEventExecutorPerLoop = true;

    @Builder.Default
    int backlog = 128;

    @Builder.Default
    boolean reuseAddress = true;
  }

  @With
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AllocatorProperties {

    @Builder.Default
    boolean pooled = true;

    @Builder.Default
    boolean preferDirect = true;
  }
}
