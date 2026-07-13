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
package org.springblade.core.swagger;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.swagger.annotation.ApiOrder;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.SpringAsmInfo;
import org.springframework.asm.Type;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * swagger配置
 *
 * @author Chill
 */
@Slf4j
@EnableSwagger
@Configuration
@AllArgsConstructor
@AutoConfigureBefore(SpringDocConfiguration.class)
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration {

	private static final String DEFAULT_BASE_PATH = "/**";
	private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String TOKEN_HEADER = "Blade-Auth";
	private static final String TENANT_HEADER = "Tenant-Id";
	private static final String REQUESTED_HEADER = "Blade-Requested-With";

	/**
	 * 接口排序扩展键。swagger-core 的 Operation#addExtension 仅接受以 x- 开头的键，其余静默丢弃，故固定该前缀
	 */
	private static final String ORDER_EXTENSION = "x-order";

	/**
	 * 引入Swagger配置类
	 */
	private final SwaggerProperties swaggerProperties;

	/**
	 * 初始化OpenAPI对象
	 */
	@Bean
	public OpenAPI openApi() {
		// 初始化OpenAPI对象，并设置API的基本信息、安全策略、联系人信息、许可信息以及外部文档链接
		return new OpenAPI()
			.components(new Components()
				// 添加安全策略，配置API密钥（Token）和鉴权机制
				.addSecuritySchemes(TOKEN_HEADER,
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.HEADER)
						.scheme("bearer")
						.bearerFormat("JWT")
						.name(TOKEN_HEADER)
				)
				// 添加安全策略，配置API密钥（Authorization）和鉴权机制
				.addSecuritySchemes(AUTHORIZATION_HEADER,
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.HEADER)
						.name(AUTHORIZATION_HEADER)
				)
				// 添加安全策略，配置租户ID（Tenant-Id）和鉴权机制
				.addSecuritySchemes(TENANT_HEADER,
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.HEADER)
						.name(TENANT_HEADER)
				)
				// 添加安全策略，配置请求头（Blade-Requested-With）和鉴权机制
				.addSecuritySchemes(REQUESTED_HEADER,
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.HEADER)
						.name(REQUESTED_HEADER)
				)
			)
			// 设置API文档的基本信息，包括标题、描述、联系方式和许可信息
			.info(new Info()
				.title(swaggerProperties.getTitle())
				.description(swaggerProperties.getDescription())
				.termsOfService(swaggerProperties.getTermsOfServiceUrl())
				.contact(new Contact()
					.name(swaggerProperties.getContact().getName())
					.email(swaggerProperties.getContact().getEmail())
					.url(swaggerProperties.getContact().getUrl())
				)
				.license(new License()
					.name(swaggerProperties.getLicense())
					.url(swaggerProperties.getLicenseUrl())
				)
				.version(swaggerProperties.getVersion())
			);
	}

	/**
	 * 每个控制器类的「方法签名 → 源码声明行号」缓存，避免重复解析字节码
	 */
	private final Map<Class<?>, Map<String, Integer>> methodLineCache = new ConcurrentHashMap<>();

	/**
	 * 「tag 名 → 类级 {@link ApiOrder} 值」映射，供重排分组顺序；operation 定制器填充、openApi 定制器消费
	 */
	private final Map<String, Integer> tagOrders = new ConcurrentHashMap<>();

	/**
	 * 将 {@link ApiOrder} 解析出的排序值写入 operation 的 x-order 扩展，并记录类级序号供分组重排
	 *
	 * <p>
	 * 用 GlobalOperationCustomizer 而非 OperationCustomizer：后者不作用于 GroupedOpenApi 分组文档，
	 * 而 BladeX 默认启用分组，普通实现会在分组文档里丢失 x-order、导致重排落空
	 * </p>
	 */
	@Bean
	public GlobalOperationCustomizer apiOrderOperationCustomizer() {
		return (operation, handlerMethod) -> {
			Integer order = resolveApiOrder(handlerMethod);
			if (order != null) {
				operation.addExtension(ORDER_EXTENSION, order);
			}
			recordTagOrder(operation, handlerMethod);
			return operation;
		};
	}

	/**
	 * 记录类级 {@link ApiOrder} 值作为该控制器所有 tag 的分组序号
	 */
	private void recordTagOrder(Operation operation, HandlerMethod handlerMethod) {
		ApiOrder classOrder = handlerMethod.getBeanType().getAnnotation(ApiOrder.class);
		if (classOrder == null || operation.getTags() == null) {
			return;
		}
		operation.getTags().forEach(tag -> tagOrders.putIfAbsent(tag, classOrder.value()));
	}

	/**
	 * 解析接口序号：方法级 {@link ApiOrder} 显式值优先；否则类上标了 {@link ApiOrder} 时取方法的源码声明行号（书写顺序）；都没有则不参与排序
	 */
	private Integer resolveApiOrder(HandlerMethod handlerMethod) {
		ApiOrder methodOrder = handlerMethod.getMethodAnnotation(ApiOrder.class);
		if (methodOrder != null) {
			return methodOrder.value();
		}
		Class<?> beanType = handlerMethod.getBeanType();
		if (beanType.isAnnotationPresent(ApiOrder.class)) {
			Method method = handlerMethod.getMethod();
			String signature = method.getName() + Type.getMethodDescriptor(method);
			return methodLineCache.computeIfAbsent(beanType, this::readMethodLines).getOrDefault(signature, Integer.MAX_VALUE);
		}
		return null;
	}

	/**
	 * 读取控制器字节码的行号表，得到各方法的源码声明行号
	 *
	 * <p>
	 * 反射的 getDeclaredMethods 不保证方法顺序，无法据其还原书写顺序；行号表由标准构建默认输出，是可靠的声明顺序来源。
	 * 复用 Spring 内置 ASM，无需额外依赖
	 * </p>
	 */
	private Map<String, Integer> readMethodLines(Class<?> beanType) {
		Map<String, Integer> lines = new HashMap<>();
		try (InputStream input = beanType.getResourceAsStream(ClassUtils.getClassFileName(beanType))) {
			if (input == null) {
				return lines;
			}
			new ClassReader(input).accept(new ClassVisitor(SpringAsmInfo.ASM_VERSION) {
				@Override
				public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
					String key = name + descriptor;
					return new MethodVisitor(SpringAsmInfo.ASM_VERSION) {
						@Override
						public void visitLineNumber(int line, Label start) {
							lines.merge(key, line, Math::min);
						}
					};
				}
			}, ClassReader.SKIP_FRAMES);
		} catch (IOException e) {
			log.warn("读取控制器 {} 字节码行号失败,该类接口回落到默认顺序", beanType.getName(), e);
		}
		return lines;
	}

	/**
	 * 为所有接口补全鉴权声明，按 x-order 重排 paths（组内接口序），并按类级 {@link ApiOrder} 重排 tags（分组序）
	 *
	 * <p>
	 * 重排依赖 Paths 的 LinkedHashMap 保序特性，重建后按插入顺序序列化；值越小越靠前、未标注沉底。
	 * 分组顺序由 swagger-ui 按顶层 tags 数组序渲染，须同时不设 springdoc.swagger-ui.tags-sorter；
	 * 并须保持 springdoc.writer-with-order-by-keys 为默认 false，否则 paths 输出会被强制按 key 字母序覆盖
	 * </p>
	 */
	@Bean
	@ConditionalOnMissingBean
	public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
		return openApi -> {
			Paths paths = openApi.getPaths();
			if (paths == null || paths.isEmpty()) {
				return;
			}
			paths.forEach((path, pathItem) -> pathItem.readOperations().forEach(operation ->
				operation.addSecurityItem(new SecurityRequirement()
					.addList(AUTHORIZATION_HEADER)
					.addList(TOKEN_HEADER)
					.addList(TENANT_HEADER)
					.addList(REQUESTED_HEADER))));
			Paths ordered = new Paths();
			paths.entrySet().stream()
				.sorted(Comparator.<Map.Entry<String, PathItem>>comparingInt(entry -> resolveOrder(entry.getValue()))
					.thenComparing(Map.Entry::getKey))
				.forEach(entry -> ordered.addPathItem(entry.getKey(), entry.getValue()));
			openApi.setPaths(ordered);
			List<Tag> tags = openApi.getTags();
			if (tags != null && !tags.isEmpty()) {
				tags.sort(Comparator.comparingInt((Tag tag) -> tagOrders.getOrDefault(tag.getName(), Integer.MAX_VALUE))
					.thenComparing(Tag::getName));
			}
		};
	}

	/**
	 * 取该路径下所有 operation 的最小 x-order 作为路径序值，同路径多方法时以最靠前者为准
	 */
	private int resolveOrder(PathItem pathItem) {
		return pathItem.readOperations().stream()
			.filter(operation -> operation.getExtensions() != null)
			.map(operation -> operation.getExtensions().get(ORDER_EXTENSION))
			.filter(Objects::nonNull)
			.mapToInt(value -> ((Number) value).intValue())
			.min()
			.orElse(Integer.MAX_VALUE);
	}

	/**
	 * 初始化GroupedOpenApi对象
	 */
	@Bean
	@ConditionalOnMissingBean
	public GroupedOpenApi defaultApi() {
		// 如果Swagger配置中的基本路径和排除路径为空，则设置默认的基本路径和排除路径
		if (CollectionUtil.isEmpty(swaggerProperties.getBasePath())) {
			swaggerProperties.getBasePath().add(DEFAULT_BASE_PATH);
		}
		if (CollectionUtil.isEmpty(swaggerProperties.getExcludePath())) {
			swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
		}
		// 获取Swagger配置中的基本路径、排除路径、基本包路径和排除包路径
		List<String> basePath = swaggerProperties.getBasePath();
		List<String> excludePath = swaggerProperties.getExcludePath();
		List<String> basePackages = swaggerProperties.getBasePackages();
		List<String> excludePackages = swaggerProperties.getExcludePackages();
		// 创建并返回GroupedOpenApi对象
		return GroupedOpenApi.builder()
			.group("default")
			.pathsToMatch(basePath.toArray(new String[0]))
			.pathsToExclude(excludePath.toArray(new String[0]))
			.packagesToScan(basePackages.toArray(new String[0]))
			.packagesToExclude(excludePackages.toArray(new String[0]))
			.build();
	}

}
