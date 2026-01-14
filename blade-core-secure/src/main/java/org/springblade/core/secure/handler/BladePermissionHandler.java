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
package org.springblade.core.secure.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springblade.core.secure.constant.PermissionConstant.permissionAllStatement;
import static org.springblade.core.secure.constant.PermissionConstant.permissionCodeStatement;
import static org.springblade.core.cache.utils.CacheUtil.SYS_CACHE;

/**
 * 默认授权校验类
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladePermissionHandler implements IPermissionHandler {

	private static final String SCOPE_CACHE_ROLE = "apiScope:role:";

	private static final String SCOPE_CACHE_CODE = "apiScope:code:";

	private final JdbcTemplate jdbcTemplate;

	@Override
	public boolean permissionAll() {
		HttpServletRequest request = WebUtil.getRequest();
		BladeUser user = AuthUtil.getUser();
		if (request == null || user == null) {
			return false;
		}
		String uri = request.getRequestURI();
		List<String> paths = permissionPath(user.getRoleId());
		if (paths.isEmpty()) {
			return false;
		}
		return paths.stream().anyMatch(uri::contains);
	}

	@Override
	public boolean hasPermission(String permission) {
		HttpServletRequest request = WebUtil.getRequest();
		BladeUser user = AuthUtil.getUser();
		if (request == null || user == null) {
			return false;
		}
		List<String> codes = permissionCode(permission, user.getRoleId());
		return !codes.isEmpty();
	}

	/**
	 * 获取接口权限地址
	 *
	 * @param roleId 角色id
	 * @return permissions
	 */
	private List<String> permissionPath(String roleId) {
		List<String> permissions = CacheUtil.get(SYS_CACHE, SCOPE_CACHE_ROLE, roleId, List.class);
		if (permissions == null) {
			List<Long> roleIds = Func.toLongList(roleId);
			permissions = jdbcTemplate.queryForList(permissionAllStatement(roleIds.size()), String.class, roleIds.toArray());
			CacheUtil.put(SYS_CACHE, SCOPE_CACHE_ROLE, roleId, permissions);
		}
		return permissions;
	}

	/**
	 * 获取接口权限信息
	 *
	 * @param permission 权限编号
	 * @param roleId     角色id
	 * @return permissions
	 */
	private List<String> permissionCode(String permission, String roleId) {
		String cacheKey = permission + StringPool.COLON + roleId;
		List<String> permissions = CacheUtil.get(SYS_CACHE, SCOPE_CACHE_CODE, cacheKey, List.class);
		if (permissions == null) {
			List<Object> args = new ArrayList<>(Collections.singletonList(permission));
			List<Long> roleIds = Func.toLongList(roleId);
			args.addAll(roleIds);
			permissions = jdbcTemplate.queryForList(permissionCodeStatement(roleIds.size()), String.class, args.toArray());
			CacheUtil.put(SYS_CACHE, SCOPE_CACHE_CODE, cacheKey, permissions);
		}
		return permissions;
	}

}
