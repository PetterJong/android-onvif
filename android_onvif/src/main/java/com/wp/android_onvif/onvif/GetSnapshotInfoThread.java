package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.onvifBean.MediaProfile;
import com.wp.android_onvif.util.HttpUtil;
import com.wp.android_onvif.util.SDCardUtils;
import com.wp.android_onvif.util.XmlDecodeUtil;

public class GetSnapshotInfoThread extends Thread{


    private Device device;
    private Context context;
    private GetSnapshotInfoThread.GetSnapshotInfoCallBack callBack;
    private MediaProfile mediaProfile;

//    private WriteFileUtil util;

    public GetSnapshotInfoThread(Device device, Context context, GetSnapshotInfoThread.GetSnapshotInfoCallBack callBack) {
        this.device = device;
        this.context = context;
        this.callBack = callBack;
        if(device.getProfiles() != null && device.getProfiles().size() > 0){
            this.mediaProfile = device.getProfiles().iterator().next();
        }
//        util = new WriteFileUtil("onvif.txt");
    }

    @Override
    public void run() {
        super.run();
        try {
            //getProfiles，需要鉴权
            String postString = OnvifUtils.getPostString("getSnapshotUri.xml", context, device,true, "000");
            String getSnapshotString = HttpUtil.postRequest(device.getMediaUrl(), postString);
            Log.v("MainActivity", getSnapshotString);
            //解析获取MediaProfile 集合
           String uri = XmlDecodeUtil.getSnapshotUri(getSnapshotString);
           byte[] bytes = HttpUtil.getByteArray(uri);
           String path = SDCardUtils.writeResoursToSDCard("hibox/pic" , "top.pic", bytes);
           callBack.getSnapshotInfoResult(true, path);
        } catch (Exception e) {
            e.printStackTrace();
            callBack.getSnapshotInfoResult(false, e.toString());
        }
    }



    public interface GetSnapshotInfoCallBack{
        void getSnapshotInfoResult(boolean isSuccess, String errorMsg);
    }

}