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
package org.springblade.core.datascope.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型
 *
 * @author lengleng, Chill
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {
	/**
	 * 全部数据
	 */
	ALL(1, "全部"),

	/**
	 * 本人可见
	 */
	OWN(2, "本人可见"),

	/**
	 * 所在机构可见
	 */
	OWN_DEPT(3, "所在机构可见"),

	/**
	 * 所在机构及子级可见
	 */
	OWN_DEPT_CHILD(4, "所在机构及子级可见"),

	/**
	 * 自定义
	 */
	CUSTOM(5, "自定义");

	/**
	 * 类型
	 */
	private final int type;
	/**
	 * 描述
	 */
	private final String description;

	public static DataScopeEnum of(Integer dataScopeType) {
		if (dataScopeType == null) {
			return null;
		}
		DataScopeEnum[] values = DataScopeEnum.values();
		for (DataScopeEnum scopeTypeEnum : values) {
			if (scopeTypeEnum.type == dataScopeType) {
				return scopeTypeEnum;
			}
		}
		return null;
	}
}
