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
package org.springblade.core.loadbalancer.config;

import net.dreamlu.mica.auto.annotation.AutoEnvPostProcessor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.DefaultPropertiesPropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

import java.util.Map;

/**
 * Feign 分页模型 JSON 支持的装配兼容处理
 *
 * <p>
 * Feign 为 Spring Data 的分页与排序模型提供的两个 JSON 扩展仅有 Jackson 3 实现，
 * 而其装配条件本应校验的 Jackson 模块基类在 Jackson 3 中已改名，上游条件退化为恒成立，
 * 于是只要分页与排序模型在类路径上就会装配，Jackson 3 缺席时中断容器启动。
 * </p>
 *
 * <p>
 * 故仅在 Jackson 3 缺席时给出关闭的默认值，该默认值优先级最低，下游可显式开启；
 * Jackson 3 在场时不再干预，交由上游条件判定。
 * </p>
 *
 * @author Chill
 */
@AutoEnvPostProcessor
public class FeignJacksonEnvPostProcessor implements EnvironmentPostProcessor, Ordered {

	/**
	 * Feign 分页模型 JSON 支持开关
	 */
	private static final String FEIGN_JACKSON_KEY = "spring.cloud.openfeign.autoconfiguration.jackson.enabled";

	/**
	 * Jackson 3 模块基类，上述两个扩展的直接父类
	 */
	private static final String JACKSON_MODULE_CLASS = "tools.jackson.databind.JacksonModule";

	@Override
	public void postProcessEnvironment(@NonNull ConfigurableEnvironment environment, SpringApplication application) {
		if (ClassUtils.isPresent(JACKSON_MODULE_CLASS, application.getClassLoader())) {
			return;
		}
		Map<String, Object> defaults = Map.of(FEIGN_JACKSON_KEY, Boolean.FALSE.toString());
		DefaultPropertiesPropertySource.addOrMerge(defaults, environment.getPropertySources());
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
