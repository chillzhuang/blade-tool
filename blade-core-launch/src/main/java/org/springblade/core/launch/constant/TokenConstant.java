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
package org.springblade.core.launch.constant;

/**
 * Token配置常量.
 *
 * @author Chill
 */
public interface TokenConstant {

	String AVATAR = "avatar";
	String HEADER = "blade-auth";
	String BEARER = "bearer";
	String CRYPTO = "crypto";
	String ACCESS_TOKEN = "access_token";
	String REFRESH_TOKEN = "refresh_token";
	String TOKEN_TYPE = "token_type";
	String EXPIRES_IN = "expires_in";
	String ACCOUNT = "account";
	String USER_ID = "user_id";
	String ROLE_ID = "role_id";
	String DEPT_ID = "dept_id";
	String USER_NAME = "user_name";
	String ROLE_NAME = "role_name";
	String TENANT_ID = "tenant_id";
	String OAUTH_ID = "oauth_id";
	String CLIENT_ID = "client_id";
	String LICENSE = "license";
	String LICENSE_NAME = "powered by bladex";
	String DEFAULT_AVATAR = "https://bladex.cn/images/logo-small.png";
	Integer AUTH_LENGTH = 7;

	/**
	 * token签名
	 */
	String SIGN_KEY = "bladexisapowerfulmicroservicearchitectureupgradedandoptimizedfromacommercialproject";
	/**
	 * key安全长度，具体见：https://tools.ietf.org/html/rfc7518#section-3.2
	 */
	int SIGN_KEY_LENGTH = 32;

}
