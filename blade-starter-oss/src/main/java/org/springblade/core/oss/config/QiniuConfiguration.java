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

import org.springblade.core.oss.QiniuTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

/**
 * 七牛云对象存储配置类
 * 用于配置七牛云存储的客户端组件及其模板类
 * 仅在配置文件中指定 oss.name=qiniu 时生效
 *
 * @author Chill
 */
@AutoConfiguration(after = OssConfiguration.class)
@ConditionalOnClass({Auth.class, UploadManager.class, BucketManager.class})
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "oss.name", havingValue = "qiniu")
public class QiniuConfiguration {

	/**
	 * 配置七牛云存储配置对象
	 * 当容器中不存在 Configuration 类型的Bean时生效
	 * 使用自动区域配置
	 *
	 * @return Configuration 七牛云存储配置对象
	 */
	@Bean
	@ConditionalOnMissingBean(com.qiniu.storage.Configuration.class)
	public com.qiniu.storage.Configuration qnConfiguration() {
		return new com.qiniu.storage.Configuration(Region.autoRegion());
	}

	/**
	 * 配置七牛云认证对象
	 * 当容器中不存在 Auth 类型的Bean时生效
	 *
	 * @param ossProperties OSS配置属性
	 * @return Auth 七牛云认证对象
	 */
	@Bean
	@ConditionalOnMissingBean(Auth.class)
	public Auth auth(OssProperties ossProperties) {
		return Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey());
	}

	/**
	 * 配置七牛云上传管理器
	 * 需要容器中存在 Configuration 类型的Bean时生效
	 *
	 * @param cfg 七牛云存储配置对象
	 * @return UploadManager 七牛云上传管理器
	 */
	@Bean
	@ConditionalOnBean(com.qiniu.storage.Configuration.class)
	public UploadManager uploadManager(com.qiniu.storage.Configuration cfg) {
		return new UploadManager(cfg);
	}

	/**
	 * 配置七牛云存储空间管理器
	 * 需要容器中存在 Configuration 类型的Bean时生效
	 *
	 * @param ossProperties OSS配置属性
	 * @param cfg          七牛云存储配置对象
	 * @return BucketManager 七牛云存储空间管理器
	 */
	@Bean
	@ConditionalOnBean(com.qiniu.storage.Configuration.class)
	public BucketManager bucketManager(OssProperties ossProperties,
									   com.qiniu.storage.Configuration cfg) {
		return new BucketManager(Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey()), cfg);
	}

	/**
	 * 配置七牛云存储操作模板
	 * 需要容器中存在 Auth、UploadManager、BucketManager 的Bean，且不存在 QiniuTemplate 的Bean时生效
	 *
	 * @param ossRule       OSS规则配置
	 * @param ossProperties OSS配置属性
	 * @param auth         七牛云认证对象
	 * @param uploadManager 七牛云上传管理器
	 * @param bucketManager 七牛云存储空间管理器
	 * @return QiniuTemplate 七牛云存储操作模板
	 */
	@Bean
	@ConditionalOnBean({Auth.class, UploadManager.class, BucketManager.class})
	@ConditionalOnMissingBean(QiniuTemplate.class)
	public QiniuTemplate qiniuTemplate(OssRule ossRule,
									   OssProperties ossProperties,
									   Auth auth,
									   UploadManager uploadManager,
									   BucketManager bucketManager) {
		return new QiniuTemplate(auth, uploadManager, bucketManager, ossProperties, ossRule);
	}

}
