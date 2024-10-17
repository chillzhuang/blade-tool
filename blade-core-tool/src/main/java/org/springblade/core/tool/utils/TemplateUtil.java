/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (smallchill@163.com).
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
package org.springblade.core.tool.utils;


import org.springblade.core.tool.support.Kv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模版解析工具类
 */
public class TemplateUtil {

	/**
	 * 支持 ${} 与 #{} 两种模版占位符
	 */
	private static final Pattern pattern = Pattern.compile("\\$\\{([^{}]+)}|\\#\\{([^{}]+)}");

	/**
	 * 解析模版
	 *
	 * @param template 模版
	 * @param params   参数
	 * @return 解析后的字符串
	 */
	public static String process(String template, Kv params) {
		Matcher matcher = pattern.matcher(template);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			String key = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
			String replacement = params.getStr(key);
			if (replacement == null) {
				throw new IllegalArgumentException("参数中缺少必要的键: " + key);
			}
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 解析模版
	 *
	 * @param template 模版
	 * @param params   参数
	 * @return 解析后的字符串
	 */
	public static String safeProcess(String template, Kv params) {
		Matcher matcher = pattern.matcher(template);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			String key = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
			String replacement = params.getStr(key);
			if (replacement != null) {
				matcher.appendReplacement(sb, replacement);
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

}
