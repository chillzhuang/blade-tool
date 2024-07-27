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
package org.springblade.core.tool.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.*;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

/**
 * Jackson工具类
 *
 * @author Chill
 */
@Slf4j
public class JsonUtil {

	/**
	 * 将对象序列化成json字符串
	 *
	 * @param value javaBean
	 * @param <T>   T 泛型标记
	 * @return jsonString json字符串
	 */
	public static <T> String toJson(T value) {
		try {
			return getInstance().writeValueAsString(value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将对象序列化成 json byte 数组
	 *
	 * @param object javaBean
	 * @return jsonString json字符串
	 */
	public static byte[] toJsonAsBytes(Object object) {
		try {
			return getInstance().writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param content   content
	 * @param valueType class
	 * @param <T>       T 泛型标记
	 * @return Bean
	 */
	public static <T> T parse(String content, Class<T> valueType) {
		try {
			return getInstance().readValue(content, valueType);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param content       content
	 * @param typeReference 泛型类型
	 * @param <T>           T 泛型标记
	 * @return Bean
	 */
	public static <T> T parse(String content, TypeReference<T> typeReference) {
		try {
			return getInstance().readValue(content, typeReference);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json byte 数组反序列化成对象
	 *
	 * @param bytes     json bytes
	 * @param valueType class
	 * @param <T>       T 泛型标记
	 * @return Bean
	 */
	public static <T> T parse(byte[] bytes, Class<T> valueType) {
		try {
			return getInstance().readValue(bytes, valueType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}


	/**
	 * 将json反序列化成对象
	 *
	 * @param bytes         bytes
	 * @param typeReference 泛型类型
	 * @param <T>           T 泛型标记
	 * @return Bean
	 */
	public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
		try {
			return getInstance().readValue(bytes, typeReference);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param in        InputStream
	 * @param valueType class
	 * @param <T>       T 泛型标记
	 * @return Bean
	 */
	public static <T> T parse(InputStream in, Class<T> valueType) {
		try {
			return getInstance().readValue(in, valueType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param in            InputStream
	 * @param typeReference 泛型类型
	 * @param <T>           T 泛型标记
	 * @return Bean
	 */
	public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
		try {
			return getInstance().readValue(in, typeReference);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成List对象
	 * @param content content
	 * @param valueTypeRef class
	 * @param <T> T 泛型标记
	 * @return List
	 */
	public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
		try {

			if (!StringUtil.startsWithIgnoreCase(content, StringPool.LEFT_SQ_BRACKET)) {
				content = StringPool.LEFT_SQ_BRACKET + content + StringPool.RIGHT_SQ_BRACKET;
			}

			List<Map<String, Object>> list = getInstance().readValue(content, new TypeReference<List<Map<String, Object>>>() {
			});
			List<T> result = new ArrayList<>();
			for (Map<String, Object> map : list) {
				result.add(toPojo(map, valueTypeRef));
			}
			return result;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static Map<String, Object> toMap(String content) {
		try {
			return getInstance().readValue(content, Map.class);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
		try {
			Map<String, Map<String, Object>> map = getInstance().readValue(content, new TypeReference<Map<String, Map<String, Object>>>() {
			});
			Map<String, T> result = new HashMap<>(16);
			for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
				result.put(entry.getKey(), toPojo(entry.getValue(), valueTypeRef));
			}
			return result;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
		return getInstance().convertValue(fromValue, toValueType);
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param jsonString jsonString
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(String jsonString) {
		try {
			return getInstance().readTree(jsonString);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param in InputStream
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(InputStream in) {
		try {
			return getInstance().readTree(in);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param content content
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(byte[] content) {
		try {
			return getInstance().readTree(content);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json byte 数组反序列化成对象
	 *
	 * @param content   json bytes
	 * @param valueType class
	 * @param <T>       T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable byte[] content, Class<T> valueType) {
		if (ObjectUtil.isEmpty(content)) {
			return null;
		}
		try {
			return getInstance().readValue(content, valueType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param jsonString jsonString
	 * @param valueType  class
	 * @param <T>        T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable String jsonString, Class<T> valueType) {
		if (StringUtil.isBlank(jsonString)) {
			return null;
		}
		try {
			return getInstance().readValue(jsonString, valueType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param in        InputStream
	 * @param valueType class
	 * @param <T>       T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable InputStream in, Class<T> valueType) {
		if (in == null) {
			return null;
		}
		try {
			return getInstance().readValue(in, valueType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param content       bytes
	 * @param typeReference 泛型类型
	 * @param <T>           T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable byte[] content, TypeReference<T> typeReference) {
		if (ObjectUtil.isEmpty(content)) {
			return null;
		}
		try {
			return getInstance().readValue(content, typeReference);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param jsonString    jsonString
	 * @param typeReference 泛型类型
	 * @param <T>           T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable String jsonString, TypeReference<T> typeReference) {
		if (StringUtil.isBlank(jsonString)) {
			return null;
		}
		try {
			return getInstance().readValue(jsonString, typeReference);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param in            InputStream
	 * @param typeReference 泛型类型
	 * @param <T>           T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable InputStream in, TypeReference<T> typeReference) {
		if (in == null) {
			return null;
		}
		try {
			return getInstance().readValue(in, typeReference);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param jsonParser JsonParser
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(JsonParser jsonParser) {
		try {
			return getInstance().readTree(jsonParser);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param content  bytes
	 * @param javaType JavaType
	 * @param <T>      T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable byte[] content, JavaType javaType) {
		if (content == null || content.length == 0) {
			return null;
		}
		try {
			return getInstance().readValue(content, javaType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param jsonString jsonString
	 * @param javaType   JavaType
	 * @param <T>        T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable String jsonString, JavaType javaType) {
		if (StringUtil.isBlank(jsonString)) {
			return null;
		}
		try {
			return getInstance().readValue(jsonString, javaType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param in       InputStream
	 * @param javaType JavaType
	 * @param <T>      T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable InputStream in, JavaType javaType) {
		if (in == null) {
			return null;
		}
		try {
			return getInstance().readValue(in, javaType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将java.io.Reader反序列化成对象
	 *
	 * @param reader   java.io.Reader
	 * @param javaType JavaType
	 * @param <T>      T 泛型标记
	 * @return Bean
	 */
	@Nullable
	public static <T> T readValue(@Nullable Reader reader, JavaType javaType) {
		if (reader == null) {
			return null;
		}
		try {
			return getInstance().readValue(reader, javaType);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * clazz 获取 JavaType
	 *
	 * @param clazz Class
	 * @return MapType
	 */
	public static JavaType getType(Class<?> clazz) {
		return getInstance().getTypeFactory().constructType(clazz);
	}

	/**
	 * 封装 map type，keyClass String
	 *
	 * @param valueClass value 类型
	 * @return MapType
	 */
	public static MapType getMapType(Class<?> valueClass) {
		return getMapType(String.class, valueClass);
	}

	/**
	 * 封装 map type
	 *
	 * @param keyClass   key 类型
	 * @param valueClass value 类型
	 * @return MapType
	 */
	public static MapType getMapType(Class<?> keyClass, Class<?> valueClass) {
		return getInstance().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
	}

	/**
	 * 封装 map type
	 *
	 * @param elementClass 集合值类型
	 * @return CollectionLikeType
	 */
	public static CollectionLikeType getListType(Class<?> elementClass) {
		return getInstance().getTypeFactory().constructCollectionLikeType(List.class, elementClass);
	}

	/**
	 * 封装参数化类型
	 *
	 * <p>
	 * 例如： Map.class, String.class, String.class 对应 Map[String, String]
	 * </p>
	 *
	 * @param parametrized     泛型参数化
	 * @param parameterClasses 泛型参数类型
	 * @return JavaType
	 */
	public static JavaType getParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
		return getInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
	}

	/**
	 * 封装参数化类型，用来构造复杂的泛型
	 *
	 * <p>
	 * 例如： Map.class, String.class, String.class 对应 Map[String, String]
	 * </p>
	 *
	 * @param parametrized   泛型参数化
	 * @param parameterTypes 泛型参数类型
	 * @return JavaType
	 */
	public static JavaType getParametricType(Class<?> parametrized, JavaType... parameterTypes) {
		return getInstance().getTypeFactory().constructParametricType(parametrized, parameterTypes);
	}


	/**
	 * 读取集合
	 *
	 * @param content      bytes
	 * @param elementClass elementClass
	 * @param <T>          泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(@Nullable byte[] content, Class<T> elementClass) {
		if (content == null || content.length == 0) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(content, getListType(elementClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param content      InputStream
	 * @param elementClass elementClass
	 * @param <T>          泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(@Nullable InputStream content, Class<T> elementClass) {
		if (content == null) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(content, getListType(elementClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param reader       java.io.Reader
	 * @param elementClass elementClass
	 * @param <T>          泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(@Nullable Reader reader, Class<T> elementClass) {
		if (reader == null) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(reader, getListType(elementClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param content      bytes
	 * @param elementClass elementClass
	 * @param <T>          泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(@Nullable String content, Class<T> elementClass) {
		if (StringUtil.isBlank(content)) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(content, getListType(elementClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param content bytes
	 * @return 集合
	 */
	public static Map<String, Object> readMap(@Nullable byte[] content) {
		return readMap(content, Object.class);
	}

	/**
	 * 读取集合
	 *
	 * @param content InputStream
	 * @return 集合
	 */
	public static Map<String, Object> readMap(@Nullable InputStream content) {
		return readMap(content, Object.class);
	}

	/**
	 * 读取集合
	 *
	 * @param reader java.io.Reader
	 * @return 集合
	 */
	public static Map<String, Object> readMap(@Nullable Reader reader) {
		return readMap(reader, Object.class);
	}

	/**
	 * 读取集合
	 *
	 * @param content bytes
	 * @return 集合
	 */
	public static Map<String, Object> readMap(@Nullable String content) {
		return readMap(content, Object.class);
	}

	/**
	 * 读取集合
	 *
	 * @param content    bytes
	 * @param valueClass 值类型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(@Nullable byte[] content, Class<?> valueClass) {
		return readMap(content, String.class, valueClass);
	}

	/**
	 * 读取集合
	 *
	 * @param content    InputStream
	 * @param valueClass 值类型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(@Nullable InputStream content, Class<?> valueClass) {
		return readMap(content, String.class, valueClass);
	}

	/**
	 * 读取集合
	 *
	 * @param reader     java.io.Reader
	 * @param valueClass 值类型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(@Nullable Reader reader, Class<?> valueClass) {
		return readMap(reader, String.class, valueClass);
	}

	/**
	 * 读取集合
	 *
	 * @param content    bytes
	 * @param valueClass 值类型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(@Nullable String content, Class<?> valueClass) {
		return readMap(content, String.class, valueClass);
	}

	/**
	 * 读取集合
	 *
	 * @param content    bytes
	 * @param keyClass   key类型
	 * @param valueClass 值类型
	 * @param <K>        泛型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(@Nullable byte[] content, Class<?> keyClass, Class<?> valueClass) {
		if (content == null || content.length == 0) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(content, getMapType(keyClass, valueClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param content    InputStream
	 * @param keyClass   key类型
	 * @param valueClass 值类型
	 * @param <K>        泛型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(@Nullable InputStream content, Class<?> keyClass, Class<?> valueClass) {
		if (content == null) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(content, getMapType(keyClass, valueClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param reader     java.io.Reader
	 * @param keyClass   key类型
	 * @param valueClass 值类型
	 * @param <K>        泛型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(@Nullable Reader reader, Class<?> keyClass, Class<?> valueClass) {
		if (reader == null) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(reader, getMapType(keyClass, valueClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param content    bytes
	 * @param keyClass   key类型
	 * @param valueClass 值类型
	 * @param <K>        泛型
	 * @param <V>        泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(@Nullable String content, Class<?> keyClass, Class<?> valueClass) {
		if (StringUtil.isBlank(content)) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(content, getMapType(keyClass, valueClass));
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 *
	 * @param content bytes
	 * @return 集合
	 */
	public static List<Map<String, Object>> readListMap(@Nullable String content) {
		if (ObjectUtil.isEmpty(content)) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(content, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * jackson 的类型转换
	 *
	 * @param fromValue   来源对象
	 * @param toValueType 转换的类型
	 * @param <T>         泛型标记
	 * @return 转换结果
	 */
	public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
		return getInstance().convertValue(fromValue, toValueType);
	}

	/**
	 * jackson 的类型转换
	 *
	 * @param fromValue   来源对象
	 * @param toValueType 转换的类型
	 * @param <T>         泛型标记
	 * @return 转换结果
	 */
	public static <T> T convertValue(Object fromValue, JavaType toValueType) {
		return getInstance().convertValue(fromValue, toValueType);
	}

	/**
	 * jackson 的类型转换
	 *
	 * @param fromValue      来源对象
	 * @param toValueTypeRef 泛型类型
	 * @param <T>            泛型标记
	 * @return 转换结果
	 */
	public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
		return getInstance().convertValue(fromValue, toValueTypeRef);
	}

	/**
	 * tree 转对象
	 *
	 * @param treeNode  TreeNode
	 * @param valueType valueType
	 * @param <T>       泛型标记
	 * @return 转换结果
	 */
	public static <T> T treeToValue(TreeNode treeNode, Class<T> valueType) {
		try {
			return getInstance().treeToValue(treeNode, valueType);
		} catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * tree 转对象
	 *
	 * @param treeNode  TreeNode
	 * @param valueType valueType
	 * @param <T>       泛型标记
	 * @return 转换结果
	 */
	public static <T> T treeToValue(TreeNode treeNode, JavaType valueType) {
		try {
			return getInstance().treeToValue(treeNode, valueType);
		} catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 对象转 tree
	 *
	 * @param fromValue fromValue
	 * @param <T>       泛型标记
	 * @return 转换结果
	 */
	public static <T extends JsonNode> T valueToTree(@Nullable Object fromValue) {
		return getInstance().valueToTree(fromValue);
	}

	/**
	 * 判断是否可以序列化
	 *
	 * @param value 对象
	 * @return 是否可以序列化
	 */
	public static boolean canSerialize(@Nullable Object value) {
		if (value == null) {
			return true;
		}
		return getInstance().canSerialize(value.getClass());
	}

	/**
	 * 判断是否可以反序列化
	 *
	 * @param type JavaType
	 * @return 是否可以反序列化
	 */
	public static boolean canDeserialize(JavaType type) {
		return getInstance().canDeserialize(type);
	}

	/**
	 * 创建 ObjectNode
	 *
	 * @return ObjectNode
	 */
	public static ObjectNode createObjectNode() {
		return getInstance().createObjectNode();
	}

	/**
	 * 创建 ArrayNode
	 *
	 * @return ArrayNode
	 */
	public static ArrayNode createArrayNode() {
		return getInstance().createArrayNode();
	}

	public static ObjectMapper getInstance() {
		return JacksonHolder.INSTANCE;
	}

	private static class JacksonHolder {
		private static final ObjectMapper INSTANCE = new JacksonObjectMapper();
	}

	public static class JacksonObjectMapper extends ObjectMapper {
		private static final long serialVersionUID = 4288193147502386170L;

		private static final Locale CHINA = Locale.CHINA;

		public JacksonObjectMapper() {
			super();
			//设置地点为中国
			super.setLocale(CHINA);
			//去掉默认的时间戳格式
			super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			//设置为中国上海时区
			super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
			//序列化时，日期的统一格式
			super.setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATETIME, Locale.CHINA));
			//序列化处理
			super.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
			super.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
			super.findAndRegisterModules();
			//失败处理
			super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//单引号处理
			super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			//反序列化时，属性不存在的兼容处理s
			super.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			//日期格式化
			super.registerModule(new BladeJavaTimeModule());
			super.findAndRegisterModules();
		}

		@Override
		public ObjectMapper copy() {
			return super.copy();
		}
	}

}
