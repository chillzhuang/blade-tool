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
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务 异常
 *
 * @author smallchill
 */
@Data
@TableName("blade_log_error")
public class LogError implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.ID_WORKER)
	private Long id;
	/**
	 * 应用名
	 */
	private String serviceId;
	/**
	 * 环境
	 */
	private String env;
	/**
	 * 服务器 ip
	 */
	private String serverIp;
	/**
	 * 服务器名
	 */
	private String serverHost;
	/**
	 * 用户代理
	 */
	private String userAgent;
	/**
	 * 请求url
	 */
	@Nullable
	private String requestUri;
	/**
	 * 操作方式
	 */
	private String method;
	/**
	 * 堆栈信息
	 */
	private String stackTrace;
	/**
	 * 异常名
	 */
	private String exceptionName;
	/**
	 * 异常消息
	 */
	private String message;
	/**
	 * 类名
	 */
	private String methodClass;
	/**
	 * 文件名
	 */
	private String fileName;
	/**
	 * 方法名
	 */
	private String methodName;
	/**
	 * 操作提交的数据
	 */
	private String params;
	/**
	 * 代码行数
	 */
	private Integer lineNumber;

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
