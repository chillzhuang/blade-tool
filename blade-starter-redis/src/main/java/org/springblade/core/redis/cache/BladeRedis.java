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

import lombok.Getter;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.NumberUtil;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Redis 工具类
 * 封装了常用的 Redis 操作，包括字符串、哈希、列表、集合、有序集合等数据结构的操作。
 *
 * @author L.cm
 */
@Getter
@SuppressWarnings("unchecked")
public class BladeRedis {
	private final RedisTemplate<String, Object> redisTemplate; // Redis 操作模板
	private final StringRedisTemplate stringRedisTemplate; // 字符串专用的 Redis 操作模板
	private final ValueOperations<String, Object> valueOps; // 字符串操作接口
	private final HashOperations<String, Object, Object> hashOps; // 哈希操作接口
	private final ListOperations<String, Object> listOps; // 列表操作接口
	private final SetOperations<String, Object> setOps; // 集合操作接口
	private final ZSetOperations<String, Object> zSetOps; // 有序集合操作接口

	/**
	 * 构造函数，初始化 Redis 操作模板和相关操作接口
	 *
	 * @param redisTemplate        Redis 操作模板
	 * @param stringRedisTemplate  字符串专用的 Redis 操作模板
	 */
	public BladeRedis(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		Assert.notNull(redisTemplate, "redisTemplate is null"); // 检查 redisTemplate 是否为空
		valueOps = redisTemplate.opsForValue(); // 初始化字符串操作接口
		hashOps = redisTemplate.opsForHash(); // 初始化哈希操作接口
		listOps = redisTemplate.opsForList(); // 初始化列表操作接口
		setOps = redisTemplate.opsForSet(); // 初始化集合操作接口
		zSetOps = redisTemplate.opsForZSet(); // 初始化有序集合操作接口
	}

	/**
	 * 设置缓存
	 *
	 * @param cacheKey 缓存键
	 * @param value    缓存值
	 */
	public void set(CacheKey cacheKey, Object value) {
		String key = cacheKey.getKey();
		Duration expire = cacheKey.getExpire();
		if (expire == null) {
			set(key, value); // 如果未设置过期时间，直接设置缓存
		} else {
			setEx(key, value, expire); // 如果设置了过期时间，设置缓存并指定过期时间
		}
	}

	/**
	 * 存放 key-value 对到 Redis
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void set(String key, Object value) {
		valueOps.set(key, value);
	}

	/**
	 * 存放 key-value 对到 Redis，并设置过期时间
	 *
	 * @param key     键
	 * @param value   值
	 * @param timeout 过期时间
	 */
	public void setEx(String key, Object value, Duration timeout) {
		valueOps.set(key, value, timeout);
	}

	/**
	 * 存放 key-value 对到 Redis，并设置过期时间（以秒为单位）
	 *
	 * @param key     键
	 * @param value   值
	 * @param seconds 过期时间（秒）
	 */
	public void setEx(String key, Object value, Long seconds) {
		valueOps.set(key, value, seconds, TimeUnit.SECONDS);
	}

	/**
	 * 存放 key-value 对到 Redis，并设置过期时间（自定义单位）
	 *
	 * @param key     键
	 * @param value   值
	 * @param timeout 过期时间
	 * @param unit    时间单位
	 */
	public void setEx(String key, Object value, long timeout, TimeUnit unit) {
		valueOps.set(key, value, timeout, unit);
	}

	/**
	 * 获取 key 对应的值
	 *
	 * @param key 键
	 * @param <T> 返回值类型
	 * @return 值
	 */
	@Nullable
	public <T> T get(String key) {
		return (T) valueOps.get(key);
	}

	/**
	 * 获取缓存值，如果缓存不存在则通过加载器获取值并设置缓存
	 *
	 * @param key    键
	 * @param loader 加载器
	 * @param <T>    返回值类型
	 * @return 值
	 */
	@Nullable
	public <T> T get(String key, Supplier<T> loader) {
		T value = this.get(key);
		if (value != null) {
			return value;
		}
		value = loader.get();
		if (value == null) {
			return null;
		}
		this.set(key, value); // 设置缓存
		return value;
	}

	/**
	 * 获取 key 对应的值
	 *
	 * @param cacheKey 缓存键
	 * @param <T>      返回值类型
	 * @return 值
	 */
	@Nullable
	public <T> T get(CacheKey cacheKey) {
		return (T) valueOps.get(cacheKey.getKey());
	}

