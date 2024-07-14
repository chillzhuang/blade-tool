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
package org.springblade.core.mp.support;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 视图包装基类
 *
 * @author Chill
 */
public abstract class BaseEntityWrapper<E, V> {

	/**
	 * 单个实体类包装
	 *
	 * @param entity 实体类
	 * @return V
	 */
	public abstract V entityVO(E entity);

	/**
	 * 实体类集合包装
	 *
	 * @param list 猎豹
	 * @return List V
	 */
	public List<V> listVO(List<E> list) {
		return list.stream().map(this::entityVO).collect(Collectors.toList());
	}

	/**
	 * 分页实体类集合包装
	 *
	 * @param pages 分页
	 * @return Page V
	 */
	public IPage<V> pageVO(IPage<E> pages) {
		List<V> records = listVO(pages.getRecords());
		IPage<V> pageVo = new Page<>(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
