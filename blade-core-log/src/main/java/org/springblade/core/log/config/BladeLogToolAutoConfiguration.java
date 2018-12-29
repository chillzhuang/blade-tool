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

package org.springblade.core.log.config;

import lombok.AllArgsConstructor;
import org.springblade.core.log.aspect.ApiLogAspect;
import org.springblade.core.log.event.ApiLogListener;
import org.springblade.core.log.event.UsualLogListener;
import org.springblade.core.log.event.ErrorLogListener;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.launch.server.ServerInfo;
import org.springblade.core.log.feign.ILogClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志工具自动配置
 *
 * @author Chill
 */
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
public class BladeLogToolAutoConfiguration {

	private final ILogClient logService;
	private final ServerInfo serverInfo;
	private final BladeProperties bladeProperties;

	@Bean
	public ApiLogAspect apiLogAspect() {
		return new ApiLogAspect();
	}

	@Bean
	public BladeLogger bladeLogger() {
		return new BladeLogger();
	}

	@Bean
	public ApiLogListener apiLogListener() {
		return new ApiLogListener(logService, serverInfo, bladeProperties);
	}

	@Bean
	public ErrorLogListener errorEventListener() {
		return new ErrorLogListener(logService, serverInfo, bladeProperties);
	}

	@Bean
	public UsualLogListener bladeEventListener() {
		return new UsualLogListener(logService, serverInfo, bladeProperties);
	}

}
