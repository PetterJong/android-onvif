package com.wp.android_onvif.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
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
            // 发送请求数据
            String Authorization =  conn.getHeaderField("Authorization");
            String msg = conn.getResponseMessage();
            String www = conn.getHeaderField("WWW-Authenticate");
            int code = conn.getResponseCode();
            if (code == 200) {
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
     * HA1 = MD5（<username>:<reaml>:<psd>）
     * HA1 = MD5(<method>:<disgestUriPath>)
     * Response = MD5(MD5(A1):<nonce>:<nc>:<conce>:<qop>:MD5(A2))
     * 对用户名、认证域(realm)以及密码的合并值计算 MD5 哈希值，结果称为 HA1。
     * 对HTTP方法以及URI的摘要的合并值计算 MD5 哈希值，例如，"GET" 和 "/dir/index.html"，结果称为 HA2。
     * 对 HA1、服务器密码随机数(nonce)、请求计数(nc)、客户端密码随机数(cnonce)、保护质量(qop)以及 HA2 的合并值计算 MD5 哈希值。结果即为客户端提供的 response 值。
     * 因为服务器拥有与客户端同样的信息，因此服务器可以进行同样的计算，以验证客户端提交的 response 值的正确性。在上面给出的例子中，结果是如下计算的。 （MD5()表示用于计算 MD5 哈希值的函数；“\”表示接下一行；引号并不参与计算）
     * 根据上面的算法所给出的示例，将在每步得出如下结果。
     * 	HA1=MD5(admin:DS-2CD2310FD-I:hibox123) = 5a423defbb16f11b397b926915dfaa3b
     * 	HA2 = MD5("GET:/onvif-http/snapshot?Profile_1") = 73d467d4c78c8f2e040b2943c9545432
     * 	Response = MD5( "5a423defbb16f11b397b926915dfaa3b:4d6b5a444f5455774d7a6b364e575669597a67794d446b3d:00000001:cuYIxHJg3bt4gqYZwqndaAkuyUXtkE8b:auth:73d467d4c78c8f2e040b2943c9545432" )
     *             = 5920528fb9287d0ef90735acf122f695
     *  HEAD = Digest username="admin",realm="DS-2CD2310FD-I",nonce="4d6b5a444f5455774d7a6b364e575669597a67794d446b3d",uri="/onvif-http/snapshot?Profile_1",cnonce="cuYIxHJg3bt4gqYZwqndaAkuyUXtkE8b",nc=00000001,response="5920528fb9287d0ef90735acf122f695",qop="auth"
     * @param url
     * @return
     */
    public static byte[] getByteArray2(String url, String user, String psd) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful() ) {
            if( response.code() == 200 &&  response.body() != null){
                return response.body().bytes();
            }
        } else if(response.code() == 401){ // 未鉴权去鉴权
            //WWW-Authenticate: Digest qop="auth", realm="DS-2CD2310FD-I", nonce="4d6a4931516a51304e554d364e445935595759785954553d", stale="TRUE"
            //WWW-Authenticate: Basic realm="DS-2CD2310FD-I"
            Headers h = response.headers();
            List<String> auths = h.values("WWW-Authenticate");
            Pattern qopPattern = Pattern.compile("qop=\"(.*?)\"");
            Pattern realmPattern = Pattern.compile("realm=\"(.*?)\"");
            Pattern noncePattern = Pattern.compile("nonce=\"(.*?)\"");
            String qop = "";
            String realm = "";
            String nonce = "";
            String method = request.method();
            String host = response.request().url().host();

            String disgestUriPath = url.split(host)[1];
            for (String head: auths) {
                Matcher qopMatcher = qopPattern.matcher(head);
                while (qopMatcher.find()){
                    try{
                        qop = qopMatcher.group(1);
                    } catch (Exception e){
                        LogClientUtils.d(tag, e.getMessage());
                    }
                }
                Matcher realmMatcher = realmPattern.matcher(head);
                while (realmMatcher.find()){
                    try{
                        realm = realmMatcher.group(1);
                    } catch (Exception e){
                        LogClientUtils.d(tag, e.getMessage());
                    }
                }
                Matcher nonceMatcher = noncePattern.matcher(head);
                while (nonceMatcher.find()){
                    try{
                        nonce = nonceMatcher.group(1);
                    } catch (Exception e){
                        LogClientUtils.d(tag, e.getMessage());
                    }
                }
            }
            return degistHttp(url, user, psd, method, disgestUriPath, nonce, realm, qop);
        }
        return null;
    }

    /**
     * http鉴权
     * @param url
     * @param user
     * @param psd
     * @param method
     * @param disgestUriPath
     * @param nonce
     * @param realm
     * @param qop
     * @return
     * @throws IOException
     */
    private static byte[] degistHttp(String url, String user, String psd, String method, String disgestUriPath, String nonce, String realm, String qop) throws IOException {
        String nc = "00000001";
        String cnonce = getNonce();
        String ha1Data = getMd5Data(user, realm, psd);
        String ha1 = MD5Util.MD5Encode(ha1Data);
        String ha2Data = getMd5Data(method, disgestUriPath);
        String ha2 = MD5Util.MD5Encode(ha2Data);
        String ha3Data = getMd5Data(ha1, nonce, nc, cnonce, qop, ha2);
        String responseData = MD5Util.MD5Encode(ha3Data);
        String headFormat = "Digest username=\"%s\",realm=\"%s\",nonce=\"%s\",uri=\"%s\",cnonce=\"%s\",nc=%s,response=\"%s\",qop=\"%s\"" ;
        String head = String.format(headFormat, user, realm, nonce, disgestUriPath, cnonce, nc, responseData, qop);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .addHeader("Authorization", head)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful() ) {
            if( response.code() == 200 &&  response.body() != null){
                return response.body().bytes();
            }
        }
        return null;
    }

    private static String getMd5Data(String... params){
        StringBuilder sb = new StringBuilder();
        for (String param : params){
            sb.append(param).append(":");
        }
        String data = sb.toString();
        return data.substring(0, data.length() - 1);
    }

    /**
     * 获取 Nonce
     *
     * @return Nonce
     */
    private static String getNonce() {
        //初始化随机数
        Random r = new Random();
        String text = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String nonce = "";
        for (int i = 0; i < 32; i++) {
            int index = r.nextInt(text.length());
            nonce = nonce + text.charAt(index);
        }
        return nonce;
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
