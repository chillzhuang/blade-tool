/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE;
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
package org.springblade.core.boot.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * feign 传递Request header
 */
@Slf4j
public class BladeFeignRequestHeaderInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs != null) {
			HttpServletRequest request = attrs.getRequest();
			Enumeration<String> headerNames = request.getHeaderNames();
			if (headerNames != null) {
				while (headerNames.hasMoreElements()) {
					String name = headerNames.nextElement();
					String value = request.getHeader(name);
					/**
					 * 遍历请求头里面的属性字段，将Authorization添加到新的请求头中转发到下游服务
					 * */
					if ("Authorization".equals(name)) {
						log.debug("添加自定义请求头key:" + name + ",value:" + value);
						requestTemplate.header(name, value);
					}
				}
			} else {
				log.warn("FeignHeadConfiguration", "获取请求头失败！");
			}
		}
	}

}
