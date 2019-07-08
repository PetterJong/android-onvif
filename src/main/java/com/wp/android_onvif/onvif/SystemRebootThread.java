package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.onvifBean.Digest;
import com.wp.android_onvif.onvifBean.MediaProfile;
import com.wp.android_onvif.util.Gsoap;
import com.wp.android_onvif.util.HttpUtil;
import com.wp.android_onvif.util.XmlDecodeUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 摄像头重启
 */

public class SystemRebootThread extends Thread {

    private Device device;
    private Context context;
    private SystemRebootCallBack callBack;

//    private WriteFileUtil util;

    public SystemRebootThread(Device device, Context context, SystemRebootCallBack callBack) {
        this.device = device;
        this.context = context;
        this.callBack = callBack;
//        util = new WriteFileUtil("onvif.txt");
    }

    @Override
    public void run() {
        super.run();
        try {
            //getProfiles，需要鉴权
            String postString = OnvifUtils.getPostString("systemReboot.xml", context, device,true);
            String getSystemDateAndTime = HttpUtil.postRequest(device.getServiceUrl(), postString);
            Log.v("MainActivity", getSystemDateAndTime);
            if(callBack != null){
                callBack.systemRebootResult(true, device, "");
            }
            //解析获取MediaProfile 集合
//            String uri = XmlDecodeUtil.getSnapshotUri(getSystemDateAndTime);
//            byte[] bytes = HttpUtil.getByteArray(uri);
//            String path = SDCardUtils.writeResoursToSDCard("hibox/pic" , "top.pic", bytes);
        } catch (Exception e) {
            e.printStackTrace();
            if(callBack != null){
                callBack.systemRebootResult(false, device, "摄像头重启失败");
            }
        }
    }

    /**
     * Author ： BlackHao
     * Time : 2018/1/11 14:24
     * Description : 获取 device 信息回调
     */
    public interface SystemRebootCallBack {
        void systemRebootResult(boolean isSuccess, Device device, String errorMsg);
    }
}
