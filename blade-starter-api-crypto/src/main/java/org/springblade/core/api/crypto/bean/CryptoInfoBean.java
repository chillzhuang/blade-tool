package org.springblade.core.api.crypto.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springblade.core.api.crypto.enums.CryptoType;

/**
 * <p>加密注解信息</p>
 *
 * @author licoy.cn, L.cm
 */
@Getter
@RequiredArgsConstructor
public class CryptoInfoBean {

	/**
	 * 加密类型
	 */
	private final CryptoType type;
	/**
	 * 私钥
	 */
	private final String secretKey;

}
