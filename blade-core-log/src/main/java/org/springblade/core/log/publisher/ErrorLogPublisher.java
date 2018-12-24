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

package org.springblade.core.log.publisher;

import org.springblade.core.log.constant.EventConstant;
import org.springblade.core.log.event.ErrorLogEvent;
import org.springblade.core.tool.utils.*;
import org.springblade.core.log.model.LogError;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常信息事件发送
 */
public class ErrorLogPublisher {

    public static void publishEvent(Throwable error, String requestUri) {
        HttpServletRequest request = WebUtil.getRequest();
        LogError logError = new LogError();
        logError.setRequestUri(requestUri);
        if (Func.isNotEmpty(error)) {
            logError.setStackTrace(Exceptions.getStackTraceAsString(error));
            logError.setExceptionName(error.getClass().getName());
            logError.setMessage(error.getMessage());
            StackTraceElement[] elements = error.getStackTrace();
            if (Func.isNotEmpty(elements)) {
                StackTraceElement element = elements[0];
                logError.setMethodName(element.getMethodName());
                logError.setMethodClass(element.getClassName());
                logError.setFileName(element.getFileName());
                logError.setLineNumber(element.getLineNumber());
            }
        }
        Map<String, Object> event = new HashMap<>();
        event.put(EventConstant.EVENT_LOG, logError);
        event.put(EventConstant.EVENT_REQUEST, request);
        SpringUtil.publishEvent(new ErrorLogEvent(event));
    }

}
