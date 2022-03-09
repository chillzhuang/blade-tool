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
package org.springblade.core.cloud.config;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.DefaultBlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 处理sentinel2021兼容问题
 *
 * @author Chill
 */
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class BladeSentinelFilterConfiguration {

	@Bean
	public SentinelWebInterceptor sentinelWebInterceptor(SentinelWebMvcConfig sentinelWebMvcConfig) {
		return new SentinelWebInterceptor(sentinelWebMvcConfig);
	}

	@Bean
	public SentinelWebMvcConfig sentinelWebMvcConfig(SentinelProperties properties,
													 Optional<UrlCleaner> urlCleanerOptional, Optional<BlockExceptionHandler> blockExceptionHandlerOptional,
													 Optional<RequestOriginParser> requestOriginParserOptional) {
		SentinelWebMvcConfig sentinelWebMvcConfig = new SentinelWebMvcConfig();
		sentinelWebMvcConfig.setHttpMethodSpecify(properties.getHttpMethodSpecify());
		sentinelWebMvcConfig.setWebContextUnify(properties.getWebContextUnify());

		if (blockExceptionHandlerOptional.isPresent()) {
			blockExceptionHandlerOptional.ifPresent(sentinelWebMvcConfig::setBlockExceptionHandler);
		} else {
			if (StringUtils.hasText(properties.getBlockPage())) {
				sentinelWebMvcConfig.setBlockExceptionHandler(
					((request, response, e) -> response.sendRedirect(properties.getBlockPage())));
			} else {
				sentinelWebMvcConfig.setBlockExceptionHandler(new DefaultBlockExceptionHandler());
			}
		}

		urlCleanerOptional.ifPresent(sentinelWebMvcConfig::setUrlCleaner);
		requestOriginParserOptional.ifPresent(sentinelWebMvcConfig::setOriginParser);
		return sentinelWebMvcConfig;
	}

}
