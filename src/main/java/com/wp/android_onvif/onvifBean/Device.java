package com.wp.android_onvif.onvifBean;

import java.util.ArrayList;

/**
 * Author ： BlackHao
 * Time : 2018/1/8 13:53
 * Description : 设备信息
 */

public class Device {

    /**
     * 用户名/密码
     */
    private String userName;
    private String psw;

    //IP地址
    private String ipAddress;

    /**
     * serviceUrl,uuid 通过广播包搜索设备获取
     */
    private String serviceUrl;
    private String uuid;
    /**
     * getCapabilities
     */
    private String mediaUrl;
    private String ptzUrl;
    private String imageUrl;
    private String eventUrl;
    private String analyticsUrl;
    /**
     * onvif MediaProfile
     */
    private ArrayList<MediaProfile> profiles;
    public static final String USER = "admin";
    public static final String PSD = "tlJwpbo6";

    public Device() {
        this("admin", "tlJwpbo6");
    }

    public Device(String userName, String psw) {
        profiles = new ArrayList<>();
        this.userName = userName;
        this.psw = psw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.ipAddress = serviceUrl.substring(serviceUrl.indexOf("//") + 2, serviceUrl.indexOf("/on"));
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getPtzUrl() {
        return ptzUrl;
    }

    public void setPtzUrl(String ptzUrl) {
        this.ptzUrl = ptzUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getAnalyticsUrl() {
        return analyticsUrl;
    }

    public void setAnalyticsUrl(String analyticsUrl) {
        this.analyticsUrl = analyticsUrl;
    }

    public ArrayList<MediaProfile> getProfiles() {
        return profiles;
    }

    public void addProfile(MediaProfile profile) {
        this.profiles.add(profile);
    }

    public void addProfiles(ArrayList<MediaProfile> profiles) {
        this.profiles.clear();
        this.profiles.addAll(profiles);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Device{" +
                "serviceUrl='" + serviceUrl + '\'' +
                ", uuid='" + uuid + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", ptzUrl='" + ptzUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", eventUrl='" + eventUrl + '\'' +
                ", analyticsUrl='" + analyticsUrl + '\'' +
                ", profiles='" + profiles + '\'' +
                '}';
    }
}

