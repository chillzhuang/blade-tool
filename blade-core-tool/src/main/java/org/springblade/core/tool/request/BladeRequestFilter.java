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
package org.springblade.core.tool.request;

import lombok.AllArgsConstructor;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Request全局过滤
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladeRequestFilter implements Filter {

	private final RequestProperties requestProperties;
	private final XssProperties xssProperties;
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public void init(FilterConfig config) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String path = ((HttpServletRequest) request).getServletPath();
		// 跳过 Request 包装
		if (!requestProperties.getEnabled() || isRequestSkip(path)) {
			chain.doFilter(request, response);
		}
		// 默认 Request 包装
		else if (!xssProperties.getEnabled() || isXssSkip(path)) {
			BladeHttpServletRequestWrapper bladeRequest = new BladeHttpServletRequestWrapper((HttpServletRequest) request);
			chain.doFilter(bladeRequest, response);
		}
		// Xss Request 包装
		else {
			XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
			chain.doFilter(xssRequest, response);
		}
	}

	private boolean isRequestSkip(String path) {
		return requestProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
	}

	private boolean isXssSkip(String path) {
		return xssProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
	}

	@Override
	public void destroy() {

	}

}
