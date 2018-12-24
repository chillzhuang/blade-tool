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
package org.springblade.core.tool.support.xss;


import org.springblade.core.tool.utils.StringUtil;

/**
 * SQL过滤
 */
public class SQLFilter {

    /**
     * SQL注入过滤
     *
     * @param str 待验证的字符串
     */
    public static String sqlInject(String str) {
        if (StringUtil.isBlank(str)) {
            return null;
        }
        //去掉'|"|;|\字符
        str = str.replace("'", "");
        str = str.replace("\"", "");
        str = str.replace(";", "");
        str = str.replace("\\", "");

        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alert", "drop"};

        //判断是否包含非法字符
        for (String keyword : keywords) {
            if (str.indexOf(keyword) != -1) {
                throw new RuntimeException("包含非法字符");
            }
        }

        return str;
    }
}
