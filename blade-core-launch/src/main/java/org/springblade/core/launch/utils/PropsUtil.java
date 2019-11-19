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
package org.springblade.core.launch.utils;

import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * 配置工具类
 *
 * @author Chill
 */
public class PropsUtil {

	/**
	 * 设置配置值，已存在则跳过
	 *
	 * @param props property
	 * @param key   key
	 * @param value value
	 */
	public static void setProperty(Properties props, String key, String value) {
		if (StringUtils.isEmpty(props.getProperty(key))) {
			props.setProperty(key, value);
		}
	}

}
