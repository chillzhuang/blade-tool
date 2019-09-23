/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springblade.core.cloud.feign;

import com.netflix.hystrix.HystrixCommand;
import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import feign.hystrix.HystrixFeign;
import org.springblade.core.tool.convert.EnumToStringConverter;
import org.springblade.core.tool.convert.StringToEnumConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.BladeFeignClientsRegistrar;
import org.springframework.cloud.openfeign.BladeHystrixTargeter;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

import java.util.ArrayList;


/**
 * blade feign 增强配置
 *
 * @author L.cm
 */
@Configuration
@ConditionalOnClass(Feign.class)
@Import(BladeFeignClientsRegistrar.class)
@AutoConfigureAfter(EnableFeignClients.class)
public class BladeFeignAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Targeter bladeFeignTargeter() {
		return new BladeHystrixTargeter();
	}

	@Configuration("hystrixFeignConfiguration")
	@ConditionalOnClass({ HystrixCommand.class, HystrixFeign.class })
	protected static class HystrixFeignConfiguration {
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		@ConditionalOnProperty("feign.hystrix.enabled")
		public Feign.Builder feignHystrixBuilder(
			RequestInterceptor requestInterceptor, Contract feignContract) {
			return HystrixFeign.builder()
				.contract(feignContract)
				.decode404()
				.requestInterceptor(requestInterceptor);
		}

		@Bean
		@ConditionalOnMissingBean
		public RequestInterceptor requestInterceptor() {
			return new BladeFeignRequestHeaderInterceptor();
		}
	}

	/**
	 * blade enum 《-》 String 转换配置
	 * @param conversionService ConversionService
	 * @return SpringMvcContract
	 */
	@Bean
	public Contract feignContract(@Qualifier("mvcConversionService") ConversionService conversionService) {
		ConverterRegistry converterRegistry =  ((ConverterRegistry) conversionService);
		converterRegistry.addConverter(new EnumToStringConverter());
		converterRegistry.addConverter(new StringToEnumConverter());
		return new BladeSpringMvcContract(new ArrayList<>(), conversionService);
	}
}
