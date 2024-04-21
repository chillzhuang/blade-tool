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
package org.springblade.core.tool.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MultipartUtils
 *
 * @author Chill
 */
public class MultipartUtil {
	/**
	 * 从HttpServletRequest中解析并返回所有的MultipartFile
	 *
	 * @param request HttpServletRequest对象，应为MultipartHttpServletRequest类型
	 * @return 包含所有MultipartFile的列表，如果没有文件或请求不是多部分请求，则返回空列表
	 */
	public static List<MultipartFile> extractMultipartFiles(HttpServletRequest request) {
		List<MultipartFile> files = new ArrayList<>();

		if (request instanceof MultipartHttpServletRequest multipartRequest) {

			// 获取所有文件的映射
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

			// 遍历映射，并将所有MultipartFile添加到列表中
			for (MultipartFile file : fileMap.values()) {
				if (file != null && !file.isEmpty()) {
					files.add(file);
				}
			}
		}

		return files;
	}
}
