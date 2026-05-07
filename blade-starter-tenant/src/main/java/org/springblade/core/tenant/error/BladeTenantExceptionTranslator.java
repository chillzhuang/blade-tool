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
package org.springblade.core.tenant.error;

import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tenant.exception.TenantException;
import org.springblade.core.tool.api.R;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 租户异常处理器
 * <p>
 * 仅当 tenant starter 上线时随 {@code TenantConfiguration} 一起激活，
 * 把 {@link TenantException} 翻译为 HTTP 403。优先级高于全局兜底翻译器，
 * 避免被 {@code Throwable} handler 吞掉。
 *
 * @author Chill
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BladeTenantExceptionTranslator {

	@ExceptionHandler(TenantException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public R handleError(TenantException e) {
		log.error("租户异常", e);
		return R.fail(e.getResultCode(), e.getMessage());
	}

}
