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
package org.springblade.core.tool.sensitive;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 敏感词分组枚举.
 *
 * @author BladeX
 */
@Getter
public enum SensitiveWord {

	// 安全敏感词组
	SECURE(Arrays.asList(
		// 认证信息类
		"password", "pwd", "token", "secret", "bearer", "key",
		// API相关
		"api_key", "access_token", "refresh_token", "auth_token",
		// 加密相关
		"private_key", "public_key", "salt", "hash",
		// 安全相关
		"security", "certificate", "credentials",
		// 数据库相关
		"connection_string", "jdbc", "database_url"
	)),

	// 身份验证相关敏感词
	AUTHENTICATION(Arrays.asList(
		"otp", "verification_code", "auth_code", "mfa_token"
	));

	private final List<String> words;

	SensitiveWord(List<String> words) {
		this.words = Collections.unmodifiableList(words);
	}
}
