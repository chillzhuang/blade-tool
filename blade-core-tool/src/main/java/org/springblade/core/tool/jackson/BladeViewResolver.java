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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 视图解析器 — 视图名/角色名 → 视图 Class
 * <p>
 * 内置四级视图：summary(0) → detail(1) → admin(2) → administrator(3)
 * 可通过 {@link BladeViewCustomizer} 注册自定义视图层级
 * </p>
 *
 * @author Chill
 */
public class BladeViewResolver {

	private final BladeJacksonProperties.View viewProperties;

	/**
	 * 视图名称 → 视图 Class 映射（线程安全）
	 */
	private final Map<String, Class<?>> viewMapping = new ConcurrentHashMap<>();

	/**
	 * 视图名称 → 优先级映射（数字越大优先级越高，线程安全）
	 */
	private final Map<String, Integer> priorityMapping = new ConcurrentHashMap<>();

	public BladeViewResolver(BladeJacksonProperties.View viewProperties) {
		this.viewProperties = viewProperties;
		// 注册内置四级视图
		registerView("summary", Views.Summary.class, 0);
		registerView("list", Views.Summary.class, 0);
		registerView("detail", Views.Detail.class, 1);
		registerView("admin", Views.Admin.class, 2);
		registerView("administrator", Views.Administrator.class, 3);
	}

	/**
	 * 注册自定义视图层级
	 *
	 * @param name      视图名称（不区分大小写）
	 * @param viewClass 视图 Class（建议继承 {@link Views} 中的接口）
	 * @param priority  优先级（数字越大权限越高，内置：summary=0, detail=1, admin=2, administrator=3）
	 */
	public void registerView(String name, Class<?> viewClass, int priority) {
		viewMapping.put(name.toLowerCase(), viewClass);
		priorityMapping.put(name.toLowerCase(), priority);
	}

	/**
	 * 视图名称 → Class（未知名称降级到 Summary — 最小权限原则）
	 *
	 * @param viewName 视图名称
	 * @return 视图 Class
	 */
	public Class<?> resolve(String viewName) {
		return viewMapping.getOrDefault(viewName.toLowerCase(), Views.Summary.class);
	}

	/**
	 * 根据角色名解析视图（支持逗号分隔多角色，取最高权限）
	 *
	 * @param roleName 角色名（可逗号分隔）
	 * @return 视图 Class
	 */
	public Class<?> resolveByRole(String roleName) {
		if (roleName == null || roleName.isEmpty()) {
			return resolve(viewProperties.getDefaultView());
		}

		Map<String, String> mapping = viewProperties.getRoleMapping();
		String bestView = null;
		int bestPriority = -1;

		for (String role : roleName.split(",")) {
			String viewName = mapping.get(role.trim().toLowerCase());
			if (viewName != null) {
				int priority = priorityMapping.getOrDefault(viewName.toLowerCase(), -1);
				if (priority > bestPriority) {
					bestPriority = priority;
					bestView = viewName;
				}
			}
		}

		return resolve(bestView != null ? bestView : viewProperties.getDefaultView());
	}

	/**
	 * 获取默认视图
	 *
	 * @return 默认视图 Class
	 */
	public Class<?> getDefaultView() {
		return resolve(viewProperties.getDefaultView());
	}

}
