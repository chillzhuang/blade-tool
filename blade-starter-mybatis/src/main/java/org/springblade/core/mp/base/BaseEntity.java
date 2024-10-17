/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (smallchill@163.com).
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
package org.springblade.core.mp.base;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 *
 * @author Chill
 */
@Data
public class BaseEntity implements Serializable {

	/**
	 * 主键
	 */
	@Schema(description = "主键")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 创建人
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@Schema(description = "创建人", hidden = true)
	private Long createUser;

	/**
	 * 创建部门
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@Schema(description = "创建部门", hidden = true)
	private Long createDept;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	@Schema(description = "创建时间", hidden = true)
	private Date createTime;

	/**
	 * 更新人
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@Schema(description = "更新人", hidden = true)
	private Long updateUser;

	/**
	 * 更新时间
	 */
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	@Schema(description = "更新时间", hidden = true)
	private Date updateTime;

	/**
	 * 状态[1:正常]
	 */
	@Schema(description = "业务状态", hidden = true)
	private Integer status;

	/**
	 * 状态[0:未删除,1:删除]
	 */
	@TableLogic
	@Schema(description = "是否已删除", hidden = true)
	private Integer isDeleted;
}
