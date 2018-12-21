package com.balckhao.blackonvif.util;

import android.util.Base64;


import com.balckhao.blackonvif.onvifBean.Digest;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Author ： BlackHao
 * Time : 2018/1/10 09:39
 * Description : 获取 onvif Digest
 */

public class Gsoap {

    /**
     * Digest = B64ENCODE( SHA1( B64DECODE( Nonce ) + Date + Password ) )
     * 生成 Digest
     */
    public static Digest getDigest(String userName, String psw) {
        Digest digest = new Digest();
        String nonce = getNonce();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.CHINA);
        String time = df.format(new Date());
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // nonce需要用Base64解码一次
            byte[] b1 = Base64.decode(nonce.getBytes(), Base64.DEFAULT);
            // 生成字符字节流
            byte[] b2 = time.getBytes(); // "2018-01-10T11:00:00Z";
            byte[] b3 = psw.getBytes();
            // 根据我们传得值的长度生成流的长度
            byte[] b4;
            // 利用sha-1加密字符
            md.update(b1, 0, b1.length);
            md.update(b2, 0, b2.length);
            md.update(b3, 0, b3.length);
            // 生成sha-1加密后的流
            b4 = md.digest();
            // 生成最终的加密字符串
            String result = new String(Base64.encode(b4, Base64.DEFAULT));
//            Log.e("Gsoap", result);
            digest.setNonce(nonce);
            digest.setCreatedTime(time);
            digest.setUserName(userName);
            digest.setEncodePsw(result);
            return digest;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取 Nonce
     *
     * @return Nonce
     */
    private static String getNonce() {
        //初始化随机数
        Random r = new Random();
        String text = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String nonce = "";
        for (int i = 0; i < text.length(); i++) {
            int index = r.nextInt(text.length());
            nonce = nonce + text.charAt(index);
        }
        return nonce;
    }
}
