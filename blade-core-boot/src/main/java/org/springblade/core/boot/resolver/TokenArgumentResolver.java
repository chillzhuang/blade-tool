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
package org.springblade.core.boot.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.SecureUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Token转化BladeUser
 *
 * @author Chill
 */
@Slf4j
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * 入参筛选
	 *
	 * @param methodParameter 参数集合
	 * @return 格式化后的参数
	 */
	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterType().equals(BladeUser.class);
	}

	/**
	 * 出参设置
	 *
	 * @param methodParameter       入参集合
	 * @param modelAndViewContainer model 和 view
	 * @param nativeWebRequest      web相关
	 * @param webDataBinderFactory  入参解析
	 * @return 包装对象
	 */
	@Override
	public Object resolveArgument(MethodParameter methodParameter,
								  ModelAndViewContainer modelAndViewContainer,
								  NativeWebRequest nativeWebRequest,
								  WebDataBinderFactory webDataBinderFactory) {
		return SecureUtil.getUser();
	}

}
