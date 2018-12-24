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
package org.springblade.core.log.feign;

import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.log.model.LogApi;
import org.springblade.core.log.model.LogBlade;
import org.springblade.core.log.model.LogError;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign接口类
 */
@FeignClient(
        value = AppConstant.APPLICATION_LOG_NAME
)
public interface ILogClient {

    String API_PREFIX = "/log";

    /**
     * 保存错误日志
     *
     * @param log
     * @return
     */
    @PostMapping(API_PREFIX + "/saveBladeLog")
	R<Boolean> saveBladeLog(@RequestBody LogBlade log);

    /**
     * 保存操作日志
     *
     * @param log
     * @return
     */
    @PostMapping(API_PREFIX + "/saveApiLog")
	R<Boolean> saveApiLog(@RequestBody LogApi log);

    /**
     * 保存错误日志
     *
     * @param log
     * @return
     */
    @PostMapping(API_PREFIX + "/saveErrorLog")
	R<Boolean> saveErrorLog(@RequestBody LogError log);

}
