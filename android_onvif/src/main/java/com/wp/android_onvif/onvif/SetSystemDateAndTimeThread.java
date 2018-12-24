package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.HttpUtil;

import java.util.Calendar;
import java.util.Date;

public class SetSystemDateAndTimeThread extends Thread {
    private Context context;
    private Device device;
    private Date date;


    public SetSystemDateAndTimeThread(Device device, Context context) {
        this.context = context;
        this.device = device;
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
            String setSystemDateAndTimeString = HttpUtil.postRequest(device.getServiceUrl(), postString);
            Log.v("MainActivity", setSystemDateAndTimeString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
