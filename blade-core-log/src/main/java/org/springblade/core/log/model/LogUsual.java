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
package org.springblade.core.log.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类
 *
 * @author smallchill
 * @since 2018-10-12
 */
@Data
@TableName("blade_log_usual")
public class LogUsual implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.ID_WORKER)
	private Long id;
	/**
	 * 服务ID
	 */
	private String serviceId;
	/**
	 * 服务器名
	 */
	private String serverHost;
	/**
	 * 服务器IP地址
	 */
	private String serverIp;
	/**
	 * 系统环境
	 */
	private String env;
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
	/**
	 * 操作方式
	 */
	private String method;
	/**
	 * 请求URI
	 */
	private String requestUri;
	/**
	 * 用户代理
	 */
	private String userAgent;
	/**
	 * 操作提交的数据
	 */
	private String params;
	/**
	 * 创建者
	 */
	private String createBy;
	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	private LocalDateTime createTime;


}
