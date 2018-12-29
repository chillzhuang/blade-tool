package org.springblade.core.tool.support;

import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;

/**
 * 字符串格式化
 *
 * @author Chill
 */
public class StrFormatter {

	/**
	 * 格式化字符串<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 转义{}： 	format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 转义\：		format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 *
	 * @param strPattern 字符串模板
	 * @param argArray   参数列表
	 * @return 结果
	 */
	public static String format(final String strPattern, final Object... argArray) {
		if (Func.isBlank(strPattern) || Func.isEmpty(argArray)) {
			return strPattern;
		}
		final int strPatternLength = strPattern.length();

		/**
		 * 初始化定义好的长度以获得更好的性能
		 */
		StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

		/**
		 * 记录已经处理到的位置
		 */
		int handledPosition = 0;

		/**
		 * 占位符所在位置
		 */
		int delimIndex;
		for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
			delimIndex = strPattern.indexOf(StringPool.EMPTY_JSON, handledPosition);
			/**
			 * 剩余部分无占位符
			 */
			if (delimIndex == -1) {
				/**
				 * 不带占位符的模板直接返回
				 */
				if (handledPosition == 0) {
					return strPattern;
				} else {
					sbuf.append(strPattern, handledPosition, strPatternLength);
					return sbuf.toString();
				}
			} else {
				/**
				 * 转义符
				 */
				if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == StringPool.BACK_SLASH) {
					/**
					 * 双转义符
					 */
					if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == StringPool.BACK_SLASH) {
						//转义符之前还有一个转义符，占位符依旧有效
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(Func.toStr(argArray[argIndex]));
						handledPosition = delimIndex + 2;
					} else {
						//占位符被转义
						argIndex--;
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(StringPool.LEFT_BRACE);
						handledPosition = delimIndex + 1;
					}
				} else {//正常占位符
					sbuf.append(strPattern, handledPosition, delimIndex);
					sbuf.append(Func.toStr(argArray[argIndex]));
					handledPosition = delimIndex + 2;
				}
			}
		}
		// append the characters following the last {} pair.
		//加入最后一个占位符后所有的字符
		sbuf.append(strPattern, handledPosition, strPattern.length());

		return sbuf.toString();
	}
}