	/**
	 * 获取缓存值，如果缓存不存在则通过加载器获取值并设置缓存
	 *
	 * @param cacheKey 缓存键
	 * @param loader   加载器
	 * @param <T>      返回值类型
	 * @return 值
	 */
	@Nullable
	public <T> T get(CacheKey cacheKey, Supplier<T> loader) {
		String key = cacheKey.getKey();
		T value = this.get(key);
		if (value != null) {
			return value;
		}
		value = loader.get();
		if (value == null) {
			return null;
		}
		this.set(cacheKey, value); // 设置缓存
		return value;
	}

	/**
	 * 删除指定的 key
	 *
	 * @param key 键
	 * @return 是否删除成功
	 */
	public Boolean del(String key) {
		return redisTemplate.delete(key);
	}

	/**
	 * 删除指定的 key
	 *
	 * @param key 缓存键
	 * @return 是否删除成功
	 */
	public Boolean del(CacheKey key) {
		return redisTemplate.delete(key.getKey());
	}

	/**
	 * 删除多个 key
	 *
	 * @param keys 键数组
	 * @return 删除的 key 数量
	 */
	public Long del(String... keys) {
		return del(Arrays.asList(keys));
	}

	/**
	 * 删除多个 key
	 *
	 * @param keys 键集合
	 * @return 删除的 key 数量
	 */
	public Long del(Collection<String> keys) {
		return redisTemplate.delete(keys);
	}

	/**
	 * 查找所有符合给定模式的 key
	 *
	 * @param pattern 匹配模式
	 * @return 符合条件的 key 集合
	 */
	public Set<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	/**
	 * 同时设置多个 key-value 对
	 *
	 * @param keysValues key-value 对数组
	 */
	public void mSet(Object... keysValues) {
		valueOps.multiSet(CollectionUtil.toMap(keysValues));
	}

	/**
	 * 获取多个 key 对应的值
	 *
	 * @param keys 键数组
	 * @return 值列表
	 */
	public List<Object> mGet(String... keys) {
		return mGet(Arrays.asList(keys));
	}

	/**
	 * 获取多个 key 对应的值
	 *
	 * @param keys 键集合
	 * @return 值列表
	 */
	public List<Object> mGet(Collection<String> keys) {
		return valueOps.multiGet(keys);
	}

	/**
	 * 将 key 中存储的数字值减一
	 *
	 * @param key 键
	 * @return 减一后的值
	 */
	public Long decr(String key) {
		return stringRedisTemplate.opsForValue().decrement(key);
	}

	/**
	 * 将 key 中存储的数字值减去指定的值
	 *
	 * @param key       键
	 * @param longValue 减去的值
	 * @return 减去后的值
	 */
	public Long decrBy(String key, long longValue) {
		return stringRedisTemplate.opsForValue().decrement(key, longValue);
	}

	/**
	 * 将 key 中存储的数字值加一
	 *
	 * @param key 键
	 * @return 加一后的值
	 */
	public Long incr(String key) {
		return stringRedisTemplate.opsForValue().increment(key);
	}

	/**
	 * 将 key 中存储的数字值加上指定的值
	 *
	 * @param key       键
	 * @param longValue 加上的值
	 * @return 加上后的值
	 */
	public Long incrBy(String key, long longValue) {
		return stringRedisTemplate.opsForValue().increment(key, longValue);
	}

	/**
	 * 获取 key 对应的递减值
	 *
	 * @param key 键
	 * @return 递减值
	 */
	public Long getDecr(String key) {
		return NumberUtil.toLong(stringRedisTemplate.opsForValue().get(key));
	}

	/**
	 * 获取 key 对应的递增值
	 *
	 * @param key 键
	 * @return 递增值
	 */
	public Long getIncr(String key) {
		return NumberUtil.toLong(stringRedisTemplate.opsForValue().get(key));
	}

	/**
	 * 获取计数器的值
	 *
	 * @param key 键
	 * @return 计数器的值
	 */
	public Long getCounter(String key) {
		return Long.valueOf(String.valueOf(valueOps.get(key)));
	}

