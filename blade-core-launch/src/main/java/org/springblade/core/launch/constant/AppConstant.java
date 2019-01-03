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
package org.springblade.core.launch.constant;

/**
 * 系统常量
 *
 * @author Chill
 */
public interface AppConstant {

	/**
	 * 应用版本
	 */
	String APPLICATION_VERSION = "2.0.0";

	/**
	 * 基础包
	 */
	String BASE_PACKAGES = "org.springblade";

	/**
	 * zookeeper id
	 */
	String ZOOKEEPER_ID = "zk";

	/**
	 * zookeeper connect string
	 */
	String ZOOKEEPER_CONNECT_STRING = "127.0.0.1:2181";

	/**
	 * zookeeper address
	 */
	String ZOOKEEPER_ADDRESS = "zookeeper://" + ZOOKEEPER_CONNECT_STRING;

	/**
	 * zookeeper root
	 */
	String ZOOKEEPER_ROOT = "/blade-services";

	/**
	 * 应用名前缀
	 */
	String APPLICATION_NAME_FREFIX = "blade-";
	/**
	 * 网关模块名称
	 */
	String APPLICATION_GATEWAY_NAME = APPLICATION_NAME_FREFIX + "gateway";
	/**
	 * 授权模块名称
	 */
	String APPLICATION_AUTH_NAME = APPLICATION_NAME_FREFIX + "auth";
	/**
	 * 监控模块名称
	 */
	String APPLICATION_ADMIN_NAME = APPLICATION_NAME_FREFIX + "admin";
	/**
	 * 配置中心模块名称
	 */
	String APPLICATION_CONFIG_NAME = APPLICATION_NAME_FREFIX + "config-server";
	/**
	 * TX模块名称
	 */
	String APPLICATION_TX_MANAGER = "tx-manager";
	/**
	 * 首页模块名称
	 */
	String APPLICATION_DESK_NAME = APPLICATION_NAME_FREFIX + "desk";
	/**
	 * 系统模块名称
	 */
	String APPLICATION_SYSTEM_NAME = APPLICATION_NAME_FREFIX + "system";
	/**
	 * 用户模块名称
	 */
	String APPLICATION_USER_NAME = APPLICATION_NAME_FREFIX + "user";
	/**
	 * 日志模块名称
	 */
	String APPLICATION_LOG_NAME = APPLICATION_NAME_FREFIX + "log";
	/**
	 * 测试模块名称
	 */
	String APPLICATION_TEST_NAME = APPLICATION_NAME_FREFIX + "test";

	/**
	 * 开发环境
	 */
	String DEV_CDOE = "dev";
	/**
	 * 生产环境
	 */
	String PROD_CODE = "prod";
	/**
	 * 测试环境
	 */
	String TEST_CODE = "test";

	/**
	 * 代码部署于 linux 上，工作默认为 mac 和 Windows
	 */
	String OS_NAME_LINUX = "LINUX";

}
