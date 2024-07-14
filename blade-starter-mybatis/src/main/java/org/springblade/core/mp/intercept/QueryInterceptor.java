/**
 * Copyright (c) 2018-2028, DreamLu 卢春梦 (qq596392912@gmail.com).
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

package org.springblade.core.mp.intercept;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.Ordered;

/**
 * 自定义 mybatis plus 查询拦截器
 *
 * @author L.cm
 */
@SuppressWarnings({"rawtypes"})
public interface QueryInterceptor extends Ordered {

	/**
	 * 拦截处理
	 *
	 * @param executor
	 * @param ms
	 * @param parameter
	 * @param rowBounds
	 * @param resultHandler
	 * @param boundSql
	 */
	void intercept(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql);

	/**
	 * 排序
	 *
	 * @return int
	 */
	@Override
	default int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
