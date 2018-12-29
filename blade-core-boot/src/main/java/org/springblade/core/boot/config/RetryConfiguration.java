/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.core.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * 重试机制
 *
 * @author Chill
 */
@Slf4j
@Configuration
public class RetryConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "configServerRetryInterceptor")
	public RetryOperationsInterceptor configServerRetryInterceptor() {
		log.info(String.format(
			"configServerRetryInterceptor: Changing backOffOptions " +
				"to initial: %s, multiplier: %s, maxInterval: %s",
			1000, 1.2, 5000));
		return RetryInterceptorBuilder
			.stateless()
			.backOffOptions(1000, 1.2, 5000)
			.maxAttempts(10)
			.build();
	}

}
