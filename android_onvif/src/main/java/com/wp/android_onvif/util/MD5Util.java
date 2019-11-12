package com.wp.android_onvif.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 字符串MD5签名
 * 
 */
public class MD5Util {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 把字节数组转换为十六进制的字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 十六进制的字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (byte aB : b) {
			resultSb.append(byteToHexString(aB));
		}
		return resultSb.toString();
	}

	/**
	 * 把一个字节转换为一个十六进制的字符串
	 * 
	 * @param b
	 *            字节
	 * @return 十六进制的字符串
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 把字符串以MD5的方式加密
	 * 
	 * @param origin
	 *            需要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
		}
		return resultString;
	}

	public static void main(String[] agrs) {

		System.out.println(MD5Encode("222222"));
	}

	/**
	 * 获取单个文件的MD5值！
	 * 当最大为位0时会被忽略
	 * @deprecated constructors that take a{{@link #getFileMD5(File)}}
	 * @param file 文件
	 * @return 文件的MD5值
	 */
	public static String getFileMD5_2(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}




	protected static MessageDigest messageDigest = null;

	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {

		}
	}


	/**
	 * 生成文件的Md5校验值
	 *
	 * @param file 文件
	 * @return Md5校验值
	 */
	public static String getFileMD5(File file) {
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				messageDigest.update(buffer, 0, numRead);
			}
			return bufferToHex(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}


	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringBuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int i = m; i < k; i++) {
			appendHexPair(bytes[i], stringBuffer);
		}
		return stringBuffer.toString();
	}


	private static void appendHexPair(byte bt, StringBuffer stringBuffer) {
		String c0 = hexDigits[(bt & 0xf0) >> 4];
		String c1 = hexDigits[bt & 0xf];
		stringBuffer.append(c0);
		stringBuffer.append(c1);
	}
}