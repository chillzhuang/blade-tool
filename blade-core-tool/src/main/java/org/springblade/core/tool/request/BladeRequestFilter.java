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
package org.springblade.core.tool.request;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.List;

/**
 * Request全局过滤
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladeRequestFilter implements Filter {

	/**
	 * 请求配置
	 */
	private final RequestProperties requestProperties;
	/**
	 * xss配置
	 */
	private final XssProperties xssProperties;
	/**
	 * 路径匹配
	 */
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * 默认拦截路径
	 */
	private final List<String> defaultBlockUrl = List.of("/**/actuator/**", "/health/**");
	/**
	 * 默认白名单
	 */
	private final List<String> defaultWhiteList = List.of("127.0.0.1", "172.30.*.*", "192.168.*.*", "10.*.*.*", "0:0:0:0:0:0:0:1");
	/**
	 * 默认提示信息
	 */
	private final static String DEFAULT_MESSAGE = "当前请求被拒绝，请联系管理员！";

	@Override
	public void init(FilterConfig config) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 获取请求
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String path = httpRequest.getServletPath();
		// 判断 拦截请求 与 白名单
		if (requestProperties.getEnabled()) {
			// 获取请求IP
			String ip = WebUtil.getIP(httpRequest);
			// 判断是否拦截请求
			if (isRequestBlock(path, ip)) {
				throw new ServletException(DEFAULT_MESSAGE);
			}
		}
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

	/**
	 * 是否白名单
	 *
	 * @param ip ip地址
	 * @return boolean
	 */
	private boolean isWhiteList(String ip) {
		List<String> whiteList = requestProperties.getWhiteList();
		String[] defaultWhiteIps = defaultWhiteList.toArray(new String[0]);
		String[] whiteIps = whiteList.toArray(new String[0]);
		return PatternMatchUtils.simpleMatch(defaultWhiteIps, ip) || PatternMatchUtils.simpleMatch(whiteIps, ip);
	}

	/**
	 * 是否黑名单
	 *
	 * @param ip ip地址
	 * @return boolean
	 */
	private boolean isBlackList(String ip) {
		List<String> blackList = requestProperties.getBlackList();
		String[] blackIps = blackList.toArray(new String[0]);
		return PatternMatchUtils.simpleMatch(blackIps, ip);
	}

	/**
	 * 是否禁用请求访问
	 *
	 * @param path 请求路径
	 * @return boolean
	 */
	private boolean isRequestBlock(String path) {
		List<String> blockUrl = requestProperties.getBlockUrl();
		return defaultBlockUrl.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path)) ||
			blockUrl.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
	}

	/**
	 * 是否拦截请求
	 *
	 * @param path 请求路径
	 * @param ip   ip地址
	 * @return boolean
	 */
	private boolean isRequestBlock(String path, String ip) {
		return (isRequestBlock(path) && !isWhiteList(ip)) || isBlackList(ip);
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
