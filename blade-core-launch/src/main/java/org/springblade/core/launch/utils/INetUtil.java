/**
 * Copyright (c) 2019-2029, DreamLu 卢春梦 (596392912@qq.com & www.dreamlu.net).
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

package org.springblade.core.launch.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INet 相关工具
 *
 * @author L.cm
 */
public class INetUtil {
	public static final String LOCAL_HOST = "127.0.0.1";
	/**
	 * 客户端地址请求头，覆盖语义，由紧邻应用的代理或网关写入
	 */
	public static final String X_REAL_IP = "X-Real-IP";
	/**
	 * 代理链请求头，追加语义，每一跳把自己看到的对端地址追加在右侧
	 */
	public static final String X_FORWARDED_FOR = "X-Forwarded-For";
	/**
	 * 部分代理取不到地址时填入的占位值，视同未提供
	 */
	private static final String UNKNOWN = "unknown";
	/**
	 * 地址字面量长度上限，含方括号与端口后缀，超长值直接丢弃，不进入正则匹配
	 */
	private static final int IP_LITERAL_MAX_LENGTH = 64;
	private static final String IPV4_LITERAL = "(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)){3}";
	/**
	 * 只放行 IPv4 与带冒号的 IPv6 字面量，主机名一律不放行，避免交给地址解析时触发 DNS 查询
	 */
	private static final Pattern IP_LITERAL = Pattern.compile("^(?:" + IPV4_LITERAL + "|[0-9A-Fa-f]{0,4}(?::[0-9A-Fa-f]{0,4}){1,7}(?::" + IPV4_LITERAL + ")?)$");
	/**
	 * 部分负载均衡会在转发头里附带来源端口，形如 IPv4:端口 或 [IPv6]:端口
	 */
	private static final Pattern IPV4_WITH_PORT = Pattern.compile("^(" + IPV4_LITERAL + "):\\d{1,5}$");
	private static final Pattern BRACKETED_IPV6 = Pattern.compile("^\\[([0-9A-Fa-f:.]+)](?::\\d{1,5})?$");

	/**
	 * 获取 服务器 hostname
	 *
	 * @return hostname
	 */
	public static String getHostName() {
		String hostname;
		try {
			InetAddress address = InetAddress.getLocalHost();
			// force a best effort reverse DNS lookup
			hostname = address.getHostName();
			if (hostname == null || hostname.isEmpty()) {
				hostname = address.toString();
			}
		} catch (UnknownHostException ignore) {
			hostname = LOCAL_HOST;
		}
		return hostname;
	}

	/**
	 * 获取 服务器 HostIp
	 *
	 * @return HostIp
	 */
	public static String getHostIp() {
		String hostAddress;
		try {
			InetAddress address = INetUtil.getLocalHostLANAddress();
			// force a best effort reverse DNS lookup
			hostAddress = address.getHostAddress();
			if (hostAddress == null || hostAddress.isEmpty()) {
				hostAddress = address.toString();
			}
		} catch (UnknownHostException ignore) {
			hostAddress = LOCAL_HOST;
		}
		return hostAddress;
	}

