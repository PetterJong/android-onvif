package com.wp.android_onvif.util;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvif.FindDevicesThread;
import com.wp.android_onvif.onvif.GetDeviceInfoThread;
import com.wp.android_onvif.onvif.GetSnapshotInfoThread;
import com.wp.android_onvif.onvif.SetImagingSettingsThread;
import com.wp.android_onvif.onvif.SetNetworkInterfaceThread;
import com.wp.android_onvif.onvif.SetSystemDateAndTimeThread;
import com.wp.android_onvif.onvif.StartFirmwareUpgradeThread;
import com.wp.android_onvif.onvif.SystemRebootThread;
import com.wp.android_onvif.onvifBean.Device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Onvif摄像头sdk (需要注意的是，大部分设备为完全兼容onvif 摄像头厂家会有自己的私有协议，所有在部分摄像头设备上无法使用不跟功能)
 * 本sdk集成
 * 截图功能 {@linkplain #getSnapshot(Context, String, String, String, String, String, GetSnapshotInfoThread.GetSnapshotInfoCallBack)}
 * 重启功能 {@linkplain #systemReboot(Context, String, SystemRebootThread.SystemRebootCallBack)}
 * 设置时间功能 {@linkplain #setSystemDateAndTime(Context, Date, String, String, String, SetSystemDateAndTimeThread.OnSetSystemDateAndTimeCallBack)}
 * 修改Ip地址 {@linkplain #setNetworkInterface(Context, String, String, String, String, SetNetworkInterfaceThread.SetNetworkInterfaceCallBack)}
 * 固件升级功能 {@linkplain #startFirmwareUpgrade(Context, String, String, StartFirmwareUpgradeThread.StartFirmwareUpgradeCallBack)}
 * 修改摄像头参数 {@linkplain #setImageingSettings(Context, String, float, SetImagingSettingsThread.SetImagingSettingsCallBack)}
 */
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

    /**
     * 此方法内用户名和密码写死，需要在此处对饮修改，否者无法登录设备
     * @param context
     */
    public synchronized static void findDevice(Context context){
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
                                        Log.d(tag, "获取摄像头信息设备" + isSuccess + " " + errorMsg);
                                        if (isSuccess) {
                                            setSystemDateAndTime(mContext, new Date(), device);
                                        }
                                        Log.d(tag, "发现摄像头设备" + device.toString());
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
     * @param user        摄像头登录帐号 //不需要了，在探索设备的时候已经设置用户名和吗密码
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
                    Log.d(tag, "摄像机时间修改成功" + device.getIpAddress());
                } else {
                    Log.e(tag, "摄像机时间修改失败" + result);
                }
            }
        });
    }

    /**
     * 重启摄像头
     *
     * @param context  context
     * @param ipAdress 摄像头地址（192.168.1.10）
     * @param callBack 获取截图接口
     */
    public static void systemReboot(final Context context, String ipAdress,
                                    final SystemRebootThread.SystemRebootCallBack callBack) {
        Device device = deviceHashMap.get(ipAdress);
        if (device != null) { // 查找设备，获取设备基本信息（mediaUri,token值等等）
            systemRebootThread(context, device, callBack);
        } else {
            callBack.systemRebootResult(false, device, "重启摄像头未找到对应的设备");
        }
    }

    /**
     *
     * 修改摄像头Ip,修改之前最好最ip验证，保证新的ip地址为192.168.1.255这个网段下，否者回到值修改后找不到该设备。
     * 在callBack返回true时必须调用探索设备方法{@link #findDevice(Context)}
     * @param context context
     * @param oldIpAdress 为修改前的ip地址
     * @param newIpAddress 修改后的ip地址
     * @param interfaceToken token "eth0"
     * @param prefixLength 长度 24
     * @param callBack 修改IP地址回调
     */
    public static void setNetworkInterface(final Context context,  String oldIpAdress, String newIpAddress,  String interfaceToken, String prefixLength,  final SetNetworkInterfaceThread.SetNetworkInterfaceCallBack callBack ){
        Device device = deviceHashMap.get(oldIpAdress);
        if (device != null) { // 查找设备，获取设备基本信息（mediaUri,token值等等）
            setNetworkInterface(context, device,interfaceToken, newIpAddress, prefixLength, callBack);
        } else {
            findDevice(mContext);
            callBack.getDeviceInfoResult(false, null, "修改摄像头Ip未找到对应的设备");
        }
    }

    /**
     * 升级固件
     * @param context context
     * @param ipAddress IP地址
     * @param filePath 固件包地址
     * @param callBack 回调
     */
    public static void startFirmwareUpgrade(final Context context, String ipAddress, String filePath, StartFirmwareUpgradeThread.StartFirmwareUpgradeCallBack callBack){
        Device device = deviceHashMap.get(ipAddress);
        if (device != null) { // 查找设备，获取设备基本信息（mediaUri,token值等等）
            StartFirmwareUpgradeThread startFirmwareUpgradeThread = new StartFirmwareUpgradeThread(filePath, device, context , callBack);
            startFirmwareUpgradeThread.start();
        } else {
            findDevice(mContext);
            callBack.startFirmwareUpgradeResult(false, null, "升级固件未找到对应的设备");
        }
    }

    /**
     * 修改摄像头参数，目前仅支持设置亮度
     * @param context context
     * @param ipAddress IP地址
     * @param brightness 亮度
     * @param callBack 回调
     */
    public static void setImageingSettings(final Context context, String ipAddress, float brightness, SetImagingSettingsThread.SetImagingSettingsCallBack callBack){
        Device device = deviceHashMap.get(ipAddress);
        if (device != null) { // 查找设备，获取设备基本信息（mediaUri,token值等等）
            SetImagingSettingsThread setImagingSettingsThread = new SetImagingSettingsThread(device, context, callBack);
            setImagingSettingsThread.setBrightness(brightness);
            setImagingSettingsThread.start();
        } else {
            findDevice(mContext);
            callBack.setImagingSettingsThreadResult(false,  null,"修改摄像头参数未找到对应的设备");
        }
    }

   /**
     * 搜索网段下的设备，可以是广播地址
     *
     * @param context  context
     * @param ipAdress 192.168.1.255
     * @param listener FindDevicesListener
     */
    private static void findDevice(Context context, String ipAdress, final FindDevicesThread.FindDevicesListener listener) {
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

    private static void systemRebootThread(Context context, final Device device, SystemRebootThread.SystemRebootCallBack callBack) {
        SystemRebootThread systemRebootThread = new SystemRebootThread(device, context, callBack);
        systemRebootThread.start();
    }

    private static void setNetworkInterface(Context context, final Device device, String interfaceToken,  String newIpAddress, String prefixLength, SetNetworkInterfaceThread.SetNetworkInterfaceCallBack callBack) {
        SetNetworkInterfaceThread systemRebootThread = new SetNetworkInterfaceThread(device, context,interfaceToken, newIpAddress, prefixLength,  callBack);
        systemRebootThread.start();
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


    public static HashMap<String, Device> getDeviceHashMap() {
        return deviceHashMap;
    }
}
