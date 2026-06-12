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
package org.springblade.core.mp.plugins;

import lombok.experimental.UtilityClass;
import org.springframework.core.NamedThreadLocal;

import java.util.function.Supplier;

/**
 * Sql 日志工具，按线程标记忽略 sql 日志打印
 *
 * @author Chill
 */
@UtilityClass
public class SqlLogUtil {

	/**
	 * sql 日志忽略线程
	 */
	private static final ThreadLocal<Boolean> SQL_LOG_IGNORE_HOLDER = new NamedThreadLocal<>("blade-sql-log-ignore") {
		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
	};

	/**
	 * 是否忽略 sql 日志
	 */
	public static Boolean isIgnore() {
		return SQL_LOG_IGNORE_HOLDER.get();
	}

	/**
	 * 忽略 sql 日志 执行函数
	 *
	 * @param supplier supplier
	 * @param <R>      泛型
	 * @return R 函数返回
	 */
	public static <R> R ignore(Supplier<R> supplier) {
		try {
			SQL_LOG_IGNORE_HOLDER.set(Boolean.TRUE);
			return supplier.get();
		} finally {
			SQL_LOG_IGNORE_HOLDER.remove();
		}
	}

	/**
	 * 忽略 sql 日志 执行函数
	 *
	 * @param runnable Runnable
	 */
	public static void ignore(Runnable runnable) {
		try {
			SQL_LOG_IGNORE_HOLDER.set(Boolean.TRUE);
			runnable.run();
		} finally {
			SQL_LOG_IGNORE_HOLDER.remove();
		}
	}

}
