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

package com.xxlabaza.utils.netty.config.client.builder;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;
import com.xxlabaza.utils.netty.config.client.NettyClientProperties;

import lombok.val;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.BindResult;

class ConfigurerNettyClientProperties implements BuildContextConfigurer<NettyClientBuildContext> {

  @Override
  public void configure (NettyClientBuildContext context) {
    val suffix = "NettyClientProperties";
    if (context.containsBean(suffix)) {
      context.properties = context.getBean(suffix, NettyClientProperties.class);
      return;
    }

    context.properties = fromConfig(context)
        .orElseGet(() -> fromEnvironment(context)
            .orElseGet(() -> fromContext(context)
                .orElseGet(() -> new NettyClientProperties())));

    ofNullable(context.config.getConnect())
        .ifPresent(context.properties::setConnect);

    val definition = new GenericBeanDefinition();
    definition.setBeanClass(NettyClientProperties.class);
    definition.setInstanceSupplier(() -> context.properties);

    context.register(suffix, definition);
  }

  private Optional<NettyClientProperties> fromConfig (NettyClientBuildContext context) {
    return ofNullable(context.config.getProperties());
  }

  private Optional<NettyClientProperties> fromEnvironment (NettyClientBuildContext context) {
    return ofNullable(context.config.getPropertiesPrefix())
        .map(prefix -> context.bind(prefix, NettyClientProperties.class))
        .map(BindResult::get);
  }

  private Optional<NettyClientProperties> fromContext (NettyClientBuildContext context) {
    val result = context.getBean(NettyClientProperties.class);
    return ofNullable(result);
  }
}
