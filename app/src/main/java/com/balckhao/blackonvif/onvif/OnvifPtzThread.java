package com.balckhao.blackonvif.onvif;

import android.content.Context;
import android.util.Log;

import com.balckhao.blackonvif.onvifBean.Device;
import com.balckhao.blackonvif.onvifBean.Digest;
import com.balckhao.blackonvif.udv.ptzControl.PtzControlView;
import com.balckhao.blackonvif.util.Gsoap;
import com.balckhao.blackonvif.util.HttpUtil;

import java.io.InputStream;

/**
 * Author ： BlackHao
 * Time : 2018/2/6 16:14
 * Description : onvif 云台 RelativeMove
 */

public class OnvifPtzThread extends Thread {

    private double x, y;
    private double zoomX;
    //运行标识
    private boolean runTag = true;
    //转动方向标识
    private int direction = 0;
    //设备信息
    private Device device;
    //Context
    private Context context;

    public OnvifPtzThread(Device device, Context context) {
        this.device = device;
        this.context = context;
        this.x = 0;
        this.y = 0;
        this.zoomX = 0;
    }

    @Override
    public void run() {
        super.run();
        while (runTag) {
            try {
                switch (direction) {
                    case PtzControlView.LEFT:
                        if (x - 0.1 >= -1) {
                            x = x - 0.1;
                            Log.e("OnvifPtzThread", sendPostString());
                        }
                        break;
                    case PtzControlView.RIGHT:
                        if (x + 0.1 <= 1) {
                            x = x + 0.1;
                            Log.e("OnvifPtzThread", sendPostString());
                        }
                        break;
                    case PtzControlView.TOP:
                        if (y + 0.1 <= 1) {
                            y = y + 0.1;
                            Log.e("OnvifPtzThread", sendPostString());
                        }
//                        if (zoomX + 0.1 <= 1) {
//                            zoomX = zoomX + 0.1;
//                            Log.e("OnvifPtzThread", sendPostString());
//                        }
                        break;
                    case PtzControlView.BOTTOM:
                        if (y - 0.1 >= -1) {
                            y = y - 0.1;
                            Log.e("OnvifPtzThread", sendPostString());
                        }
//                        if (zoomX - 0.1 >= 0) {
//                            zoomX = zoomX - 0.1;
//                            Log.e("OnvifPtzThread", sendPostString());
//                        }
                        break;
                }
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过用户名/密码/assets 文件获取对应需要发送的String
     *
     * @return 需要发送的 string
     */
    private String sendPostString() throws Exception {
        //读取文件内容
        String postString = "";
        InputStream is = context.getAssets().open("relativeMove.xml");
        byte[] postData = new byte[is.available()];
        if (is.read(postData) > 0) {
            postString = new String(postData, "utf-8");
        }
        //获取digest
        Digest digest = Gsoap.getDigest(device.getUserName(), device.getPsw());
        //需要digest
        if (digest != null) {
            postString = String.format(postString, digest.getUserName(),
                    digest.getEncodePsw(), digest.getNonce(), digest.getCreatedTime(),
                    device.getProfiles().get(0).getToken(), x + "", y + "", zoomX + "");
        }
//        Log.e("OnvifPtzThread", postString);
        return HttpUtil.postRequest(device.getPtzUrl(), postString);
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void finish() {
        runTag = false;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
