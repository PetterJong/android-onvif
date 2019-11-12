package com.wp.android_onvif.util;

import android.util.Xml;

import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.onvifBean.ImageSetting;
import com.wp.android_onvif.onvifBean.MediaProfile;
import com.wp.android_onvif.onvifBean.NetworkInterface;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Author ： BlackHao
 * Time : 2018/1/8 15:54
 * Description : xml 解析
 */

public class XmlDecodeUtil {
    /**
     * 获取设备信息
     */
    public static Device getDeviceInfo(String xml) throws Exception {
        Device device = new Device();
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("XAddrs")) {
                        String addrs = parser.nextText();
                        String[] strs = addrs.split(" ");
                        String url = strs[0];
                        device.setServiceUrl(url);
                    }
                    if (parser.getName().equals("MessageID")) {
                        device.setUuid(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return device;
    }

    /**
     * 解析 xml数据，获取 MediaUrl,PtzUrl
     *
     * @param xml    需要解析的数据
     * @param device 对应的device
     */
    public static void getCapabilitiesUrl(String xml, Device device) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("Media")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setMediaUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("PTZ")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setPtzUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("Events")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setEventUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("Analytics")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setAnalyticsUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("Imaging")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setImageUrl(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * 解析 xml数据，获取 MediaUrl,PtzUrl
     *
     * @param xml    需要解析的数据
     * @param device 对应的device
     */
    public static void getDeviceInformation(String xml, Device device) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("FirmwareVersion")) {
                        String firmwareVersion = parser.nextText();
                        device.setFirmwareVersion(firmwareVersion);
                    } else if (parser.getName().equals("SerialNumber")) {
                        String srialNumber = parser.nextText();
                        device.setSerialNumber(srialNumber);
                    } else if (parser.getName().equals("Manufacturer")) {
                        String manufacturer = parser.nextText();
                        device.setManufacturer(manufacturer);
                    } else if (parser.getName().equals("Model")) {
                        String model = parser.nextText();
                        device.setModle(model);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
    }


    /**
     * 解析 xml数据，获取 MediaUrl,PtzUrl
     *
     * @param xml    需要解析的数据
     * @param device 对应的device
     */
    public static void getGetImageSetting(String xml, Device device) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        String tag = "";
        parser.setInput(input, "UTF-8");
        ImageSetting imageSetting = new ImageSetting();
        ImageSetting.Exposure exposure = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("Brightness")) {
                        float brightness = Float.parseFloat(parser.nextText());
                        imageSetting.setBrightness(brightness);
                    } else if (parser.getName().equals("ColorSaturation")) {
                        float colorSaturation = Float.parseFloat(parser.nextText());
                        imageSetting.setColorSaturation(colorSaturation);
                    } else if (parser.getName().equals("Contrast")) {
                        float contrast = Float.parseFloat(parser.nextText());
                        imageSetting.setContrast(contrast);
                    } else if (parser.getName().equals("Exposure")) {
                        exposure = new ImageSetting.Exposure();
                        imageSetting.setExposure(exposure);
                        tag = "Exposure";
                    }  else if (parser.getName().equals("Mode")) {
                        if("Exposure".equalsIgnoreCase(tag)){
                            String mode =  parser.nextText();
                            exposure.setMode(mode);
                        }
                    } else if (parser.getName().equals("MinExposureTime")) {
                        if("Exposure".equalsIgnoreCase(tag)){
                            int minExposureTime = Integer.parseInt(parser.nextText());
                            exposure.setMinExposureTime(minExposureTime);
                        }
                    } else if (parser.getName().equals("MaxExposureTime")) {
                        if("Exposure".equalsIgnoreCase(tag)){
                            int maxExposureTime = Integer.parseInt(parser.nextText());
                            exposure.setMaxExposureTime(maxExposureTime);
                        }
                    }  else if (parser.getName().equals("ExposureTime")) {
                        if("Exposure".equalsIgnoreCase(tag)){
                            float exposureTime = Float.parseFloat(parser.nextText());
                            exposure.setExposureTime(exposureTime);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        device.setImageSetting(imageSetting);
    }

    /**
     * 解析 xml数据，获取 MediaUrl,PtzUrl
     *
     * @param xml    需要解析的数据
     * @param device 对应的device
     */
    public static void getNetworkInterface(String xml, Device device) throws Exception {
        NetworkInterface networkInterface = device.getNetworkInterface();
        if(networkInterface == null){
            networkInterface = new NetworkInterface();
            device.setNetworkInterface(networkInterface);
        }
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        String tag = "";
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("NetworkInterfaces")) {
                        String interfaceToken = parser.getAttributeValue(null, "token");
                        networkInterface.setInterfaceToken(interfaceToken);
                    } else if(parser.getName().equals("MTU")){
                        String mtu =  parser.nextText();
                        networkInterface.setMtu(mtu);
                    } else if(parser.getName().equals("IPv4")){
                        tag = "IPv4";
                    } else if(parser.getName().equals("PrefixLength")) {
                        if("IPv4".equals(tag)){
                            String ipvtPrefixLength = parser.nextText();
                            networkInterface.setIpvtPrefixLength(ipvtPrefixLength);
                        }
                    } else if(parser.getName().equals("IPv6")){
                        tag = "IPv6";
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
    }


    /**
     * 解析 xml数据，获取 MediaProfile
     *
     * @param xml 需要解析的数据
     */
    public static ArrayList<MediaProfile> getMediaProfiles(String xml) throws Exception {
        //初始化XmlPullParser
        XmlPullParser parser = Xml.newPullParser();
        //
        ArrayList<MediaProfile> profiles = new ArrayList<>();
        MediaProfile profile = null;
        //tag 用来判断当前解析Video还是Audio
        String tag = "";
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("Profiles")) {
                        profile = new MediaProfile();
                        String token = parser.getAttributeValue(null, "token");
                        //获取token
                        if (token != null) {
                            profile.setToken(token);
                        }
                        parser.next();
                        //获取name
                        if (parser.getName() != null && parser.getName().equals("Name")) {
                            profile.setName(parser.nextText());
                        }
                    } else if (parser.getName().equals("VideoEncoderConfiguration") && profile != null) {
                        //获取VideoEncode Token
                        String token = parser.getAttributeValue(null, "token");
                        profile.getVideoEncode().setToken(token);
//                        profile.getVideoEncode().setvideoSourceConfigurationToken(parser.getAttributeValue(0));
                        tag = "Video";
                    } else if (parser.getName().equals("AudioEncoderConfiguration") && profile != null) {
                        //获取AudioEncode Token
                        String tokenName = parser.getAttributeValue(null, "token");
                        profile.getAudioEncode().setTokenName(tokenName);
//                        profile.getAudioEncode().setvideoSourceConfigurationToken(parser.getAttributeValue(0));
                        tag = "Audio";
                    } else if (parser.getName().equals("VideoSourceConfiguration") && profile != null) {
                        //获取AudioEncode Token
                        String videoSourceConfigurationToken = parser.getAttributeValue(null, "token");
                        profile.getVideSource().setVideoSourceConfigurationToken(videoSourceConfigurationToken);
                        tag = "videoSource";
                    }  else if (parser.getName().equals("AudioSourceConfiguration") && profile != null) {
                        //获取AudioEncode Token
                        String audioSourceConfigurationToken = parser.getAttributeValue(null, "token");
                        profile.getAudioSource().setAudioSourceConfigurationToken(audioSourceConfigurationToken);
                        tag = "audioSource";
                    }  else if (parser.getName().equals("Name") && profile != null) {
                        //获取AudioEncode Token
                        if (tag.equals("videoSource")) {
                            String text = parser.nextText();
                            profile.getVideSource().setName(text);
                        }
                    } else if (parser.getName().equals("UseCount") && profile != null) {
                        //获取UseCount
                        if (tag.equals("videoSource")) {
                            String text = parser.nextText();
                            profile.getVideSource().setUserCount(text);
                        }
                    } else if (parser.getName().equals("SourceToken") && profile != null) {
                        //获取SourceToken
                        if (tag.equals("videoSource")) {
                            String videoSourceToken = parser.nextText();
                            profile.getVideSource().setVideoSourceToken(videoSourceToken);
                        } else if(tag.equals("audioSource")){
                            // 音频
                            String audioSourceToken = parser.nextText();
                            profile.getAudioSource().setAudioSourceToken(audioSourceToken);
                        }
                    } else if (parser.getName().equals("Bounds") && profile != null) {
                        //获取AudioEncode Token
                        if (tag.equals("videoSource")) {
                            String height = parser.getAttributeValue(null, "height");
                            String width = parser.getAttributeValue(null, "width");
                            profile.getVideSource().setHeight(Integer.parseInt(height));
                            profile.getVideSource().setWidth(Integer.parseInt(width));
                        }
                    } else if (parser.getName().equals("Width") && profile != null) {
                        //分辨率宽
                        String text = parser.nextText();
                        if (tag.equals("Video")) {
                            profile.getVideoEncode().setWidth(Integer.parseInt(text));
                        }
                    } else if (parser.getName().equals("Height") && profile != null) {
                        //分辨率高
                        String text = parser.nextText();
                        if (tag.equals("Video")) {
                            profile.getVideoEncode().setHeight(Integer.parseInt(text));
                        }
                    } else if (parser.getName().equals("FrameRateLimit") && profile != null) {
                        //帧率
                        String text = parser.nextText();
                        if (tag.equals("Video")) {
                            profile.getVideoEncode().setFrameRate(Integer.parseInt(text));
                        }
                    } else if (parser.getName().equals("Encoding") && profile != null) {
                        //编码格式
                        String text = parser.nextText();
                        if (tag.equals("Video")) {
                            profile.getVideoEncode().setEncoding(text);
                        } else if (tag.equals("Audio")) {
                            profile.getAudioEncode().setEncoding(text);
                        }
                    } else if (parser.getName().equals("Bitrate") && profile != null) {
                        //Bitrate
                        String text = parser.nextText();
                        if (tag.equals("Audio")) {
                            profile.getAudioEncode().setBitrate(Integer.parseInt(text));
                        }
                    } else if (parser.getName().equals("SampleRate") && profile != null) {
                        //SampleRate
                        String text = parser.nextText();
                        if (tag.equals("Audio")) {
                            profile.getAudioEncode().setSampleRate(Integer.parseInt(text));
                        }
                    } else if (parser.getName().equals("PTZConfiguration") && profile != null) {
                        //获取VideoEncode Token
                        profile.getPtzConfiguration().setToken(parser.getAttributeValue(0));
                        tag = "Ptz";
                    } else if (parser.getName().equals("NodeToken") && profile != null) {
                        //NodeToken
                        String text = parser.nextText();
                        if (tag.equals("Ptz")) {
                            profile.getPtzConfiguration().setNodeToken(text);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("Profiles")) {
                        profiles.add(profile);
                    }
                    if (parser.getName().equals("AudioEncoderConfiguration")
                            || parser.getName().equals("VideoEncoderConfiguration") || parser.getName().equals("PTZConfiguration")) {
                        tag = "";
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }

        return profiles;
    }

    /**
     * 获取 RTSP URL
     */
    public static String getStreamUri(String xml) throws Exception {
        String mediaUrl = "";
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("Uri")) {
                        mediaUrl = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return mediaUrl;
    }

    /**
     * 获取 截图uri
     */
    public static String getSnapshotUri(String xml) throws Exception {
        String mediaUrl = "";
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("Uri")) {
                        mediaUrl = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return mediaUrl;
    }

    public static String getUploadUri(String xml) throws Exception {
        String UploadUri = "";
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("UploadUri")) {
                        UploadUri = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return UploadUri;
    }

}
