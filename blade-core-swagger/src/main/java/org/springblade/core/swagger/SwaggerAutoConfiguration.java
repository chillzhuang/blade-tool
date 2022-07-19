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
package org.springblade.core.swagger;


import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springblade.core.launch.props.BladeProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * swagger配置
 *
 * @author Chill
 */
@EnableSwagger
@AutoConfiguration
@AllArgsConstructor
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerAutoConfiguration {

	private static final String DEFAULT_BASE_PATH = "/**";
	private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

	/**
	 * 引入Knife4j扩展类
	 */
	private final OpenApiExtensionResolver openApiExtensionResolver;

	/**
	 * 引入Blade环境变量
	 */
	private final BladeProperties bladeProperties;

	@Bean
	@ConditionalOnMissingBean
	public SwaggerProperties swaggerProperties() {
		return new SwaggerProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public Docket api(SwaggerProperties swaggerProperties) {
		if (swaggerProperties.getBasePath().size() == 0) {
			swaggerProperties.getBasePath().add(DEFAULT_BASE_PATH);
		}
		if (swaggerProperties.getExcludePath().size() == 0) {
			swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
		}
		ApiSelectorBuilder apis = new Docket(DocumentationType.SWAGGER_2)
			.host(swaggerProperties.getHost())
			.apiInfo(apiInfo(swaggerProperties)).select()
			.apis(SwaggerUtil.basePackages(swaggerProperties.getBasePackages()));
		swaggerProperties.getBasePath().forEach(p -> apis.paths(PathSelectors.ant(p)));
		swaggerProperties.getExcludePath().forEach(p -> apis.paths(PathSelectors.ant(p).negate()));
		return apis.build().securityContexts(securityContexts(swaggerProperties)).securitySchemes(securitySchemas(swaggerProperties))
			.extensions(openApiExtensionResolver.buildExtensions(bladeProperties.getName()));
	}

	/**
	 * 配置默认的全局鉴权策略的开关，通过正则表达式进行匹配；默认匹配所有URL
	 */
	private List<SecurityContext> securityContexts(SwaggerProperties swaggerProperties) {
		return Collections.singletonList(SecurityContext.builder()
			.securityReferences(defaultAuth(swaggerProperties))
			.forPaths(PathSelectors.regex(swaggerProperties.getAuthorization().getAuthRegex()))
			.build());
	}

	/**
	 * 默认的全局鉴权策略
	 */
	private List<SecurityReference> defaultAuth(SwaggerProperties swaggerProperties) {
		List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
		List<SecurityReference> securityReferenceList = new ArrayList<>();
		List<SwaggerProperties.AuthorizationScope> swaggerScopeList = swaggerProperties.getAuthorization().getAuthorizationScopeList();
		swaggerScopeList.forEach(authorizationScope -> authorizationScopeList.add(new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
		if (authorizationScopeList.size() == 0) {
			authorizationScopeList.add(new AuthorizationScope("global", "accessEverywhere"));
		}
		AuthorizationScope[] authorizationScopes = authorizationScopeList.toArray(new AuthorizationScope[0]);
		swaggerScopeList.forEach(authorizationScope -> securityReferenceList.add(new SecurityReference(authorizationScope.getName(), authorizationScopes)));
		if (securityReferenceList.size() == 0) {
			securityReferenceList.add(new SecurityReference(SwaggerUtil.clientInfo().getName(), authorizationScopes));
			securityReferenceList.add(new SecurityReference(SwaggerUtil.bladeAuth().getName(), authorizationScopes));
			securityReferenceList.add(new SecurityReference(SwaggerUtil.bladeTenant().getName(), authorizationScopes));
		}
		return securityReferenceList;
	}

	/**
	 * 配置安全策略
	 */
	private List<SecurityScheme> securitySchemas(SwaggerProperties swaggerProperties) {
		List<SwaggerProperties.AuthorizationApiKey> swaggerApiKeyList = swaggerProperties.getAuthorization().getAuthorizationApiKeyList();
		if (swaggerApiKeyList.size() == 0) {
			return Lists.newArrayList(SwaggerUtil.clientInfo(), SwaggerUtil.bladeAuth(), SwaggerUtil.bladeTenant());
		} else {
			List<SecurityScheme> securitySchemeList = new ArrayList<>();
			swaggerApiKeyList.forEach(authorizationApiKey -> securitySchemeList.add(new ApiKey(authorizationApiKey.getName(), authorizationApiKey.getKeyName(), authorizationApiKey.getPassAs())));
			return securitySchemeList;
		}
	}

	/**
	 * 配置基本信息
	 */
	private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
		return new ApiInfoBuilder()
			.title(swaggerProperties.getTitle())
			.description(swaggerProperties.getDescription())
			.license(swaggerProperties.getLicense())
			.licenseUrl(swaggerProperties.getLicenseUrl())
			.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
			.contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
			.version(swaggerProperties.getVersion())
			.build();
	}

}
