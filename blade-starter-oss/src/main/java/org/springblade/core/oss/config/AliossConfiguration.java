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
package org.springblade.core.oss.config;

import org.springblade.core.oss.AliossTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;

/**
 * 阿里云OSS对象存储配置类
 * 用于配置阿里云OSS客户端及其模板类
 * 仅在配置文件中指定 oss.name=alioss 时生效
 *
 * @author Chill
 */
@AutoConfiguration(after = OssConfiguration.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass({OSSClient.class})
@ConditionalOnProperty(value = "oss.name", havingValue = "alioss")
public class AliossConfiguration {

	/**
	 * 配置阿里云OSS客户端
	 * 当容器中不存在 OSSClient 类型的Bean时生效
	 * 配置包括：
	 * - 最大HTTP连接数
	 * - Socket传输超时时间
	 * - 建立连接超时时间
	 * - 连接池获取连接超时时间
	 * - 连接空闲超时时间
	 * - 请求失败重试次数
	 *
	 * @param ossProperties OSS配置属性
	 * @return OSSClient 阿里云OSS客户端实例
	 */
	@Bean
	@ConditionalOnMissingBean(OSSClient.class)
	public OSSClient ossClient(OssProperties ossProperties) {
		// 创建ClientConfiguration。ClientConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
		ClientConfiguration conf = new ClientConfiguration();
		// 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
		conf.setMaxConnections(1024);
		// 设置Socket层传输数据的超时时间，默认为50000毫秒。
		conf.setSocketTimeout(50000);
		// 设置建立连接的超时时间，默认为50000毫秒。
		conf.setConnectionTimeout(50000);
		// 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
		conf.setConnectionRequestTimeout(1000);
		// 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
		conf.setIdleConnectionTime(60000);
		// 设置失败请求重试次数，默认为3次。
		conf.setMaxErrorRetry(5);
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKey(), ossProperties.getSecretKey());
		return new OSSClient(ossProperties.getEndpoint(), credentialsProvider, conf);
	}

	/**
	 * 配置阿里云OSS操作模板
	 * 需要容器中存在 OSSClient 的Bean，且不存在 AliossTemplate 的Bean时生效
	 *
	 * @param ossRule      OSS规则配置
	 * @param ossProperties OSS配置属性
	 * @param ossClient    阿里云OSS客户端
	 * @return AliossTemplate 阿里云OSS操作模板
	 */
	@Bean
	@ConditionalOnBean({OSSClient.class})
	@ConditionalOnMissingBean(AliossTemplate.class)
	public AliossTemplate aliossTemplate(OssRule ossRule,
										 OssProperties ossProperties,
										 OSSClient ossClient) {
		return new AliossTemplate(ossClient, ossProperties, ossRule);
	}

}
