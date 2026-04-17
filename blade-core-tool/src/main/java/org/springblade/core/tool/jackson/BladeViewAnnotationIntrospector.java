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

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.io.Serial;

/**
 * 自定义 Jackson 注解内省器
 * <p>
 * 让 Jackson 序列化引擎识别字段上的 {@link BladeView} 注解，
 * 将其等同于标准的 {@code @JsonView} 处理。
 * </p>
 * <p>
 * 继承 {@link JacksonAnnotationIntrospector}，所有标准 Jackson 注解
 * （{@code @JsonIgnore}、{@code @JsonSerialize}、{@code @JsonFormat} 等）
 * 通过 super 正常工作。
 * </p>
 *
 * @author Chill
 */
public class BladeViewAnnotationIntrospector extends JacksonAnnotationIntrospector {

	@Serial
	private static final long serialVersionUID = 1L;

	@Override
	public Class<?>[] findViews(Annotated a) {
		// 优先识别 @BladeView（仅处理非 Auto 的静态视图标注）
		BladeView bladeView = _findAnnotation(a, BladeView.class);
		if (bladeView != null && bladeView.value() != BladeView.Auto.class) {
			return new Class<?>[]{bladeView.value()};
		}
		// 兼容：仍然识别标准 @JsonView
		return super.findViews(a);
	}

}
