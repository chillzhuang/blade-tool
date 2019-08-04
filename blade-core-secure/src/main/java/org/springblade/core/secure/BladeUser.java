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
package org.springblade.core.secure;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体
 *
 * @author Chill
 */
@Data
public class BladeUser implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 客户端id
	 */
	@ApiModelProperty(hidden = true)
	private String clientId;

	/**
	 * 用户id
	 */
	@ApiModelProperty(hidden = true)
	private Integer userId;
	/**
	 * 租户ID
	 */
	@ApiModelProperty(hidden = true)
	private String tenantId;
	/**
	 * 昵称
	 */
	@ApiModelProperty(hidden = true)
	private String userName;
	/**
	 * 账号
	 */
	@ApiModelProperty(hidden = true)
	private String account;
	/**
	 * 角色id
	 */
	@ApiModelProperty(hidden = true)
	private String roleId;
	/**
	 * 角色名
	 */
	@ApiModelProperty(hidden = true)
	private String roleName;

}
