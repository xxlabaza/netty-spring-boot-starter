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

package com.xxlabaza.utils.netty.config.server.builder;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;
import com.xxlabaza.utils.netty.config.server.NettyServerProperties;

import lombok.val;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.BindResult;

class ConfigurerNettyServerProperties implements BuildContextConfigurer<NettyServerBuildContext> {

  @Override
  public void configure (NettyServerBuildContext context) {
    val suffix = "NettyServerProperties";
    if (context.containsBean(suffix)) {
      context.properties = context.getBean(suffix, NettyServerProperties.class);
      return;
    }

    context.properties = fromConfig(context)
        .orElseGet(() -> fromEnvironment(context)
            .orElseGet(() -> fromContext(context)
                .orElseGet(() -> new NettyServerProperties())));

    ofNullable(context.config.getBind())
        .ifPresent(context.properties::setBind);

    val definition = new GenericBeanDefinition();
    definition.setBeanClass(NettyServerProperties.class);
    definition.setInstanceSupplier(() -> context.properties);

    context.register(suffix, definition);
  }

  private Optional<NettyServerProperties> fromConfig (NettyServerBuildContext context) {
    return ofNullable(context.config.getProperties());
  }

  private Optional<NettyServerProperties> fromEnvironment (NettyServerBuildContext context) {
    return ofNullable(context.config.getPropertiesPrefix())
        .map(prefix -> context.bind(prefix, NettyServerProperties.class))
        .map(BindResult::get);
  }

  private Optional<NettyServerProperties> fromContext (NettyServerBuildContext context) {
    val result = context.getBean(NettyServerProperties.class);
    return ofNullable(result);
  }
}
