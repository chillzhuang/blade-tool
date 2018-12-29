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

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Protostuff 工具类
 *
 * @author L.cm
 */
public class ProtostuffUtil {

	/**
	 * 避免每次序列化都重新申请Buffer空间
	 */
	private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
	/**
	 * 缓存Schema
	 */
	private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

	/**
	 * 序列化方法，把指定对象序列化成字节数组
	 *
	 * @param obj obj
	 * @param <T> T
	 * @return byte[]
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] serialize(T obj) {
		Class<T> clazz = (Class<T>) obj.getClass();
		Schema<T> schema = getSchema(clazz);
		byte[] data;
		try {
			data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} finally {
			buffer.clear();
		}
		return data;
	}

	/**
	 * 反序列化方法，将字节数组反序列化成指定Class类型
	 *
	 * @param data data
	 * @param clazz clazz
	 * @param <T> T
	 * @return T
	 */
	public static <T> T deserialize(byte[] data, Class<T> clazz) {
		Schema<T> schema = getSchema(clazz);
		T obj = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(data, obj, schema);
		return obj;
	}

	/**
	 * 获取Schema
	 * @param clazz clazz
	 * @param <T> T
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	private static <T> Schema<T> getSchema(Class<T> clazz) {
		Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
		if (Objects.isNull(schema)) {
			//这个schema通过RuntimeSchema进行懒创建并缓存
			//所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
			schema = RuntimeSchema.getSchema(clazz);
			if (Objects.nonNull(schema)) {
				schemaCache.put(clazz, schema);
			}
		}
		return schema;
	}

}
