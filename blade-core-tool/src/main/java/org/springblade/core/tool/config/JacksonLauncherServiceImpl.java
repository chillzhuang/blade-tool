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
package org.springblade.core.tool.config;

import net.dreamlu.mica.auto.annotation.AutoService;
import org.springblade.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

import java.util.Properties;

/**
 * Jackson 启动服务
 *
 * @author Chill
 */
@AutoService(LauncherService.class)
public class JacksonLauncherServiceImpl implements LauncherService {

	@Override
	public void launcher(SpringApplicationBuilder builder, String appName, String profile) {
		Properties props = System.getProperties();
		// Spring Boot 4 默认 JSON 底座为 Jackson 3，命令式 HTTP 消息转换器与反应式编解码器需分别指定首选实现，
		// 两处同时锁回 Jackson 2，确保 @BladeView 视图过滤、大数转字符串等既有序列化约定在 servlet 与 webflux 两侧表现一致
		props.setProperty("spring.http.converters.preferred-json-mapper", "jackson2");
		props.setProperty("spring.http.codecs.preferred-json-mapper", "jackson2");
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
