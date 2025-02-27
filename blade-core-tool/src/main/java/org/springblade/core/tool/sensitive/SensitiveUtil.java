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

import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 敏感信息处理工具类
 * <p>
 * 支持以下功能：
 * 1. 内置敏感类型处理（手机号、邮箱、身份证等）
 * 2. 自定义正则表达式处理
 * 3. 敏感词处理
 * 4. 支持按行处理或整体处理
 * 5. 支持自定义替换符
 * 6. 支持泛型返回值
 * </p>
 *
 * @author BladeX
 */
public class SensitiveUtil {
	private static final String DEFAULT_REPLACEMENT = "******";
	private static final String LINE_SEPARATOR = System.lineSeparator();

	// 预编译的默认配置
	private static final SensitiveConfig DEFAULT_CONFIG = SensitiveConfig.builder()
		.sensitiveTypes(EnumSet.of(
			SensitiveType.MOBILE,
			SensitiveType.ID_CARD,
			SensitiveType.EMAIL,
			SensitiveType.BANK_CARD
		))
		.sensitiveWords(EnumSet.of(
			SensitiveWord.SECURE,
			SensitiveWord.AUTHENTICATION
		))
		.processLineByLine(true)
		.replacement(DEFAULT_REPLACEMENT)
		.build();

	/**
	 * 使用默认配置处理敏感信息
	 *
	 * @param content 待处理内容
	 * @return 处理后的结果
	 */
	public static String process(String content) {
		return process(content, DEFAULT_CONFIG);
	}

	/**
	 * 使用自定义配置处理敏感信息
	 *
	 * @param content 待处理内容
	 * @param config  自定义配置
	 * @return 处理后的结果
	 */
	public static String process(String content, SensitiveConfig config) {
		if (StringUtil.isBlank(content)) {
			return content;
		}

		String processedContent = content;
		String placeholder = StringUtil.isBlank(config.getReplacement()) ?
			DEFAULT_REPLACEMENT : config.getReplacement();

		// 1. 处理内置敏感类型
		if (!CollectionUtil.isEmpty(config.getSensitiveTypes())) {
			processedContent = processRegexPatterns(processedContent, config.getSensitiveTypes());
		}

		// 2. 处理自定义正则
		if (!CollectionUtil.isEmpty(config.getCustomPatterns())) {
			processedContent = processCustomPatterns(processedContent,
				config.getCustomPatterns(),
				placeholder);
		}

		// 3. 处理敏感词
		if (!CollectionUtil.isEmpty(config.getSensitiveWords())) {
			List<String> words = getSensitiveWords(config.getSensitiveWords());
			processedContent = processSensitiveWords(processedContent,
				words,
				placeholder,
				config.isProcessLineByLine());
		}

		return processedContent;
	}

	/**
	 * 处理单个敏感类型
	 *
	 * @param content 待处理内容
	 * @param type    敏感类型
	 * @return 处理后的结果
	 */
	public static String process(String content, SensitiveType type) {
		if (StringUtil.isBlank(content) || type == null) {
			return content;
		}
		return processRegexPatterns(content, Collections.singleton(type));
	}

	/**
	 * 处理多个敏感类型
	 *
	 * @param content 待处理内容
	 * @param types   敏感类型集合
	 * @return 处理后的结果
	 */
	public static String process(String content, Set<SensitiveType> types) {
		if (StringUtil.isBlank(content) || CollectionUtil.isEmpty(types)) {
			return content;
		}
		return processRegexPatterns(content, types);
	}

	/**
	 * 使用自定义正则处理（使用默认替换符）
	 *
	 * @param content 待处理内容
	 * @param regex   正则表达式
	 * @return 处理后的结果
	 */
	public static String processWithRegex(String content, String regex) {
		return processWithRegex(content, regex, DEFAULT_REPLACEMENT);
	}

	/**
	 * 使用自定义正则处理（使用自定义替换符）
	 *
	 * @param content     待处理内容
	 * @param regex       正则表达式
	 * @param replacement 替换内容
	 * @return 处理后的结果
	 */
	public static String processWithRegex(String content, String regex, String replacement) {
		if (StringUtil.isBlank(content) || StringUtil.isBlank(regex)) {
			return content;
		}
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(content).replaceAll(replacement);
	}

	/**
	 * 处理敏感词（使用默认配置）
	 *
	 * @param content 待处理内容
	 * @param words   敏感词列表
	 * @return 处理后的结果
	 */
	public static String processWithWords(String content, List<String> words) {
		return processWithWords(content, words, DEFAULT_REPLACEMENT, true);
	}

	/**
	 * 处理敏感词（使用完整参数）
	 *
	 * @param content           待处理内容
	 * @param words             敏感词列表
	 * @param placeholder       替换符
	 * @param processLineByLine 是否按行处理
	 * @return 处理后的结果
	 */
	public static String processWithWords(String content,
										  List<String> words,
										  String placeholder,
										  boolean processLineByLine) {
		if (StringUtil.isBlank(content) || CollectionUtil.isEmpty(words)) {
			return content;
		}
		return processSensitiveWords(content, words, placeholder, processLineByLine);
	}

	/**
	 * 处理正则表达式
	 */
	private static String processRegexPatterns(String content, Set<SensitiveType> types) {
		String result = content;
		for (SensitiveType type : types) {
			result = type.replaceAll(result);
		}
		return result;
	}

	/**
	 * 处理自定义正则表达式
	 */
	private static String processCustomPatterns(String content,
												Map<String, Pattern> patterns,
												String placeholder) {
		String result = content;
		for (Pattern pattern : patterns.values()) {
			result = pattern.matcher(result).replaceAll(placeholder);
		}
		return result;
	}

	/**
	 * 获取敏感词列表
	 */
	private static List<String> getSensitiveWords(Set<SensitiveWord> groups) {
		List<String> words = new ArrayList<>();
		for (SensitiveWord group : groups) {
			words.addAll(group.getWords());
		}
		return words;
	}

	/**
	 * 处理敏感词
	 */
	private static String processSensitiveWords(String content,
												List<String> words,
												String placeholder,
												boolean processLineByLine) {
		return processLineByLine ?
			maskSensitiveLines(content, words, placeholder) :
			maskSensitiveContent(content, words, placeholder);
	}

	/**
	 * 按行处理敏感词
	 */
	private static String maskSensitiveLines(String content,
											 List<String> words,
											 String placeholder) {
		String[] lines = content.split(LINE_SEPARATOR);
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			boolean containsSensitive = words.stream()
				.anyMatch(word -> line.toLowerCase().contains(word.toLowerCase()));

			result.append(containsSensitive ? placeholder : line);

			if (i < lines.length - 1) {
				result.append(LINE_SEPARATOR);
			}
		}

		return result.toString();
	}

	/**
	 * 处理整体内容中的敏感词
	 */
	private static String maskSensitiveContent(String content,
											   List<String> words,
											   String placeholder) {
		boolean containsSensitive = words.stream()
			.anyMatch(word -> content.toLowerCase().contains(word.toLowerCase()));

		return containsSensitive ? placeholder : content;
	}
}
