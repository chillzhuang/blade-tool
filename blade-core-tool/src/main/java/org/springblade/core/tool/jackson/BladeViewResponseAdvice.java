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
package org.springblade.core.tool.jackson;

import lombok.RequiredArgsConstructor;
import net.dreamlu.mica.auto.annotation.AutoIgnore;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * {@link BladeView} 响应拦截器
 * <p>
 * 拦截 Controller 方法/类上的 {@link BladeView} 注解，
 * 解析视图 Class 后包装为 {@link MappingJacksonValue} 交给 Jackson 处理。
 * </p>
 *
 * @author Chill
 */
@AutoIgnore
@ControllerAdvice
@RequiredArgsConstructor
public class BladeViewResponseAdvice implements ResponseBodyAdvice<Object> {

	private final BladeViewResolver viewResolver;

	@Nullable
	private final BladeRoleSupplier roleNameSupplier;

	@Override
	public boolean supports(@NonNull MethodParameter returnType,
							@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.hasMethodAnnotation(BladeView.class)
			|| returnType.getDeclaringClass().isAnnotationPresent(BladeView.class);
	}

	@Override
	public Object beforeBodyWrite(@Nullable Object body,
								  @NonNull MethodParameter returnType,
								  @NonNull MediaType selectedContentType,
								  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  @NonNull ServerHttpRequest request,
								  @NonNull ServerHttpResponse response) {
		if (body == null) {
			return null;
		}

		// 方法级优先于类级
		BladeView annotation = returnType.getMethodAnnotation(BladeView.class);
		if (annotation == null) {
			annotation = returnType.getDeclaringClass().getAnnotation(BladeView.class);
		}
		if (annotation == null) {
			return body;
		}

		Class<?> viewClass = resolveViewClass(annotation);

		MappingJacksonValue container;
		if (body instanceof MappingJacksonValue) {
			container = (MappingJacksonValue) body;
		} else {
			container = new MappingJacksonValue(body);
		}
		container.setSerializationView(viewClass);
		return container;
	}

	private Class<?> resolveViewClass(BladeView annotation) {
		if (annotation.value() != BladeView.Auto.class) {
			return annotation.value();
		}
		if (roleNameSupplier != null) {
			return viewResolver.resolveByRole(roleNameSupplier.get());
		}
		return viewResolver.getDefaultView();
	}

}
