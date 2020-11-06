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
package org.springblade.core.report.endpoint;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.report.entity.ReportFileEntity;
import org.springblade.core.report.service.IReportFileService;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * UReport API端点
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping("/report/rest")
public class ReportEndpoint {

	private final IReportFileService service;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	public R<ReportFileEntity> detail(ReportFileEntity file) {
		ReportFileEntity detail = service.getOne(Condition.getQueryWrapper(file));
		return R.data(detail);
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	public R<IPage<ReportFileEntity>> list(@RequestParam Map<String, Object> file, Query query) {
		IPage<ReportFileEntity> pages = service.page(Condition.getPage(query), Condition.getQueryWrapper(file, ReportFileEntity.class));
		return R.data(pages);
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	public R remove(@RequestParam String ids) {
		boolean temp = service.removeByIds(Func.toLongList(ids));
		return R.status(temp);
	}

}
