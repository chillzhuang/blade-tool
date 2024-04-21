/**
 * Copyright (c) 2018-2028, DreamLu 卢春梦 (qq596392912@gmail.com).
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

package org.springblade.core.launch.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 请求日志级别，来源 okHttp
 *
 * @author L.cm
 */
@Getter
@RequiredArgsConstructor
public enum BladeLogLevel {
	/**
	 * No logs.
	 */
	NONE(0),

	/**
	 * Logs request and response lines.
	 *
	 * <p>Example:
	 * <pre>{@code
	 * --> POST /greeting http/1.1 (3-byte body)
	 *
	 * <-- 200 OK (22ms, 6-byte body)
	 * }</pre>
	 */
	BASIC(1),

	/**
	 * Logs request and response lines and their respective headers.
	 *
	 * <p>Example:
	 * <pre>{@code
	 * --> POST /greeting http/1.1
	 * Host: example.com
	 * Content-Type: plain/text
	 * Content-Length: 3
	 * --> END POST
	 *
	 * <-- 200 OK (22ms)
	 * Content-Type: plain/text
	 * Content-Length: 6
	 * <-- END HTTP
	 * }</pre>
	 */
	HEADERS(2),

	/**
	 * Logs request and response lines and their respective headers and bodies (if present).
	 *
	 * <p>Example:
	 * <pre>{@code
	 * --> POST /greeting http/1.1
	 * Host: example.com
	 * Content-Type: plain/text
	 * Content-Length: 3
	 *
	 * Hi?
	 * --> END POST
	 *
	 * <-- 200 OK (22ms)
	 * Content-Type: plain/text
	 * Content-Length: 6
	 *
	 * Hello!
	 * <-- END HTTP
	 * }</pre>
	 */
	BODY(3);

	/**
	 * 请求日志配置前缀
	 */
	public static final String REQ_LOG_PROPS_PREFIX = "blade.log.request";
	/**
	 * 控制台日志是否启用
	 */
	public static final String CONSOLE_LOG_ENABLED_PROP = "blade.log.console.enabled";

	/**
	 * 级别
	 */
	private final int level;

	/**
	 * 当前版本 小于和等于 比较的版本
	 *
	 * @param level LogLevel
	 * @return 是否小于和等于
	 */
	public boolean lte(BladeLogLevel level) {
		return this.level <= level.level;
	}

}
