package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.onvifBean.MediaProfile;
import com.wp.android_onvif.util.FileUtils;
import com.wp.android_onvif.util.HttpUtil;
import com.wp.android_onvif.util.XmlDecodeUtil;

/**
 * 获取摄像机截图
 */
public class GetSnapshotInfoThread extends Thread{
    private static String tag = "OnvifSdk";

    private Device device;
    private Context context;
    private GetSnapshotInfoThread.GetSnapshotInfoCallBack callBack;
    private MediaProfile mediaProfile;

    private String picRootPath;
    private String picFileName;

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

    public void setPath(String picRootPath, String picFileName){
        this.picRootPath = picRootPath;
        this.picFileName = picFileName;
    }


    @Override
    public void run() {
        super.run();
        try {
            //getProfiles，需要鉴权
            String postString = OnvifUtils.getPostString("getSnapshotUri.xml", context, device,true,
                    mediaProfile == null?"000":mediaProfile.getToken());
            String getSnapshotString = HttpUtil.postRequest(device.getMediaUrl(), postString);
            Log.v(tag, getSnapshotString);
            //解析获取MediaProfile 集合
           String uri = XmlDecodeUtil.getSnapshotUri(getSnapshotString);
           byte[] bytes = HttpUtil.getByteArray(uri);
           String path = FileUtils.writeResoursToSDCard(picRootPath , picFileName, bytes);
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