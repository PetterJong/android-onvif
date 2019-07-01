package com.wp.android_onvif.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author ： BlackHao
 * Time : 2017/1/9 14:20
 * Description : 网络请求类
 */

public class HttpUtil {

    private static String tag = "OnvifSdk";

    /**
     * POST 请求
     */
    public static String postRequest(String baseUrl, String params) throws Exception {
        String receive = "";
        // 新建一个URL对象
        URL url = new URL(baseUrl);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        //设置请求允许输入 默认是true
        // Post请求必须设置允许输出 默认false
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        // 设置为Post请求
        urlConn.setRequestMethod("POST");
        // Post请求不能使用缓存
        urlConn.setUseCaches(false);
        //设置本次连接是否自动处理重定向
        urlConn.setInstanceFollowRedirects(true);
        // 配置请求Content-Type,application/soap+xml
        urlConn.setRequestProperty("Content-Type", "application/soap+xml");
        // 开始连接
        urlConn.connect();
        // 发送请求数据
        urlConn.getOutputStream().write(params.getBytes());
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
            InputStream is = urlConn.getInputStream();
            byte[] data = new byte[1024];
            int n;
            while ((n = is.read(data)) != -1) {
                receive = receive + new String(data, 0, n);
            }
        } else {
            throw new Exception("ResponseCodeError : " + urlConn.getResponseCode());
        }
        // 关闭连接
        urlConn.disconnect();
        return receive;
    }


    /**
     * 下载图片(GET的请求方式)
     *
     * @param webSite url地址
     * @return byte[]
     */
    public static byte[] getByteArray(String webSite) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(webSite);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //连接的超时时间
            conn.setReadTimeout(8000);
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 上传文件
     *
     * @param urlStr
     * @return
     */
    public static String postUpload(String urlStr, String filePath) {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------"; //boundary就是request头和上传文件内容的分隔符
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置本次连接是否自动处理重定向
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // file
            File file = new File(filePath);
            if (!file.exists()) {
                throw new Exception("固件包不存在");
            }
            String filename = file.getName();

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"" + filename + "\"; filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type:application/octetstream" + "\r\n\r\n");

            out.write(strBuf.toString().getBytes());

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            StringBuffer strBufResult = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBufResult.append(line).append("\n");
            }
            res = strBufResult.toString();
            reader.close();
        } catch (Exception e) {
            Log.d(tag, "发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return res;
    }


    public static boolean upload(String url, String filePath) throws Exception {
        boolean result = false;
        File file = new File(filePath);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .addHeader("Content-Type", "application/octet-stream")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.code() == 200) {
            result = true;
        }
        return result;
    }


}
