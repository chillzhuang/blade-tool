/**
 * Copyright (c) 2018-2028, DreamLu 卢春梦 (qq596392912@gmail.com).
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
package org.springblade.core.tool.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * url处理工具类
 *
 * @author L.cm
 */
public class UrlUtil extends org.springframework.web.util.UriUtils {

	/**
	 * url 编码，同js decodeURIComponent
	 *
	 * @param source  url
	 * @param charset 字符集
	 * @return 编码后的url
	 */
	public static String encodeURL(String source, Charset charset) {
		return UrlUtil.encode(source, charset.name());
	}

	/**
	 * url 解码
	 *
	 * @param source  url
	 * @param charset 字符集
	 * @return 解码url
	 */
	public static String decodeURL(String source, Charset charset) {
		return UrlUtil.decode(source, charset.name());
	}

	/**
	 * 获取url路径
	 *
	 * @param uriStr 路径
	 * @return url路径
	 */
	public static String getPath(String uriStr) {
		URI uri;

		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException var3) {
			throw new RuntimeException(var3);
		}

		return uri.getPath();
	}

}
