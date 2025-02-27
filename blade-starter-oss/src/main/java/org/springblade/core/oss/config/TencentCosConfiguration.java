/**
 * Copyright (c) 2018-2099, yangkai.shen.
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
package org.springblade.core.oss.config;

import org.springblade.core.oss.TencentCosTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 腾讯云 COS 自动装配
 * </p>
 *
 * @author yangkai.shen
 */
@AllArgsConstructor
@AutoConfiguration(after = OssConfiguration.class)
@ConditionalOnClass({COSClient.class})
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "oss.name", havingValue = "tencentcos")
public class TencentCosConfiguration {

	/**
	 * OSS配置属性
	 */
	private final OssProperties ossProperties;

	/**
	 * OSS规则对象
	 */
	private final OssRule ossRule;

	/**
	 * 配置腾讯云COS客户端
	 * 当容器中不存在 COSClient 类型的Bean时生效
	 * 配置包括：
	 * - 用户身份信息（secretId, secretKey）
	 * - 存储桶区域设置
	 * - 最大HTTP连接数
	 * - Socket传输超时时间
	 * - 建立连接超时时间
	 * - 连接池获取连接超时时间
	 *
	 * @return COSClient 腾讯云COS客户端实例
	 */
	@Bean
	@ConditionalOnMissingBean(COSClient.class)
	public COSClient ossClient() {
		// 初始化用户身份信息（secretId, secretKey）
		COSCredentials credentials = new BasicCOSCredentials(ossProperties.getAccessKey(), ossProperties.getSecretKey());
		// 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
		Region region = new Region(ossProperties.getRegion());
		// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
		ClientConfig clientConfig = new ClientConfig(region);
		// 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
		clientConfig.setMaxConnectionsCount(1024);
		// 设置Socket层传输数据的超时时间，默认为50000毫秒。
		clientConfig.setSocketTimeout(50000);
		// 设置建立连接的超时时间，默认为50000毫秒。
		clientConfig.setConnectionTimeout(50000);
		// 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
		clientConfig.setConnectionRequestTimeout(1000);
		return new COSClient(credentials, clientConfig);
	}

	/**
	 * 配置腾讯云COS操作模板
	 * 需要容器中存在 COSClient 的Bean，且不存在 TencentCosTemplate 的Bean时生效
	 *
	 * @param cosClient 腾讯云COS客户端
	 * @return TencentCosTemplate 腾讯云COS操作模板
	 */
	@Bean
	@ConditionalOnBean({COSClient.class})
	@ConditionalOnMissingBean(TencentCosTemplate.class)
	public TencentCosTemplate tencentCosTemplate(COSClient cosClient) {
		return new TencentCosTemplate(cosClient, ossProperties, ossRule);
	}

}
