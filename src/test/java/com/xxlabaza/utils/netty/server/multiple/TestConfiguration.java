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

package com.xxlabaza.utils.netty.server.multiple;

import com.xxlabaza.utils.netty.ApplicationContextHolder;
import com.xxlabaza.utils.netty.config.server.NettyServerConfig;

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
  NettyServerConfig secondNettyServerConfig () {
    return NettyServerConfig.builder()
        .namePrefix("second")
        .propertiesPrefix("second.server")
        .build();
  }

  @Bean
  NettyServerConfig thirdNettyServerConfig () {
    return NettyServerConfig.builder()
        .namePrefix("third")
        .propertiesPrefix("third.server")
        .build();
  }
}
