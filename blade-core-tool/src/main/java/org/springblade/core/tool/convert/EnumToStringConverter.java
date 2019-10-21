package org.springblade.core.tool.convert;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.ConvertUtil;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 接收参数 同 jackson Enum -》 String 转换
 *
 * @author L.cm
 */
@Slf4j
public class EnumToStringConverter implements ConditionalGenericConverter {
	/**
	 * 缓存 Enum 类信息，提供性能
	 */
	private static final ConcurrentMap<Class<?>, AccessibleObject> ENUM_CACHE_MAP = new ConcurrentHashMap<>(8);

	@Nullable
	private static AccessibleObject getAnnotation(Class<?> clazz) {
		Set<AccessibleObject> accessibleObjects = new HashSet<>();
		// JsonValue METHOD, FIELD
		Field[] fields = clazz.getDeclaredFields();
		Collections.addAll(accessibleObjects, fields);
		// methods
		Method[] methods = clazz.getDeclaredMethods();
		Collections.addAll(accessibleObjects, methods);
		for (AccessibleObject accessibleObject : accessibleObjects) {
			// 复用 jackson 的 JsonValue 注解
			JsonValue jsonValue = accessibleObject.getAnnotation(JsonValue.class);
			if (jsonValue != null && jsonValue.value()) {
				accessibleObject.setAccessible(true);
				return accessibleObject;
			}
		}
		return null;
	}

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> pairSet = new HashSet<>(3);
		pairSet.add(new ConvertiblePair(Enum.class, String.class));
		pairSet.add(new ConvertiblePair(Enum.class, Integer.class));
		pairSet.add(new ConvertiblePair(Enum.class, Long.class));
		return Collections.unmodifiableSet(pairSet);
	}

	@Override
	public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Class<?> sourceClazz = sourceType.getType();
		AccessibleObject accessibleObject = ENUM_CACHE_MAP.computeIfAbsent(sourceClazz, EnumToStringConverter::getAnnotation);
		Class<?> targetClazz = targetType.getType();
		// 如果为null，走默认的转换
		if (accessibleObject == null) {
			if (String.class == targetClazz) {
				return ((Enum) source).name();
			}
			int ordinal = ((Enum) source).ordinal();
			return ConvertUtil.convert(ordinal, targetClazz);
		}
		try {
			return EnumToStringConverter.invoke(sourceClazz, accessibleObject, source, targetClazz);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Nullable
	private static Object invoke(Class<?> clazz, AccessibleObject accessibleObject, Object source, Class<?> targetClazz)
		throws IllegalAccessException, InvocationTargetException {
		Object value = null;
		if (accessibleObject instanceof Field) {
			Field field = (Field) accessibleObject;
			value = field.get(source);
		} else if (accessibleObject instanceof Method) {
			Method method = (Method) accessibleObject;
			Class<?> paramType = method.getParameterTypes()[0];
			// 类型转换
			Object object = ConvertUtil.convert(source, paramType);
			value = method.invoke(clazz, object);
		}
		if (value == null) {
			return null;
		}
		return ConvertUtil.convert(value, targetClazz);
	}
}
