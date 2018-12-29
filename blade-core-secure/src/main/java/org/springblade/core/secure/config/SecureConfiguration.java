/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE;
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
package org.springblade.core.secure.config;


import lombok.AllArgsConstructor;
import org.springblade.core.secure.aspect.AuthAspect;
import org.springblade.core.secure.interceptor.SecureInterceptor;
import org.springblade.core.secure.registry.SecureRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类
 *
 * @author Chill
 */
@Order
@Configuration
@AllArgsConstructor
public class SecureConfiguration implements WebMvcConfigurer {

	private final SecureRegistry secureRegistry;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (secureRegistry.isEnable()) {
			registry.addInterceptor(new SecureInterceptor())
				.excludePathPatterns(secureRegistry.getExcludePatterns())
				.excludePathPatterns(secureRegistry.getDefaultExcludePatterns());
		}
	}

	@Bean
	public AuthAspect authAspect() {
		return new AuthAspect();
	}

}
