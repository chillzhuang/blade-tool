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


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * logApi、logError、logUsual的父类，拥有相同的属性值
 *
 * @author jiang
 */
@Data
public class LogAbstract implements Serializable {

	protected static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.ID_WORKER)
	protected Long id;

	/**
	 * 服务ID
	 */
	protected String serviceId;
	/**
	 * 服务器 ip
	 */
	protected String serverIp;
	/**
	 * 服务器名
	 */
	protected String serverHost;
	/**
	 * 环境
	 */
	protected String env;
	/**
	 * 操作IP地址
	 */
	protected String remoteIp;
	/**
	 * 用户代理
	 */
	protected String userAgent;
	/**
	 * 请求URI
	 */
	protected String requestUri;
	/**
	 * 操作方式
	 */
	protected String method;
	/**
	 * 方法类
	 */
	protected String methodClass;
	/**
	 * 方法名
	 */
	protected String methodName;
	/**
	 * 操作提交的数据
	 */
	protected String params;
	/**
	 * 执行时间
	 */
	protected String time;

	/**
	 * 创建人
	 */
	protected String createBy;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	protected Date createTime;

}
