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

package org.springblade.core.log.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.publisher.ApiLogPublisher;

/**
 * 操作日志使用spring event异步入库
 *
 * @author Chill
 */
@Slf4j
@Aspect
public class ApiLogAspect {

	@Around("@annotation(apiLog)")
	public Object around(ProceedingJoinPoint point, ApiLog apiLog) throws Throwable {
		//获取类名
		String className = point.getTarget().getClass().getName();
		//获取方法
		String methodName = point.getSignature().getName();
		// 发送异步日志事件
		long beginTime = System.currentTimeMillis();
		//执行方法
		Object result = point.proceed();
		//执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;
		//记录日志
		ApiLogPublisher.publishEvent(methodName, className, apiLog, time);
		return result;
	}

}
