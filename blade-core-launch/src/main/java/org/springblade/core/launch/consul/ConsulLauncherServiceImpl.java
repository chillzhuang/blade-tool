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
package org.springblade.core.launch.consul;

import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

/**
 * consul启动拓展
 *
 * @author Chill
 */
public class ConsulLauncherServiceImpl implements LauncherService {

	@Override
	public void launcher(SpringApplicationBuilder builder, String appName, String profile) {
		Properties props = System.getProperties();
		if (props.getProperty(ConsulConstant.CONSUL_HOST_KEY) == null) {
			props.setProperty(ConsulConstant.CONSUL_HOST_KEY, profile.equals(AppConstant.DEV_CDOE) ? ConsulConstant.CONSUL_DEV_HOST : ConsulConstant.CONSUL_PROD_HOST);
		}
		if (props.getProperty(ConsulConstant.CONSUL_PORT_KEY) == null) {
			props.setProperty(ConsulConstant.CONSUL_PORT_KEY, ConsulConstant.CONSUL_PORT);
		}
		props.setProperty(ConsulConstant.CONSUL_CONFIG_FORMAT_KEY, ConsulConstant.CONSUL_CONFIG_FORMAT);
		props.setProperty(ConsulConstant.CONSUL_WATCH_DELAY_KEY, ConsulConstant.CONSUL_WATCH_DELAY);
		props.setProperty(ConsulConstant.CONSUL_WATCH_ENABLED_KEY, ConsulConstant.CONSUL_WATCH_ENABLED);
	}

}
