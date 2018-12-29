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


import org.springblade.core.tool.support.BeanProperty;
import org.springblade.core.tool.support.BaseBeanCopier;
import org.springframework.beans.BeansException;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实体工具类
 *
 * @author L.cm
 */
public class BeanUtil extends org.springframework.beans.BeanUtils {

	/**
	 * 实例化对象
	 * @param clazz 类
	 * @param <T> 泛型标记
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz) {
		return (T) instantiateClass(clazz);
	}

	/**
	 * 实例化对象
	 * @param clazzStr 类名
	 * @param <T> 泛型标记
	 * @return 对象
	 */
	public static <T> T newInstance(String clazzStr) {
		try {
			Class<?> clazz = Class.forName(clazzStr);
			return newInstance(clazz);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取Bean的属性
	 * @param bean bean
	 * @param propertyName 属性名
	 * @return 属性值
	 */
	public static Object getProperty(Object bean, String propertyName) {
		Assert.notNull(bean, "bean Could not null");
		return BeanMap.create(bean).get(propertyName);
	}

	/**
	 * 设置Bean属性
	 * @param bean bean
	 * @param propertyName 属性名
	 * @param value 属性值
	 */
	public static void setProperty(Object bean, String propertyName, Object value) {
		Assert.notNull(bean, "bean Could not null");
		BeanMap.create(bean).put(propertyName, value);
	}

	/**
	 * 深复制
	 *
	 * 注意：不支持链式Bean
	 *
	 * @param source 源对象
	 * @param <T> 泛型标记
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T source) {
		return (T) BeanUtil.copy(source, source.getClass());
	}

	/**
	 * copy 对象属性到另一个对象，默认不使用Convert
	 *
	 * 注意：不支持链式Bean，链式用 copyProperties
	 *
	 * @param source 源对象
	 * @param clazz 类名
	 * @param <T> 泛型标记
	 * @return T
	 */
	public static <T> T copy(Object source, Class<T> clazz) {
		BaseBeanCopier copier = BaseBeanCopier.create(source.getClass(), clazz, false);

		T to = newInstance(clazz);
		copier.copy(source, to, null);
		return to;
	}

	/**
	 * 拷贝对象
	 *
	 * 注意：不支持链式Bean，链式用 copyProperties
	 *
	 * @param source 源对象
	 * @param targetBean 需要赋值的对象
	 */
	public static void copy(Object source, Object targetBean) {
		BaseBeanCopier copier = BaseBeanCopier
			.create(source.getClass(), targetBean.getClass(), false);

		copier.copy(source, targetBean, null);
	}

	/**
	 * Copy the property values of the given source bean into the target class.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * @param source the source bean
	 * @param target the target bean class
	 * @param <T> 泛型标记
	 * @throws BeansException if the copying failed
	 * @return T
	 */
	public static <T> T copyProperties(Object source, Class<T> target) throws BeansException {
		T to = newInstance(target);
		BeanUtil.copyProperties(source, to);
		return to;
	}

	/**
	 * 将对象装成map形式
	 * @param bean 源对象
	 * @return {Map}
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object bean) {
		return BeanMap.create(bean);
	}

	/**
	 * 将map 转为 bean
	 * @param beanMap map
	 * @param valueType 对象类型
	 * @param <T> 泛型标记
	 * @return {T}
	 */
	public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
		T bean = BeanUtil.newInstance(valueType);
		BeanMap.create(bean).putAll(beanMap);
		return bean;
	}

	/**
	 * 给一个Bean添加字段
	 * @param superBean 父级Bean
	 * @param props 新增属性
	 * @return  {Object}
	 */
	public static Object generator(Object superBean, BeanProperty... props) {
		Class<?> superclass = superBean.getClass();
		Object genBean = generator(superclass, props);
		BeanUtil.copy(superBean, genBean);
		return genBean;
	}

	/**
	 * 给一个class添加字段
	 * @param superclass 父级
	 * @param props 新增属性
	 * @return {Object}
	 */
	public static Object generator(Class<?> superclass, BeanProperty... props) {
		BeanGenerator generator = new BeanGenerator();
		generator.setSuperclass(superclass);
		generator.setUseCache(true);
		for (BeanProperty prop : props) {
			generator.addProperty(prop.getName(), prop.getType());
		}
		return generator.create();
	}

	/**
	 * 获取 Bean 的所有 get方法
	 * @param type 类
	 * @return PropertyDescriptor数组
	 */
	public static PropertyDescriptor[] getBeanGetters(Class type) {
		return getPropertiesHelper(type, true, false);
	}

	/**
	 * 获取 Bean 的所有 set方法
	 * @param type 类
	 * @return PropertyDescriptor数组
	 */
	public static PropertyDescriptor[] getBeanSetters(Class type) {
		return getPropertiesHelper(type, false, true);
	}

	private static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
		try {
			PropertyDescriptor[] all = BeanUtil.getPropertyDescriptors(type);
			if (read && write) {
				return all;
			} else {
				List<PropertyDescriptor> properties = new ArrayList<>(all.length);
				for (PropertyDescriptor pd : all) {
					if (read && pd.getReadMethod() != null) {
						properties.add(pd);
					} else if (write && pd.getWriteMethod() != null) {
						properties.add(pd);
					}
				}
				return properties.toArray(new PropertyDescriptor[0]);
			}
		} catch (BeansException ex) {
			throw new CodeGenerationException(ex);
		}
	}

}
