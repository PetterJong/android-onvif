package com.balckhao.blackonvif.onvif;

import android.content.Context;

import com.balckhao.blackonvif.onvifBean.Device;
import com.balckhao.blackonvif.onvifBean.Digest;
import com.balckhao.blackonvif.util.Gsoap;

import java.io.IOException;
import java.io.InputStream;

public class OnvifUtils {

    /**
     * 通过用户名/密码/assets 文件获取对应需要发送的String
     * @param fileName assets文件名
     * @param context context
     * @param device onvif设备
     * @param needDigest 是否需要鉴权
     * @param params XML 参数
     * @return 需要发送的 string
     */

    public static String getPostString(String fileName, Context context,  Device device, boolean needDigest, String... params) throws IOException {
        //读取文件内容
        String postString = "";
        InputStream is = context.getAssets().open(fileName);
        byte[] postData = new byte[is.available()];
        if (is.read(postData) > 0) {
            postString = new String(postData, "utf-8");
        }
        //获取digest
        Digest digest = Gsoap.getDigest(device.getUserName(), device.getPsw());
        //需要digest
        if (needDigest && digest != null) {
            if (params.length > 0) {
                postString = String.format(postString, digest.getUserName(),
                        digest.getEncodePsw(), digest.getNonce(), digest.getCreatedTime(), params[0]);
            } else {
                postString = String.format(postString, digest.getUserName(),
                        digest.getEncodePsw(), digest.getNonce(), digest.getCreatedTime());
            }

        }
        return postString;
    }
}
