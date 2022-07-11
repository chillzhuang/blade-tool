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
package org.springblade.core.loadbalancer.config;

import org.springblade.core.loadbalancer.props.BladeLoadBalancerProperties;
import org.springblade.core.loadbalancer.rule.GrayscaleLoadBalancer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * blade 负载均衡策略
 *
 * @author Chill
 */
@AutoConfiguration
@AutoConfigureBefore(LoadBalancerClientConfiguration.class)
@EnableConfigurationProperties(BladeLoadBalancerProperties.class)
@ConditionalOnProperty(value = BladeLoadBalancerProperties.PROPERTIES_PREFIX + ".enabled", matchIfMissing = true)
@Order(BladeLoadBalancerConfiguration.REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER)
public class BladeLoadBalancerConfiguration {
	public static final int REACTIVE_SERVICE_INSTANCE_SUPPLIER_ORDER = 193827465;

	@Bean
	public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment,
																				   LoadBalancerClientFactory loadBalancerClientFactory,
																				   BladeLoadBalancerProperties bladeLoadBalancerProperties) {
		String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
		return new GrayscaleLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), bladeLoadBalancerProperties);
	}

	@Bean
	public LoadBalancerClientSpecification loadBalancerClientSpecification() {
		return new LoadBalancerClientSpecification("default.bladeLoadBalancerConfiguration",
			new Class[]{BladeLoadBalancerConfiguration.class});
	}

}
