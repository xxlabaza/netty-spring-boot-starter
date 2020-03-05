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

import static java.util.Collections.unmodifiableMap;
import static lombok.AccessLevel.PRIVATE;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;

@FieldDefaults(level = PRIVATE, makeFinal = true)
class ConnectionsRepository implements AutoCloseable {

  Map<InetSocketAddress, Channel> repository = new ConcurrentHashMap<>();

  public Channel get (@NonNull InetSocketAddress address) {
    return repository.get(address);
  }

  public int size () {
    return repository.size();
  }

  public Map<InetSocketAddress, Channel> all () {
    return unmodifiableMap(repository);
  }

  public void add (@NonNull Channel channel) {
    val remoteAddress = (InetSocketAddress) channel.remoteAddress();
    repository.put(remoteAddress, channel);
  }

  public void remove (@NonNull InetSocketAddress address) {
    repository.remove(address);
  }

  @Override
  public void close () throws Exception {
    repository.clear();
  }
}
