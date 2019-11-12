package com.wp.android_onvif.util;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

	/**
	 * 将二进制文件写入到制定的文件夹中
	 * 
	 * @param parentPath
	 * @param fileName
	 * @param content
	 * @return boolean
	 */

	public static String writeResoursToSDCard(String parentPath, String fileName,
			byte[] content) {
//		Log.v("123", "parentPath :"+parentPath);
		File parentFile = new File(parentPath);
		
		if (!parentFile.mkdirs()) {
			parentFile.mkdirs();
//			Log.v("123", "parentFile.mkdirs:"+parentFile.mkdirs());
		}
		String path = parentPath + fileName;
//		Log.v("123", "path :"+path);
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(path);
			
			fo.write(content, 0, content.length);
			return path;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fo != null) {
					fo.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取SDCard的抽象路径
	 *
	 * @return File
	 */
	public static File getSDCardFile() {
		if (getSDCardState()) {
			return Environment.getExternalStorageDirectory();
		}
		return null;
	}

	/**
	 * 获取SDCard状态
	 *
	 * @return boolean
	 */
	public static boolean getSDCardState() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

}
