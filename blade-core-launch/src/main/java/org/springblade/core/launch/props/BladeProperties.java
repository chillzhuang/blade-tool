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
package org.springblade.core.launch.props;

import lombok.Getter;
import lombok.Setter;
import org.springblade.core.launch.constant.AppConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 配置文件
 *
 * @author Chill
 */
@ConfigurationProperties("blade")
public class BladeProperties implements EnvironmentAware, EnvironmentCapable {
	@Nullable
	private Environment environment;

	/**
	 * 开发环境
	 */
	@Getter
	@Setter
	private String env;

	/**
	 * 服务名
	 */
	@Getter
	@Setter
	private String name;

	/**
	 * 判断是否为 本地开发环境
	 */
	@Getter
	@Setter
	private Boolean isLocal = Boolean.FALSE;

	/**
	 * 装载自定义配置blade.prop.xxx
	 */
	@Getter
	private final Map<String, String> prop = new HashMap<>();

	/**
	 * 获取配置
	 *
	 * @param key key
	 * @return value
	 */
	@Nullable
	public String get(String key) {
		return get(key, null);
	}

	/**
	 * 获取配置
	 *
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return value
	 */
	@Nullable
	public String get(String key, @Nullable String defaultValue) {
		String value = prop.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * 获取配置
	 *
	 * @param key key
	 * @return int value
	 */
	@Nullable
	public Integer getInt(String key) {
		return getInt(key, null);
	}

	/**
	 * 获取配置
	 *
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return int value
	 */
	@Nullable
	public Integer getInt(String key, @Nullable Integer defaultValue) {
		String value = prop.get(key);
		if (value != null) {
			return Integer.valueOf(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 获取配置
	 *
	 * @param key key
	 * @return long value
	 */
	@Nullable
	public Long getLong(String key) {
		return getLong(key, null);
	}

	/**
	 * 获取配置
	 *
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return long value
	 */
	@Nullable
	public Long getLong(String key, @Nullable Long defaultValue) {
		String value = prop.get(key);
		if (value != null) {
			return Long.valueOf(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 获取配置
	 *
	 * @param key key
	 * @return Boolean value
	 */
	@Nullable
	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	/**
	 * 获取配置
	 *
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return Boolean value
	 */
	@Nullable
	public Boolean getBoolean(String key, @Nullable Boolean defaultValue) {
		String value = prop.get(key);
		if (value != null) {
			value = value.toLowerCase().trim();
			return Boolean.parseBoolean(value);
		}
		return defaultValue;
	}

	/**
	 * 获取配置
	 *
	 * @param key key
	 * @return double value
	 */
	@Nullable
	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	/**
	 * 获取配置
	 *
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return double value
	 */
	@Nullable
	public Double getDouble(String key, @Nullable Double defaultValue) {
		String value = prop.get(key);
		if (value != null) {
			return Double.parseDouble(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 是否是开发环境
	 *
	 * @return boolean
	 */
	public boolean isDev() {
		return AppConstant.DEV_CODE.equals(getEnv());
	}

	/**
	 * 是否是生产环境
	 *
	 * @return boolean
	 */
	public boolean isProd() {
		return AppConstant.PROD_CODE.equals(getEnv());
	}

	/**
	 * 是否是测试环境
	 *
	 * @return boolean
	 */
	public boolean isTest() {
		return AppConstant.TEST_CODE.equals(getEnv());
	}

	/**
	 * 判断是否存在key
	 *
	 * @param key prop key
	 * @return boolean
	 */
	public boolean containsKey(String key) {
		return prop.containsKey(key);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public Environment getEnvironment() {
		Objects.requireNonNull(environment, "Spring boot 环境下 Environment 不可能为null");
		return this.environment;
	}
}
