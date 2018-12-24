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
package org.springblade.core.launch.server;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.commons.util.InetUtils;

/**
 * 服务器信息
 */
public class ServerInfo {

    private ServerProperties serverProperties;
    private InetUtils inetUtils;
    private String hostName;
    private String ip;
    private Integer prot;
    private String ipWithPort;

    public ServerInfo(ServerProperties serverProperties, InetUtils inetUtils) {
        this.serverProperties = serverProperties;
        this.inetUtils = inetUtils;
        this.hostName = getHostInfo().getHostname();
        this.ip = getHostInfo().getIpAddress();
        this.prot = serverProperties.getPort();
        this.ipWithPort = String.format("%s:%d", ip, prot);
    }

    public InetUtils.HostInfo getHostInfo() {
        return inetUtils.findFirstNonLoopbackHostInfo();
    }

    public String getIP() {
        return this.ip;
    }

    public Integer getPort() {
        return this.prot;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getIPWithPort() {
        return this.ipWithPort;
    }

}
