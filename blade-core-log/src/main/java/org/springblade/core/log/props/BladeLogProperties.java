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
package org.springblade.core.log.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步配置
 *
 * @author Chill
 */
@Getter
@Setter
@ConfigurationProperties(BladeLogProperties.PREFIX)
public class BladeLogProperties {
	/**
	 * 前缀
	 */
	public static final String PREFIX = "blade.log";

	/**
	 * 是否开启 api 日志
	 */
	private Boolean api = Boolean.TRUE;
	/**
	 * 是否开启 error 日志
	 */
	private Boolean error = Boolean.TRUE;
	/**
	 * 是否开启 usual 日志
	 */
	private Boolean usual = Boolean.TRUE;
}
