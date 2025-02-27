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

import java.util.regex.Pattern;

/**
 * 脱敏类型枚举.
 *
 * @author BladeX
 */
@Getter
public enum SensitiveType {
	GLOBAL("全局", "(.{2}).*(.{2})", "$1****$2"),
	MOBILE("手机号", "(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
	EMAIL("电子邮箱", "(\\w{2})\\w+(@\\w+\\.\\w+)", "$1****$2"),
	ID_CARD("身份证号", "(\\d{4})\\d{10}(\\w{4})", "$1**********$2"),
	BANK_CARD("银行卡号", "(\\d{4})\\d+(\\d{4})", "$1****$2"),
	IP_ADDRESS("IP地址", "(\\d{1,3}\\.\\d{1,3})\\.\\d{1,3}\\.\\d{1,3}", "$1.***.***"),
	MAC_ADDRESS("MAC地址", "([0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}):[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}", "$1:****"),
	;

	private final String desc;
	private final String regex;
	private final String replacement;
	private final Pattern pattern;

	SensitiveType(String desc, String regex, String replacement) {
		this.desc = desc;
		this.regex = regex;
		this.replacement = replacement;
		this.pattern = Pattern.compile(regex);
	}
}
