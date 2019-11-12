package com.wp.android_onvif;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.wp.android_onvif.onvif.GetSnapshotInfoThread;
import com.wp.android_onvif.onvif.SetSystemDateAndTimeThread;
import com.wp.android_onvif.util.OnvifSdk;

import java.io.File;
import java.util.Date;

public class SampleActivity extends AppCompatActivity {
    public static final String USER = "admin";
    public static final String PSD = "tlJwpbo6";
    public static final String topIp = "192.168.1.10";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        OnvifSdk.initSdk(getApplicationContext()); // 初始化sdk 探索网段为192.168.1.255下的设备
        /*
        截图功能
         */
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hibox/pic2";
        OnvifSdk.getSnapshot(this, topIp, path, "top.jpg", new GetSnapshotInfoThread.GetSnapshotInfoCallBack() {
            @Override
            public void getSnapshotInfoResult(final boolean isSuccess, final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SampleActivity.this, (isSuccess ? "截图成功" : "截图失败") + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        /*
        同步时间
         */
        OnvifSdk.setSystemDateAndTime(this, new Date(), topIp, USER, PSD, new SetSystemDateAndTimeThread.OnSetSystemDateAndTimeCallBack() {
            @Override
            public void setSystemDateAndTimeResult(final boolean isSuccess, final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SampleActivity.this, isSuccess ? "设置时间成功" : ("设置时间失败" + result), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        // 其他功能，重启摄像头，修改IP地址，固件升级请在OnvifSdk查看
    }

}
