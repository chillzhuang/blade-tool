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
package com.alibaba.cloud.seata.feign;

import feign.Client;
import feign.Feign;
import feign.Retryer;
import lombok.RequiredArgsConstructor;
import org.springblade.core.cloud.feign.BladeFeignSentinel;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 重写SeataFeignClientAutoConfiguration以适配最新API
 *
 * @author Chill
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Client.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class SeataFeignClientAutoConfiguration {

	@Bean
	@Scope("prototype")
	@ConditionalOnClass(name = "com.alibaba.csp.sentinel.SphU")
	@ConditionalOnProperty(name = "feign.sentinel.enabled", havingValue = "true")
	Feign.Builder feignSentinelBuilder(BeanFactory beanFactory) {
		return BladeFeignSentinel.builder().retryer(Retryer.NEVER_RETRY)
			.client(new SeataFeignClient(beanFactory));
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope("prototype")
	Feign.Builder feignBuilder(BeanFactory beanFactory) {
		return SeataFeignBuilder.builder(beanFactory);
	}

	@Configuration(proxyBeanMethods = false)
	@RequiredArgsConstructor
	protected static class FeignBeanPostProcessorConfiguration {
		private final LoadBalancedRetryFactory loadBalancedRetryFactory;
		private final LoadBalancerProperties properties;
		private final LoadBalancerClientFactory loadBalancerClientFactory;

		@Bean
		SeataBeanPostProcessor seataBeanPostProcessor(SeataFeignObjectWrapper seataFeignObjectWrapper) {
			return new SeataBeanPostProcessor(seataFeignObjectWrapper);
		}

		@Bean
		SeataContextBeanPostProcessor seataContextBeanPostProcessor(BeanFactory beanFactory) {
			return new SeataContextBeanPostProcessor(beanFactory);
		}

		@Bean
		SeataFeignObjectWrapper seataFeignObjectWrapper(BeanFactory beanFactory) {
			return new SeataFeignObjectWrapper(beanFactory, loadBalancedRetryFactory, properties, loadBalancerClientFactory);
		}

	}
}
