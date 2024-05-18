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
package org.springblade.core.tool.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 校验通用工具类
 *
 * @author Chill
 */
public class ValidationUtil {

	/**
	 * 自定义字段校验
	 *
	 * @param value   字段值
	 * @param regex   正则表达式
	 * @param message 验证消息
	 * @return String
	 */
	public static String validateField(String value, String regex, String message) {
		if (!RegexUtil.match(regex, value)) {
			return message;
		}
		return StringPool.EMPTY;
	}

	/**
	 * 如果字段值为空，则设置一个默认值
	 *
	 * @param getter        字段的getter方法
	 * @param setter        字段的setter方法
	 * @param valueSupplier 默认值提供方法
	 */
	public static <T> void setValueIfBlank(Supplier<T> getter, Consumer<T> setter, Supplier<T> valueSupplier) {
		if (ObjectUtil.isEmpty(getter.get())) {
			setter.accept(valueSupplier.get());
		}
	}
}
