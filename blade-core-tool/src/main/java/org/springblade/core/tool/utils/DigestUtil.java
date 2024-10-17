/**
 * Copyright (c) 2018-2099, DreamLu 卢春梦 (qq596392912@gmail.com).
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

import org.springframework.lang.Nullable;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密相关工具类直接使用Spring util封装，减少jar依赖
 *
 * @author L.cm
 */
public class DigestUtil extends org.springframework.util.DigestUtils {

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(final String data) {
		return DigestUtil.md5DigestAsHex(data.getBytes(Charsets.UTF_8));
	}

	/**
	 * Return a hexadecimal string representation of the MD5 digest of the given bytes.
	 *
	 * @param bytes the bytes to calculate the digest over
	 * @return a hexadecimal digest string
	 */
	public static String md5Hex(final byte[] bytes) {
		return DigestUtil.md5DigestAsHex(bytes);
	}

	private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

	public static String sha1(String srcStr) {
		return hash("SHA-1", srcStr);
	}

	public static String sha256(String srcStr) {
		return hash("SHA-256", srcStr);
	}

	public static String sha384(String srcStr) {
		return hash("SHA-384", srcStr);
	}

	public static String sha512(String srcStr) {
		return hash("SHA-512", srcStr);
	}


	/**
	 * hmacMd5
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a byte array
	 */
	public static byte[] hmacMd5(String data, String key) {
		return DigestUtil.hmacMd5(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacMd5
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a byte array
	 */
	public static byte[] hmacMd5(final byte[] bytes, String key) {
		return DigestUtil.digestHmac("HmacMD5", bytes, key);
	}

	/**
	 * hmacMd5 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacMd5Hex(String data, String key) {
		return DigestUtil.encodeHex(hmacMd5(data.getBytes(Charsets.UTF_8), key));
	}

	/**
	 * hmacMd5 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacMd5Hex(final byte[] bytes, String key) {
		return DigestUtil.encodeHex(hmacMd5(bytes, key));
	}

	/**
	 * hmacSha1
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a byte array
	 */
	public static byte[] hmacSha1(String data, String key) {
		return DigestUtil.hmacSha1(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha1
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a byte array
	 */
	public static byte[] hmacSha1(final byte[] bytes, String key) {
		return DigestUtil.digestHmac("HmacSHA1", bytes, key);
	}

	/**
	 * hmacSha1 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha1Hex(String data, String key) {
		return DigestUtil.encodeHex(hmacSha1(data.getBytes(Charsets.UTF_8), key));
	}

	/**
	 * hmacSha1 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha1Hex(final byte[] bytes, String key) {
		return DigestUtil.encodeHex(hmacSha1(bytes, key));
	}

	/**
	 * hmacSha224
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static byte[] hmacSha224(String data, String key) {
		return DigestUtil.hmacSha224(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha224
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static byte[] hmacSha224(final byte[] bytes, String key) {
		return DigestUtil.digestHmac("HmacSHA224", bytes, key);
	}

	/**
	 * hmacSha224 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha224Hex(String data, String key) {
		return DigestUtil.encodeHex(hmacSha224(data.getBytes(Charsets.UTF_8), key));
	}

	/**
	 * hmacSha224 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha224Hex(final byte[] bytes, String key) {
		return DigestUtil.encodeHex(hmacSha224(bytes, key));
	}

	/**
	 * hmacSha256
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static byte[] hmacSha256(String data, String key) {
		return DigestUtil.hmacSha256(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha256
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a byte array
	 */
	public static byte[] hmacSha256(final byte[] bytes, String key) {
		return DigestUtil.digestHmac("HmacSHA256", bytes, key);
	}

	/**
	 * hmacSha256 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a byte array
	 */
	public static String hmacSha256Hex(String data, String key) {
		return DigestUtil.encodeHex(hmacSha256(data.getBytes(Charsets.UTF_8), key));
	}

	/**
	 * hmacSha256 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha256Hex(final byte[] bytes, String key) {
		return DigestUtil.encodeHex(hmacSha256(bytes, key));
	}

	/**
	 * hmacSha384
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a byte array
	 */
	public static byte[] hmacSha384(String data, String key) {
		return DigestUtil.hmacSha384(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha384
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a byte array
	 */
	public static byte[] hmacSha384(final byte[] bytes, String key) {
		return DigestUtil.digestHmac("HmacSHA384", bytes, key);
	}

	/**
	 * hmacSha384 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha384Hex(String data, String key) {
		return DigestUtil.encodeHex(hmacSha384(data.getBytes(Charsets.UTF_8), key));
	}

	/**
	 * hmacSha384 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha384Hex(final byte[] bytes, String key) {
		return DigestUtil.encodeHex(hmacSha384(bytes, key));
	}

	/**
	 * hmacSha512
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a byte array
	 */
	public static byte[] hmacSha512(String data, String key) {
		return DigestUtil.hmacSha512(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha512
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a byte array
	 */
	public static byte[] hmacSha512(final byte[] bytes, String key) {
		return DigestUtil.digestHmac("HmacSHA512", bytes, key);
	}

	/**
	 * hmacSha512 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha512Hex(String data, String key) {
		return DigestUtil.encodeHex(hmacSha512(data.getBytes(Charsets.UTF_8), key));
	}

	/**
	 * hmacSha512 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha512Hex(final byte[] bytes, String key) {
		return DigestUtil.encodeHex(hmacSha512(bytes, key));
	}

	/**
	 * digest Hmac Hex
	 *
	 * @param algorithm 算法
	 * @param text      text
	 * @return digest as a hex string
	 */
	public static String digestHmacHex(String algorithm, String text, String key) {
		return digestHmacHex(algorithm, text.getBytes(StandardCharsets.UTF_8), key);
	}

	/**
	 * digest Hmac Hex
	 *
	 * @param algorithm 算法
	 * @param bytes     Data to digest
	 * @return digest as a hex string
	 */
	public static String digestHmacHex(String algorithm, final byte[] bytes, String key) {
		return DigestUtil.encodeHex(DigestUtil.digestHmac(algorithm, bytes, key));
	}

	/**
	 * digest Hmac
	 *
	 * @param algorithm 算法
	 * @param bytes     Data to digest
	 * @return digest as a byte array
	 */
	public static byte[] digestHmac(String algorithm, final byte[] bytes, String key) {
		SecretKey secretKey = new SecretKeySpec(key.getBytes(Charsets.UTF_8), algorithm);
		try {
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			return mac.doFinal(bytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * encode Hex
	 *
	 * @param bytes Data to Hex
	 * @return bytes as a hex string
	 */
	public static String encodeHex(byte[] bytes) {
		return HexUtil.encodeToString(bytes);
	}

	/**
	 * decode Hex
	 *
	 * @param hexStr Hex string
	 * @return decode hex to bytes
	 */
	public static byte[] decodeHex(final String hexStr) {
		return HexUtil.decode(hexStr);
	}

	public static String hash(String algorithm, String srcStr) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] bytes = md.digest(srcStr.getBytes(Charsets.UTF_8));
			return toHex(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static String toHex(byte[] bytes) {
		StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}

	public static boolean slowEquals(@Nullable String a, @Nullable String b) {
		if (a == null || b == null) {
			return false;
		}
		return slowEquals(a.getBytes(Charsets.UTF_8), b.getBytes(Charsets.UTF_8));
	}

	public static boolean slowEquals(@Nullable byte[] a, @Nullable byte[] b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	/**
	 * 自定义加密 先MD5再SHA1
	 *
	 * @param data 数据
	 * @return String
	 */
	public static String encrypt(String data) {
		return sha1(md5Hex(data));
	}

}
