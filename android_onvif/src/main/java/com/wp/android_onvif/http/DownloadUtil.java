
package com.wp.android_onvif.http;

import java.io.File;
import java.io.IOException;

/**
 * 下载广告视频工具类
 */
public class DownloadUtil {
    private String tag = "DownloadUtil";
    private static DownloadUtil downloadUtil;

    public static DownloadUtil getInstance() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
    }



    /**
     *
     * @param url 下载地址
     * @param filePath 文件路径
     * @param fileName  文件名称
     * @param Breakpoint 是否支持断点
     * @param listener 下载回调
     */
    public void download(String url, final String filePath, final String fileName,boolean Breakpoint, final DownloadUtil.OnDownloadListener listener) {
        FileDownload download = new FileDownload();
        download.setUrl(url);
        download.setBreakpoint(Breakpoint);
        download.setFileName(fileName);
        download.setFilePath(filePath);
        download.setListener(listener);
        download.download();
    }

    /**
     *
     * @param url 下载地址
     * @param filePath 文件路径
     * @param fileName  文件名称
     * @param listener 下载回调
     */
    public void download(String url, final String filePath, final String fileName, final DownloadUtil.OnDownloadListener listener) {

        download(url, filePath, fileName, false, listener);
//        RequestUtils.download(false, url, new Callback() {
//
//            private long sum;
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (listener != null) {
//                    listener.onDownloadFailed(e.getMessage());
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                LogClientUtils.d(tag, "当前线程：" + Thread.currentThread() + "  返回结果：" + response.code());
//                if (response.code() == 200 && response.body() != null) {
//                    byte[] buf = new byte[2048];
//                    int percent = -1;
//                    FileOutputStream fos = null;
//                    InputStream is = null;
//                    // 储存下载文件的目录
//                    try {
//                        is = response.body().byteStream();
//                        long total = response.body().contentLength();
//                        LogClientUtils.d(tag, "文件总大小 (KB)" + total / 1024 );
//                        File dirFile = new File(filePath);
//                        if(!dirFile.exists()){
//                            dirFile.mkdirs();
//                        }
//                        fos = new FileOutputStream(filePath+ fileName);
//                        int len = 0;
//                        while ((len = is.read(buf)) != -1) {
//                            fos.write(buf, 0, len);
//                            sum += len;
//
//                            float temp = ((float)sum / total) ;
//                            int curPer = (int) (temp * 100);
//                            if(curPer % 10 == 0){
//                                if(percent != curPer){
//                                    percent = curPer;
//                                    LogClientUtils.d(tag, "文件下载进度 " + curPer +"%" );
//                                }
//                            }
//
//                            // 下载中
//                        }
//                        fos.flush();
//                        if(sum >= total){
//                            if (listener != null) {
//                                listener.onDownloadSuccess(filePath + fileName);
//                            }
//                        }
//                    } catch (Exception e){
//                        if(fos != null){
//                            try {
//                                fos.close();
//                            } catch (IOException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                        if(is != null){
//                            try {
//                                is.close();
//                            } catch (IOException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                        if (listener != null) {
//                            listener.onDownloadFailed(e.getMessage());
//                        }
//
//                    }
//
//
////                    byte[] bytes = response.body().bytes();
////                    FileUtils.writeResoursToSDCard(filePath, fileName, bytes);
////                    if (listener != null) {
////                        listener.onDownloadSuccess(filePath + fileName);
////                    }
//                } else {
//                    if (listener != null) {
//                        listener.onDownloadFailed("下载失败 " + response.code());
//                    }
//                }
//            }
//        });
    }


    private String getNameFromUrl(String url) {
        return url.substring((url.lastIndexOf("/") + 0x1));
    }

    private String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        return downloadFile.getAbsolutePath();
    }


    public interface OnDownloadListener {
        public void onDownloadSuccess(String path);

        public void onDownloadFailed(String message);
    }
}
