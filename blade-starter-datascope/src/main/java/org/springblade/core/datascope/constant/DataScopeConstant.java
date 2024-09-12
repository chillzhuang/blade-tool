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
package org.springblade.core.datascope.constant;

import java.util.StringJoiner;

/**
 * 数据权限常量
 *
 * @author Chill
 */
public interface DataScopeConstant {

	String DEFAULT_COLUMN = "create_dept";

	/**
	 * 获取部门数据
	 */
	String DATA_BY_DEPT = "select id from blade_dept where ancestors like concat(concat('%', ?),'%') and is_deleted = 0";

	/**
	 * 根据resourceCode获取数据权限配置
	 */
	String DATA_BY_CODE = "select resource_code, scope_column, scope_field, scope_type, scope_value from blade_scope_data where resource_code = ?";

	/**
	 * 根据mapperId获取数据权限配置
	 *
	 * @param size 数量
	 * @return String
	 */
	static String dataByMapper(int size) {
		return "select resource_code, scope_column, scope_field, scope_type, scope_value from blade_scope_data where scope_class = ? and id in (select scope_id from blade_role_scope where role_id in (" + buildHolder(size) + "))";
	}

	/**
	 * 获取Sql占位符
	 *
	 * @param size 数量
	 * @return String
	 */
	static String buildHolder(int size) {
		StringJoiner joiner = new StringJoiner(",");
		for (int i = 0; i < size; i++) {
			joiner.add("?");
		}
		return joiner.toString();
	}

}
