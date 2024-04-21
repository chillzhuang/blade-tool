/**
 * Copyright (c) 2019-2029, DreamLu 卢春梦 (596392912@qq.com & www.dreamlu.net).
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

package org.springblade.core.launch.props;

import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * 自定义资源文件读取，优先级最低
 *
 * @author L.cm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BladePropertySource {

	/**
	 * Indicate the resource location(s) of the properties file to be loaded.
	 * for example, {@code "classpath:/com/example/app.yml"}
	 *
	 * @return location(s)
	 */
	String value();

	/**
	 * load app-{activeProfile}.yml
	 *
	 * @return {boolean}
	 */
	boolean loadActiveProfile() default true;

	/**
	 * Get the order value of this resource.
	 *
	 * @return order
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

}
