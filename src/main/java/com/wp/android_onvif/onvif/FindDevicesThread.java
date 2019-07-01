package com.wp.android_onvif.onvif;

import android.content.Context;
import android.util.Log;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.XmlDecodeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Author ： BlackHao
 * Time : 2018/1/8 14:38
 * Description : 利用线程搜索局域网内设备
 */

public class FindDevicesThread extends Thread {
    private static String tag = "OnvifSdk";
    private byte[] sendData;
    private boolean readResult = false;
    private String ipAdress;

    //回调借口
    private FindDevicesListener listener;

    /**
     *
     * @param context
     * @param ipAdress ip地址（192.168.1.10）
     * @param listener
     */
    public FindDevicesThread(Context context, String ipAdress, FindDevicesListener listener) {
        this.listener = listener;
        this.ipAdress = ipAdress;
        InputStream fis = null;
        try {
            //从assets读取文件
            fis = context.getAssets().open("probe.xml");
            sendData = new byte[fis.available()];
            readResult = fis.read(sendData) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        super.run();
        DatagramSocket udpSocket = null;
        DatagramPacket receivePacket;
        DatagramPacket sendPacket;
        //设备列表集合
        ArrayList<Device> devices = new ArrayList<>();
        byte[] by = new byte[1024 * 16];
        if (readResult) {
            try {
                //端口号
                int BROADCAST_PORT = 3702;
                //初始化
                udpSocket = new DatagramSocket(BROADCAST_PORT);
                udpSocket.setSoTimeout(4 * 1000);
                udpSocket.setBroadcast(true);
                //DatagramPacket
                sendPacket = new DatagramPacket(sendData, sendData.length);
                sendPacket.setAddress(InetAddress.getByName(ipAdress));
                sendPacket.setPort(BROADCAST_PORT);
                //发送
                udpSocket.send(sendPacket);
                //接受数据
                receivePacket = new DatagramPacket(by, by.length);
                long startTime = System.currentTimeMillis();
                long endTime = System.currentTimeMillis();
                while (endTime - startTime < 4  * 1000) {
                    udpSocket.receive(receivePacket);
                    String str = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Log.v(tag, str);
                    devices.add(XmlDecodeUtil.getDeviceInfo(str));
                    endTime = System.currentTimeMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (udpSocket != null) {
                    udpSocket.close();
                }
            }
        }
        //回调结果
        if (listener != null) {
            listener.searchResult(devices);
        }
    }

    /**
     * Author ： BlackHao
     * Time : 2018/1/9 11:13
     * Description : 搜索设备回调
     */
    public interface FindDevicesListener {
        void searchResult(ArrayList<Device> devices);
    }
}
