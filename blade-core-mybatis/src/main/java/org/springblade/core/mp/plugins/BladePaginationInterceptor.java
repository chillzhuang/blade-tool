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
package org.springblade.core.mp.plugins;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springblade.core.mp.intercept.QueryInterceptor;

/**
 * 拓展分页拦截器
 *
 * @author Chill
 */
@Setter
public class BladePaginationInterceptor extends PaginationInnerInterceptor {

	/**
	 * 查询拦截器
	 */
	private QueryInterceptor[] queryInterceptors;

	@SneakyThrows
	@Override
	public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		QueryInterceptorExecutor.exec(queryInterceptors, executor, ms, parameter, rowBounds, resultHandler, boundSql);
		return super.willDoQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
	}

}
