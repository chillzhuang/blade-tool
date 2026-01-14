/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (bladejava@qq.com).
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
package org.springblade.core.secure.annotation;

import org.springblade.core.tool.utils.StringPool;

import java.lang.annotation.*;

/**
 * 权限注解，用于检查权限，规定访问权限
 * 支持以下几种使用方式：
 * <p>
 * 1. 单个属性模式：
 * <pre>{@code
 *   @PreAuth(permission = "user:add")
 *   @PreAuth(role = "admin")
 * }</pre>
 *
 * 2. 组合属性模式：
 * <pre>{@code
 *   @PreAuth(role = "admin", permission = "user:add")
 * }</pre>
 *
 * 3. SpEL表达式模式：
 * <pre>{@code
 *   @PreAuth("#userVO.id<10")
 *   @PreAuth("hasRole('admin')")
 *   @PreAuth("hasPermission('user:add')")
 *   @PreAuth("hasPermission(#test) and hasRole('admin')")
 * }</pre>
 *
 * @author Chill
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuth {

	/**
	 * Spring el表达式
	 */
	String value() default StringPool.EMPTY;

	/**
	 * 接口权限编码
	 */
	String permission() default StringPool.EMPTY;

	/**
	 * 角色权限
	 */
	String role() default StringPool.EMPTY;

}

