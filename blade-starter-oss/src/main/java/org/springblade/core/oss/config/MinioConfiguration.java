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

import org.springblade.core.oss.MinioTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.minio.MinioClient;
import lombok.SneakyThrows;

/**
 * MinIO对象存储配置类
 * 用于配置MinIO客户端及其模板类
 * 仅在配置文件中指定 oss.name=minio 时生效
 *
 * @author Chill
 */
@AutoConfiguration(after = OssConfiguration.class)
@ConditionalOnClass({MinioClient.class})
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "oss.name", havingValue = "minio")
public class MinioConfiguration {

	/**
	 * 配置MinIO客户端
	 * 当容器中不存在 MinioClient 类型的Bean时生效
	 *
	 * @param ossProperties OSS配置属性
	 * @return MinioClient MinIO客户端实例
	 */
	@Bean
	@SneakyThrows
	@ConditionalOnMissingBean(MinioClient.class)
	public MinioClient minioClient(OssProperties ossProperties) {
		return MinioClient.builder()
			.endpoint(ossProperties.getEndpoint())
			.credentials(ossProperties.getAccessKey(), ossProperties.getSecretKey())
			.build();
	}

	/**
	 * 配置MinIO操作模板
	 * 需要容器中存在 MinioClient 的Bean，且不存在 MinioTemplate 的Bean时生效
	 *
	 * @param ossRule      OSS规则配置
	 * @param ossProperties OSS配置属性
	 * @param minioClient  MinIO客户端
	 * @return MinioTemplate MinIO操作模板
	 */
	@Bean
	@ConditionalOnBean({MinioClient.class})
	@ConditionalOnMissingBean(MinioTemplate.class)
	public MinioTemplate minioTemplate(OssRule ossRule,
									   OssProperties ossProperties,
									   MinioClient minioClient) {
		return new MinioTemplate(minioClient, ossRule, ossProperties);
	}

}
