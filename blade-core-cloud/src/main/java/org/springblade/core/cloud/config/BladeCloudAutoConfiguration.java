/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (bladejava@qq.com).
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
package org.springblade.core.cloud.config;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import feign.Feign;
import feign.RequestInterceptor;
import org.springblade.core.cloud.feign.BladeFeignSentinel;
import org.springblade.core.cloud.header.BladeFeignRequestHeaderInterceptor;
import org.springblade.core.cloud.sentinel.BladeBlockExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;


/**
 * blade cloud 增强配置
 *
 * @author Chill
 */
@AutoConfiguration
@Import(BladeSentinelFilterConfiguration.class)
@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class BladeCloudAutoConfiguration {

	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "feign.sentinel.enabled")
	public Feign.Builder feignSentinelBuilder(RequestInterceptor requestInterceptor) {
		return BladeFeignSentinel.builder().requestInterceptor(requestInterceptor);
	}

	@Bean
	@ConditionalOnMissingBean
	public BlockExceptionHandler blockExceptionHandler() {
		return new BladeBlockExceptionHandler();
	}

	@Bean
	@ConditionalOnMissingBean
	public RequestInterceptor requestInterceptor() {
		return new BladeFeignRequestHeaderInterceptor();
	}

}
