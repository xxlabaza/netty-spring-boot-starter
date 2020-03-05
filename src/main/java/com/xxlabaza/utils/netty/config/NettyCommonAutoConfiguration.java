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

package com.xxlabaza.utils.netty.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The auto configuration for different common Netty's beans.
 */
@Configuration
public class NettyCommonAutoConfiguration {

  /**
   * Creates a converter from {@link Integer} to {@link InetSocketAddress}.
   *
   * @return the converter for {@link InetSocketAddress}.
   */
  @Bean
  @ConfigurationPropertiesBinding
  ConverterIntegerToInetSocketAddress converterIntegerToInetSocketAddress () {
    return new ConverterIntegerToInetSocketAddress();
  }

  /**
   * Creates a converter from {@link String} to {@link InetSocketAddress}.
   *
   * @return the converter for {@link InetSocketAddress}.
   */
  @Bean
  @ConfigurationPropertiesBinding
  ConverterStringToInetSocketAddress converterStringToInetSocketAddress () {
    return new ConverterStringToInetSocketAddress();
  }
}
