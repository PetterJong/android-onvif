package com.balckhao.blackonvif.onvifBean;

/**
 * Author ： BlackHao
 * Time : 2018/1/10 14:08
 * Description : onvif Digest
 */

public class Digest {
    private String userName;
    private String nonce;
    private String encodePsw;
    private String createdTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getEncodePsw() {
        return encodePsw;
    }

    public void setEncodePsw(String encodePsw) {
        this.encodePsw = encodePsw;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "Digest{" +
                "userName='" + userName + '\'' +
                ", nonce='" + nonce + '\'' +
                ", encodePsw='" + encodePsw + '\'' +
                ", createdTime='" + createdTime + '\'' +
                '}';
    }
}
