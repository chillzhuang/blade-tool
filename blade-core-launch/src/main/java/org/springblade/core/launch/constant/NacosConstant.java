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
package org.springblade.core.launch.constant;

/**
 * Nacos常量.
 *
 * @author Chill
 */
public interface NacosConstant {

	/**
	 * nacos 地址
	 */
	String NACOS_ADDR = "127.0.0.1:8848";

	/**
	 * nacos 配置前缀
	 */
	String NACOS_CONFIG_PREFIX = "blade";

	/**
	 * nacos 组配置后缀
	 */
	String NACOS_GROUP_SUFFIX = "-group";

	/**
	 * nacos 配置文件类型
	 */
	String NACOS_CONFIG_FORMAT = "yaml";
}
