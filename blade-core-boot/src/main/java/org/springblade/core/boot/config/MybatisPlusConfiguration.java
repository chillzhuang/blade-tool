/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE;
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

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.mp.BladeMetaObjectHandler;
import org.springblade.core.launch.constant.AppConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * mybatisplus 配置
 *
 * @author smallchill
 */
@Configuration
@MapperScan("org.springblade.**.mapper.**")
public class MybatisPlusConfiguration {

	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}

	@Bean
	public MetaObjectHandler metaObjectHandler() {
		return new BladeMetaObjectHandler();
	}

	/**
	 * SQL执行效率插件
	 *
	 * @return PerformanceInterceptor
	 */
	@Bean
	@Profile({AppConstant.DEV_CDOE, AppConstant.TEST_CODE})
	public PerformanceInterceptor performanceInterceptor() {
		return new PerformanceInterceptor();
	}

}

