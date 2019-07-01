package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.HttpUtil;

/**
 * 获取摄像机时间
 */
public class GetSystemDateAndTimeThread extends Thread{
    private static String tag = "OnvifSdk";

    private Device device;
    private Context context;


    public GetSystemDateAndTimeThread(Device device, Context context) {
        this.device = device;
        this.context = context;
    }


    @Override
    public void run() {
        super.run();
        try {
            //getProfiles，需要鉴权
            String postString = OnvifUtils.getPostString("getSystemDateAndTime.xml", context, device,true);
            String getSystemDateAndTime = HttpUtil.postRequest(device.getServiceUrl(), postString);
            Log.v(tag, getSystemDateAndTime);
            //解析获取MediaProfile 集合
//            String uri = XmlDecodeUtil.getSnapshotUri(getSystemDateAndTime);
//            byte[] bytes = HttpUtil.getByteArray(uri);
//            String path = SDCardUtils.writeResoursToSDCard("hibox/pic" , "top.pic", bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
