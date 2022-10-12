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
package org.springblade.core.secure.props;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.launch.constant.TokenConstant;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * secure放行额外配置
 *
 * @author Chill
 */
@Slf4j
@Data
@ConfigurationProperties("blade.token")
public class BladeTokenProperties {

	/**
	 * token签名
	 */
	private String signKey = StringPool.EMPTY;

	/**
	 * 获取签名规则
	 */
	public String getSignKey() {
		if (this.signKey.length() < TokenConstant.SIGN_KEY_LENGTH) {
			log.warn("Token已启用默认签名,请前往blade.token.sign-key设置32位的key");
			return TokenConstant.SIGN_KEY;
		}
		return this.signKey;
	}

}
