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
package org.springblade.core.boot.tenant;

import org.springblade.core.secure.utils.SecureUtil;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * TenantId 工具
 *
 * @author L.cm
 */
public class TenantIdUtil {
	private static final ThreadLocal<String> tl = new ThreadLocal<>();

	/**
	 * 获取租户id
	 *
	 * @return 租户id
	 */
	public static String get() {
		String tenantId = tl.get();
		if (tenantId != null) {
			return tenantId;
		}
		return SecureUtil.getTenantId();
	}

	/**
	 * 使用租户 id 执行函数
	 *
	 * @param tenantId tenantId
	 * @param supplier supplier
	 * @param <R>      泛型
	 * @return R 函数返回
	 */
	public static <R> R use(String tenantId, Supplier<R> supplier) {
		Assert.hasText(tenantId, "参数 tenantId 为空");
		tl.set(tenantId);
		try {
			return supplier.get();
		} finally {
			tl.remove();
		}
	}

}
