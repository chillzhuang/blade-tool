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
package org.springblade.core.social.utils;

import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.*;
import org.springblade.core.social.props.SocialProperties;

import java.util.Objects;

/**
 * SocialUtil
 *
 * @author Chill
 */
public class SocialUtil {

	/**
	 * 根据具体的授权来源，获取授权请求工具类
	 *
	 * @param source 授权来源
	 * @return AuthRequest
	 */
	public static AuthRequest getAuthRequest(String source, SocialProperties socialProperties) {
		AuthDefaultSource authSource = Objects.requireNonNull(AuthDefaultSource.valueOf(source.toUpperCase()));
		AuthConfig authConfig = socialProperties.getOauth().get(authSource);
		if (authConfig == null) {
			throw new AuthException("未获取到有效的Auth配置");
		}
		AuthRequest authRequest = null;
		switch (authSource) {
			case GITHUB:
				authRequest = new AuthGithubRequest(authConfig);
				break;
			case GITEE:
				authRequest = new AuthGiteeRequest(authConfig);
				break;
			case OSCHINA:
				authRequest = new AuthOschinaRequest(authConfig);
				break;
			case QQ:
				authRequest = new AuthQqRequest(authConfig);
				break;
			case WECHAT_OPEN:
				authRequest = new AuthWeChatOpenRequest(authConfig);
				break;
			case WECHAT_ENTERPRISE:
				authRequest = new AuthWeChatEnterpriseRequest(authConfig);
				break;
			case WECHAT_MP:
				authRequest = new AuthWeChatMpRequest(authConfig);
				break;
			case DINGTALK:
				authRequest = new AuthDingTalkRequest(authConfig);
				break;
			case ALIPAY:
				// 支付宝在创建回调地址时，不允许使用localhost或者127.0.0.1，所以这儿的回调地址使用的局域网内的ip
				authRequest = new AuthAlipayRequest(authConfig);
				break;
			case BAIDU:
				authRequest = new AuthBaiduRequest(authConfig);
				break;
			case WEIBO:
				authRequest = new AuthWeiboRequest(authConfig);
				break;
			case CODING:
				authRequest = new AuthCodingRequest(authConfig);
				break;
			case CSDN:
				authRequest = new AuthCsdnRequest(authConfig);
				break;
			case TAOBAO:
				authRequest = new AuthTaobaoRequest(authConfig);
				break;
			case GOOGLE:
				authRequest = new AuthGoogleRequest(authConfig);
				break;
			case FACEBOOK:
				authRequest = new AuthFacebookRequest(authConfig);
				break;
			case DOUYIN:
				authRequest = new AuthDouyinRequest(authConfig);
				break;
			case LINKEDIN:
				authRequest = new AuthLinkedinRequest(authConfig);
				break;
			case MICROSOFT:
				authRequest = new AuthMicrosoftRequest(authConfig);
				break;
			case MI:
				authRequest = new AuthMiRequest(authConfig);
				break;
			case TOUTIAO:
				authRequest = new AuthToutiaoRequest(authConfig);
				break;
			case TEAMBITION:
				authRequest = new AuthTeambitionRequest(authConfig);
				break;
			case PINTEREST:
				authRequest = new AuthPinterestRequest(authConfig);
				break;
			case RENREN:
				authRequest = new AuthRenrenRequest(authConfig);
				break;
			case STACK_OVERFLOW:
				authRequest = new AuthStackOverflowRequest(authConfig);
				break;
			case HUAWEI:
				authRequest = new AuthHuaweiRequest(authConfig);
				break;
			case KUJIALE:
				authRequest = new AuthKujialeRequest(authConfig);
				break;
			case GITLAB:
				authRequest = new AuthGitlabRequest(authConfig);
				break;
			case MEITUAN:
				authRequest = new AuthMeituanRequest(authConfig);
				break;
			case ELEME:
				authRequest = new AuthElemeRequest(authConfig);
				break;
			case TWITTER:
				authRequest = new AuthTwitterRequest(authConfig);
				break;
			default:
				break;
		}
		if (null == authRequest) {
			throw new AuthException("未获取到有效的Auth配置");
		}
		return authRequest;
	}

}
