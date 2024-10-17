/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (bladejava@qq.com).
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
package org.springblade.core.datascope.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据权限参数配置类
 *
 * @author Chill
 */
@Data
@ConfigurationProperties(prefix = "blade.data-scope")
public class DataScopeProperties {

	/**
	 * 开启数据权限
	 */
	private Boolean enabled = true;
	/**
	 * mapper方法匹配关键字
	 */
	private List<String> mapperKey = Arrays.asList("page", "Page", "list", "List");

	/**
	 * mapper过滤
	 */
	private List<String> mapperExclude = Collections.singletonList("FlowMapper");

}
