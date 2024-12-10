/**
 * Copyright (c) 2018-2099, DreamLu 卢春梦 (qq596392912@gmail.com).
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

package org.springblade.core.redis.cache;


import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * cache key
 *
 * @author L.cm
 */
public interface ICacheKey {

	/**
	 * 获取前缀
	 *
	 * @return key 前缀
	 */
	String getPrefix();

	/**
	 * 超时时间
	 *
	 * @return 超时时间
	 */
	@Nullable
	default Duration getExpire() {
		return null;
	}

	/**
	 * 组装 cache key
	 *
	 * @param suffix 参数
	 * @return cache key
	 */
	default CacheKey getKey(Object... suffix) {
		String prefix = this.getPrefix();
		// 拼接参数
		String key;
		if (ObjectUtil.isEmpty(suffix)) {
			key = prefix;
		} else {
			key = prefix.concat(StringUtil.join(suffix, StringPool.COLON));
		}
		Duration expire = this.getExpire();
		return expire == null ? new CacheKey(key) : new CacheKey(key, expire);
	}

}
