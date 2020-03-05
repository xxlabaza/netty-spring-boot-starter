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

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;

/**
 * A build context for creating a bean in a dynamic manner.
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public abstract class BuildContext {

  DefaultListableBeanFactory beanFactory;

  Binder binder;

  @Getter
  Map<String, BeanDefinition> definitions;

  /**
   * Context's constructor.
   *
   * @param beanFactory Spring's bean factory.
   *
   * @param environment Spring's environment.
   */
  protected BuildContext (DefaultListableBeanFactory beanFactory, Environment environment) {
    this.beanFactory = beanFactory;
    definitions = new HashMap<>();

    val sources = ConfigurationPropertySources.get(environment);
    val placeholdersResolver = new PropertySourcesPlaceholdersResolver(environment);

    val conversionService = new DefaultConversionService();
    getBeansOfType(Converter.class)
        .forEach(conversionService::addConverter);

    binder = new Binder(sources, placeholdersResolver, conversionService);
  }

  /**
   * Creates a string by a suffix (prefix should be taken from a config).
   *
   * @param suffix the name's suffix
   *
   * @return a full string based on the suffix.
   */
  public abstract String nameWithSuffix (String suffix);

  /**
   * Adds a bean's definition to the register.
   *
   * @param suffix the name's suffix
   *
   * @param definition the bean's definition.
   */
  public void register (String suffix, BeanDefinition definition) {
    val name = nameWithSuffix(suffix);
    definitions.put(name, definition);
  }

  /**
   * Checks the existens of a bean by its name's suffix.
   *
   * @param suffix the name's suffix
   *
   * @return {@code true} if the bean exists, {@code false} otherwise.
   */
  public boolean containsBean (String suffix) {
    val name = nameWithSuffix(suffix);
    return beanFactory.containsBean(name);
  }

  /**
   * Return the bean instance that uniquely matches the given object type, if any.
   *
   * @param <T> the type of the desired bean.
   *
   * @param type type the bean must match; can be an interface or superclass.
   *
   * @return an instance of the single bean matching the required type or
   *         {@code null} if it doesn't exist.
   */
  public <T> T getBean (Class<T> type) {
    try {
      return beanFactory.getBean(type);
    } catch (BeansException ex) {
      return null;
    }
  }

  /**
   * Return an instance, which may be shared or independent, of the specified bean.
   *
   * @param <T> the type of the desired bean.
   *
   * @param suffix the name's suffix
   *
   * @param type type the bean must match; can be an interface or superclass.
   *
   * @return an instance of the bean.
   */
  public <T> T getBean (String suffix, Class<T> type) {
    val name = nameWithSuffix(suffix);
    return beanFactory.getBean(name, type);
  }

  /**
   * Return the bean instances that match the given object type (including subclasses).
   *
   * @param <T> the type of the desired beans.
   *
   * @param type the class or interface to match, or {@code null} for all concrete beans
   *
   * @return a List with the matching beans.
   */
  public <T> List<T> getBeansOfType (Class<T> type) {
    val map = beanFactory.getBeansOfType(type);
    return map == null
           ? emptyList()
           : new ArrayList<>(map.values());
  }

  /**
   * Binds the {@link Environment}'s properties by a specified prefix
   * to a specific type.
   *
   * @param <T> specified type
   *
   * @param prefix the configuration property prefix to bind
   *
   * @param target the target class
   *
   * @return the binding result (never null)
   */
  public <T> BindResult<T> bind (String prefix, Class<T> target) {
    return binder.bind(prefix, target);
  }
}
