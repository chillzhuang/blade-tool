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

package org.springblade.core.log.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.constant.EventConstant;
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.launch.server.ServerInfo;
import org.springblade.core.tool.utils.URLUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springblade.core.log.feign.ILogClient;
import org.springblade.core.log.model.LogApi;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * 异步监听日志事件
 */
@Slf4j
@Component
@AllArgsConstructor
public class ApiLogListener {

    private final ILogClient logService;
    private final ServerInfo serverInfo;
    private final BladeProperties bladeProperties;


	@Async
	@Order
	@EventListener(ApiLogEvent.class)
	public void saveApiLog(ApiLogEvent event) {
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        LogApi logApi = (LogApi) source.get(EventConstant.EVENT_LOG);
        HttpServletRequest request = (HttpServletRequest) source.get(EventConstant.EVENT_REQUEST);
        logApi.setServiceId(bladeProperties.getName());
        logApi.setServerHost(serverInfo.getHostName());
        logApi.setServerIp(serverInfo.getIPWithPort());
        logApi.setEnv(bladeProperties.getEnv());
        logApi.setRemoteIp(WebUtil.getIP(request));
        logApi.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
        logApi.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        logApi.setMethod(request.getMethod());
        logApi.setParams(WebUtil.getRequestParamString(request));
        logApi.setCreateBy(SecureUtil.getUserAccount(request));
        logApi.setCreateTime(LocalDateTime.now());
        logService.saveApiLog(logApi);
	}

}
