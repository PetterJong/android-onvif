package com.wp.android_onvif.onvifBean;

public class NetworkInterface {

    private String interfaceToken;
    private String mtu;
    private String ipvtPrefixLength;

    public String getInterfaceToken() {
        return interfaceToken;
    }

    public void setInterfaceToken(String interfaceToken) {
        this.interfaceToken = interfaceToken;
    }

    public String getMtu() {
        return mtu;
    }

    public void setMtu(String mtu) {
        this.mtu = mtu;
    }

    public String getIpvtPrefixLength() {
        return ipvtPrefixLength;
    }

    public void setIpvtPrefixLength(String ipvtPrefixLength) {
        this.ipvtPrefixLength = ipvtPrefixLength;
    }
}
