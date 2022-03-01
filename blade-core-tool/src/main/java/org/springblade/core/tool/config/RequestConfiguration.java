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
package org.springblade.core.tool.config;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.request.BladeRequestFilter;
import org.springblade.core.tool.request.RequestProperties;
import org.springblade.core.tool.request.XssProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;

/**
 * 过滤器配置类
 *
 * @author Chill
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@EnableConfigurationProperties({RequestProperties.class, XssProperties.class})
public class RequestConfiguration {

	private final RequestProperties requestProperties;
	private final XssProperties xssProperties;

	/**
	 * 全局过滤器
	 */
	@Bean
	public FilterRegistrationBean<BladeRequestFilter> bladeFilterRegistration() {
		FilterRegistrationBean<BladeRequestFilter> registration = new FilterRegistrationBean<>();
		registration.setDispatcherTypes(DispatcherType.REQUEST);
		registration.setFilter(new BladeRequestFilter(requestProperties, xssProperties));
		registration.addUrlPatterns("/*");
		registration.setName("bladeRequestFilter");
		registration.setOrder(Ordered.LOWEST_PRECEDENCE);
		return registration;
	}
}
