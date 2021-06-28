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
package org.springblade.core.boot.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.boot.props.MybatisPlusProperties;
import org.springblade.core.mp.plugins.SqlLogInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatisplus 配置
 *
 * @author Chill
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@MapperScan("org.springblade.**.mapper.**")
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusConfiguration {

	private final TenantLineHandler tenantLineHandler;

	/**
	 * mybatis-plus 拦截器集合
	 */
	@Bean
	@ConditionalOnMissingBean(MybatisPlusInterceptor.class)
	public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusProperties mybatisPlusProperties) {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 配置租户拦截器
		interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler));
		// 配置分页拦截器
		PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
		paginationInnerInterceptor.setMaxLimit(mybatisPlusProperties.getPageLimit());
		paginationInnerInterceptor.setOverflow(mybatisPlusProperties.getOverflow());
		interceptor.addInnerInterceptor(paginationInnerInterceptor);
		return interceptor;
	}

	/**
	 * sql 日志
	 *
	 * @return SqlLogInterceptor
	 */
	@Bean
	@ConditionalOnProperty(value = "blade.mybatis-plus.sql-log", matchIfMissing = true)
	public SqlLogInterceptor sqlLogInterceptor() {
		return new SqlLogInterceptor();
	}

}

