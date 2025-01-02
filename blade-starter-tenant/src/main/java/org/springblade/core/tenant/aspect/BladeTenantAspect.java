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

package org.springblade.core.tenant.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springblade.core.tenant.BladeTenantHolder;
import org.springblade.core.tenant.annotation.TenantIgnore;

/**
 * 自定义租户切面
 *
 * @author Chill
 */
@Slf4j
@Aspect
public class BladeTenantAspect {

	@Around("@annotation(tenantIgnore)")
	public Object around(ProceedingJoinPoint point, TenantIgnore tenantIgnore) throws Throwable {
		try {
			//开启忽略
			BladeTenantHolder.setIgnore(Boolean.TRUE);
			//执行方法
			return point.proceed();
		} finally {
			//关闭忽略
			BladeTenantHolder.clear();
		}
	}

}
