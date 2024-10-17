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
package org.springblade.core.mp.base;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 基础业务接口
 *
 * @param <T>
 * @author Chill
 */
public interface BaseService<T> extends IService<T> {

	/**
	 * 逻辑删除
	 *
	 * @param ids id集合(逗号分隔)
	 * @return boolean
	 */
	boolean deleteLogic(@NotEmpty List<Long> ids);

	/**
	 * 判断字段是否重复
	 *
	 * @param field      字段
	 * @param value      字段值
	 * @param excludedId 排除的id
	 * @return boolean
	 */
	boolean isFieldDuplicate(SFunction<T, ?> field, Object value, Long excludedId);

}
