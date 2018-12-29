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
package org.springblade.core.boot.ctrl;

import org.springblade.core.boot.file.BladeFile;
import org.springblade.core.boot.file.BladeFileUtil;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Blade控制器封装类
 *
 * @author smallchill
 */
public class BladeController {

	/**
	 * ============================     BINDER    =================================================
	 */

	/**
	 * initBinder
	 *
	 * @param binder binder
	 */
	@InitBinder
	protected void initBinder(ServletRequestDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	/**
	 * ============================     REQUEST    =================================================
	 */

	@Autowired
	private HttpServletRequest request;

	/**
	 * 获取request
	 *
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * 获取当前用户
	 *
	 * @return BladeUser
	 */
	public BladeUser getUser() {
		return SecureUtil.getUser();
	}

	/** ============================     API_RESULT    =================================================  */

	/**
	 * 返回ApiResult
	 *
	 * @param data 数据
	 * @param <T>  T 泛型标记
	 * @return R
	 */
	public <T> R<T> data(T data) {
		return R.data(data);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param data 数据
	 * @param msg  消息
	 * @param <T>  T 泛型标记
	 * @return R
	 */
	public <T> R<T> data(T data, String msg) {
		return R.data(data, msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param data 数据
	 * @param msg  消息
	 * @param code 状态码
	 * @param <T>  T 泛型标记
	 * @return R
	 */
	public <T> R<T> data(T data, String msg, int code) {
		return R.data(code, data, msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param msg 消息
	 * @return R
	 */
	public R success(String msg) {
		return R.success(msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param msg 消息
	 * @return R
	 */
	public R failure(String msg) {
		return R.failure(msg);
	}

	/**
	 * 返回ApiResult
	 *
	 * @param flag 是否成功
	 * @return R
	 */
	public R status(boolean flag) {
		return R.status(flag);
	}


	/**============================     FILE    =================================================  */

	/**
	 * 获取BladeFile封装类
	 *
	 * @param file 文件
	 * @return BladeFile
	 */
	public BladeFile getFile(MultipartFile file) {
		return BladeFileUtil.getFile(file);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param file 文件
	 * @param dir  目录
	 * @return BladeFile
	 */
	public BladeFile getFile(MultipartFile file, String dir) {
		return BladeFileUtil.getFile(file, dir);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param file        文件
	 * @param dir         目录
	 * @param path        路径
	 * @param virtualPath 虚拟路径
	 * @return BladeFile
	 */
	public BladeFile getFile(MultipartFile file, String dir, String path, String virtualPath) {
		return BladeFileUtil.getFile(file, dir, path, virtualPath);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param files 文件集合
	 * @return BladeFile
	 */
	public List<BladeFile> getFiles(List<MultipartFile> files) {
		return BladeFileUtil.getFiles(files);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param files 文件集合
	 * @param dir   目录
	 * @return BladeFile
	 */
	public List<BladeFile> getFiles(List<MultipartFile> files, String dir) {
		return BladeFileUtil.getFiles(files, dir);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param files       文件集合
	 * @param dir         目录
	 * @param path        目录
	 * @param virtualPath 虚拟路径
	 * @return BladeFile
	 */
	public List<BladeFile> getFiles(List<MultipartFile> files, String dir, String path, String virtualPath) {
		return BladeFileUtil.getFiles(files, dir, path, virtualPath);
	}


}
