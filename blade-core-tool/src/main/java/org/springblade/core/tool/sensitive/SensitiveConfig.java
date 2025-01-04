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
package org.springblade.core.tool.sensitive;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 敏感信息处理配置类
 *
 * @author BladeX
 */
@Builder
@Data
public class SensitiveConfig {
	// 启用的内置正则脱敏类型
	private Set<SensitiveType> sensitiveTypes;

	// 启用的内置敏感词分组
	private Set<SensitiveWord> sensitiveWords;

	// 自定义敏感词列表
	private List<String> customSensitiveWords;

	// 自定义正则表达式脱敏规则
	private Map<String, Pattern> customPatterns;

	// 自定义替换文本（可选，有默认值）
	private String replacement;

	// 是否按行处理
	private boolean processLineByLine;
}
