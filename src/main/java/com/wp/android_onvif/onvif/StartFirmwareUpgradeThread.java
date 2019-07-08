package com.wp.android_onvif.onvif;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.HttpUtil;
import com.wp.android_onvif.util.XmlDecodeUtil;

/**
 * 固件升级功能
 */

public class StartFirmwareUpgradeThread extends Thread {

    private Device device;
    private Context context;
    private String filePath; // "sdcard/hibox/file/test0428.bin"
    private StartFirmwareUpgradeCallBack callBack;

//    private WriteFileUtil util;

    public StartFirmwareUpgradeThread(String filePath, Device device, Context context, StartFirmwareUpgradeCallBack callBack) {
        this.filePath = filePath;
        this.device = device;
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        super.run();
        try {
            //getCapabilities，不需要鉴权
            String postString = OnvifUtils.getPostString("startFirmwareUpgrade.xml", context, device, false);
            String caps = HttpUtil.postRequest(device.getServiceUrl(), postString);
            //解析返回的xml数据获取存在的url
            try {
                String uploadUri = XmlDecodeUtil.getUploadUri(caps);
                Log.d("MainActivity", uploadUri);
                if(TextUtils.isEmpty(uploadUri)){
                    callBack.startFirmwareUpgradeResult(false, device, "uploadUri empty");
                }  else {
                    boolean result = HttpUtil.upload(uploadUri, filePath);
                    callBack.startFirmwareUpgradeResult(result, device, "");
                }
            } catch (Exception e){
                callBack.startFirmwareUpgradeResult(true, device, e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            callBack.startFirmwareUpgradeResult(false,device, e.toString());
        }
    }


    /**
     * Author ： BlackHao
     * Time : 2018/1/11 14:24
     * Description : 获取 device 信息回调
     */
    public interface StartFirmwareUpgradeCallBack {
        void startFirmwareUpgradeResult(boolean isSuccess, Device device, String errorMsg);
    }
}
