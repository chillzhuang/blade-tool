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
package org.springblade.core.tool.support;

import org.springblade.core.tool.utils.BeanUtil;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 * spring cglib 魔改
 *
 * <p>
 *     1. 支持链式 bean
 *     2. 自定义的 BeanCopier 解决 spring boot 和 cglib ClassLoader classLoader 不一致的问题
 * </p>
 *
 * @author L.cm
 */
public abstract class BaseBeanCopier {
	private static final BeanCopierKey KEY_FACTORY = (BeanCopierKey) KeyFactory.create(BeanCopierKey.class);
	private static final Type CONVERTER = TypeUtils.parseType("org.springframework.cglib.core.Converter");
	private static final Type BEAN_COPIER = TypeUtils.parseType(BaseBeanCopier.class.getName());
	private static final Signature COPY = new Signature("copy", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER});
	private static final Signature CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");

	interface BeanCopierKey {
		/**
		 * 实例化
		 * @param source 源
		 * @param target 目标
		 * @param useConverter 是否使用转换
		 * @return
		 */
		Object newInstance(String source, String target, boolean useConverter);
	}

	public static BaseBeanCopier create(Class source, Class target, boolean useConverter) {
		return BaseBeanCopier.create(source, target, null, useConverter);
	}

	public static BaseBeanCopier create(Class source, Class target, ClassLoader classLoader, boolean useConverter) {
		Generator gen;
		if (classLoader == null) {
			gen = new Generator();
		} else {
			gen = new Generator(classLoader);
		}
		gen.setSource(source);
		gen.setTarget(target);
		gen.setUseConverter(useConverter);
		return gen.create();
	}

	/**
	 * 拷贝
	 * @param from 源
	 * @param to 目标
	 * @param converter 转换器
	 */
	abstract public void copy(Object from, Object to, Converter converter);

	public static class Generator extends AbstractClassGenerator {
		private static final Source SOURCE = new Source(BaseBeanCopier.class.getName());
		private final ClassLoader classLoader;
		private Class source;
		private Class target;
		private boolean useConverter;

		Generator() {
			super(SOURCE);
			this.classLoader = null;
		}

		Generator(ClassLoader classLoader) {
			super(SOURCE);
			this.classLoader = classLoader;
		}

		public void setSource(Class source) {
			if (!Modifier.isPublic(source.getModifiers())) {
				setNamePrefix(source.getName());
			}
			this.source = source;
		}

		public void setTarget(Class target) {
			if (!Modifier.isPublic(target.getModifiers())) {
				setNamePrefix(target.getName());
			}

			this.target = target;
		}

		public void setUseConverter(boolean useConverter) {
			this.useConverter = useConverter;
		}

		@Override
		protected ClassLoader getDefaultClassLoader() {
			return target.getClassLoader();
		}

		@Override
		protected ProtectionDomain getProtectionDomain() {
			return ReflectUtils.getProtectionDomain(source);
		}

		public BaseBeanCopier create() {
			Object key = KEY_FACTORY.newInstance(source.getName(), target.getName(), useConverter);
			return (BaseBeanCopier) super.create(key);
		}

		@Override
		public void generateClass(ClassVisitor v) {
			Type sourceType = Type.getType(source);
			Type targetType = Type.getType(target);
			ClassEmitter ce = new ClassEmitter(v);
			ce.begin_class(Constants.V1_2,
				Constants.ACC_PUBLIC,
				getClassName(),
				BEAN_COPIER,
				null,
				Constants.SOURCE_FILE);

			EmitUtils.null_constructor(ce);
			CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);

			// 2018.12.27 by L.cm 支持链式 bean
			PropertyDescriptor[] getters = BeanUtil.getBeanGetters(source);
			PropertyDescriptor[] setters = BeanUtil.getBeanSetters(target);
			Map<String, Object> names = new HashMap<>(16);
			for (PropertyDescriptor getter : getters) {
				names.put(getter.getName(), getter);
			}

			Local targetLocal = e.make_local();
			Local sourceLocal = e.make_local();
			e.load_arg(1);
			e.checkcast(targetType);
			e.store_local(targetLocal);
			e.load_arg(0);
			e.checkcast(sourceType);
			e.store_local(sourceLocal);

			for (int i = 0; i < setters.length; i++) {
				PropertyDescriptor setter = setters[i];
				PropertyDescriptor getter = (PropertyDescriptor) names.get(setter.getName());
				if (getter != null) {
					MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
					MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
					if (useConverter) {
						Type setterType = write.getSignature().getArgumentTypes()[0];
						e.load_local(targetLocal);
						e.load_arg(2);
						e.load_local(sourceLocal);
						e.invoke(read);
						e.box(read.getSignature().getReturnType());
						EmitUtils.load_class(e, setterType);
						e.push(write.getSignature().getName());
						e.invoke_interface(CONVERTER, CONVERT);
						e.unbox_or_zero(setterType);
						e.invoke(write);
					} else if (compatible(getter, setter)) {
						// 2018.12.27 by L.cm 支持链式 bean
						e.load_local(targetLocal);
						e.load_local(sourceLocal);
						e.invoke(read);
						e.invoke(write);
					}
				}
			}
			e.return_value();
			e.end_method();
			ce.end_class();
		}

		private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
			return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
		}

		@Override
		protected Object firstInstance(Class type) {
			return ReflectUtils.newInstance(type);
		}

		@Override
		protected Object nextInstance(Object instance) {
			return instance;
		}
	}
}
