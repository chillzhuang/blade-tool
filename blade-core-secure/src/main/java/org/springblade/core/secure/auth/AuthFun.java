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
package org.springblade.core.secure.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springblade.core.launch.constant.TokenConstant;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.handler.IPermissionHandler;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.*;

import java.util.Objects;

/**
 * 权限判断
 *
 * @author Chill
 */
public class AuthFun {

	/**
	 * 权限校验处理器
	 */
	private static IPermissionHandler permissionHandler;

	private static IPermissionHandler getPermissionHandler() {
		if (permissionHandler == null) {
			permissionHandler = SpringUtil.getBean(IPermissionHandler.class);
		}
		return permissionHandler;
	}

	/**
	 * 判断角色是否具有接口权限（全量校验）
	 *
	 * @return {boolean}
	 */
	public boolean permissionAll() {
		return getPermissionHandler().permissionAll();
	}

	/**
	 * 判断角色是否具有接口权限
	 *
	 * @param permission 权限编号
	 * @return {boolean}
	 */
	public boolean hasPermission(String permission) {
		return getPermissionHandler().hasPermission(permission);
	}

	/**
	 * 放行所有请求
	 *
	 * @return {boolean}
	 */
	public boolean permitAll() {
		return true;
	}

	/**
	 * 只有超管角色才可访问
	 *
	 * @return {boolean}
	 */
	public boolean denyAll() {
		return hasRole(RoleConstant.ADMIN);
	}

	/**
	 * 是否已授权
	 *
	 * @return {boolean}
	 */
	public boolean hasAuth() {
		return AuthUtil.hasAuth();
	}

	/**
	 * 是否有时间授权
	 *
	 * @param start 开始时间
	 * @param end   结束时间
	 * @return {boolean}
	 */
	public boolean hasTimeAuth(Integer start, Integer end) {
		Integer hour = DateUtil.hour();
		return hour >= start && hour <= end;
	}

	/**
	 * 判断是否有该角色权限
	 *
	 * @param role 单角色
	 * @return {boolean}
	 */
	public boolean hasRole(String role) {
		return hasAnyRole(role);
	}

	/**
	 * 判断是否具有所有角色权限
	 *
	 * @param role 角色集合
	 * @return {boolean}
	 */
	public boolean hasAllRole(String... role) {
		for (String r : role) {
			if (!hasRole(r)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否有该角色权限
	 *
	 * @param role 角色集合
	 * @return {boolean}
	 */
	public boolean hasAnyRole(String... role) {
		BladeUser user = AuthUtil.getUser();
		if (user == null) {
			return false;
		}
		String userRole = user.getRoleName();
		if (StringUtil.isBlank(userRole)) {
			return false;
		}
		String[] roles = Func.toStrArray(userRole);
		for (String r : role) {
			if (CollectionUtil.contains(roles, r)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断请求是否为加密token
	 *
	 * @return {boolean}
	 */
	public boolean hasCrypto() {
		HttpServletRequest request = WebUtil.getRequest();
		String auth = Objects.requireNonNull(request).getHeader(TokenConstant.HEADER);
		return SecureUtil.isCrypto(
			StringUtil.isNotBlank(auth) ? auth : request.getParameter(TokenConstant.HEADER)
		);
	}

	/**
	 * 判断是否有该请求头
	 *
	 * @param header 请求头
	 * @return {boolean}
	 */
	public boolean hasHeader(String header) {
		HttpServletRequest request = WebUtil.getRequest();
		String value = Objects.requireNonNull(request).getHeader(header);
		return StringUtil.isNotBlank(value);
	}

	/**
	 * 判断是否有该请求头
	 *
	 * @param header 请求头
	 * @param key    请求值
	 * @return {boolean}
	 */
	public boolean hasHeader(String header, String key) {
		HttpServletRequest request = WebUtil.getRequest();
		String value = Objects.requireNonNull(request).getHeader(header);
		return StringUtil.equals(value, key);
	}

}
