package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.onvifBean.MediaProfile;
import com.wp.android_onvif.util.HttpUtil;

/**
 * 获取摄像机截图
 */
public class GetImagingSettingsThread extends Thread {
    private static String tag = "OnvifSdk";

    private Device device;
    private Context context;
    private GetImagingSettingsCallBack callBack;
    private MediaProfile mediaProfile;


    public GetImagingSettingsThread(Device device, Context context, GetImagingSettingsCallBack callBack) {
        this.device = device;
        this.context = context;
        this.callBack = callBack;
        if (device.getProfiles() != null && device.getProfiles().size() > 0) {
            this.mediaProfile = device.getProfiles().iterator().next();
        }
//        util = new WriteFileUtil("onvif.txt");
    }



    @Override
    public void run() {
        super.run();
        try {
            if (mediaProfile == null || mediaProfile.getVideSource() == null) {
                callBack.getImagingSettingsThreadResult(false, device, "未获取到设备token");
                return;
            }
            //SetImagingSettingsThread，需要鉴权
            String postString = OnvifUtils.getPostString("getImagingSettings.xml", context, device, true,
                    mediaProfile.getVideSource().getToken());
            String getImagingSettings = HttpUtil.postRequest(device.getMediaUrl(), postString);
            // TODO 解析获取自己需要的值
            Log.v(tag, getImagingSettings);
            callBack.getImagingSettingsThreadResult(true, device, getImagingSettings);
        } catch (Exception e) {
            e.printStackTrace();
            callBack.getImagingSettingsThreadResult(false, device, e.toString());
        }
    }


    public interface GetImagingSettingsCallBack {
        void getImagingSettingsThreadResult(boolean isSuccess, Device device, String errorMsg);
    }

}