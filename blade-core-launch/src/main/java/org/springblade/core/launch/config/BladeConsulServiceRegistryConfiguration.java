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
package org.springblade.core.launch.config;

import com.ecwid.consul.v1.ConsulClient;
import org.springblade.core.launch.consul.BladeConsulServiceRegistry;
import org.springblade.core.launch.server.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Consul自定义注册规则
 */
@Configuration
@ConditionalOnConsulEnabled
@AutoConfigureBefore(ConsulServiceRegistryAutoConfiguration.class)
public class BladeConsulServiceRegistryConfiguration {

	@Autowired(required = false)
	private TtlScheduler ttlScheduler;

	@Autowired
	private ServerInfo serverInfo;

	@Bean
	public ConsulServiceRegistry consulServiceRegistry(ConsulClient consulClient, ConsulDiscoveryProperties properties,
													   HeartbeatProperties heartbeatProperties) {
		return new BladeConsulServiceRegistry(consulClient, properties, ttlScheduler, heartbeatProperties, serverInfo);
	}

}
