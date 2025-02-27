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
package org.springblade.core.oss;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.oss.model.OssFile;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * QiniuTemplate
 *
 * @author Chill
 */
@AllArgsConstructor
public class QiniuTemplate implements OssTemplate {
	/**
	 * 七牛云认证对象
	 */
	private final Auth auth;

	/**
	 * 七牛云上传管理对象
	 */
	private final UploadManager uploadManager;

	/**
	 * 七牛云存储空间管理对象
	 */
	private final BucketManager bucketManager;

	/**
	 * OSS配置属性
	 */
	private final OssProperties ossProperties;

	/**
	 * OSS规则对象
	 */
	private final OssRule ossRule;

	@Override
	@SneakyThrows
	public void makeBucket(String bucketName) {
		if (!CollectionUtil.contains(bucketManager.buckets(), getBucketName(bucketName))) {
			bucketManager.createBucket(getBucketName(bucketName), Zone.autoZone().getRegion());
		}
	}

	@Override
	@SneakyThrows
	public void removeBucket(String bucketName) {

	}

	@Override
	@SneakyThrows
	public boolean bucketExists(String bucketName) {
		return CollectionUtil.contains(bucketManager.buckets(), getBucketName(bucketName));
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName) {
		bucketManager.copy(getBucketName(bucketName), fileName, getBucketName(destBucketName), fileName);
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
		bucketManager.copy(getBucketName(bucketName), fileName, getBucketName(destBucketName), destFileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String fileName) {
		return statFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String bucketName, String fileName) {
		FileInfo stat = bucketManager.stat(getBucketName(bucketName), fileName);
		OssFile ossFile = new OssFile();
		ossFile.setName(Func.isEmpty(stat.key) ? fileName : stat.key);
		ossFile.setLink(fileLink(ossFile.getName()));
		ossFile.setHash(stat.hash);
		ossFile.setLength(stat.fsize);
		ossFile.setPutTime(new Date(stat.putTime / 10000));
		ossFile.setContentType(stat.mimeType);
		return ossFile;
	}

	@Override
	@SneakyThrows
	public String filePath(String fileName) {
		return getBucketName().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String filePath(String bucketName, String fileName) {
		return getBucketName(bucketName).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String fileName) {
		return getEndpoint().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String bucketName, String fileName) {
		return getEndpoint().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(MultipartFile file) {
		return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String fileName, MultipartFile file) {
		return putFile(ossProperties.getBucketName(), fileName, file);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String bucketName, String fileName, MultipartFile file) {
		return putFile(bucketName, fileName, file.getInputStream());
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String fileName, InputStream stream) {
		return putFile(ossProperties.getBucketName(), fileName, stream);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String bucketName, String fileName, InputStream stream) {
		return put(bucketName, stream, fileName, false);
	}

	/**
	 * 上传文件到七牛云
	 *
	 * @param bucketName 存储空间名称
	 * @param stream     输入流
	 * @param key       文件名
	 * @param cover     是否覆盖上传
	 * @return BladeFile 上传文件信息
	 */
	@SneakyThrows
	public BladeFile put(String bucketName, InputStream stream, String key, boolean cover) {
		makeBucket(bucketName);
		String originalName = key;
		key = getFileName(key);
		// 覆盖上传
		if (cover) {
			uploadManager.put(stream, key, getUploadToken(bucketName, key), null, null);
		} else {
			Response response = uploadManager.put(stream, key, getUploadToken(bucketName), null, null);
			int retry = 0;
			int retryCount = 5;
			while (response.needRetry() && retry < retryCount) {
				response = uploadManager.put(stream, key, getUploadToken(bucketName), null, null);
				retry++;
			}
		}
		BladeFile file = new BladeFile();
		file.setOriginalName(originalName);
		file.setName(key);
		file.setDomain(getOssHost());
		file.setLink(fileLink(bucketName, key));
		return file;
	}

	@Override
	@SneakyThrows
	public void removeFile(String fileName) {
		bucketManager.delete(getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFile(String bucketName, String fileName) {
		bucketManager.delete(getBucketName(bucketName), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFiles(List<String> fileNames) {
		fileNames.forEach(this::removeFile);
	}

	@Override
	@SneakyThrows
	public void removeFiles(String bucketName, List<String> fileNames) {
		fileNames.forEach(fileName -> removeFile(getBucketName(bucketName), fileName));
	}

	/**
	 * 获取默认存储空间名称
	 *
	 * @return String 存储空间名称
	 */
	private String getBucketName() {
		return getBucketName(ossProperties.getBucketName());
	}

	/**
	 * 根据规则生成存储空间名称
	 *
	 * @param bucketName 存储空间名称
	 * @return String 处理后的存储空间名称
	 */
	private String getBucketName(String bucketName) {
		return ossRule.bucketName(bucketName);
	}

	/**
	 * 根据规则生成文件名称
	 *
	 * @param originalFilename 原始文件名
	 * @return String 处理后的文件名
	 */
	private String getFileName(String originalFilename) {
		return ossRule.fileName(originalFilename);
	}

	/**
	 * 获取普通上传凭证
	 *
	 * @param bucketName 存储空间名称
	 * @return String 上传凭证
	 */
	public String getUploadToken(String bucketName) {
		return auth.uploadToken(getBucketName(bucketName));
	}

	/**
	 * 获取覆盖上传凭证
	 *
	 * @param bucketName 存储空间名称
	 * @param key       文件名
	 * @return String 上传凭证
	 */
	private String getUploadToken(String bucketName, String key) {
		return auth.uploadToken(getBucketName(bucketName), key);
	}

	/**
	 * 获取七牛云访问域名
	 *
	 * @return String 访问域名
	 */
	public String getOssHost() {
		return getEndpoint();
	}

	/**
	 * 获取七牛云服务的Endpoint
	 *
	 * @return String 七牛云Endpoint
	 */
	public String getEndpoint() {
		if (StringUtil.isBlank(ossProperties.getTransformEndpoint())) {
			return ossProperties.getEndpoint();
		}
		return ossProperties.getTransformEndpoint();
	}

}
