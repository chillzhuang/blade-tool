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

package org.springblade.core.redis.config;


import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.pubsub.RPubSubListenerDetector;
import org.springblade.core.redis.pubsub.RPubSubListenerLazyFilter;
import org.springblade.core.redis.pubsub.RPubSubPublisher;
import org.springblade.core.redis.pubsub.RedisPubSubPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redisson pub/sub 发布配置
 *
 * @author L.cm
 */
@AutoConfiguration
public class RedisPubSubConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		return container;
	}

	@Bean
	public RPubSubPublisher topicEventPublisher(BladeRedis bladeRedis,
												RedisSerializer<Object> redisSerializer) {
		return new RedisPubSubPublisher(bladeRedis, redisSerializer);
	}

	@Bean
	@ConditionalOnBean(RedisSerializer.class)
	public static RPubSubListenerDetector topicListenerDetector(RedisMessageListenerContainer redisMessageListenerContainer,
														 RedisSerializer<Object> redisSerializer) {
		return new RPubSubListenerDetector(redisMessageListenerContainer, redisSerializer);
	}

	@Bean
	public RPubSubListenerLazyFilter rPubSubListenerLazyFilter() {
		return new RPubSubListenerLazyFilter();
	}

}
