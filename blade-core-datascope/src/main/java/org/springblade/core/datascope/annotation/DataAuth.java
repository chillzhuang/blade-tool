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
package org.springblade.core.datascope.annotation;

import org.springblade.core.datascope.constant.DataScopeConstant;
import org.springblade.core.datascope.enums.DataScopeEnum;

import java.lang.annotation.*;

/**
 * 数据权限定义
 *
 * @author Chill
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataAuth {

	/**
	 * 资源编号
	 */
	String code() default "";

	/**
	 * 数据权限对应字段
	 */
	String column() default DataScopeConstant.DEFAULT_COLUMN;

	/**
	 * 数据权限规则
	 */
	DataScopeEnum type() default DataScopeEnum.ALL;

	/**
	 * 可见字段
	 */
	String field() default "*";

	/**
	 * 数据权限规则值域
	 */
	String value() default "";

}

