package com.wysoft.https_base.util;

import java.security.MessageDigest;

/**
 * MD5加密工具类.
 * @author Wuyong
 *
 */
public class MD5 {
	private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	/**
	 * 创建通过MD5加密的密文密码.
	 * @param inputString 输入串
	 * @return 加密后的密码
	 */
	public static String createPassword(String inputString) {
		return encodeByMD5(inputString);
	}
	
	/**
	 * 验证MD5密码.
	 * @param password 源密码
	 * @param inputString 输入密码
	 * @return boolean
	 */
	public static boolean authenticatePassword(String password,
			String inputString) {
		return (password.equals(encodeByMD5(inputString)));
	}
	
	/**
	 * 对字符串使用MD5算法加密.
	 * @param originString 源串
	 * @return 密文
	 */
	public static String encodeByMD5(String originString) {
		if (originString != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] results = md.digest(originString.getBytes());
				return byteArrayToHexString(results);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; ++i) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static void main(String[] args) {
		System.out.println(createPassword("123"));
	}
}
