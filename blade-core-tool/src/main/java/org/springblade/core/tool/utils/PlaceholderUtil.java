package org.springblade.core.tool.utils;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 占位符解析器
 *
 * @author meilin.huang, chill
 */
public class PlaceholderUtil {
	/**
	 * 默认前缀占位符
	 */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/**
	 * 默认后缀占位符
	 */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	/**
	 * 默认单例解析器
	 */
	private static final PlaceholderUtil DEFAULT_RESOLVER = new PlaceholderUtil();

	/**
	 * 占位符前缀
	 */
	private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	/**
	 * 占位符后缀
	 */
	private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;


	private PlaceholderUtil() {
	}

	private PlaceholderUtil(String placeholderPrefix, String placeholderSuffix) {
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * 获取默认的占位符解析器，即占位符前缀为"${", 后缀为"}"
	 *
	 * @return PlaceholderUtil
	 */
	public static PlaceholderUtil getDefaultResolver() {
		return DEFAULT_RESOLVER;
	}

	public static PlaceholderUtil getResolver(String placeholderPrefix, String placeholderSuffix) {
		return new PlaceholderUtil(placeholderPrefix, placeholderSuffix);
	}

	/**
	 * 解析带有指定占位符的模板字符串，默认占位符为前缀：${  后缀：}
	 *
	 * @param content 要解析的带有占位符的模板字符串
	 * @param values  按照模板占位符索引位置设置对应的值
	 * @return {String}
	 */
	public String resolve(String content, String... values) {
		int start = content.indexOf(this.placeholderPrefix);
		if (start == -1) {
			return content;
		}
		//值索引
		int valueIndex = 0;
		StringBuilder result = new StringBuilder(content);
		while (start != -1) {
			int end = result.indexOf(this.placeholderSuffix);
			String replaceContent = values[valueIndex++];
			result.replace(start, end + this.placeholderSuffix.length(), replaceContent);
			start = result.indexOf(this.placeholderPrefix, start + replaceContent.length());
		}
		return result.toString();
	}

	/**
	 * 解析带有指定占位符的模板字符串，默认占位符为前缀：${  后缀：}
	 *
	 * @param content 要解析的带有占位符的模板字符串
	 * @param values  按照模板占位符索引位置设置对应的值
	 * @return {String}
	 */
	public String resolve(String content, Object[] values) {
		return resolve(content, Stream.of(values).map(String::valueOf).toArray(String[]::new));
	}

	/**
	 * 根据替换规则来替换指定模板中的占位符值
	 *
	 * @param content 要解析的字符串
	 * @param rule    解析规则回调
	 * @return {String}
	 */
	public String resolveByRule(String content, Function<String, String> rule) {
		int start = content.indexOf(this.placeholderPrefix);
		if (start == -1) {
			return content;
		}
		StringBuilder result = new StringBuilder(content);
		while (start != -1) {
			int end = result.indexOf(this.placeholderSuffix, start + 1);
			//获取占位符属性值，如${id}, 即获取id
			String placeholder = result.substring(start + this.placeholderPrefix.length(), end);
			//替换整个占位符内容，即将${id}值替换为替换规则回调中的内容
			String replaceContent = placeholder.trim().isEmpty() ? "" : rule.apply(placeholder);
			result.replace(start, end + this.placeholderSuffix.length(), replaceContent);
			start = result.indexOf(this.placeholderPrefix, start + replaceContent.length());
		}
		return result.toString();
	}

	/**
	 * 替换模板中占位符内容，占位符的内容即为map key对应的值，key为占位符中的内容
	 *
	 * @param content  模板内容
	 * @param valueMap 值映射
	 * @return 替换完成后的字符串
	 */
	public String resolveByMap(String content, final Map<String, Object> valueMap) {
		return resolveByRule(content, placeholderValue -> String.valueOf(valueMap.get(placeholderValue)));
	}

	/**
	 * 根据properties文件替换占位符内容
	 *
	 * @param content    模板内容
	 * @param properties 配置
	 * @return {String}
	 */
	public String resolveByProperties(String content, final Properties properties) {
		return resolveByRule(content, properties::getProperty);
	}

}
