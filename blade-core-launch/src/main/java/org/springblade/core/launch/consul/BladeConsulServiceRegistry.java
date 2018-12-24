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
package org.springblade.core.launch.consul;

import com.ecwid.consul.v1.ConsulClient;
import org.springblade.core.launch.server.ServerInfo;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;

/**
 * Consul自定义注册规则
 */
public class BladeConsulServiceRegistry extends ConsulServiceRegistry {

	private ServerInfo serverInfo;

	public BladeConsulServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties, TtlScheduler ttlScheduler, HeartbeatProperties heartbeatProperties, ServerInfo serverInfo) {
		super(client, properties, ttlScheduler, heartbeatProperties);
		this.serverInfo = serverInfo;
	}

	@Override
	public void register(ConsulRegistration reg) {
		reg.getService().setId(reg.getService().getName() + "-" + serverInfo.getIP() + "-" + serverInfo.getPort());
		super.register(reg);
	}

}
