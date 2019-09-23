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

package org.springblade.core.log.utils;

import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.launch.server.ServerInfo;
import org.springblade.core.log.model.LogAbstract;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.UrlUtil;
import org.springblade.core.tool.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Log 相关工具
 *
 * @author Chill
 */
public class LogAbstractUtil {

	/**
	 * 向log中添加补齐request的信息
	 *
	 * @param request     请求
	 * @param logAbstract 日志基础类
	 */
	public static void addRequestInfoToLog(HttpServletRequest request, LogAbstract logAbstract) {
		logAbstract.setRemoteIp(WebUtil.getIP(request));
		logAbstract.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
		logAbstract.setRequestUri(UrlUtil.getPath(request.getRequestURI()));
		logAbstract.setMethod(request.getMethod());
		logAbstract.setParams(WebUtil.getRequestParamString(request));
		logAbstract.setCreateBy(SecureUtil.getUserAccount(request));
	}

	/**
	 * 向log中添加补齐其他的信息（eg：blade、server等）
	 *
	 * @param logAbstract     日志基础类
	 * @param bladeProperties 配置信息
	 * @param serverInfo      服务信息
	 */
	public static void addOtherInfoToLog(LogAbstract logAbstract, BladeProperties bladeProperties, ServerInfo serverInfo) {
		logAbstract.setServiceId(bladeProperties.getName());
		logAbstract.setServerHost(serverInfo.getHostName());
		logAbstract.setServerIp(serverInfo.getIpWithPort());
		logAbstract.setEnv(bladeProperties.getEnv());
		logAbstract.setCreateTime(DateUtil.now());

		//这里判断一下params为null的情况，否则blade-log服务在解析该字段的时候，可能会报出NPE
		if (logAbstract.getParams() == null) {
			logAbstract.setParams(StringPool.EMPTY);
		}
	}
}
