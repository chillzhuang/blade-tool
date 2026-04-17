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

import java.util.function.Supplier;

/**
 * BladeView 角色名提供者
 * <p>
 * 用于 {@link BladeViewResponseAdvice} 动态模式下按角色解析视图。
 * 自定义时请实现本接口而非通用的 {@code Supplier<String>}，
 * 以避免与业务工程中其它 {@code Supplier<String>} Bean 产生歧义。
 * </p>
 *
 * <h3>示例</h3>
 * <pre>
 * &#64;Bean
 * public BladeRoleSupplier myRoleSupplier() {
 *     return () -&gt; currentUserHolder.getRole();
 * }
 * </pre>
 *
 * @author Chill
 */
@FunctionalInterface
public interface BladeRoleSupplier extends Supplier<String> {
}
