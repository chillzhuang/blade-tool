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
package org.springblade.core.datascope.handler;

import lombok.RequiredArgsConstructor;
import org.springblade.core.datascope.enums.DataScopeEnum;
import org.springblade.core.datascope.model.DataScopeModel;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.PlaceholderUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 默认数据权限规则
 *
 * @author Chill
 */
@RequiredArgsConstructor
public class BladeDataScopeHandler implements DataScopeHandler {

	private final ScopeModelHandler scopeModelHandler;

	@Override
	public String sqlCondition(String mapperId, DataScopeModel dataScope, BladeUser bladeUser, String originalSql) {

		//数据权限资源编号
		String code = dataScope.getResourceCode();

		//根据mapperId从数据库中获取对应模型
		DataScopeModel dataScopeDb = scopeModelHandler.getDataScopeByMapper(mapperId, bladeUser.getRoleId());

		//mapperId配置未取到则从数据库中根据资源编号获取
		if (dataScopeDb == null && StringUtil.isNotBlank(code)) {
			dataScopeDb = scopeModelHandler.getDataScopeByCode(code);
		}

		//未从数据库找到对应配置则采用默认
		dataScope = (dataScopeDb != null) ? dataScopeDb : dataScope;

		//判断数据权限类型并组装对应Sql
		Integer scopeRule = Objects.requireNonNull(dataScope).getScopeType();
		DataScopeEnum scopeTypeEnum = DataScopeEnum.of(scopeRule);
		List<Long> ids = new ArrayList<>();
		String whereSql = "where scope.{} in ({})";
		if (DataScopeEnum.ALL == scopeTypeEnum || StringUtil.containsAny(bladeUser.getRoleName(), RoleConstant.ADMIN)) {
			return null;
		} else if (DataScopeEnum.CUSTOM == scopeTypeEnum) {
			whereSql = PlaceholderUtil.getDefaultResolver().resolveByMap(dataScope.getScopeValue(), BeanUtil.toMap(bladeUser));
		} else if (DataScopeEnum.OWN == scopeTypeEnum) {
			ids.add(bladeUser.getUserId());
		} else if (DataScopeEnum.OWN_DEPT == scopeTypeEnum) {
			ids.addAll(Func.toLongList(bladeUser.getDeptId()));
		} else if (DataScopeEnum.OWN_DEPT_CHILD == scopeTypeEnum) {
			List<Long> deptIds = Func.toLongList(bladeUser.getDeptId());
			ids.addAll(deptIds);
			deptIds.forEach(deptId -> {
				List<Long> deptIdList = scopeModelHandler.getDeptAncestors(deptId);
				ids.addAll(deptIdList);
			});
		}
		return StringUtil.format("select {} from ({}) scope " + whereSql, Func.toStr(dataScope.getScopeField(), "*"), originalSql, dataScope.getScopeColumn(), StringUtil.join(ids));
	}

}
