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
package org.springblade.core.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.core.report.entity.ReportFileEntity;
import org.springblade.core.report.mapper.ReportFileMapper;
import org.springblade.core.report.service.IReportFileService;
import org.springframework.stereotype.Service;

/**
 * UReport Service
 *
 * @author Chill
 */
@Service
public class ReportFileServiceImpl extends ServiceImpl<ReportFileMapper, ReportFileEntity> implements IReportFileService {
}
