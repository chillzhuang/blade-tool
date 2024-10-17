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
package org.springblade.core.datascope.handler;

import org.springblade.core.datascope.model.DataScopeModel;
import org.springblade.core.secure.BladeUser;

/**
 * 数据权限规则
 *
 * @author Chill
 */
public interface DataScopeHandler {

	/**
	 * 获取过滤sql
	 *
	 * @param mapperId    数据查询类
	 * @param dataScope   数据权限类
	 * @param bladeUser   当前用户信息
	 * @param originalSql 原始Sql
	 * @return sql
	 */
	String sqlCondition(String mapperId, DataScopeModel dataScope, BladeUser bladeUser, String originalSql);

}
