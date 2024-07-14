package org.springblade.core.api.crypto.util;

import org.springblade.core.api.crypto.annotation.decrypt.ApiDecrypt;
import org.springblade.core.api.crypto.annotation.encrypt.ApiEncrypt;
import org.springblade.core.api.crypto.bean.CryptoInfoBean;
import org.springblade.core.api.crypto.config.ApiCryptoProperties;
import org.springblade.core.api.crypto.enums.CryptoType;
import org.springblade.core.api.crypto.exception.EncryptBodyFailException;
import org.springblade.core.api.crypto.exception.EncryptMethodNotFoundException;
import org.springblade.core.api.crypto.exception.KeyNotConfiguredException;
import org.springblade.core.tool.utils.AesUtil;
import org.springblade.core.tool.utils.ClassUtil;
import org.springblade.core.tool.utils.DesUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/**
 * <p>辅助检测工具类</p>
 *
 * @author licoy.cn, L.cm
 */
public class ApiCryptoUtil {

	/**
	 * 获取方法控制器上的加密注解信息
	 *
	 * @param methodParameter 控制器方法
	 * @return 加密注解信息
	 */
	@Nullable
	public static CryptoInfoBean getEncryptInfo(MethodParameter methodParameter) {
		ApiEncrypt encryptBody = ClassUtil.getAnnotation(methodParameter.getMethod(), ApiEncrypt.class);
		if (encryptBody == null) {
			return null;
		}
		return new CryptoInfoBean(encryptBody.value(), encryptBody.secretKey());
	}

	/**
	 * 获取方法控制器上的解密注解信息
	 *
	 * @param methodParameter 控制器方法
	 * @return 加密注解信息
	 */
	@Nullable
	public static CryptoInfoBean getDecryptInfo(MethodParameter methodParameter) {
		ApiDecrypt decryptBody = ClassUtil.getAnnotation(methodParameter.getMethod(), ApiDecrypt.class);
		if (decryptBody == null) {
			return null;
		}
		return new CryptoInfoBean(decryptBody.value(), decryptBody.secretKey());
	}

	/**
	 * 选择加密方式并进行加密
	 *
	 * @param jsonData json 数据
	 * @param infoBean 加密信息
	 * @return 加密结果
	 */
	public static String encryptData(ApiCryptoProperties properties, byte[] jsonData, CryptoInfoBean infoBean) {
		CryptoType type = infoBean.getType();
		if (type == null) {
			throw new EncryptMethodNotFoundException();
		}
		String secretKey = infoBean.getSecretKey();
		if (type == CryptoType.DES) {
			secretKey = ApiCryptoUtil.checkSecretKey(properties.getDesKey(), secretKey, "DES");
			return DesUtil.encryptToBase64(jsonData, secretKey);
		}
		if (type == CryptoType.AES) {
			secretKey = ApiCryptoUtil.checkSecretKey(properties.getAesKey(), secretKey, "AES");
			return AesUtil.encryptToBase64(jsonData, secretKey);
		}
		throw new EncryptBodyFailException();
	}

	/**
	 * 选择加密方式并进行解密
	 *
	 * @param bodyData byte array
	 * @param infoBean 加密信息
	 * @return 解密结果
	 */
	public static byte[] decryptData(ApiCryptoProperties properties, byte[] bodyData, CryptoInfoBean infoBean) {
		CryptoType type = infoBean.getType();
		if (type == null) {
			throw new EncryptMethodNotFoundException();
		}
		String secretKey = infoBean.getSecretKey();
		if (type == CryptoType.AES) {
			secretKey = ApiCryptoUtil.checkSecretKey(properties.getAesKey(), secretKey, "AES");
			return AesUtil.decryptFormBase64(bodyData, secretKey);
		}
		if (type == CryptoType.DES) {
			secretKey = ApiCryptoUtil.checkSecretKey(properties.getDesKey(), secretKey, "DES");
			return DesUtil.decryptFormBase64(bodyData, secretKey);
		}
		throw new EncryptMethodNotFoundException();
	}

	/**
	 * 检验私钥
	 *
	 * @param k1      配置的私钥
	 * @param k2      注解上的私钥
	 * @param keyName key名称
	 * @return 私钥
	 */
	private static String checkSecretKey(String k1, String k2, String keyName) {
		if (StringUtil.isBlank(k1) && StringUtil.isBlank(k2)) {
			throw new KeyNotConfiguredException(String.format("%s key is not configured (未配置%s)", keyName, keyName));
		}
		return StringUtil.isBlank(k2) ? k1 : k2;
	}
}
