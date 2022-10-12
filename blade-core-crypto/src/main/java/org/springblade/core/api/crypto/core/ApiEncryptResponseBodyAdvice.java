package org.springblade.core.api.crypto.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.api.crypto.annotation.encrypt.ApiEncrypt;
import org.springblade.core.api.crypto.bean.CryptoInfoBean;
import org.springblade.core.api.crypto.config.ApiCryptoProperties;
import org.springblade.core.api.crypto.exception.EncryptBodyFailException;
import org.springblade.core.api.crypto.util.ApiCryptoUtil;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.ClassUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * 响应数据的加密处理<br>
 * 本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.ResponseBody}</strong>
 * 或者控制类上含有<strong>{@link org.springframework.web.bind.annotation.RestController}</strong>
 * 以及package为<strong><code>org.springblade.core.api.signature.annotation.encrypt</code></strong>下的注解有效
 *
 * @author licoy.cn, L.cm
 * @see ResponseBodyAdvice
 */
@Slf4j
@Order(1)
@AutoConfiguration
@ControllerAdvice
@RequiredArgsConstructor
@ConditionalOnProperty(value = ApiCryptoProperties.PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
public class ApiEncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {
	private final ApiCryptoProperties properties;

	@Override
	public boolean supports(MethodParameter returnType, @NonNull Class converterType) {
		return ClassUtil.isAnnotated(returnType.getMethod(), ApiEncrypt.class);
	}

	@Nullable
	@Override
	public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
								  @NonNull Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
		if (body == null) {
			return null;
		}
		response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
		CryptoInfoBean cryptoInfoBean = ApiCryptoUtil.getEncryptInfo(returnType);
		if (cryptoInfoBean != null) {
			byte[] bodyJsonBytes = JsonUtil.toJsonAsBytes(body);
			return ApiCryptoUtil.encryptData(properties, bodyJsonBytes, cryptoInfoBean);
		}
		throw new EncryptBodyFailException();
	}

}
