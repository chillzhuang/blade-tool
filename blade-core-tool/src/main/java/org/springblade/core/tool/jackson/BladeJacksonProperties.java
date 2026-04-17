/**
 * Copyright (c) 2018-2099, DreamLu 卢春梦 (qq596392912@gmail.com).
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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * jackson 配置
 *
 * @author L.cm
 */
@Getter
@Setter
@ConfigurationProperties("blade.jackson")
public class BladeJacksonProperties {

	/**
	 * null 转为 空，字符串转成""，数组转为[]，对象转为{}，数字转为-1
	 */
	private Boolean nullToEmpty = Boolean.TRUE;
	/**
	 * 响应到前端，大数值自动写出为 String，避免精度丢失
	 */
	private Boolean bigNumToString = Boolean.TRUE;

	/**
	 * 支持 MediaType text/plain，用于和 blade-api-crypto 一起使用
	 */
	private Boolean supportTextPlain = Boolean.FALSE;

	/**
	 * 视图配置
	 */
	private View view = new View();

	/**
	 * Jackson Views 视图配置
	 *
	 * @author Chill
	 */
	@Getter
	@Setter
	public static class View {
		/**
		 * 动态模式的默认视图（角色获取不到时降级）
		 * <p>可选: summary / detail / admin / administrator</p>
		 */
		private String defaultView = "summary";
		/**
		 * 角色 → 视图映射
		 */
		private Map<String, String> roleMapping = new LinkedHashMap<>();
	}

}
