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
package org.springblade.core.launch.constant;

/**
 * Consul常量.
 *
 * @author Chill
 */
public interface ConsulConstant {

	/**
	 * consul dev 地址
	 */
	String CONSUL_HOST = "http://localhost";

	/**
	 * consul端口
	 */
	String CONSUL_PORT = "8500";

	/**
	 * consul端口
	 */
	String CONSUL_CONFIG_FORMAT = "yaml";

	/**
	 * consul端口
	 */
	String CONSUL_WATCH_DELAY = "1000";

	/**
	 * consul端口
	 */
	String CONSUL_WATCH_ENABLED = "true";
}
