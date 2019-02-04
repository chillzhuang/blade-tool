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
package org.springblade.core.launch.server;

import lombok.Getter;
import org.springblade.core.launch.utils.INetUtil;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 服务器信息
 *
 * @author Chill
 */
@Getter
@Configuration
public class ServerInfo implements SmartInitializingSingleton {
	private final ServerProperties serverProperties;
	private String hostName;
	private String ip;
	private Integer port;
	private String ipWithPort;

	@Autowired(required = false)
	public ServerInfo(ServerProperties serverProperties) {
		this.serverProperties = serverProperties;
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.hostName = INetUtil.getHostName();
		this.ip = INetUtil.getHostIp();
		this.port = serverProperties.getPort();
		this.ipWithPort = String.format("%s:%d", ip, port);
	}
}
