

# android-onvif
## 1. 介绍
- android-onvif是一个基于android系统控制onvif协议摄像头的软件项目，例如海康摄像头等。本项目已将接口封装，使用简单。后续将扩展更多功能。。。
- 使用前请先确认摄像头是否支持onvif，onvif协议是一个庞大的协议，摄像头可能未完全支持onvif，我当初调试的就是如此，如果你是定制的可以要求厂家放开出来。可以使用odm查看是否支持某个功能控制，odm上显示灰色不能点击，即表示此摄像头该功能对应的onvif协议未实现
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191112091401189.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NpbmF0XzMzMjg1MTI3,size_16,color_FFFFFF,t_70)
## 2. 功能介绍
 - 探索与发现摄像头
 - 摄像头参数获取和获取
 - 摄像头账号密码修改
 - 摄像头的固件升级
 - 摄像头截图 
 - 修改摄像头时间
 - 重启摄像头
 - 修改摄像头ip


## 3. 使用介绍
 - 1.在使用前请确认摄像头和你的设备在同一网段下，否者将无法探索该设备。
 - 2.确认使用的摄像头完全支持onvif协议，否者可能导致部分功能无法使用。检测方式使用onvif device manager，如果在该软件都无法修改，则说明不支持。
 - 3.在代码中修改对应的账号和密码，代码中使用是我设备对应的账号密码

## 4. 权限申请
 - 	\<uses-permission android:name="android.permission.INTERNET" />
 -   \<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 -   \<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 ...
## 5. 推荐学习：
- android studio集成onvif协议的网络摄像头 https://blog.csdn.net/sinat_33285127/article/details/85164883
- onvif协议控制之鉴权方式 https://blog.csdn.net/sinat_33285127/article/details/85164883
...

