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
package org.springblade.core.mp.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties.CoreConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.AllArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.mp.intercept.QueryInterceptor;
import org.springblade.core.mp.plugins.BladePaginationInterceptor;
import org.springblade.core.mp.plugins.SqlLogInterceptor;
import org.springblade.core.mp.props.MybatisPlusProperties;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * mybatis plus 配置
 *
 * @author Chill
 */
@AutoConfiguration
@AllArgsConstructor
@MapperScan("org.springblade.**.mapper.**")
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusConfiguration {

	/**
	 * 租户拦截器
	 */
	@Bean
	@ConditionalOnMissingBean(TenantLineInnerInterceptor.class)
	public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
		return new TenantLineInnerInterceptor(new TenantLineHandler() {
			@Override
			public Expression getTenantId() {
				return new StringValue(Func.toStr(SecureUtil.getTenantId(), BladeConstant.ADMIN_TENANT_ID));
			}

			@Override
			public boolean ignoreTable(String tableName) {
				return true;
			}
		});
	}

	/**
	 * mybatis-plus 拦截器集合
	 */
	@Bean
	@ConditionalOnMissingBean(MybatisPlusInterceptor.class)
	public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<QueryInterceptor[]> queryInterceptors,
														 TenantLineInnerInterceptor tenantLineInnerInterceptor,
														 MybatisPlusProperties mybatisPlusProperties) {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 配置租户拦截器
		interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
		// 配置分页拦截器
		BladePaginationInterceptor paginationInterceptor = new BladePaginationInterceptor();
		// 配置自定义查询拦截器
		QueryInterceptor[] queryInterceptorArray = queryInterceptors.getIfAvailable();
		if (ObjectUtil.isNotEmpty(queryInterceptorArray)) {
			AnnotationAwareOrderComparator.sort(queryInterceptorArray);
			paginationInterceptor.setQueryInterceptors(queryInterceptorArray);
		}
		paginationInterceptor.setMaxLimit(mybatisPlusProperties.getPageLimit());
		paginationInterceptor.setOverflow(mybatisPlusProperties.getOverflow());
		interceptor.addInnerInterceptor(paginationInterceptor);
		return interceptor;
	}

	/**
	 * sql 日志
	 *
	 * @return SqlLogInterceptor
	 */
	@Bean
	@ConditionalOnProperty(value = "blade.mybatis-plus.sql-log", matchIfMissing = true)
	public SqlLogInterceptor sqlLogInterceptor(MybatisPlusProperties properties) {
		return new SqlLogInterceptor(properties);
	}

	/**
	 * 关闭 mybatis 默认日志
	 */
	@Bean
	public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
		return properties -> {
			CoreConfiguration configuration = properties.getConfiguration();
			if (configuration != null) {
				Class<? extends Log> logImpl = configuration.getLogImpl();
				if (logImpl == null) {
					configuration.setLogImpl(NoLoggingImpl.class);
				}
			}
		};
	}

}

