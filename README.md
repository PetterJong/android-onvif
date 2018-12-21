Android onvif 搜索设备并获取信息<br>
此项目是基于Android 通过onvif协议实现搜索设备以及获取设备信息，
实现搜索设备，获取设备信息，以及截图功能

重点：
请阅读assets中的xml文件，这些是onvif协议定义的通信格式（soap）
其余功能请自行了解onvif协议，可按照此方式获取摄像头参数

请修改Device中对应的摄像头用户名密码
    public static final String USER = "admin";
    public static final String PSD = "tlJwpbo6";
    public Device() {
        this("admin", "tlJwpbo6");
    }

    FindDevicesThread中的ip地址
    sendPacket.setAddress(InetAddress.getByName("192.168.1.10"));


本项目借鉴csdn相关blog:http://blog.csdn.net/a512337862/article/details/79281648
在原有的基础项目之上增加了，截图功能；
