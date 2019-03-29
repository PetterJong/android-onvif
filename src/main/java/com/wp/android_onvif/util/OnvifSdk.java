package com.wp.android_onvif.util;

import android.content.Context;

import com.hibox.library.util.LogClientUtils;
import com.wp.android_onvif.onvif.FindDevicesThread;
import com.wp.android_onvif.onvif.GetDeviceInfoThread;
import com.wp.android_onvif.onvif.GetSnapshotInfoThread;
import com.wp.android_onvif.onvif.SetSystemDateAndTimeThread;
import com.wp.android_onvif.onvifBean.Device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class OnvifSdk {
    private static String tag = "OnvifSdk";

    private static HashMap<String, Device> deviceHashMap = new HashMap<>();

    private static Context mContext;
    private static boolean searching; //正在搜索


    /**
     * 初始化sdk,方法为异步操作，探索设备的时间为4秒，需注意
     * 探索发现设备
     *
     * @param context
     */
    public static void initSdk(Context context) {
        mContext = context;
        findDevice(context);
    }

    private synchronized static void findDevice(Context context){
        if(searching){
            return;
        }
        searching = true;
        deviceHashMap.clear();
        findDevice(context, "192.168.1.255", new FindDevicesThread.FindDevicesListener() {
            @Override
            public void searchResult(ArrayList<Device> devices) {
                if (devices != null) {
                    for (Device device : devices) {
                        if (device.getServiceUrl() != null && device.getIpAddress() != null) {
                            String ip = device.getIpAddress();
                            ip = ip.split(":")[0];
                            if (ip.equals("192.168.1.20")) { // 设置海康摄像头用户名密码，海康摄像头兼容问题，暂时还不能使用onvif方法
                                device.setUserName("admin");
                                device.setPsw("admin123");
                            } else {
                                device.setUserName("admin"); // 设置主辅摄用户名密码
                                device.setPsw("tlJwpbo6");
                                getDeviceInfo(mContext, device, new GetDeviceInfoThread.GetDeviceInfoCallBack() {
                                    @Override
                                    public void getDeviceInfoResult(boolean isSuccess, Device device, String errorMsg) {
                                        LogClientUtils.d(tag, "获取摄像头信息设备" + isSuccess + " " + errorMsg);
                                        if (isSuccess) {
                                            setSystemDateAndTime(mContext, new Date(), device);
                                        }
                                        LogClientUtils.d(tag, "发现摄像头设备" + device.toString());
                                    }
                                });
                            }
                            deviceHashMap.put(ip, device);
                        }
                    }
//                    LogClientUtils.d(tag, "发现摄像头设备" + deviceHashMap.toString());
                    searching = false;
                }
            }
        });
    }

    /**
     * 获取摄像头截图
     *
     * @param context     context
     * @param ipAdress    摄像头地址（192.168.1.10）
     * @param user        摄像头登录帐号
     * @param pd          摄像头登录密码
     * @param picRootPath 截图保存的路劲
     * @param picFileName 截图保存的名称
     * @param callBack    获取截图接口
     */
    public static void getSnapshot(final Context context, String ipAdress, String user, String pd,
                                   final String picRootPath, final String picFileName,
                                   final GetSnapshotInfoThread.GetSnapshotInfoCallBack callBack) {
        Device device = deviceHashMap.get(ipAdress);
        if (device != null) { // 查找设备，获取设备基本信息（mediaUri,token值等等）
            getSnapshot(context, device, picRootPath, picFileName, callBack);
        } else {
            findDevice(context); // 重新搜索一遍，保证摄像头重连成功能够通过onvif搜索到此设备
//            creatDevice(ipAdress, user, pd);
            callBack.getSnapshotInfoResult(false, "未找到对应的设备");
        }
    }

    /**
     * 设置摄像头时间
     *
     * @param context  context
     * @param date     修改的时间
     * @param ipAdress 摄像头地址（192.168.1.10）
     * @param user     摄像头登录帐号
     * @param pd       摄像头登录密码
     * @param callBack 获取截图接口
     */
    public static void setSystemDateAndTime(final Context context, final Date date, String ipAdress, String user, String pd,
                                            final SetSystemDateAndTimeThread.OnSetSystemDateAndTimeCallBack callBack) {
        Device device = deviceHashMap.get(ipAdress);
        if (device != null) { // 查找设备，获取设备基本信息（mediaUri,token值等等）
            setSystemDateAndTimeThread(context, date, device, callBack);
        } else {
            callBack.setSystemDateAndTimeResult(false, "未找到对应的设备");
        }
    }

    /**
     * 设置摄像头时间
     *
     * @param context context
     * @param date    修改的时间
     * @param device  摄像机设备
     */
    private static void setSystemDateAndTime(final Context context, final Date date, final Device device) {
        setSystemDateAndTimeThread(context, date, device, new SetSystemDateAndTimeThread.OnSetSystemDateAndTimeCallBack() {
            @Override
            public void setSystemDateAndTimeResult(boolean isSuccess, String result) {
                if (isSuccess) {
                    LogClientUtils.d(tag, "摄像机时间修改成功" + device.getIpAddress());
                } else {
                    LogClientUtils.e(tag, "摄像机时间修改失败" + result);
                }
            }
        });
    }


    /**
     * 搜索网段下的设备，可以是广播地址
     *
     * @param context  context
     * @param ipAdress 192.168.1.255
     * @param listener FindDevicesListener
     */
    public static void findDevice(Context context, String ipAdress, final FindDevicesThread.FindDevicesListener listener) {
        FindDevicesThread findDevicesThread = new FindDevicesThread(context, ipAdress, new FindDevicesThread.FindDevicesListener() {
            @Override
            public void searchResult(ArrayList<Device> devices) {
                listener.searchResult(devices);
            }
        });
        findDevicesThread.start();
    }

    private static void getDeviceInfo(final Context context, final Device device, final GetDeviceInfoThread.GetDeviceInfoCallBack callBack) {
        GetDeviceInfoThread getDeviceInfoThread = new GetDeviceInfoThread(device, context, new GetDeviceInfoThread.GetDeviceInfoCallBack() {
            @Override
            public void getDeviceInfoResult(boolean isSuccess, Device device, String errorMsg) {
                callBack.getDeviceInfoResult(isSuccess, device, errorMsg);

            }
        });
        getDeviceInfoThread.start();
    }

    private static void getSnapshot(Context context, final Device device, String picRootPath, String picFileName, GetSnapshotInfoThread.GetSnapshotInfoCallBack callBack) {
        GetSnapshotInfoThread getSnapshotInfoThread = new GetSnapshotInfoThread(device, context, callBack);
        getSnapshotInfoThread.setPath(picRootPath, picFileName);
        getSnapshotInfoThread.start();
    }

    private static void setSystemDateAndTimeThread(Context context, Date date, final Device device, SetSystemDateAndTimeThread.OnSetSystemDateAndTimeCallBack callBack) {
        SetSystemDateAndTimeThread setSystemDateAndTimeThread = new SetSystemDateAndTimeThread(device, context, callBack);
        setSystemDateAndTimeThread.setTime(date);
        setSystemDateAndTimeThread.start();
    }


    /**
     * 正规操作应该是先统统findDevice搜索设备，然后在获取设备信息。
     * 但是基于onvif我们能够确定摄像机的uri格式，知道其中的固定参数，为了节省时间，我们可以直接写死
     *
     * @param ipAdress ipAdress
     * @param username username
     * @param pwd      pwd
     */
    private static void creatDevice(String ipAdress, String username, String pwd) {
        Device device = new Device();
        device.setPsw(pwd);
        device.setUserName(username);
        device.setIpAddress(ipAdress + ":8899");
        device.setServiceUrl("http://" + ipAdress + ":8899/onvif/device_service");
        device.setMediaUrl("http://" + ipAdress + ":8899/onvif/Media");
    }


}
