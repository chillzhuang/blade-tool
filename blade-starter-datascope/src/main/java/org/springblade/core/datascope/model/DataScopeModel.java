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
package org.springblade.core.datascope.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springblade.core.datascope.constant.DataScopeConstant;
import org.springblade.core.datascope.enums.DataScopeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据权限实体类
 *
 * @author Chill
 */
@Data
@NoArgsConstructor
public class DataScopeModel implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 构造器创建
	 */
	public DataScopeModel(Boolean searched) {
		this.searched = searched;
	}

	/**
	 * 是否已查询
	 */
	private Boolean searched = Boolean.FALSE;
	/**
	 * 资源编号
	 */
	private String resourceCode;
	/**
	 * 数据权限字段
	 */
	private String scopeColumn = DataScopeConstant.DEFAULT_COLUMN;
	/**
	 * 数据权限规则
	 */
	private Integer scopeType = DataScopeEnum.ALL.getType();
	/**
	 * 可见字段
	 */
	private String scopeField;
	/**
	 * 数据权限规则值
	 */
	private String scopeValue;
}
