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
package org.springblade.core.report.config;

import com.bstek.ureport.UReportPropertyPlaceholderConfigurer;
import com.bstek.ureport.console.UReportServlet;
import com.bstek.ureport.provider.report.ReportProvider;
import org.springblade.core.report.props.ReportDatabaseProperties;
import org.springblade.core.report.props.ReportProperties;
import org.springblade.core.report.provider.DatabaseProvider;
import org.springblade.core.report.provider.ReportPlaceholderProvider;
import org.springblade.core.report.service.IReportFileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

import javax.servlet.Servlet;

/**
 * UReport配置类
 *
 * @author Chill
 */
@Order
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "report.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({ReportProperties.class, ReportDatabaseProperties.class})
@ImportResource("classpath:ureport-console-context.xml")
public class ReportConfiguration {

	@Bean
	public ServletRegistrationBean<Servlet> registrationBean() {
		return new ServletRegistrationBean<>(new UReportServlet(), "/ureport/*");
	}

	@Bean
	public UReportPropertyPlaceholderConfigurer uReportPropertyPlaceholderConfigurer(ReportProperties properties) {
		return new ReportPlaceholderProvider(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public ReportProvider reportProvider(ReportDatabaseProperties properties, IReportFileService service) {
		return new DatabaseProvider(properties, service);
	}

}
