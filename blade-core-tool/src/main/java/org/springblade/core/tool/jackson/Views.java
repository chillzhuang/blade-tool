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
package org.springblade.core.tool.jackson;

/**
 * BladeX 全局视图层级定义
 * <p>
 * 四级视图：Summary → Detail → Admin → Administrator
 * 继承即包含：高级视图自动包含所有低级视图的字段
 * 未标注 {@link BladeView} 的字段在任何视图下始终输出
 * </p>
 *
 * @author Chill
 */
public final class Views {

	private Views() {
	}

	/**
	 * 摘要视图 — 列表、下拉、搜索
	 */
	public interface Summary {
	}

	/**
	 * 详情视图 — 详情页、个人中心
	 */
	public interface Detail extends Summary {
	}

	/**
	 * 管理视图 — 普通管理员(admin)
	 */
	public interface Admin extends Detail {
	}

	/**
	 * 超管视图 — 超级管理员(administrator)
	 */
	public interface Administrator extends Admin {
	}

}
