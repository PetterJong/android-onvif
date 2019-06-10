package com.wp.android_onvif.onvif;

import android.content.Context;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.HttpUtil;

/**
 * Author ： BlackHao
 * Time : 2018/1/11 14:20
 * Description : 获取 device 相关信息
 */

public class SetNetworkInterfaceThread extends Thread {

    private Device device;
    private Context context;
    private String interfaceToken;
    private String newIpAddress;
    private String prefixLength;
    private SetNetworkInterfaceCallBack callBack;

//    private WriteFileUtil util;

    public SetNetworkInterfaceThread(Device device, Context context,String interfaceToken, String ipAddress, String prefixLength, SetNetworkInterfaceCallBack callBack) {
        this.device = device;
        this.context = context;
        this.callBack = callBack;
        this.interfaceToken = interfaceToken;
        this.newIpAddress = ipAddress;
        this.prefixLength = prefixLength;
    }

    @Override
    public void run() {
        super.run();
        try {
            //getCapabilities，不需要鉴权
            String postString = OnvifUtils.getPostString("setNetworkInterface.xml", context, device, true,  interfaceToken, newIpAddress, prefixLength );
            String caps = HttpUtil.postRequest(device.getServiceUrl(), postString);
            //解析返回的xml数据获取存在的url
//            XmlDecodeUtil.getCapabilitiesUrl(caps, device);

            callBack.getDeviceInfoResult(true, device, "NO_ERROR");

//            postString = getPostString("getConfigOptions.xml", true);
//            caps = HttpUtil.postRequest(device.getPtzUrl(), postString);
//            util.writeData(caps.getBytes());

//            util.finishWrite();
        } catch (Exception e) {
            e.printStackTrace();
            callBack.getDeviceInfoResult(false,device, e.toString());
        }
    }

    /**
     * Author ： BlackHao
     * Time : 2018/1/11 14:24
     * Description : 获取 device 信息回调
     */
    public interface SetNetworkInterfaceCallBack {
        void getDeviceInfoResult(boolean isSuccess, Device device, String errorMsg);
    }
}
