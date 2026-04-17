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
package org.springblade.core.tool.config;

import org.springblade.core.tool.jackson.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * BladeView 视图序列化自动装配
 * <p>
 * 框架内置常驻加载，无需手动开启。
 * 当 Controller 方法未标注 {@link BladeView} 时不产生任何额外开销。
 * 所有 Bean 均支持 {@link ConditionalOnMissingBean}，可由用户自定义覆盖。
 * </p>
 *
 * @author Chill
 */
@AutoConfiguration(after = JacksonConfiguration.class)
public class BladeViewAutoConfiguration {

	/**
	 * 视图解析器
	 */
	@Bean
	@ConditionalOnMissingBean
	public BladeViewResolver bladeViewResolver(BladeJacksonProperties properties, ObjectProvider<BladeViewCustomizer> viewCustomizer) {
		BladeViewResolver resolver = new BladeViewResolver(properties.getView());
		viewCustomizer.orderedStream().forEach(customizer -> customizer.customize(resolver));
		return resolver;
	}

	/**
	 * 响应拦截器
	 */
	@Bean
	@ConditionalOnMissingBean
	public BladeViewResponseAdvice bladeViewResponseAdvice(BladeViewResolver viewResolver, ObjectProvider<BladeRoleSupplier> roleNameSupplier) {
		return new BladeViewResponseAdvice(viewResolver, roleNameSupplier.getIfAvailable());
	}

}
