/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.core.cloud.feign;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import feign.Contract;
import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.Target;
import lombok.SneakyThrows;
import org.springblade.core.cloud.sentinel.BladeSentinelInvocationHandler;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * feign集成sentinel自动配置
 * 重写 {@link com.alibaba.cloud.sentinel.feign.SentinelFeign} 适配最新API
 *
 * @author Chill
 */
public class BladeFeignSentinel {

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends Feign.Builder implements ApplicationContextAware {
		private Contract contract = new Contract.Default();
		private ApplicationContext applicationContext;
		private FeignContext feignContext;

		@Override
		public Feign.Builder invocationHandlerFactory(
			InvocationHandlerFactory invocationHandlerFactory) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Builder contract(Contract contract) {
			this.contract = contract;
			return this;
		}

		@Override
		public Feign build() {
			super.invocationHandlerFactory(new InvocationHandlerFactory() {
				@SneakyThrows
				@Override
				public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
					// 注解取值以避免循环依赖的问题
					FeignClient feignClient = AnnotationUtils.findAnnotation(target.type(), FeignClient.class);
					Class fallback = feignClient.fallback();
					Class fallbackFactory = feignClient.fallbackFactory();
					String contextId = feignClient.contextId();

					if (!StringUtils.hasText(contextId)) {
						contextId = feignClient.name();
					}

					Object fallbackInstance;
					FallbackFactory fallbackFactoryInstance;
					// 判断fallback类型
					if (void.class != fallback) {
						fallbackInstance = getFromContext(contextId, "fallback", fallback, target.type());
						return new BladeSentinelInvocationHandler(target, dispatch, new FallbackFactory.Default(fallbackInstance));
					}
					if (void.class != fallbackFactory) {
						fallbackFactoryInstance = (FallbackFactory) getFromContext(contextId, "fallbackFactory", fallbackFactory, FallbackFactory.class);
						return new BladeSentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
					}
					// 默认fallbackFactory
					BladeFallbackFactory bladeFallbackFactory = new BladeFallbackFactory(target);
					return new BladeSentinelInvocationHandler(target, dispatch, bladeFallbackFactory);
				}

				private Object getFromContext(String name, String type, Class fallbackType, Class targetType) {
					Object fallbackInstance = feignContext.getInstance(name, fallbackType);
					if (fallbackInstance == null) {
						throw new IllegalStateException(
							String.format("No %s instance of type %s found for feign client %s",
								type, fallbackType, name)
						);
					}

					if (!targetType.isAssignableFrom(fallbackType)) {
						throw new IllegalStateException(
							String.format("Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
								type, fallbackType, targetType, name)
						);
					}
					return fallbackInstance;
				}
			});
			super.contract(new SentinelContractHolder(contract));
			return super.build();
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
			feignContext = this.applicationContext.getBean(FeignContext.class);
		}
	}

}
