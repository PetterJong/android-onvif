package com.balckhao.blackonvif.util;

import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author ： BlackHao
 * Time : 2018/2/6 14:23
 * Description : 写文件工具类
 */

public class WriteFileUtil {

    private FileOutputStream fos;

    public WriteFileUtil(String fileName) {
        try {
            fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/SONA/" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeData(byte[] data) {
        try {
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finishWrite() {
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
