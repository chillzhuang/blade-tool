/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (smallchill@163.com).
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
package org.springblade.core.loadbalancer.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.List;

/**
 * LoadBalancer 配置
 *
 * @author Chill
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(BladeLoadBalancerProperties.PROPERTIES_PREFIX)
public class BladeLoadBalancerProperties {
	public static final String PROPERTIES_PREFIX = "blade.loadbalancer";

	/**
	 * 是否开启自定义负载均衡
	 */
	private boolean enabled = true;
	/**
	 * 灰度服务版本
	 */
	private String version;
	/**
	 * 优先的ip列表，支持通配符，例如：10.20.0.8*、10.20.0.*
	 */
	private List<String> priorIpPattern = new ArrayList<>();

}