	/**
	 * 检查 key 是否存在
	 *
	 * @param key 键
	 * @return 是否存在
	 */
	public Boolean exists(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 随机返回一个 key
	 *
	 * @return 随机 key
	 */
	public String randomKey() {
		return redisTemplate.randomKey();
	}

	/**
	 * 将 key 改名为 newkey
	 *
	 * @param oldkey 旧键
	 * @param newkey 新键
	 */
	public void rename(String oldkey, String newkey) {
		redisTemplate.rename(oldkey, newkey);
	}

	/**
	 * 将 key 移动到指定的数据库
	 *
	 * @param key     键
	 * @param dbIndex 数据库索引
	 * @return 是否移动成功
	 */
	public Boolean move(String key, int dbIndex) {
		return redisTemplate.move(key, dbIndex);
	}

	/**
	 * 为 key 设置过期时间（以秒为单位）
	 *
	 * @param key     键
	 * @param seconds 过期时间（秒）
	 * @return 是否设置成功
	 */
	public Boolean expire(String key, long seconds) {
		return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
	}

	/**
	 * 为 key 设置过期时间
	 *
	 * @param key     键
	 * @param timeout 过期时间
	 * @return 是否设置成功
	 */
	public Boolean expire(String key, Duration timeout) {
		return expire(key, timeout.getSeconds());
	}

	/**
	 * 为 key 设置过期时间（指定时间点）
	 *
	 * @param key  键
	 * @param date 过期时间点
	 * @return 是否设置成功
	 */
	public Boolean expireAt(String key, Date date) {
		return redisTemplate.expireAt(key, date);
	}

	/**
	 * 为 key 设置过期时间（指定时间戳）
	 *
	 * @param key      键
	 * @param unixTime 过期时间戳
	 * @return 是否设置成功
	 */
	public Boolean expireAt(String key, long unixTime) {
		return expireAt(key, new Date(unixTime));
	}

	/**
	 * 为 key 设置过期时间（以毫秒为单位）
	 *
	 * @param key         键
	 * @param milliseconds 过期时间（毫秒）
	 * @return 是否设置成功
	 */
	public Boolean pexpire(String key, long milliseconds) {
		return redisTemplate.expire(key, milliseconds, TimeUnit.MILLISECONDS);
	}

	/**
	 * 设置 key 的值并返回旧值
	 *
	 * @param key   键
	 * @param value 新值
	 * @param <T>   返回值类型
	 * @return 旧值
	 */
	public <T> T getSet(String key, Object value) {
		return (T) valueOps.getAndSet(key, value);
	}

	/**
	 * 移除 key 的过期时间，使其永久有效
	 *
	 * @param key 键
	 * @return 是否移除成功
	 */
	public Boolean persist(String key) {
		return redisTemplate.persist(key);
	}

	/**
	 * 返回 key 所存储的值的类型
	 *
	 * @param key 键
	 * @return 值类型
	 */
	public String type(String key) {
		return redisTemplate.type(key).code();
	}

	/**
	 * 返回 key 的剩余生存时间（以秒为单位）
	 *
	 * @param key 键
	 * @return 剩余生存时间（秒）
	 */
	public Long ttl(String key) {
		return redisTemplate.getExpire(key);
	}

	/**
	 * 返回 key 的剩余生存时间（以毫秒为单位）
	 *
	 * @param key 键
	 * @return 剩余生存时间（毫秒）
	 */
	public Long pttl(String key) {
		return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
	}

	/**
	 * 设置哈希表中指定字段的值
	 *
	 * @param key   键
	 * @param field 字段
	 * @param value 值
	 */
	public void hSet(String key, Object field, Object value) {
		hashOps.put(key, field, value);
	}

	/**
	 * 同时设置哈希表中多个字段的值
	 *
	 * @param key  键
	 * @param hash 字段-值映射
	 */
	public void hMset(String key, Map<Object, Object> hash) {
		hashOps.putAll(key, hash);
	}

	/**
	 * 获取哈希表中指定字段的值
	 *
	 * @param key   键
	 * @param field 字段
	 * @param <T>   返回值类型
	 * @return 值
	 */
	public <T> T hGet(String key, Object field) {
		return (T) hashOps.get(key, field);
	}

	/**
	 * 获取哈希表中多个字段的值
	 *
	 * @param key    键
	 * @param fields 字段数组
	 * @return 值列表
	 */
	public List hmGet(String key, Object... fields) {
		return hmGet(key, Arrays.asList(fields));
	}

	/**
	 * 获取哈希表中多个字段的值
	 *
	 * @param key      键
	 * @param hashKeys 字段集合
	 * @return 值列表
	 */
	public List hmGet(String key, Collection<Object> hashKeys) {
		return hashOps.multiGet(key, hashKeys);
	}

	/**
	 * 删除哈希表中一个或多个字段
	 *
	 * @param key    键
	 * @param fields 字段数组
	 * @return 删除的字段数量
	 */
	public Long hDel(String key, Object... fields) {
		return hashOps.delete(key, fields);
	}

	/**
	 * 检查哈希表中是否存在指定字段
	 *
	 * @param key   键
	 * @param field 字段
	 * @return 是否存在
	 */
	public Boolean hExists(String key, Object field) {
		return hashOps.hasKey(key, field);
	}

	/**
	 * 获取哈希表中所有字段和值
	 *
	 * @param key 键
	 * @return 字段-值映射
	 */
	public Map hGetAll(String key) {
		return hashOps.entries(key);
	}

	/**
	 * 获取哈希表中所有字段的值
	 *
	 * @param key 键
	 * @return 值列表
	 */
	public List hVals(String key) {
		return hashOps.values(key);
	}

	/**
	 * 获取哈希表中所有字段
	 *
	 * @param key 键
	 * @return 字段集合
	 */
	public Set<Object> hKeys(String key) {
		return hashOps.keys(key);
	}

	/**
	 * 获取哈希表中字段的数量
	 *
	 * @param key 键
	 * @return 字段数量
	 */
	public Long hLen(String key) {
		return hashOps.size(key);
	}

	/**
	 * 为哈希表中指定字段的值加上增量
	 *
	 * @param key   键
	 * @param field 字段
	 * @param value 增量
	 * @return 增加后的值
	 */
	public Long hIncrBy(String key, Object field, long value) {
		return hashOps.increment(key, field, value);
	}

	/**
	 * 为哈希表中指定字段的值加上浮点数增量
	 *
	 * @param key   键
	 * @param field 字段
	 * @param value 增量
	 * @return 增加后的值
	 */
	public Double hIncrByFloat(String key, Object field, double value) {
		return hashOps.increment(key, field, value);
	}

	/**
	 * 获取列表中指定下标的元素
	 *
	 * @param key   键
	 * @param index 下标
	 * @param <T>   返回值类型
	 * @return 元素
	 */
	public <T> T lIndex(String key, long index) {
		return (T) listOps.index(key, index);
	}

	/**
	 * 获取列表的长度
	 *
	 * @param key 键
	 * @return 列表长度
	 */
	public Long lLen(String key) {
		return listOps.size(key);
	}

	/**
	 * 移除并返回列表的头元素
	 *
	 * @param key 键
	 * @param <T> 返回值类型
	 * @return 头元素
	 */
	public <T> T lPop(String key) {
		return (T) listOps.leftPop(key);
	}

	/**
	 * 将一个或多个值插入到列表的头部
	 *
	 * @param key    键
	 * @param values 值数组
	 * @return 插入后列表的长度
	 */
	public Long lPush(String key, Object... values) {
		return listOps.leftPush(key, values);
	}

	/**
	 * 设置列表中指定下标的元素
	 *
	 * @param key   键
	 * @param index 下标
	 * @param value 值
	 */
	public void lSet(String key, long index, Object value) {
		listOps.set(key, index, value);
	}

	/**
	 * 移除列表中与指定值相等的元素
	 *
	 * @param key   键
	 * @param count 移除数量
	 * @param value 值
	 * @return 移除的元素数量
	 */
	public Long lRem(String key, long count, Object value) {
		return listOps.remove(key, count, value);
	}

	/**
	 * 获取列表中指定区间的元素
	 *
	 * @param key   键
	 * @param start 起始下标
	 * @param end   结束下标
	 * @return 元素列表
	 */
	public List lRange(String key, long start, long end) {
		return listOps.range(key, start, end);
	}

	/**
	 * 对列表进行修剪，只保留指定区间的元素
	 *
	 * @param key   键
	 * @param start 起始下标
	 * @param end   结束下标
	 */
	public void lTrim(String key, long start, long end) {
		listOps.trim(key, start, end);
	}

	/**
	 * 移除并返回列表的尾元素
	 *
	 * @param key 键
	 * @param <T> 返回值类型
	 * @return 尾元素
	 */
	public <T> T rPop(String key) {
		return (T) listOps.rightPop(key);
	}

	/**
	 * 将一个或多个值插入到列表的尾部
	 *
	 * @param key    键
	 * @param values 值数组
	 * @return 插入后列表的长度
	 */
	public Long rPush(String key, Object... values) {
		return listOps.rightPushAll(key, values);
	}

	/**
	 * 将列表的尾元素弹出并插入到另一个列表的头部
	 *
	 * @param srcKey 源列表键
	 * @param dstKey 目标列表键
	 * @param <T>    返回值类型
	 * @return 弹出的元素
	 */
	public <T> T rPopLPush(String srcKey, String dstKey) {
		return (T) listOps.rightPopAndLeftPush(srcKey, dstKey);
	}

	/**
	 * 将一个或多个成员添加到集合中
	 *
	 * @param key     键
	 * @param members 成员数组
	 * @return 添加的成员数量
	 */
	public Long sAdd(String key, Object... members) {
		return setOps.add(key, members);
	}

	/**
	 * 移除并返回集合中的一个随机元素
	 *
	 * @param key 键
	 * @param <T> 返回值类型
	 * @return 随机元素
	 */
	public <T> T sPop(String key) {
		return (T) setOps.pop(key);
	}

	/**
	 * 获取集合中的所有成员
	 *
	 * @param key 键
	 * @return 成员集合
	 */
	public Set sMembers(String key) {
		return setOps.members(key);
	}

	/**
	 * 检查成员是否在集合中
	 *
	 * @param key    键
	 * @param member 成员
	 * @return 是否在集合中
	 */
	public boolean sIsMember(String key, Object member) {
		return setOps.isMember(key, member);
	}

	/**
	 * 返回多个集合的交集
	 *
	 * @param key      键
	 * @param otherKey 另一个集合键
	 * @return 交集集合
	 */
	public Set sInter(String key, String otherKey) {
		return setOps.intersect(key, otherKey);
	}

	/**
	 * 返回多个集合的交集
	 *
	 * @param key       键
	 * @param otherKeys 其他集合键集合
	 * @return 交集集合
	 */
	public Set sInter(String key, Collection<String> otherKeys) {
		return setOps.intersect(key, otherKeys);
	}

	/**
	 * 返回集合中的一个随机成员
	 *
	 * @param key 键
	 * @param <T> 返回值类型
	 * @return 随机成员
	 */
	public <T> T sRandMember(String key) {
		return (T) setOps.randomMember(key);
	}

	/**
	 * 返回集合中的多个随机成员
	 *
	 * @param key   键
	 * @param count 随机成员数量
	 * @return 随机成员列表
	 */
	public List sRandMember(String key, int count) {
		return setOps.randomMembers(key, count);
	}

	/**
	 * 移除集合中的一个或多个成员
	 *
	 * @param key     键
	 * @param members 成员数组
	 * @return 移除的成员数量
	 */
	public Long sRem(String key, Object... members) {
		return setOps.remove(key, members);
	}

	/**
	 * 返回多个集合的并集
	 *
	 * @param key      键
	 * @param otherKey 另一个集合键
	 * @return 并集集合
	 */
	public Set sUnion(String key, String otherKey) {
		return setOps.union(key, otherKey);
	}

	/**
	 * 返回多个集合的并集
	 *
	 * @param key       键
	 * @param otherKeys 其他集合键集合
	 * @return 并集集合
	 */
	public Set sUnion(String key, Collection<String> otherKeys) {
		return setOps.union(key, otherKeys);
	}

	/**
	 * 返回多个集合的差集
	 *
	 * @param key      键
	 * @param otherKey 另一个集合键
	 * @return 差集集合
	 */
	public Set sDiff(String key, String otherKey) {
		return setOps.difference(key, otherKey);
	}

	/**
	 * 返回多个集合的差集
	 *
	 * @param key       键
	 * @param otherKeys 其他集合键集合
	 * @return 差集集合
	 */
	public Set sDiff(String key, Collection<String> otherKeys) {
		return setOps.difference(key, otherKeys);
	}

	/**
	 * 将一个或多个成员及其分数添加到有序集合中
	 *
	 * @param key    键
	 * @param member 成员
	 * @param score  分数
	 * @return 是否添加成功
	 */
	public Boolean zAdd(String key, Object member, double score) {
		return zSetOps.add(key, member, score);
	}

	/**
	 * 将一个或多个成员及其分数添加到有序集合中
	 *
	 * @param key           键
	 * @param scoreMembers 成员-分数映射
	 * @return 添加的成员数量
	 */
	public Long zAdd(String key, Map<Object, Double> scoreMembers) {
		Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
		scoreMembers.forEach((k, v) -> {
			tuples.add(new DefaultTypedTuple<>(k, v));
		});
		return zSetOps.add(key, tuples);
	}

	/**
	 * 获取有序集合的基数
	 *
	 * @param key 键
	 * @return 有序集合的基数
	 */
	public Long zCard(String key) {
		return zSetOps.zCard(key);
	}

	/**
	 * 返回有序集合中分数在指定范围内的成员数量
	 *
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 成员数量
	 */
	public Long zCount(String key, double min, double max) {
		return zSetOps.count(key, min, max);
	}

	/**
	 * 为有序集合中的成员增加分数
	 *
	 * @param key    键
	 * @param member 成员
	 * @param score  增加的分数
	 * @return 增加后的分数
	 */
	public Double zIncrBy(String key, Object member, double score) {
		return zSetOps.incrementScore(key, member, score);
	}

	/**
	 * 返回有序集合中指定区间的成员（按分数递增排序）
	 *
	 * @param key   键
	 * @param start 起始下标
	 * @param end   结束下标
	 * @return 成员集合
	 */
	public Set zRange(String key, long start, long end) {
		return zSetOps.range(key, start, end);
	}

	/**
	 * 返回有序集合中指定区间的成员（按分数递减排序）
	 *
	 * @param key   键
	 * @param start 起始下标
	 * @param end   结束下标
	 * @return 成员集合
	 */
	public Set zRevrange(String key, long start, long end) {
		return zSetOps.reverseRange(key, start, end);
	}

	/**
	 * 返回有序集合中分数在指定范围内的成员（按分数递增排序）
	 *
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 成员集合
	 */
	public Set zRangeByScore(String key, double min, double max) {
		return zSetOps.rangeByScore(key, min, max);
	}

	/**
	 * 返回有序集合中成员的排名（按分数递增排序）
	 *
	 * @param key    键
	 * @param member 成员
	 * @return 排名
	 */
	public Long zRank(String key, Object member) {
		return zSetOps.rank(key, member);
	}

	/**
	 * 返回有序集合中成员的排名（按分数递减排序）
	 *
	 * @param key    键
	 * @param member 成员
	 * @return 排名
	 */
	public Long zRevrank(String key, Object member) {
		return zSetOps.reverseRank(key, member);
	}

	/**
	 * 移除有序集合中的一个或多个成员
	 *
	 * @param key     键
	 * @param members 成员数组
	 * @return 移除的成员数量
	 */
	public Long zRem(String key, Object... members) {
		return zSetOps.remove(key, members);
	}

	/**
	 * 返回有序集合中成员的分数
	 *
	 * @param key    键
	 * @param member 成员
	 * @return 分数
	 */
	public Double zScore(String key, Object member) {
		return zSetOps.score(key, member);
	}

	/**
	 * 发布消息到指定频道
	 *
	 * @param channel 频道
	 * @param message 消息
	 * @param mapper  消息序列化函数
	 * @param <T>     消息类型
	 * @return 接收到消息的客户端数量
	 */
	@Nullable
	public <T> Long publish(String channel, T message, Function<T, byte[]> mapper) {
		return redisTemplate.execute((RedisCallback<Long>) redis -> {
			byte[] channelBytes = keySerialize(channel);
			return redis.publish(channelBytes, mapper.apply(message));
		});
	}

	/**
	 * 序列化 Redis 键
	 *
	 * @param redisKey Redis 键
	 * @return 序列化后的字节数组
	 */
	public byte[] keySerialize(String redisKey) {
		RedisSerializer<String> keySerializer = (RedisSerializer<String>) this.redisTemplate.getKeySerializer();
		return Objects.requireNonNull(keySerializer.serialize(redisKey), "Redis key is null.");
	}
}
