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
 * @author Chill
 */
@Data
@TableName("blade_log_api")
public class LogApi implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.ID_WORKER)
	private Long id;

	/**
	 * 日志类型
	 */
	private String type;
	/**
	 * 日志标题
	 */
	private String title;
	/**
	 * 服务ID
	 */
	private String serviceId;
	/**
	 * 服务器 ip
	 */
	private String serverIp;
	/**
	 * 服务器名
	 */
	private String serverHost;
	/**
	 * 环境
	 */
	private String env;
	/**
	 * 操作IP地址
	 */
	private String remoteIp;
	/**
	 * 用户代理
	 */
	private String userAgent;
	/**
	 * 请求URI
	 */
	private String requestUri;
	/**
	 * 操作方式
	 */
	private String method;
	/**
	 * 方法类
	 */
	private String methodClass;
	/**
	 * 方法名
	 */
	private String methodName;
	/**
	 * 操作提交的数据
	 */
	private String params;
	/**
	 * 执行时间
	 */
	private String time;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	private LocalDateTime createTime;


}
