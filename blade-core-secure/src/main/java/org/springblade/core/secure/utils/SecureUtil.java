/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE;
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
package org.springblade.core.secure.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.date.DateField;
import org.springblade.core.tool.date.DateTime;
import org.springblade.core.tool.date.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.WebUtil;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Secure工具类
 */
public class SecureUtil {
	public static final String BLADE_USER_REQUEST_ATTR = "_BLADE_USER_REQUEST_ATTR_";

	public final static String header = "Authorization";
	public final static String bearer = "bearer";
	public final static String account = "account";
	public final static String userId = "userId";
	public final static String roleId = "roleId";
	public final static String userName = "userName";
	public final static String roleName = "roleName";
	private static String base64Security = DatatypeConverter.printBase64Binary("SpringBlade".getBytes());

	/**
	 * 获取用户信息
	 *
	 * @return
	 */
	public static BladeUser getUser() {
		HttpServletRequest request = WebUtil.getRequest();
		// 优先从 request 中获取
		BladeUser bladeUser = (BladeUser) request.getAttribute(BLADE_USER_REQUEST_ATTR);
		if (bladeUser == null) {
			bladeUser = getUser(request);
			if (bladeUser != null) {
				// 设置到 request 中
				request.setAttribute(BLADE_USER_REQUEST_ATTR, bladeUser);
			}
		}
		return bladeUser;
	}

	/**
	 * 获取用户信息
	 *
	 * @return
	 */
	public static BladeUser getUser(HttpServletRequest request) {
		Claims claims = getClaims(request);
		if (claims == null) {
			return new BladeUser();
		}
		Integer userId = Func.toInt(claims.get(SecureUtil.userId));
		String roleId = Func.toStr(claims.get(SecureUtil.roleId));
		String account = Func.toStr(claims.get(SecureUtil.account));
		String roleName = Func.toStr(claims.get(SecureUtil.roleName));
		BladeUser bladeUser = new BladeUser();
		bladeUser.setAccount(account);
		bladeUser.setUserId(userId);
		bladeUser.setRoleId(roleId);
		bladeUser.setRoleName(roleName);
		return bladeUser;
	}

	/**
	 * 获取Claims
	 *
	 * @return
	 */
	public static Claims getClaims(HttpServletRequest request) {
		String auth = request.getHeader(SecureUtil.header);
		if ((auth != null) && (auth.length() > 7)) {
			String HeadStr = auth.substring(0, 6).toLowerCase();
			if (HeadStr.compareTo(SecureUtil.bearer) == 0) {
				auth = auth.substring(7);
				return SecureUtil.parseJWT(auth);
			}
		}
		return null;
	}

	/**
	 * 获取请求头
	 *
	 * @return
	 */
	public static String getHeader() {
		return getHeader(WebUtil.getRequest());
	}

	/**
	 * 获取请求头
	 *
	 * @param request
	 * @return
	 */
	public static String getHeader(HttpServletRequest request) {
		return request.getHeader(header);
	}

	/**
	 * 解析jsonWebToken
	 *
	 * @param jsonWebToken
	 * @return
	 */
	public static Claims parseJWT(String jsonWebToken) {
		try {
			Claims claims = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
				.parseClaimsJws(jsonWebToken).getBody();
			return claims;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 创建jwt
	 *
	 * @param user     用户
	 * @param audience audience
	 * @param issuer   issuer
	 * @param isExpire isExpire
	 * @return
	 */
	public static String createJWT(Map<String, String> user, String audience, String issuer, boolean isExpire) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//生成签名密钥
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//添加构成JWT的类
		JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JsonWebToken")
			.setIssuer(issuer)
			.setAudience(audience)
			.signWith(signatureAlgorithm, signingKey);

		//设置JWT参数
		user.forEach(builder::claim);

		//添加Token过期时间
		if (isExpire) {
			long expMillis = nowMillis + getExpire();
			Date exp = new Date(expMillis);
			builder.setExpiration(exp).setNotBefore(now);
		}

		//生成JWT
		return builder.compact();
	}

	/**
	 * 获取过期时间(次日凌晨3点)
	 *
	 * @return
	 */
	public static long getExpire() {
		DateTime dateTime = DateUtil.endOfDay(new Date());
		DateTime offset = DateUtil.offset(dateTime, DateField.HOUR, 3);

		return offset.getTime() - System.currentTimeMillis();
	}
}
