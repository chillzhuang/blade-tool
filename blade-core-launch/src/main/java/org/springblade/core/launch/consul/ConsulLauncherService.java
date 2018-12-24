package org.springblade.core.launch.consul;

import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

/**
 * consul启动拓展
 */
public class ConsulLauncherService implements LauncherService {

	@Override
	public void launcher(SpringApplicationBuilder builder, String appName, String profile) {
		Properties props = System.getProperties();
		props.setProperty("spring.cloud.consul.host", profile.equals(AppConstant.DEV_CDOE) ? AppConstant.CONSUL_DEV_HOST : AppConstant.CONSUL_PROD_HOST);
		props.setProperty("spring.cloud.consul.port", AppConstant.CONSUL_PORT);
		props.setProperty("spring.cloud.consul.config.format", AppConstant.CONSUL_CONFIG_FORMAT);
		props.setProperty("spring.cloud.consul.watch.delay", AppConstant.CONSUL_WATCH_DELAY);
		props.setProperty("spring.cloud.consul.watch.enabled", AppConstant.CONSUL_WATCH_ENABLED);
	}

}
