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

import java.io.File;

/**
 * 文件代理接口
 *
 * @author Chill
 */
public interface IFileProxy {

	/**
	 * 返回路径[物理路径][虚拟路径]
	 *
	 * @param file 文件
	 * @param dir  目录
	 * @return
	 */
	String[] path(File file, String dir);

	/**
	 * 文件重命名策略
	 *
	 * @param file 文件
	 * @param path 路径
	 * @return
	 */
	File rename(File file, String path);

	/**
	 * 图片压缩
	 *
	 * @param path 路径
	 */
	void compress(String path);

}
