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
package org.springblade.core.tool.jackson;

/**
 * 视图自定义扩展接口
 * <p>
 * 用户可通过实现此接口来注册自定义视图层级，
 * 注册为 Spring Bean 即可生效。
 * </p>
 *
 * <h3>示例：新增超管以上的超级视图</h3>
 * <pre>
 * // 1. 定义自定义视图接口
 * public interface SuperView extends Views.Administrator {}
 *
 * // 2. 注册到解析器
 * &#64;Bean
 * public BladeViewCustomizer myViewCustomizer() {
 *     return resolver -> resolver.registerView("super", SuperView.class, 4);
 * }
 *
 * // 3. 在 YAML 中映射角色
 * // blade.jackson.view.role-mapping.superadmin: super
 * </pre>
 *
 * @author Chill
 */
@FunctionalInterface
public interface BladeViewCustomizer {

	/**
	 * 自定义视图注册
	 *
	 * @param resolver 视图解析器
	 */
	void customize(BladeViewResolver resolver);

}
