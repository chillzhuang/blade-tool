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
package org.springblade.core.tool.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 *
 * @author L.cm
 */
public class CollectionUtil extends org.springframework.util.CollectionUtils {

	/**
	 * Check whether the given Array contains the given element.
	 *
	 * @param array   the Array to check
	 * @param element the element to look for
	 * @param <T>     The generic tag
	 * @return {@code true} if found, {@code false} else
	 */
	public static <T> boolean contains(@Nullable T[] array, final T element) {
		if (array == null) {
			return false;
		}
		return Arrays.stream(array).anyMatch(x -> ObjectUtil.nullSafeEquals(x, element));
	}

	/**
	 * 对象是否为数组对象
	 *
	 * @param obj 对象
	 * @return 是否为数组对象，如果为{@code null} 返回false
	 */
	public static boolean isArray(Object obj) {
		if (null == obj) {
			return false;
		}
		return obj.getClass().isArray();
	}

	/**
	 * Determine whether the given Collection is not empty:
	 * i.e. {@code null} or of zero length.
	 *
	 * @param coll the Collection to check
	 * @return boolean
	 */
	public static boolean isNotEmpty(@Nullable Collection<?> coll) {
		return !CollectionUtils.isEmpty(coll);
	}

	/**
	 * Determine whether the given Map is not empty:
	 * i.e. {@code null} or of zero length.
	 *
	 * @param map the Map to check
	 * @return boolean
	 */
	public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
		return !CollectionUtils.isEmpty(map);
	}

}
