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
package org.springblade.core.swagger.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口排序注解
 *
 * <p>
 * OpenAPI 3 / springdoc 无接口排序原生注解，文档内接口与分组默认排列不贴合业务书写顺序。本注解按标注位置区分两个排序维度：
 * 标注在方法上，value 为该接口在所属分组内的显式序号；标注在类上，value 为该控制器 @Tag 分组之间的排序序号，
 * 类内各接口则按源码声明顺序排列，个别接口需打破声明顺序时可再在方法上标注精确覆盖。均为值越小越靠前，未标注者排在最后。
 * </p>
 *
 * @author Chill
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiOrder {

	/**
	 * 排序值，越小越靠前
	 *
	 * <p>
	 * 标注在方法上时为该接口在所属分组内的显式序号；标注在类上时为该控制器 @Tag 分组之间的排序序号，
	 * 分组内各接口不受此值影响，按源码声明顺序排列。默认值使未显式指定顺序者排在最后
	 * </p>
	 */
	int value() default Integer.MAX_VALUE;

}
