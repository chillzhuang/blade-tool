/**
 * Copyright (c) 2018-2028, DreamLu 卢春梦 (qq596392912@gmail.com).
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
package org.springblade.core.tool.utils;

import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * 完全兼容微信所使用的AES加密方式。
 * aes的key必须是256byte长（比如32个字符），可以使用AesKit.genAesKey()来生成一组key
 *
 * @author L.cm
 */
public class AesUtil {

	private AesUtil() {
	}

	public static String genAesKey() {
		return StringUtil.random(32);
	}

	public static byte[] encrypt(byte[] content, String aesTextKey) {
		return encrypt(content, aesTextKey.getBytes(Charsets.UTF_8));
	}

	public static byte[] encrypt(String content, String aesTextKey) {
		return encrypt(content.getBytes(Charsets.UTF_8), aesTextKey.getBytes(Charsets.UTF_8));
	}

	public static byte[] encrypt(String content, java.nio.charset.Charset charset, String aesTextKey) {
		return encrypt(content.getBytes(charset), aesTextKey.getBytes(Charsets.UTF_8));
	}

	public static byte[] decrypt(byte[] content, String aesTextKey) {
		return decrypt(content, aesTextKey.getBytes(Charsets.UTF_8));
	}

	public static String decryptToStr(byte[] content, String aesTextKey) {
		return new String(decrypt(content, aesTextKey.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
	}

	public static String decryptToStr(byte[] content, String aesTextKey, java.nio.charset.Charset charset) {
		return new String(decrypt(content, aesTextKey.getBytes(Charsets.UTF_8)), charset);
	}

	public static byte[] encrypt(byte[] content, byte[] aesKey) {
		Assert.isTrue(aesKey.length == 32, "IllegalAesKey, aesKey's length must be 32");
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
			return cipher.doFinal(Pkcs7Encoder.encode(content));
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static byte[] decrypt(byte[] encrypted, byte[] aesKey) {
		Assert.isTrue(aesKey.length == 32, "IllegalAesKey, aesKey's length must be 32");
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
			cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
			return Pkcs7Encoder.decode(cipher.doFinal(encrypted));
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 提供基于PKCS7算法的加解密接口.
	 */
	static class Pkcs7Encoder {
		static int BLOCK_SIZE = 32;

		static byte[] encode(byte[] src) {
			int count = src.length;
			// 计算需要填充的位数
			int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
			if (amountToPad == 0) {
				amountToPad = BLOCK_SIZE;
			}
			// 获得补位所用的字符
			byte pad = (byte) (amountToPad & 0xFF);
			byte[] pads = new byte[amountToPad];
			for (int index = 0; index < amountToPad; index++) {
				pads[index] = pad;
			}
			int length = count + amountToPad;
			byte[] dest = new byte[length];
			System.arraycopy(src, 0, dest, 0, count);
			System.arraycopy(pads, 0, dest, count, amountToPad);
			return dest;
		}

		static byte[] decode(byte[] decrypted) {
			int pad = (int) decrypted[decrypted.length - 1];
			if (pad < 1 || pad > BLOCK_SIZE) {
				pad = 0;
			}
			if (pad > 0) {
				return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
			}
			return decrypted;
		}
	}
}
