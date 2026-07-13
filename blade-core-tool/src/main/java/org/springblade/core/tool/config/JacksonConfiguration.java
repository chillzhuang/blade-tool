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
package org.springblade.core.tool.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springblade.core.tool.jackson.BladeJacksonProperties;
import org.springblade.core.tool.jackson.BladeJavaTimeModule;
import org.springblade.core.tool.jackson.BladeViewAnnotationIntrospector;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson2.autoconfigure.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Jackson配置类
 *
 * @author Chill
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@EnableConfigurationProperties(BladeJacksonProperties.class)
@SuppressWarnings({"deprecation", "removal"})
public class JacksonConfiguration {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer bladeJacksonCustomizer() {
		return builder -> {
			// 默认视图包含 / 兼容宽松的读取特性 / 允许空字符串反序列化为 null 对象
			builder.featuresToEnable(
				MapperFeature.DEFAULT_VIEW_INCLUSION,
				JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,
				JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
				JsonParser.Feature.ALLOW_SINGLE_QUOTES,
				DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			// 日期不序列化为时间戳 / 空对象不报错 / 未知属性不报错
			builder.featuresToDisable(
				SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
				SerializationFeature.FAIL_ON_EMPTY_BEANS,
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			builder.locale(Locale.CHINA);
			builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
			builder.dateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATETIME, Locale.CHINA));
			builder.modulesToInstall(new BladeJavaTimeModule());
			builder.findModulesViaServiceLoader(true);
			// 视图注解内省器，让 Jackson 识别 @BladeView
			builder.annotationIntrospector(introspector -> new BladeViewAnnotationIntrospector());
		};
	}

}
