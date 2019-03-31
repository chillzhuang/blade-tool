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
package org.springblade.core.secure.provider;

import java.io.Serializable;

/**
 * 多终端详情接口
 *
 * @author Chill
 */
public interface IClientDetails extends Serializable {

	/**
	 * 客户端id.
	 *
	 * @return String.
	 */
	String getClientId();

	/**
	 * 客户端密钥.
	 *
	 * @return String.
	 */
	String getClientSecret();

	/**
	 * 客户端token过期时间
	 *
	 * @return Integer
	 */
	Integer getAccessTokenValidity();

	/**
	 * 客户端刷新token过期时间
	 *
	 * @return Integer
	 */
	Integer getRefreshTokenValidity();

}
