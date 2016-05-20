package com.schytd.discount.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;

public class NetTools {
	/**
	 * 通过byte[]获取对应的MD5码
	 *
	 * @param bytes
	 *            数据
	 * @return 对应的MD5码
	 */
	public static String getMD5Code(byte[] bytes) {
		String md5Code = null;
		if (bytes == null)
			return null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes, 0, bytes.length);
			BigInteger bigInt = new BigInteger(1, digest.digest());
			md5Code = bigInt.toString(16);
		} catch (Exception e) {
//			log.error("获取MD5码失败！失败原因：" + e.getMessage());
		}
		return md5Code;
	}

	/**
	 * 二进制转十六进制字符串
	 *
	 * @param bytes
	 * @return
	 */
	public static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

	/**
	 * 获取SHA1Digest码
	 *
	 * @param data
	 * @return
	 */
	public static byte[] getSHA1Digest(String data) {
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			bytes = md.digest(data.getBytes("UTF-8"));
		} catch (Exception e) {
//			log.error("获取SHA1码失败！失败原因：" + e.getMessage());
		}
		return bytes;

	}
	public static String sign(List<NameValuePair> list,String secret) {
		List<String> keys = new ArrayList<String>();
		StringBuilder strBuilder = new StringBuilder();
		for(NameValuePair valuePair : list) {
			keys.add(valuePair.getName());
		}
		Collections.sort(keys);
		strBuilder.append(secret);
		for(String key : keys) {
				for(NameValuePair valuePair : list) {
					if(key.equals(valuePair.getName())) {
						if (!StrTools.isNull(key)) {
							strBuilder.append(key);
							strBuilder.append(valuePair.getValue());
						}
						break;
					}
			}
		}
		strBuilder.append(secret);
		return byte2hex(getSHA1Digest(strBuilder.toString()));
	}
}
