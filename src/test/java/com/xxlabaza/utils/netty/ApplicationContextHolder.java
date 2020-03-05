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

package com.xxlabaza.utils.netty;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * An app{@link ApplicationContext} context golder for test purposes.
 */
public class ApplicationContextHolder implements ApplicationContextAware {

  /**
   * Static {@link ApplicationContext} instance holder.
   */
  public static ApplicationContext context;

  @Override
  public synchronized void setApplicationContext (ApplicationContext value) throws BeansException {
    if (ApplicationContextHolder.context == null) {
      ApplicationContextHolder.context = value;
    }
  }

  /**
   * Return the bean instance that uniquely matches the given object type, if any.
   *
   * @param <T> the type of the desired bean.
   *
   * @param type type the bean must match; can be an interface or superclass.
   *
   * @return an instance of the single bean matching the required type.
   */
  public static <T> T getBean (Class<T> type) {
    return context.getBean(type);
  }

  /**
   * Return an instance, which may be shared or independent, of the specified bean.
   *
   * @param <T> the type of the desired bean.
   *
   * @param name the name of the bean to retrieve
   *
   * @param type type the bean must match; can be an interface or superclass.
   *
   * @return an instance of the bean.
   */
  public static <T> T getBean (String name, Class<T> type) {
    return context.getBean(name, type);
  }

  /**
   * Return the bean instances that match the given object type (including subclasses).
   *
   * @param <T> the type of the desired beans.
   *
   * @param type the class or interface to match, or {@code null} for all concrete beans
   *
   * @return a Map with the matching beans, containing the bean names as
   *         keys and the corresponding bean instances as values
   */
  public static <T> Map<String, T> getBeans (Class<T> type) {
    return context.getBeansOfType(type);
  }
}
