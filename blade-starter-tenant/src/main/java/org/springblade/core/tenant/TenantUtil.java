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
package org.springblade.core.tenant;

import lombok.experimental.UtilityClass;
import org.springblade.core.secure.utils.AuthUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * TenantId 工具
 *
 * @author L.cm，BladeX
 */
@UtilityClass
public class TenantUtil {
	/**
	 * 租户ID线程
	 */
	private static final ThreadLocal<String> TENANT_ID_HOLDER = new NamedThreadLocal<>("blade-tenant-id") {
		@Override
		protected String initialValue() {
			return null;
		}
	};

	/**
	 * 租户状态线程
	 */
	private static final ThreadLocal<Boolean> TENANT_IGNORE_HOLDER = new NamedThreadLocal<>("blade-tenant-ignore") {
		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
	};

	/**
	 * 获取租户id
	 *
	 * @return 租户id
	 */
	public static String getTenantId() {
		String tenantId = TENANT_ID_HOLDER.get();
		if (tenantId != null) {
			return tenantId;
		}
		return AuthUtil.getTenantId();
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
		try {
			TENANT_ID_HOLDER.set(tenantId);
			return supplier.get();
		} finally {
			TENANT_ID_HOLDER.remove();
		}
	}

	/**
	 * 使用租户 id 执行函数
	 *
	 * @param tenantId tenantId
	 * @param runnable Runnable
	 */
	public static void use(String tenantId, Runnable runnable) {
		Assert.hasText(tenantId, "参数 tenantId 为空");
		try {
			TENANT_ID_HOLDER.set(tenantId);
			runnable.run();
		} finally {
			TENANT_ID_HOLDER.remove();
		}
	}

	/**
	 * 是否忽略租户
	 */
	public static Boolean isIgnore() {
		return TENANT_IGNORE_HOLDER.get();
	}

	/**
	 * 忽略租户 执行函数
	 *
	 * @param supplier supplier
	 * @param <R>      泛型
	 * @return R 函数返回
	 */
	public static <R> R ignore(Supplier<R> supplier) {
		try {
			TENANT_IGNORE_HOLDER.set(Boolean.TRUE);
			return supplier.get();
		} finally {
			TENANT_IGNORE_HOLDER.remove();
		}
	}

	/**
	 * 忽略租户 执行函数
	 *
	 * @param runnable Runnable
	 */
	public static void ignore(Runnable runnable) {
		try {
			TENANT_IGNORE_HOLDER.set(Boolean.TRUE);
			runnable.run();
		} finally {
			TENANT_IGNORE_HOLDER.remove();
		}
	}

}
