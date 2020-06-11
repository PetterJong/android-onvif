

# 实现功能

本模块实现了以下功能：

- mdm的主入口，执行notifyMain
- 用户切换人口。执行用户切换
- mdm的信息展示页面包含
  - 消息页面
  - 注册信息页面
  - 应用卸载页面
  - 菜单页面
    - 移动管理
    - 签到
    - 下载应用商店
    - 扫码配置apn
    - 一键报警
    - 取消激活
    - 恢复重置及应用卸载功能
    - 关于等
- mdm Application，执行notifyApplicationDestroy，notifyApplication
- log功能初始化
- http功能初始化
- 开机自启动广播
# android-onvif
##1. 介绍
android-onvif是一个基于android系统控制onvif协议摄像头的软件项目，例如海康摄像头等。本项目已将接口封装，使用简单。后续将扩展更多功能。。。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191112091401189.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NpbmF0XzMzMjg1MTI3,size_16,color_FFFFFF,t_70)
##2. 功能介绍
-探索与发现摄像头
-摄像头参数获取和获取
-摄像头账号密码修改
-摄像头的固件升级
-摄像头截图
-修改摄像头时间
-重启摄像头
-修改摄像头ip


##3. 使用介绍
-1.在使用前请确认摄像头和你的设备在同一网段下，否者将无法探索该设备。
-2.确认使用的摄像头完全支持onvif协议，否者可能导致部分功能无法使用。检测方式使用onvif device manager，如果在该软件都无法修改，则说明不支持。
-3.在代码中修改对应的账号和密码，代码中使用是我设备对应的账号密码

##4. 权限申请
  	\<uses-permission android:name="android.permission.INTERNET" />
    \<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    \<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

