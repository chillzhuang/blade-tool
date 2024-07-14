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
package org.springblade.core.loadbalancer.rule;

import net.dreamlu.mica.auto.annotation.AutoEnvPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

/**
 * 灰度版本 自动处理
 *
 * @author Chill
 */
@AutoEnvPostProcessor
public class GrayscaleEnvPostProcessor implements EnvironmentPostProcessor, Ordered {
	private static final String GREYSCALE_KEY = "blade.loadbalancer.version";
	private static final String METADATA_KEY = "spring.cloud.nacos.discovery.metadata.version";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String version = environment.getProperty(GREYSCALE_KEY);

		if (StringUtils.hasText(version)) {
			environment.getSystemProperties().put(METADATA_KEY, version);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
