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
package org.springblade.core.log.model;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 实体类
 *
 * @author Chill
 * @since 2018-10-12
 */
@Data
@TableName("blade_log_usual")
public class LogUsual extends LogAbstract implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 日志级别
	 */
	private String logLevel;
	/**
	 * 日志业务id
	 */
	private String logId;
	/**
	 * 日志数据
	 */
	private String logData;


}
