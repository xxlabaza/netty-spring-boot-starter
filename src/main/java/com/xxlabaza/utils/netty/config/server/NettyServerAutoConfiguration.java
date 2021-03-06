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

import com.xxlabaza.utils.netty.config.server.builder.NettyServerBuilderConfiguration;
import com.xxlabaza.utils.netty.config.server.lifecycle.NettyServerLifecycleConfiguration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    DefaultNettyServerConfiguration.class,
    NettyServerBuilderConfiguration.class,
    NettyServerLifecycleConfiguration.class
})
@AutoConfigureAfter(PropertyPlaceholderAutoConfiguration.class)
public class NettyServerAutoConfiguration {

}
