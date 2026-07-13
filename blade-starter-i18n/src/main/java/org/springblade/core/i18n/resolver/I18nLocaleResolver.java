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
package org.springblade.core.i18n.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springblade.core.i18n.props.I18nProperties;
import org.springblade.core.i18n.utils.LocaleParseUtil;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * I18nLocale解析器
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class I18nLocaleResolver implements LocaleResolver {

	private final I18nProperties properties;
	private final Locale defaultLocale;
	private final Set<String> supportedLocales;

	public I18nLocaleResolver(I18nProperties properties) {
		this.properties = properties;
		this.defaultLocale = LocaleParseUtil.parseLocale(properties.getDefaultLocale());
		this.supportedLocales = properties.getSupportLocales().stream()
			.map(String::toLowerCase)
			.collect(Collectors.toSet());
	}

	@NonNull
	@Override
	public Locale resolveLocale(@NonNull HttpServletRequest request) {
		return LocaleParseUtil.resolveFromRequest(
			request,
			properties.getHeaderName(),
			properties.getParamName(),
			supportedLocales,
			defaultLocale
		);
	}

	@Override
	public void setLocale(@NonNull HttpServletRequest request, HttpServletResponse response, Locale locale) {
		if (response != null && locale != null) {
			// 检查是否支持该locale
			boolean isSupported = supportedLocales.isEmpty() ||
				supportedLocales.contains(locale.toString().toLowerCase()) ||
				supportedLocales.contains(locale.getLanguage().toLowerCase());

			if (isSupported) {
				// 可选：设置响应头提示客户端当前的 Locale
				response.setHeader(properties.getHeaderName(), locale.toLanguageTag());
			}
		}
	}
}
