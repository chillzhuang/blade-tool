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
package org.springblade.core.secure;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * 用户实体
 *
 * @author Chill
 */
@Data
public class BladeUser implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 * 客户端id
	 */
	@Schema(accessMode = READ_ONLY)
	private String clientId;

	/**
	 * 用户id
	 */
	@Schema(accessMode = READ_ONLY)
	private Long userId;
	/**
	 * 租户ID
	 */
	@Schema(accessMode = READ_ONLY)
	private String tenantId;
	/**
	 * 部门id
	 */
	@Schema(accessMode = READ_ONLY)
	private String deptId;
	/**
	 * 昵称
	 */
	@Schema(accessMode = READ_ONLY)
	private String userName;
	/**
	 * 账号
	 */
	@Schema(accessMode = READ_ONLY)
	private String account;
	/**
	 * 角色id
	 */
	@Schema(accessMode = READ_ONLY)
	private String roleId;
	/**
	 * 角色名
	 */
	@Schema(accessMode = READ_ONLY)
	private String roleName;

}
