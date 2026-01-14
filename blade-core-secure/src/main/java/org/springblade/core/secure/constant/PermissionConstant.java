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
package org.springblade.core.secure.constant;

import org.springblade.core.tool.utils.StringUtil;

/**
 * 权限校验常量
 *
 * @author Chill
 */
public interface PermissionConstant {

	/**
	 * 获取角色所有的权限编号
	 *
	 * @param size 数量
	 * @return string
	 */
	static String permissionAllStatement(int size) {
		return StringUtil.format("select scope_path as path from blade_scope_api where id in (select scope_id from blade_role_scope where scope_category = 2 and role_id in ({}))", buildHolder(size));
	}

	/**
	 * 获取角色指定的权限编号
	 *
	 * @param size 数量
	 * @return string
	 */
	static String permissionCodeStatement(int size) {
		return StringUtil.format("select resource_code as code from blade_scope_api where resource_code = ? and id in (select scope_id from blade_role_scope where scope_category = 2 and role_id in ({}))", buildHolder(size));
	}

	/**
	 * 获取Sql占位符
	 *
	 * @param size 数量
	 * @return String
	 */
	static String buildHolder(int size) {
		StringBuilder builder = StringUtil.builder().append("?,".repeat(Math.max(0, size)));
		return StringUtil.removeSuffix(builder.toString(), ",");
	}

}
