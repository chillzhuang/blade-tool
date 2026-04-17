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

import java.lang.annotation.*;

/**
 * BladeX 视图控制注解（全场景统一）
 * <p>
 * 用在字段上：标记该字段的可见视图层级（替代 {@code @JsonView}）
 * 用在方法/类上：控制接口的视图过滤策略
 * </p>
 *
 * <h3>字段标注</h3>
 * <pre>
 * &#64;BladeView(Views.Detail.class)
 * private String tenantName;
 * </pre>
 *
 * <h3>Controller 标注</h3>
 * <pre>
 * // 静态：明确指定视图
 * &#64;BladeView(Views.Summary.class)
 * public R&lt;List&lt;UserVO&gt;&gt; list() { ... }
 *
 * // 动态：根据用户角色自动解析
 * &#64;BladeView
 * public R&lt;UserVO&gt; detail() { ... }
 *
 * // 类级别默认（方法级可覆盖）
 * &#64;BladeView(Views.Admin.class)
 * public class AdminController { ... }
 * </pre>
 *
 * @author Chill
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BladeView {

	/**
	 * 视图类型
	 * <p>
	 * 字段上：指定 Views.Summary / Detail / Admin / Administrator
	 * 方法上：指定具体视图 = 静态模式；默认 Auto.class = 动态模式
	 * </p>
	 */
	Class<?> value() default Auto.class;

	/**
	 * 动态解析标记（仅用于 Controller 方法/类）
	 */
	interface Auto {
	}

}
