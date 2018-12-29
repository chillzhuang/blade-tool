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
package org.springblade.core.boot.file;

import org.springblade.core.tool.constant.SystemConstant;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 上传文件封装
 * @author Chill
 */
public class BladeFile {
	/**
	 * 上传文件在附件表中的id
	 */
	private Object fileId;

	/**
	 * 上传文件
	 */
	private MultipartFile file;

	/**
	 * 上传分类文件夹
	 */
	private String dir;

	/**
	 * 上传物理路径
	 */
	private String uploadPath;

	/**
	 * 上传虚拟路径
	 */
	private String uploadVirtualPath;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 真实文件名
	 */
	private String originalFileName;

	public BladeFile() {

	}

	public BladeFile(MultipartFile file, String dir) {
		this.dir = dir;
		this.file = file;
		this.fileName = file.getName();
		this.originalFileName = file.getOriginalFilename();
		this.uploadPath = BladeFileUtil.formatUrl(File.separator + SystemConstant.me().getUploadRealPath() + File.separator + dir + File.separator + DateUtil.format(new Date(), "yyyyMMdd") + File.separator + this.originalFileName);
		this.uploadVirtualPath = BladeFileUtil.formatUrl(SystemConstant.me().getUploadCtxPath().replace(SystemConstant.me().getContextPath(), "") + File.separator + dir + File.separator + DateUtil.format(new Date(), "yyyyMMdd") + File.separator + this.originalFileName);
	}

	public BladeFile(MultipartFile file, String dir, String uploadPath, String uploadVirtualPath) {
		this(file, dir);
		if (null != uploadPath) {
			this.uploadPath = BladeFileUtil.formatUrl(uploadPath);
			this.uploadVirtualPath = BladeFileUtil.formatUrl(uploadVirtualPath);
		}
	}

	/**
	 * 图片上传
	 */
	public void transfer() {
		transfer(SystemConstant.me().isCompress());
	}

	/**
	 * 图片上传
	 *
	 * @param compress 是否压缩
	 */
	public void transfer(boolean compress) {
		IFileProxy fileFactory = FileProxyManager.me().getDefaultFileProxyFactory();
		this.transfer(fileFactory, compress);
	}

	/**
	 * 图片上传
	 *
	 * @param fileFactory 文件上传工厂类
	 * @param compress    是否压缩
	 */
	public void transfer(IFileProxy fileFactory, boolean compress) {
		try {
			File file = new File(uploadPath);

			if (null != fileFactory) {
				String[] path = fileFactory.path(file, dir);
				this.uploadPath = path[0];
				this.uploadVirtualPath = path[1];
				file = fileFactory.rename(file, path[0]);
			}

			File pfile = file.getParentFile();
			if (!pfile.exists()) {
				pfile.mkdirs();
			}

			this.file.transferTo(file);

			if (compress) {
				fileFactory.compress(this.uploadPath);
			}

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	public String getUploadVirtualPath() {
		return uploadVirtualPath;
	}

	public void setUploadVirtualPath(String uploadVirtualPath) {
		this.uploadVirtualPath = uploadVirtualPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

}
