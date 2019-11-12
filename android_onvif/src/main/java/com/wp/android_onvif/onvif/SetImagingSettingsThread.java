package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.onvifBean.MediaProfile;
import com.wp.android_onvif.util.HttpUtil;

/**
 * 设置摄像机参数
 */
public class SetImagingSettingsThread extends Thread {
    private static String tag = "OnvifSdk";

    private Device device;
    private Context context;
    private SetImagingSettingsCallBack callBack;
    private MediaProfile mediaProfile;


    private Float brightness; // 亮度
//    private WriteFileUtil util;

    public SetImagingSettingsThread(Device device, Context context, SetImagingSettingsCallBack callBack) {
        this.device = device;
        this.context = context;
        this.callBack = callBack;
        if (device.getProfiles() != null && device.getProfiles().size() > 0) {
            this.mediaProfile = device.getProfiles().iterator().next();
        }
//        util = new WriteFileUtil("onvif.txt");
    }

    public void setBrightness(Float brightness) {
        this.brightness = brightness;
    }


    @Override
    public void run() {
        super.run();
        try {
            if (brightness == null) {
                callBack.setImagingSettingsThreadResult(false, device, "未设置参数");
                return;
            }
            if (mediaProfile == null || mediaProfile.getVideSource() == null) {
                callBack.setImagingSettingsThreadResult(false, device, "未获取到设备token");
                return;
            }
            //SetImagingSettingsThread，需要鉴权
            String postString = OnvifUtils.getPostString("setImagingSettings.xml", context, device, true,
                    mediaProfile.getVideSource().getVideoSourceToken(), String.valueOf(brightness));
            String setImagingSettings = HttpUtil.postRequest(device.getImageUrl(), postString);
            Log.v(tag, setImagingSettings);
            callBack.setImagingSettingsThreadResult(true, device, "");
        } catch (Exception e) {
            e.printStackTrace();
            callBack.setImagingSettingsThreadResult(false, device, e.toString());
        }
    }


    public interface SetImagingSettingsCallBack {
        void setImagingSettingsThreadResult(boolean isSuccess, Device device, String errorMsg);
    }

}