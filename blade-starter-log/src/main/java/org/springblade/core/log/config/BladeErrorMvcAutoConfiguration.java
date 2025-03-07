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
package org.springblade.core.log.config;


import lombok.AllArgsConstructor;
import org.springblade.core.log.error.BladeErrorAttributes;
import org.springblade.core.log.error.BladeErrorController;
import org.springblade.core.log.props.BladeLogProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.Servlet;

/**
 * 统一异常处理
 *
 * @author Chill
 */
@AutoConfiguration
@AllArgsConstructor
@ConditionalOnWebApplication
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties(BladeLogProperties.class)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
public class BladeErrorMvcAutoConfiguration {

	private final ServerProperties serverProperties;
	private final BladeLogProperties bladeLogProperties;


	@Bean
	@ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
	public DefaultErrorAttributes errorAttributes() {
		return new BladeErrorAttributes(bladeLogProperties);
	}

	@Bean
	@ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
	public BasicErrorController basicErrorController(ErrorAttributes errorAttributes) {
		return new BladeErrorController(errorAttributes, serverProperties.getError());
	}

}
