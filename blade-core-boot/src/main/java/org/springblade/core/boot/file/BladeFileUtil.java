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
package org.springblade.core.boot.file;

import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 文件工具类
 *
 * @author smallchill
 */
public class BladeFileUtil {

	/**
	 * 定义允许上传的文件扩展名
	 */
	private static HashMap<String, String> extMap = new HashMap<String, String>();
	private static String IS_DIR = "is_dir";
	private static String FILE_NAME = "filename";
	private static String FILE_SIZE = "filesize";

	/**
	 * 图片扩展名
	 */
	private static String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};

	static {
		extMap.put("image", ".gif,.jpg,.jpeg,.png,.bmp,.JPG,.JPEG,.PNG");
		extMap.put("flash", ".swf,.flv");
		extMap.put("media", ".swf,.flv,.mp3,.mp4,.wav,.wma,.wmv,.mid,.avi,.mpg,.asf,.rm,.rmvb");
		extMap.put("file", ".doc,.docx,.xls,.xlsx,.ppt,.htm,.html,.txt,.zip,.rar,.gz,.bz2");
		extMap.put("allfile", ".gif,.jpg,.jpeg,.png,.bmp,.swf,.flv,.mp3,.mp4,.wav,.wma,.wmv,.mid,.avi,.mpg,.asf,.rm,.rmvb,.doc,.docx,.xls,.xlsx,.ppt,.htm,.html,.txt,.zip,.rar,.gz,.bz2");
	}

	/**
	 * 获取文件后缀
	 *
	 * @param @param  fileName
	 * @param @return 设定文件
	 * @return String 返回类型
	 */
	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf(StringPool.DOT));
	}

	/**
	 * 测试文件后缀 只让指定后缀的文件上传，像jsp,war,sh等危险的后缀禁止
	 *
	 * @return
	 */
	public static boolean testExt(String dir, String fileName) {
		String fileExt = getFileExt(fileName);

		String ext = extMap.get(dir);
		if (StringUtil.isBlank(ext) || ext.indexOf(fileExt) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 文件管理排序
	 */
	public enum FileSort {

		/**
		 * 大小
		 */
		size,

		/**
		 * 类型
		 */
		type,

		/**
		 * 名称
		 */
		name;

		/**
		 * 文本排序转换成枚举
		 *
		 * @param sort
		 * @return
		 */
		public static FileSort of(String sort) {
			try {
				return FileSort.valueOf(sort);
			} catch (Exception e) {
				return FileSort.name;
			}
		}
	}

	public static class NameComparator implements Comparator {
		@Override
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable) a;
			Hashtable hashB = (Hashtable) b;
			if (((Boolean) hashA.get(IS_DIR)) && !((Boolean) hashB.get(IS_DIR))) {
				return -1;
			} else if (!((Boolean) hashA.get(IS_DIR)) && ((Boolean) hashB.get(IS_DIR))) {
				return 1;
			} else {
				return ((String) hashA.get(FILE_NAME)).compareTo((String) hashB.get(FILE_NAME));
			}
		}
	}

	public static class SizeComparator implements Comparator {
		@Override
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable) a;
			Hashtable hashB = (Hashtable) b;
			if (((Boolean) hashA.get(IS_DIR)) && !((Boolean) hashB.get(IS_DIR))) {
				return -1;
			} else if (!((Boolean) hashA.get(IS_DIR)) && ((Boolean) hashB.get(IS_DIR))) {
				return 1;
			} else {
				if (((Long) hashA.get(FILE_SIZE)) > ((Long) hashB.get(FILE_SIZE))) {
					return 1;
				} else if (((Long) hashA.get(FILE_SIZE)) < ((Long) hashB.get(FILE_SIZE))) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	public static class TypeComparator implements Comparator {
		@Override
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable) a;
			Hashtable hashB = (Hashtable) b;
			if (((Boolean) hashA.get(IS_DIR)) && !((Boolean) hashB.get(IS_DIR))) {
				return -1;
			} else if (!((Boolean) hashA.get(IS_DIR)) && ((Boolean) hashB.get(IS_DIR))) {
				return 1;
			} else {
				return ((String) hashA.get("filetype")).compareTo((String) hashB.get("filetype"));
			}
		}
	}

	public static String formatUrl(String url) {
		return url.replaceAll("\\\\", "/");
	}


	/********************************BladeFile封装********************************************************/

	/**
	 * 获取BladeFile封装类
	 *
	 * @param file
	 * @return
	 */
	public static BladeFile getFile(MultipartFile file) {
		return getFile(file, "image", null, null);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param file
	 * @param dir
	 * @return
	 */
	public static BladeFile getFile(MultipartFile file, String dir) {
		return getFile(file, dir, null, null);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param file
	 * @param dir
	 * @param path
	 * @param virtualPath
	 * @return
	 */
	public static BladeFile getFile(MultipartFile file, String dir, String path, String virtualPath) {
		return new BladeFile(file, dir, path, virtualPath);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param files
	 * @return
	 */
	public static List<BladeFile> getFiles(List<MultipartFile> files) {
		return getFiles(files, "image", null, null);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param files
	 * @param dir
	 * @return
	 */
	public static List<BladeFile> getFiles(List<MultipartFile> files, String dir) {
		return getFiles(files, dir, null, null);
	}

	/**
	 * 获取BladeFile封装类
	 *
	 * @param files
	 * @param path
	 * @param virtualPath
	 * @return
	 */
	public static List<BladeFile> getFiles(List<MultipartFile> files, String dir, String path, String virtualPath) {
		List<BladeFile> list = new ArrayList<>();
		for (MultipartFile file : files) {
			list.add(new BladeFile(file, dir, path, virtualPath));
		}
		return list;
	}

}
