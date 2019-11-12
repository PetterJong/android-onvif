package com.example.android_onvif_tools;

import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wp.android_onvif.Constant;
import com.wp.android_onvif.http.DownloadUtil;
import com.wp.android_onvif.onvif.GeNetWorkInterfaceThread;
import com.wp.android_onvif.onvif.GetImagingSettingsThread;
import com.wp.android_onvif.onvif.GetSnapshotInfoThread;
import com.wp.android_onvif.onvif.SetImagingSettingsThread;
import com.wp.android_onvif.onvif.SetNetworkInterfaceThread;
import com.wp.android_onvif.onvif.SetSystemDateAndTimeThread;
import com.wp.android_onvif.onvif.SetUserThread;
import com.wp.android_onvif.onvif.StartFirmwareUpgradeThread;
import com.wp.android_onvif.onvif.SystemRebootThread;
import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.LogClientUtils;
import com.wp.android_onvif.util.OnvifSdk;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String tag = "MainActivity";
    public static final String USER = "admin";
    public static final String PSD = "hibox123";
    public static final String topIp = "192.168.1.20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnvifSdk.initSdk(this); // 初始化sdk
        findViewById(R.id.bt_discover).setOnClickListener(this);
        findViewById(R.id.bt_network).setOnClickListener(this);
        findViewById(R.id.bt_set_user).setOnClickListener(this);
        findViewById(R.id.bt_ipc_firmware).setOnClickListener(this);
        findViewById(R.id.bt_cap).setOnClickListener(this);
        findViewById(R.id.bt_update_time).setOnClickListener(this);
        findViewById(R.id.bt_system_reboot).setOnClickListener(this);
        findViewById(R.id.bt_setip).setOnClickListener(this);
        findViewById(R.id.bt_setip2).setOnClickListener(this);
        findViewById(R.id.bt_getImageSettings).setOnClickListener(this);
        findViewById(R.id.bt_setImageSettings).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_discover:
                OnvifSdk.findDevice(this);
                break;
            case R.id.bt_network:
                OnvifSdk.getNetworkInterface(this, "192.168.1.20", new GeNetWorkInterfaceThread.GetNetWorkInterfaceCallBack() {
                    @Override
                    public void getNetWorkInterfaceThreadResult(final boolean isSuccess, Device device, String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, isSuccess ? "获取网络接口配置成功" : "获取网络接口配置失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            case R.id.bt_set_user: // 修改或者添加用户，当用户名已存在是则修改，不存在时则添加一个新用户
//                getDigest();
                OnvifSdk.setUser(this, topIp, USER, PSD, new SetUserThread.SetUserCallBack() {
                    @Override
                    public void setUserThreadResult(final boolean isSuccess, com.wp.android_onvif.onvifBean.Device device, String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, isSuccess ? "修改密码成功" : "修改密码失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            case R.id.bt_ipc_firmware: // 摄像头固件升级,此固件仅支持我们的摄像头。开发者可以通过此过程了解升级步骤。会先获取固件上传的uri，上传完成后自动升级。
                DownloadUtil.getInstance().download("https://hibox-bucket.oss-cn-beijing.aliyuncs.com/temp/IPC_V2.3.18.0425.bin", "", "", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(String path) {
                        startFirmwareUpgrade("192.168.1.20", path);
                    }

                    @Override
                    public void onDownloadFailed(String message) {
                        LogClientUtils.v(tag, "下载失败");
                    }
                });
                break;
            case R.id.bt_cap: // 获取摄像头截图
                String path = Constant.ROOT_PATH + "pic2";
                OnvifSdk.getSnapshot(this, topIp, path, "top.jpg", new GetSnapshotInfoThread.GetSnapshotInfoCallBack() {
                    @Override
                    public void getSnapshotInfoResult(final boolean isSuccess, final String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, (isSuccess ? "截图成功" : "截图失败") + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            case R.id.bt_update_time: // 更新摄像头时间
                OnvifSdk.setSystemDateAndTime(MainActivity.this, new Date(), topIp, USER, PSD, new SetSystemDateAndTimeThread.OnSetSystemDateAndTimeCallBack() {
                    @Override
                    public void setSystemDateAndTimeResult(final boolean isSuccess, final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, isSuccess ? "设置时间成功" : ("设置时间失败" + result), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                break;
            case R.id.bt_system_reboot: // 重启摄像头
                OnvifSdk.systemReboot(MainActivity.this, topIp, new SystemRebootThread.SystemRebootCallBack() {

                    @Override
                    public void systemRebootResult(final boolean isSuccess, com.wp.android_onvif.onvifBean.Device device, final String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, isSuccess ? "重启摄像头成功" : errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                break;
            case R.id.bt_setip: // 修改摄像头ip
                OnvifSdk.setNetworkInterface(MainActivity.this, "192.168.1.20", "192.168.1.21", new SetNetworkInterfaceThread.SetNetworkInterfaceCallBack() {
                    @Override
                    public void getDeviceInfoResult(final boolean isSuccess, com.wp.android_onvif.onvifBean.Device device, final String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, isSuccess ? "修改摄像头ip成功" : errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (isSuccess) {
                            SystemClock.sleep(3000);
                            OnvifSdk.findDevice(MainActivity.this);
                        }
                    }
                });
                break;
            case R.id.bt_setip2: // 修改摄像头ip
                OnvifSdk.setNetworkInterface(MainActivity.this, "192.168.1.21", "192.168.1.20",
                        new SetNetworkInterfaceThread.SetNetworkInterfaceCallBack() {
                            @Override
                            public void getDeviceInfoResult(final boolean isSuccess, com.wp.android_onvif.onvifBean.Device device, final String errorMsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, isSuccess ? "修改摄像头ip成功" : errorMsg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                if (isSuccess) {
                                    SystemClock.sleep(3000);
                                    OnvifSdk.findDevice(MainActivity.this);
                                }
                            }
                        });
                break;

            case R.id.bt_getImageSettings:
                OnvifSdk.getImageingSettings(MainActivity.this, "192.168.1.20", new GetImagingSettingsThread.GetImagingSettingsCallBack() {
                    @Override
                    public void getImagingSettingsThreadResult(final boolean isSuccess, Device device, final String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, isSuccess ? "获取摄像头参数成功" : errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            case R.id.bt_setImageSettings: // 修改摄像头参数（亮度）
                OnvifSdk.setImageingSettings(MainActivity.this, "192.168.1.20", (float) 50,
                        new SetImagingSettingsThread.SetImagingSettingsCallBack() {
                            @Override
                            public void setImagingSettingsThreadResult(final boolean isSuccess, com.wp.android_onvif.onvifBean.Device device, final String errorMsg) {
                                if (isSuccess) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, isSuccess ? "设置参数成功" : errorMsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, isSuccess ? "设置参数失败" : errorMsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                break;

        }
    }


    private void startFirmwareUpgrade(String ip, String filePath) {
        HashMap<String, Device> cameraHashMap = OnvifSdk.getDeviceHashMap();
        // 通过token区分摄像头版本，矩视的token="MainStream",为profiles='[MediaProfile{token='MainStream', name='MainStream',
        for (final com.wp.android_onvif.onvifBean.Device camera : cameraHashMap.values()) {
            if (ip.equalsIgnoreCase(camera.getIpAddress())) {
                OnvifSdk.startFirmwareUpgrade(MainActivity.this, camera.getIpAddress(), filePath, new StartFirmwareUpgradeThread.StartFirmwareUpgradeCallBack() {
                    @Override
                    public void startFirmwareUpgradeResult(boolean isSuccess, com.wp.android_onvif.onvifBean.Device device, String errorMsg) {
                        if (isSuccess) {
                            LogClientUtils.d(tag, camera.getIpAddress() + "固件升级成功！！！" + errorMsg);
                        } else {
                            LogClientUtils.d(tag, camera.getIpAddress() + "固件升级失败！！！" + errorMsg);
                        }
                    }
                });
            }
        }
    }

}
