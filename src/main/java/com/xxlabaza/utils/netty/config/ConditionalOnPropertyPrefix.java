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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Optional.ofNullable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.xxlabaza.utils.netty.config.ConditionalOnPropertyPrefix.OnPropertyPrefix;

import lombok.val;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Conditional(OnPropertyPrefix.class)
public @interface ConditionalOnPropertyPrefix {

  String value ();

  class OnPropertyPrefix implements Condition {

    @Override
    public boolean matches (ConditionContext context, AnnotatedTypeMetadata metadata) {
      val annotationName = ConditionalOnPropertyPrefix.class.getName();
      val allAnnotationAttributes = metadata.getAllAnnotationAttributes(annotationName);
      if (allAnnotationAttributes == null) {
        return false;
      }

      val value = allAnnotationAttributes.get("value");
      if (value == null || value.isEmpty()) {
        return false;
      }

      val prefix = value.get(0).toString();
      return ofNullable(context)
          .map(ConditionContext::getEnvironment)
          .filter(it -> it instanceof AbstractEnvironment)
          .map(it -> (AbstractEnvironment) it)
          .map(AbstractEnvironment::getPropertySources)
          .map(MutablePropertySources::spliterator)
          .map(spliterator -> StreamSupport.stream(spliterator, false)
              .filter(it -> it instanceof EnumerablePropertySource)
              .map(it -> (EnumerablePropertySource<?>) it)
              .map(EnumerablePropertySource::getPropertyNames)
              .flatMap(Stream::of)
              .anyMatch(it -> it.startsWith(prefix)))
          .orElse(false);
    }
  }
}
