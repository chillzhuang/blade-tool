/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (smallchill@163.com).
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
package org.springblade.core.boot.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.config.MybatisPlusConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 多租户配置类
 *
 * @author Chill
 */
@AutoConfiguration
@AllArgsConstructor
@AutoConfigureBefore(MybatisPlusConfiguration.class)
@EnableConfigurationProperties(BladeTenantProperties.class)
public class TenantConfiguration {

	/**
	 * 自定义多租户处理器
	 *
	 * @param tenantProperties 多租户配置类
	 * @return TenantHandler
	 */
	@Bean
	@Primary
	public TenantLineHandler bladeTenantHandler(BladeTenantProperties tenantProperties) {
		return new BladeTenantHandler(tenantProperties);
	}

	/**
	 * 自定义租户拦截器
	 *
	 * @param tenantHandler 多租户处理器
	 * @return BladeTenantInterceptor
	 */
	@Bean
	@Primary
	public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantLineHandler tenantHandler) {
		BladeTenantInterceptor tenantInterceptor = new BladeTenantInterceptor();
		tenantInterceptor.setTenantLineHandler(tenantHandler);
		return tenantInterceptor;
	}

	/**
	 * 自定义租户id生成器
	 *
	 * @return TenantId
	 */
	@Bean
	@ConditionalOnMissingBean(TenantId.class)
	public TenantId tenantId() {
		return new BladeTenantId();
	}

}
