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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xxlabaza.utils.netty.config.BuildContextConfigurer;
import com.xxlabaza.utils.netty.config.server.NettyServerConfig;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

@AllArgsConstructor
class NettyServerConfigsBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

  private static final List<BuildContextConfigurer<NettyServerBuildContext>> BUILDERS = asList(
      new ConfigurerNettyServerProperties(),
      new ConfigurerEventLoopGroups(),
      new ConfigurerChannelInitializer(),
      new ConfigurerServerBootstrap(),
      new ConfigurerNettyServer()
  );

  @NonNull
  Environment environment;

  @Override
  public void postProcessBeanFactory (ConfigurableListableBeanFactory beanFactory) {
    val map = beanFactory.getBeansOfType(NettyServerConfig.class);
    if (map == null) {
      return;
    }

    val factory = (DefaultListableBeanFactory) beanFactory;
    map.values()
        .stream()
        .map(config -> NettyServerBuildContext.builder()
            .beanFactory(factory)
            .environment(environment)
            .config(config)
            .build())
        .peek(this::process)
        .map(NettyServerBuildContext::getDefinitions)
        .map(Map::entrySet)
        .flatMap(Collection::stream)
        .collect(toMap(Entry::getKey, Entry::getValue))
        .forEach((name, definition) -> {
          factory.registerBeanDefinition(name, definition);
        });
  }

  private void process (NettyServerBuildContext context) {
    BUILDERS.forEach(it -> it.configure(context));
  }
}
