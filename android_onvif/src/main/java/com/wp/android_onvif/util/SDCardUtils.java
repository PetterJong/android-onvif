package com.wp.android_onvif.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SDCardUtils {


	//  <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
	// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	//  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

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

	/**
	 * 获取SDCard的抽象路径
	 * 
	 * @return File
	 */
	public static File getSDCardFilePath() {
		if (getSDCardState()) {
			return Environment.getExternalStorageDirectory();
		}
		return null;
	}

	/**
	 * 过得SDCard的总空间大小
	 * 
	 * @return long 单位 M
	 */
	@SuppressWarnings("deprecation")
	public static long getSDCardSize() {
		if (getSDCardFilePath() != null) {
			StatFs fs = new StatFs(getSDCardFilePath().getAbsolutePath());
			return fs.getBlockSize() * fs.getBlockCount() / 1024 / 1024;
		}
		return 0;
	}

	/**
	 * 得到SDCard剩余空间大小
	 * 
	 * @return long 单位M
	 */
	@SuppressWarnings("deprecation")
	public static long getSDCardFreeSize() {
		if (getSDCardFilePath() != null) {
			StatFs fs = new StatFs(getSDCardFilePath().getAbsolutePath());
			return fs.getBlockSize() * fs.getFreeBlocks() / 1024 / 1024;
		}
		return 0;
	}

	/**
	 * 将二进制文件写入到制定的文件夹中
	 * 
	 * @param folder
	 * @param fileName
	 * @param content
	 * @return boolean
	 */

	public static String writeResoursToSDCard(String folder, String fileName,
			byte[] content) {
		String parentPath = getSDCardFilePath().getAbsolutePath()
				+File.separator + folder;
//		Log.v("123", "parentPath :"+parentPath);
		File parentFile = new File(parentPath);
		
		if (!parentFile.mkdirs()) {
			parentFile.mkdirs();
//			Log.v("123", "parentFile.mkdirs:"+parentFile.mkdirs());
		}
		String path = parentPath + File.separator + fileName;
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
	
	public static byte[] getResoursFromSDCard(String dirpath){
		String path = getSDCardFilePath().getAbsolutePath()+"/"+dirpath;
		Log.v("123","path :" +path);
		File file = new File(path);
		FileInputStream fi = null;
		ByteArrayOutputStream baos = null;
		try {
			fi = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			int len = -1;
			byte[] buf = new byte[1024];
			while((len = fi.read(buf)) != -1){
				baos.write(buf, 0, len);
			}
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(baos != null){
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(fi != null){
					try {
						fi.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

}
