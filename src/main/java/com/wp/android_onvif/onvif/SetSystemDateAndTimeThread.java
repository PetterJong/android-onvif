package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.hibox.library.constant.Constant;
import com.hibox.library.util.FileUtils;
import com.hibox.library.util.LogClientUtils;
import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.HttpUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 设置摄像机时间
 */
public class SetSystemDateAndTimeThread extends Thread {
    private Context context;
    private Device device;
    private Date date;
    private OnSetSystemDateAndTimeCallBack callBack;


    public SetSystemDateAndTimeThread(Device device, Context context, OnSetSystemDateAndTimeCallBack callBack) {
        this.context = context;
        this.device = device;
        this.callBack = callBack;
    }

    public void setTime(Date date) {
        this.date = date;
    }

    @Override
    public void run() {
        super.run();
        try {
            Calendar calendar = Calendar.getInstance();
            if(date != null){
                calendar.setTime(date);
            }
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            String mine = String.valueOf(calendar.get(Calendar.MINUTE));
            String second = String.valueOf(calendar.get(Calendar.SECOND));
            String postString = OnvifUtils.getPostString("setSystemDateAndTime.xml", context, device, true,
                    year, month, day, hour, mine, second);
//            FileUtils.writeResoursToSDCard(Constant.ROOT_PATH, "settime.txt", postString.getBytes());
            String setSystemDateAndTimeString = HttpUtil.postRequest(device.getServiceUrl(), postString);
            Log.v("MainActivity", setSystemDateAndTimeString);
            if(callBack != null){
                callBack.setSystemDateAndTimeResult(true,"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callBack.setSystemDateAndTimeResult(false,e.getMessage());
        }
    }

    public interface OnSetSystemDateAndTimeCallBack{
        void setSystemDateAndTimeResult(boolean isSuccess, String result);
    }
}
