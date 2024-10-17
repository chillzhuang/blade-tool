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
package org.springblade.core.mp.support;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * 分页工具
 *
 * @author Chill
 */
@Data
@Accessors(chain = true)
@Schema(description = "查询条件")
public class Query {

	/**
	 * 当前页
	 */
	@Schema(description = "当前页")
	private Integer current;

	/**
	 * 每页的数量
	 */
	@Schema(description = "每页的数量")
	private Integer size;

	/**
	 * 排序的字段名
	 */
	@Schema(accessMode = READ_ONLY)
	private String ascs;

	/**
	 * 排序方式
	 */
	@Schema(accessMode = READ_ONLY)
	private String descs;

}
