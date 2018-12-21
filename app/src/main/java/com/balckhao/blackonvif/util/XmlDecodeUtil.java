package com.balckhao.blackonvif.util;

import android.util.Xml;

import com.balckhao.blackonvif.onvifBean.Device;
import com.balckhao.blackonvif.onvifBean.MediaProfile;

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
//                        String token = parser.getAttributeValue("token","token");
//                        profile.setToken(token);
                       String token = parser.getNamespace("token");
                        //获取token
                        profile.setToken(parser.getAttributeValue(1));
                        parser.next();
                        //获取name
                        if (parser.getName() != null && parser.getName().equals("Name")) {
                            profile.setName(parser.nextText());
                        }
                    } else if (parser.getName().equals("VideoEncoderConfiguration") && profile != null) {
                        //获取VideoEncode Token
                        profile.getVideoEncode().setToken(parser.getAttributeValue(0));
                        tag = "Video";
                    } else if (parser.getName().equals("AudioEncoderConfiguration") && profile != null) {
                        //获取AudioEncode Token
                        profile.getAudioEncode().setToken(parser.getAttributeValue(0));
                        tag = "Audio";
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

}
