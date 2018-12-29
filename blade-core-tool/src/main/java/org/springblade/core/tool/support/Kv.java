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
package org.springblade.core.tool.support;

import org.springblade.core.tool.utils.Func;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * 链式map
 *
 * @author Chill
 */
public class Kv extends LinkedCaseInsensitiveMap<Object> {


	private Kv() {

	}

	/**
	 * 创建Kv
	 *
	 * @return Kv
	 */
	public static Kv init() {
		return new Kv();
	}

	public static HashMap newMap() {
		return new HashMap(16);
	}

	/**
	 * 设置列
	 *
	 * @param attr  属性
	 * @param value 值
	 * @return 本身
	 */
	public Kv set(String attr, Object value) {
		this.put(attr, value);
		return this;
	}

	/**
	 * 设置列，当键或值为null时忽略
	 *
	 * @param attr  属性
	 * @param value 值
	 * @return 本身
	 */
	public Kv setIgnoreNull(String attr, Object value) {
		if (null != attr && null != value) {
			set(attr, value);
		}
		return this;
	}

	public Object getObj(String key) {
		return super.get(key);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param <T>          值类型
	 * @param attr         字段名
	 * @param defaultValue 默认值
	 * @return 字段值
	 */
	public <T> T get(String attr, T defaultValue) {
		final Object result = get(attr);
		return (T) (result != null ? result : defaultValue);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public String getStr(String attr) {
		return Func.toStr(get(attr), null);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Integer getInt(String attr) {
		return Func.toInt(get(attr), -1);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Long getLong(String attr) {
		return Func.toLong(get(attr), -1L);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Float getFloat(String attr) {
		return Func.toFloat(get(attr), null);
	}

	public Double getDouble(String attr) {
		return Func.toDouble(get(attr), null);
	}


	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Boolean getBool(String attr) {
		return Func.toBoolean(get(attr), null);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public byte[] getBytes(String attr) {
		return get(attr, null);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Date getDate(String attr) {
		return get(attr, null);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Time getTime(String attr) {
		return get(attr, null);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Timestamp getTimestamp(String attr) {
		return get(attr, null);
	}

	/**
	 * 获得特定类型值
	 *
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Number getNumber(String attr) {
		return get(attr, null);
	}

	@Override
	public Kv clone() {
		return (Kv) super.clone();
	}

}
