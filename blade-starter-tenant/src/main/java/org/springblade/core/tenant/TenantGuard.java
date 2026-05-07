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
package org.springblade.core.tenant;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tenant.exception.TenantException;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户归属守卫
 * <p>
 * 统一拦截「目标实体是否属于当前会话租户」的越权写操作。平台超管（administrator）默认放行。
 * 通过反射访问实体的 getId / getTenantId / setTenantId 方法，调用方无需传入 lambda。
 *
 * @author Chill
 */
public class TenantGuard {

	private static final String METHOD_GET_ID = "getId";
	private static final String METHOD_GET_TENANT_ID = "getTenantId";
	private static final String METHOD_SET_TENANT_ID = "setTenantId";

	private static final ConcurrentHashMap<Class<?>, Method> ID_GETTER_CACHE = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Class<?>, Method> TENANT_GETTER_CACHE = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Class<?>, Method> TENANT_SETTER_CACHE = new ConcurrentHashMap<>();

	private TenantGuard() {
	}

	/**
	 * 受租户隔离保护的实体类型
	 * <p>
	 * 集中管理实体中文标签，避免调用处散落硬编码字符串。新增受保护实体时，
	 * 在此枚举追加常量即可，调用方使用 static import 让代码更紧凑。
	 */
	public enum EntityType {

		ROLE("角色"),
		USER("用户"),
		DEPT("部门"),
		DEPT_PARENT("上级部门"),
		POST("岗位"),
		;

		private final String label;

		EntityType(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}
	}

	/**
	 * 校验单个 id 归属当前会话租户
	 * <p>
	 * 适用于 update/detail by id 等场景。校验通过后返回查询出的实体，
	 * 调用方可基于返回值进行后续业务（如把 existEntity.tenantId 回写到入参以防篡改）。
	 *
	 * @param service    MyBatis-Plus IService 实例
	 * @param id         目标主键
	 * @param entityType 实体类型枚举
	 * @param <T>        实体类型
	 * @return 查询出的实体（仅当超管查询不存在的 id 时可能为 null）
	 * @throws TenantException 当非超管且目标实体不存在或归属其他租户时抛出
	 */
	public static <T> T verify(IService<T> service, Long id, EntityType entityType) {
		T entity = service.getById(id);
		if (SecureUtil.isAdministrator()) {
			return entity;
		}
		if (entity == null || !SecureUtil.getTenantId().equals(tenantIdOf(entity))) {
			throw new TenantException("无权操作非本租户的" + entityType.label());
		}
		return entity;
	}

	/**
	 * 提交时（新增 / 修改）的租户绑定守卫
	 * <p>
	 * 新增（id 为空）：超管放行，非超管强制写入当前会话 tenantId，避免前端注入。<br/>
	 * 修改（id 不空）：调用 {@link #verify} 校验归属，并把已存在实体的 tenantId 回写到入参，防止 update 篡改。
	 *
	 * @param service    MyBatis-Plus IService 实例
	 * @param entity     入参实体
	 * @param entityType 实体类型
	 * @param <T>        实体类型
	 * @throws TenantException 当修改路径下目标实体不存在或归属其他租户时抛出
	 */
	public static <T> void bindTenant(IService<T> service, T entity, EntityType entityType) {
		Long id = idOf(entity);
		if (id == null) {
			if (!SecureUtil.isAdministrator()) {
				bindTenantId(entity, SecureUtil.getTenantId());
			}
			return;
		}
		T existEntity = verify(service, id, entityType);
		if (existEntity == null) {
			return;
		}
		// 非超管路径下 verify 已确保 existEntity.tenantId 等于会话 tenantId，直接复用避免重复反射
		String tenantId = SecureUtil.isAdministrator() ? tenantIdOf(existEntity) : SecureUtil.getTenantId();
		bindTenantId(entity, tenantId);
	}

	/**
	 * 校验批量 ids 全部归属当前会话租户，并返回查询出的实体列表
	 * <p>
	 * 适用于 batch remove/grant/reset/unlock 等场景。任一 id 不存在或归属其他租户，整体抛异常。
	 * 超管放行。返回值供调用方复用，避免二次查询。
	 *
	 * @param service    MyBatis-Plus IService 实例
	 * @param ids        目标主键集合
	 * @param entityType 实体类型枚举
	 * @param <T>        实体类型
	 * @return 查询出的实体列表（ids 为空时返回空列表）
	 * @throws TenantException 当非超管且任一目标实体不存在或归属其他租户时抛出
	 */
	public static <T> List<T> verifyBatch(IService<T> service, List<Long> ids, EntityType entityType) {
		if (CollectionUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}
		List<T> list = service.listByIds(ids);
		if (SecureUtil.isAdministrator()) {
			return list;
		}
		String currentTenantId = SecureUtil.getTenantId();
		if (list.size() != ids.size() || list.stream().anyMatch(entity -> !currentTenantId.equals(tenantIdOf(entity)))) {
			throw new TenantException("无权操作非本租户的" + entityType.label());
		}
		return list;
	}

	/**
	 * 反射读取实体主键
	 */
	private static Long idOf(Object entity) {
		Method method = ID_GETTER_CACHE.computeIfAbsent(entity.getClass(),
			clazz -> requireMethod(clazz, METHOD_GET_ID));
		Object value = ReflectUtil.invokeMethod(method, entity);
		if (value != null && !(value instanceof Long)) {
			throw new IllegalArgumentException("实体 " + entity.getClass().getSimpleName() + " 的主键类型必须为 Long");
		}
		return (Long) value;
	}

	/**
	 * 反射读取实体 tenantId
	 */
	private static String tenantIdOf(Object entity) {
		Method method = TENANT_GETTER_CACHE.computeIfAbsent(entity.getClass(),
			clazz -> requireMethod(clazz, METHOD_GET_TENANT_ID));
		return (String) ReflectUtil.invokeMethod(method, entity);
	}

	/**
	 * 反射写入实体 tenantId
	 */
	private static void bindTenantId(Object entity, String tenantId) {
		Method method = TENANT_SETTER_CACHE.computeIfAbsent(entity.getClass(),
			clazz -> requireMethod(clazz, METHOD_SET_TENANT_ID, String.class));
		ReflectUtil.invokeMethod(method, entity, tenantId);
	}

	private static Method requireMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		Method method = ReflectUtil.findMethod(clazz, name, paramTypes);
		if (method == null) {
			throw new IllegalArgumentException("实体 " + clazz.getSimpleName() + " 缺少 " + name + " 方法，不支持租户守卫");
		}
		return method;
	}

}
