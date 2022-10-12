package org.springblade.core.api.crypto.exception;

/**
 * <p>解密数据失败异常</p>
 *
 * @author licoy.cn
 */
public class DecryptBodyFailException extends RuntimeException {

	public DecryptBodyFailException(String message) {
		super(message);
	}
}
