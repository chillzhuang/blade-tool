/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
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
package org.springblade.core.mp.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 定义常用的 sql关键字
 *
 * @author Chill
 */
public class SqlKeyword {
	/**
	 * 常规sql字符匹配关键词
	 */
	private final static String SQL_REGEX = "(?i)(?<![a-z])('|%|--|insert|delete|select|sleep|count|updatexml|group|union|drop|truncate|alter|grant|execute|exec|xp_cmdshell|call|declare|sql)(?![a-z])";

	/**
	 * 二次匹配防止双写等注入手段
	 */
	private final static Pattern PATTERN = Pattern.compile("(?:--|[\"';%]|\\binsert\\b|\\bdelete\\b|\\bselect\\b|\\bcount\\b|\\bupdatexml\\b|\\bsleep\\b|group\\s+by|\\bunion\\b|\\bdrop\\b|\\btruncate\\b|\\balter\\b|\\bgrant\\b|\\bexecute\\b|\\bxp_cmdshell\\b|\\bcall\\b|\\bdeclare\\b|\\bsql\\b)");
	/**
	 * sql注入警告语
	 */
	private final static String SQL_INJECTION_MESSAGE = "SQL keyword injection prevention processing!";

	private static final String EQUAL = "_equal";
	private static final String NOT_EQUAL = "_notequal";
	private static final String LIKE = "_like";
	private static final String LIKE_LEFT = "_likeleft";
	private static final String LIKE_RIGHT = "_likeright";
	private static final String NOT_LIKE = "_notlike";
	private static final String GE = "_ge";
	private static final String LE = "_le";
	private static final String GT = "_gt";
	private static final String LT = "_lt";
	private static final String DATE_GE = "_datege";
	private static final String DATE_GT = "_dategt";
	private static final String DATE_EQUAL = "_dateequal";
	private static final String DATE_LT = "_datelt";
	private static final String DATE_LE = "_datele";
	private static final String IS_NULL = "_null";
	private static final String NOT_NULL = "_notnull";
	private static final String IGNORE = "_ignore";

	/**
	 * 条件构造器
	 *
	 * @param query 查询字段
	 * @param qw    查询包装类
	 */
	public static void buildCondition(Map<String, Object> query, QueryWrapper<?> qw) {
		if (Func.isEmpty(query)) {
			return;
		}
		query.forEach((k, v) -> {
			if (Func.hasEmpty(k, v) || k.endsWith(IGNORE)) {
				return;
			}
			// 过滤sql注入关键词
			k = filter(k);
			if (k.endsWith(EQUAL)) {
				qw.eq(getColumn(k, EQUAL), v);
			} else if (k.endsWith(NOT_EQUAL)) {
				qw.ne(getColumn(k, NOT_EQUAL), v);
			} else if (k.endsWith(LIKE_LEFT)) {
				qw.likeLeft(getColumn(k, LIKE_LEFT), v);
			} else if (k.endsWith(LIKE_RIGHT)) {
				qw.likeRight(getColumn(k, LIKE_RIGHT), v);
			} else if (k.endsWith(NOT_LIKE)) {
				qw.notLike(getColumn(k, NOT_LIKE), v);
			} else if (k.endsWith(GE)) {
				qw.ge(getColumn(k, GE), v);
			} else if (k.endsWith(LE)) {
				qw.le(getColumn(k, LE), v);
			} else if (k.endsWith(GT)) {
				qw.gt(getColumn(k, GT), v);
			} else if (k.endsWith(LT)) {
				qw.lt(getColumn(k, LT), v);
			} else if (k.endsWith(DATE_GE)) {
				qw.ge(getColumn(k, DATE_GE), DateUtil.parse(String.valueOf(v), DateUtil.PATTERN_DATETIME));
			} else if (k.endsWith(DATE_GT)) {
				qw.gt(getColumn(k, DATE_GT), DateUtil.parse(String.valueOf(v), DateUtil.PATTERN_DATETIME));
			} else if (k.endsWith(DATE_EQUAL)) {
				qw.eq(getColumn(k, DATE_EQUAL), DateUtil.parse(String.valueOf(v), DateUtil.PATTERN_DATETIME));
			} else if (k.endsWith(DATE_LE)) {
				qw.le(getColumn(k, DATE_LE), DateUtil.parse(String.valueOf(v), DateUtil.PATTERN_DATETIME));
			} else if (k.endsWith(DATE_LT)) {
				qw.lt(getColumn(k, DATE_LT), DateUtil.parse(String.valueOf(v), DateUtil.PATTERN_DATETIME));
			} else if (k.endsWith(IS_NULL)) {
				qw.isNull(getColumn(k, IS_NULL));
			} else if (k.endsWith(NOT_NULL)) {
				qw.isNotNull(getColumn(k, NOT_NULL));
			} else {
				qw.like(getColumn(k, LIKE), v);
			}
		});
	}

	/**
	 * 获取数据库字段
	 *
	 * @param column  字段名
	 * @param keyword 关键字
	 * @return String
	 */
	private static String getColumn(String column, String keyword) {
		return StringUtil.humpToUnderline(StringUtil.removeSuffix(column, keyword));
	}

	/**
	 * 把SQL关键字替换为空字符串
	 *
	 * @param param 关键字
	 * @return string
	 */
	@SneakyThrows(SQLException.class)
	public static String filter(String param) {
		// 清除特殊字符
		String cleaned = StringUtil.cleanIdentifier(param);
		if (cleaned == null) {
			return null;
		}
		// 将校验到的sql关键词替换为空字符串
		String sql = cleaned.replaceAll(SQL_REGEX, StringPool.EMPTY);
		// 二次校验，避免双写绕过等情况出现
		if (match(sql)) {
			throw new SQLException(SQL_INJECTION_MESSAGE);
		}
		return sql;
	}

	/**
	 * 判断字符是否包含SQL关键字
	 *
	 * @param param 关键字
	 * @return boolean
	 */
	public static Boolean match(String param) {
		return Func.isNotEmpty(param) && PATTERN.matcher(param).find();
	}

}