	/**
	 * <a href="https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java">...</a>
	 *
	 * <p>
	 * Returns an <code>InetAddress</code> object encapsulating what is most likely the machine's LAN IP address.
	 * <p/>
	 * This method is intended for use as a replacement of JDK method <code>InetAddress.getLocalHost</code>, because
	 * that method is ambiguous on Linux systems. Linux systems enumerate the loopback network interface the same
	 * way as regular LAN network interfaces, but the JDK <code>InetAddress.getLocalHost</code> method does not
	 * specify the algorithm used to select the address returned under such circumstances, and will often return the
	 * loopback address, which is not valid for network communication. Details
	 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
	 * <p/>
	 * This method will scan all IP addresses on all network interfaces on the host machine to determine the IP address
	 * most likely to be the machine's LAN address. If the machine has multiple IP addresses, this method will prefer
	 * a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the machine has one (and will return the
	 * first site-local address if the machine has more than one), but if the machine does not hold a site-local
	 * address, this method will return simply the first non-loopback address found (IPv4 or IPv6).
	 * <p/>
	 * If this method cannot find a non-loopback address using this selection algorithm, it will fall back to
	 * calling and returning the result of JDK method <code>InetAddress.getLocalHost</code>.
	 * <p/>
	 *
	 * @throws UnknownHostException If the LAN address of the machine cannot be found.
	 */
	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
				NetworkInterface iface = ifaces.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
					InetAddress inetAddr = inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {

						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it immediately...
							return inetAddr;
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily site-local.
							// Store it as a candidate to be returned if site-local address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
							// only the first. For subsequent iterations, candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other non-loopback address.
				// Server might have a non-site-local address assigned to its NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress;
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost() returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	/**
	 * 尝试端口时候被占用
	 *
	 * @param port 端口号
	 * @return 没有被占用：true,被占用：false
	 */
	public static boolean tryPort(int port) {
		try (ServerSocket ignore = new ServerSocket(port)) {
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 解析客户端地址
	 * <p>
	 * 对端为公网地址时即客户端本身，忽略一切请求头；对端为内网地址时视为代理，按以下顺序判定：
	 * 沿 X-Forwarded-For 自右向左找到的第一个公网地址，即穿过内网代理链之前的客户端；
	 * 其次是公网的 X-Real-IP，覆盖对应网关标定与覆盖式写入的代理；
	 * 再次是 X-Forwarded-For 最左一段，即整条链都在内网时的链路起点；
	 * 最后是内网的 X-Real-IP，对应只写该头的代理；都没有则回退对端。
	 * X-Forwarded-For 只作为追加语义读取，所以公网客户端的真实地址总在其自带内容的右侧，无法借请求头改变结果；
	 * 内网客户端仍可改变结果，属有意接受的边界。可信前提是应用端口只从内网可达。
	 * 两个请求头都按到达顺序传入全部头行，部分代理会另起一行而不是追加到已有行。
	 *
	 * @param peer               连接对端地址
	 * @param realIpValues       X-Real-IP 全部头行
	 * @param forwardedForValues X-Forwarded-For 全部头行
	 * @return 客户端地址，无连接对端时为 null
	 */
	public static String resolveClientIp(String peer, List<String> realIpValues, List<String> forwardedForValues) {
		if (!isInternalIp(peer)) {
			return peer;
		}
		List<String> chain = nearestFirstChain(forwardedForValues);
		for (String hop : chain) {
			if (!isInternalIp(hop)) {
				return hop;
			}
		}
		String realIp = nearestRealIp(realIpValues);
		if (realIp != null && !isInternalIp(realIp)) {
			return realIp;
		}
		if (!chain.isEmpty()) {
			return chain.getLast();
		}
		return realIp != null ? realIp : peer;
	}

	/**
	 * 判断是否内网地址
	 * <p>
	 * 覆盖回环、任意本地、链路本地、站点本地、运营商级 NAT 段与 IPv6 唯一本地段，
	 * 后两段是云厂商负载均衡与容器网络常用而 JDK 未归入站点本地的范围。
	 *
	 * @param ip 地址字面量，主机名视为非内网
	 * @return boolean
	 */
	public static boolean isInternalIp(String ip) {
		String literal = normalizeIpLiteral(ip);
		if (literal == null) {
			return false;
		}
		InetAddress address = getInetAddress(literal);
		return address != null && isInternalIp(address);
	}

	/**
	 * 判断是否内网地址
	 *
	 * @param address InetAddress
	 * @return boolean
	 */
	public static boolean isInternalIp(InetAddress address) {
		if (address.isAnyLocalAddress() || address.isLoopbackAddress()
			|| address.isLinkLocalAddress() || address.isSiteLocalAddress()) {
			return true;
		}
		byte[] raw = address.getAddress();
		if (raw.length == 4) {
			// 100.64.0.0/10
			return (raw[0] & 0xFF) == 100 && (raw[1] & 0xC0) == 64;
		}
		// fc00::/7
		return raw.length == 16 && (raw[0] & 0xFE) == 0xFC;
	}

	/**
	 * 将地址字面量转成 InetAddress
	 * <p>
	 * 入参须为地址字面量，传入主机名会触发 DNS 解析。
	 *
	 * @param ip 地址字面量
	 * @return InetAddress，无法解析时为 null
	 */
	public static InetAddress getInetAddress(String ip) {
		try {
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			return null;
		}
	}

	/**
	 * 把 X-Forwarded-For 全部头行展开为离应用由近到远的合法地址链：后到的头行在前，同一行内自右向左
	 *
	 * @param forwardedForValues X-Forwarded-For 全部头行
	 * @return 地址链
	 */
	private static List<String> nearestFirstChain(List<String> forwardedForValues) {
		List<String> chain = new ArrayList<>();
		if (forwardedForValues == null) {
			return chain;
		}
		for (int line = forwardedForValues.size() - 1; line >= 0; line--) {
			String value = forwardedForValues.get(line);
			if (!isPresent(value)) {
				continue;
			}
			String[] segments = value.split(",");
			for (int index = segments.length - 1; index >= 0; index--) {
				String hop = normalizeIpLiteral(segments[index]);
				if (hop != null) {
					chain.add(hop);
				}
			}
		}
		return chain;
	}

	/**
	 * 取最后到达的合法 X-Real-IP，多行时后到的一行由更靠近应用的代理写入
	 *
	 * @param realIpValues X-Real-IP 全部头行
	 * @return 地址字面量，没有合法值时为 null
	 */
	private static String nearestRealIp(List<String> realIpValues) {
		if (realIpValues == null) {
			return null;
		}
		for (int line = realIpValues.size() - 1; line >= 0; line--) {
			String literal = normalizeIpLiteral(realIpValues.get(line));
			if (literal != null) {
				return literal;
			}
		}
		return null;
	}

	/**
	 * 归一化地址字面量：去除首尾空白、IPv4 的端口后缀、IPv6 的方括号与端口后缀
	 *
	 * @param value 待归一化的值
	 * @return 地址字面量，不合法时为 null
	 */
	private static String normalizeIpLiteral(String value) {
		if (!isPresent(value)) {
			return null;
		}
		String literal = value.trim();
		if (literal.length() > IP_LITERAL_MAX_LENGTH) {
			return null;
		}
		Matcher bracketed = BRACKETED_IPV6.matcher(literal);
		if (bracketed.matches()) {
			literal = bracketed.group(1);
		} else {
			Matcher withPort = IPV4_WITH_PORT.matcher(literal);
			if (withPort.matches()) {
				literal = withPort.group(1);
			}
		}
		return IP_LITERAL.matcher(literal).matches() ? literal : null;
	}

	/**
	 * 判断请求头值是否真实提供
	 *
	 * @param value 请求头值
	 * @return boolean
	 */
	private static boolean isPresent(String value) {
		return value != null && !value.isBlank() && !UNKNOWN.equalsIgnoreCase(value.trim());
	}
}
