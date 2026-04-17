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
package org.springblade.core.secure.config;

import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.jackson.BladeRoleSupplier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Jackson Views 角色提供者自动装配
 * <p>
 * 默认通过 {@link SecureUtil#getUserRole()} 获取当前用户角色名，
 * 用户可自定义 {@link BladeRoleSupplier} Bean 来覆盖。
 * </p>
 *
 * @author Chill
 */
@AutoConfiguration
public class BladeViewRoleConfiguration {

	/**
	 * 默认角色名称提供者
	 * <p>使用 {@link ConditionalOnMissingBean} 允许用户自定义覆盖</p>
	 */
	@Bean
	@ConditionalOnMissingBean
	public BladeRoleSupplier roleNameSupplier() {
		return SecureUtil::getUserRole;
	}

}
