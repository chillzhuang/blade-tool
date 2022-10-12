package org.springblade.core.api.crypto.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

/**
 * <p>解密信息输入流</p>
 *
 * @author licoy.cn, L.cm
 */
@Getter
@RequiredArgsConstructor
public class DecryptHttpInputMessage implements HttpInputMessage {
	private final InputStream body;
	private final HttpHeaders headers;
}
