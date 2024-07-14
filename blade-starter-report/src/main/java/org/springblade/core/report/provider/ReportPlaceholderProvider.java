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
package org.springblade.core.report.provider;

import com.bstek.ureport.UReportPropertyPlaceholderConfigurer;
import org.springblade.core.report.props.ReportProperties;

import java.util.Properties;

/**
 * UReport自定义配置
 *
 * @author Chill
 */
public class ReportPlaceholderProvider extends UReportPropertyPlaceholderConfigurer {

	public ReportPlaceholderProvider(ReportProperties properties) {
		Properties props = new Properties();
		props.setProperty("ureport.disableHttpSessionReportCache", properties.getDisableHttpSessionReportCache().toString());
		props.setProperty("ureport.disableFileProvider", properties.getDisableFileProvider().toString());
		props.setProperty("ureport.fileStoreDir", properties.getFileStoreDir());
		props.setProperty("ureport.debug", properties.getDebug().toString());
		this.setProperties(props);
	}

}
