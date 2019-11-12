package com.wp.android_onvif.http;

import android.text.TextUtils;

import com.wp.android_onvif.Constant;
import com.wp.android_onvif.util.LogClientUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 断电下载
 */
public class FileDownload {

    private String tag = "DownloadUtil";
    /**
     * 总的大小
     */
    private long totalSize = 0;

    private boolean isBreakpoint; // 是否支持断点下载
    private String url;
    private boolean isDownloading;
    private String fileName; // 文件名称
    private String filePath; // 父路径
    private DownloadUtil.OnDownloadListener listener;
    private int percent; // 当前进度

    /**
     * 计算已经下载过的文件大小
     *
     * @param url
     * @return
     */
    private long calculateDownloadedLength(String url) {
        File file = createFile(url);
        if (file.exists()) {
            if (isBreakpoint()) {
                return file.length();
            } else {
                file.delete();
            }
        }
        return 0;
    }

    /**
     * 创建文件
     *
     * @param url 资源地址
     * @return
     */
    protected File createFile(String url) {
        if(TextUtils.isEmpty(fileName) || TextUtils.isEmpty(filePath)){
            if (url == null)
                return null;
            String[] strs = url.split("/");
            if (strs.length < 2) {
                LogClientUtils.v(tag, "url 格式不正确");
            }
            final String fileName = strs[strs.length - 1];
            File partentFile = new File(Constant.ROOT_PATH);
            if(!partentFile.exists()){
                partentFile.mkdirs();
            }
            return new File(Constant.ROOT_PATH, fileName);
        } else {
            File partentFile = new File(filePath);
            if(!partentFile.exists()){
                partentFile.mkdirs();
            }
            return new File(partentFile, fileName);
        }
    }


    /**
     * 下载文件
     */
    public void download() {
        if (TextUtils.isEmpty(url)) {
            LogClientUtils.e(tag, "下载地址为空");
            return;
        }
        setDownloading(true);
        download(url);
    }


    protected void download(final String url) {
        final long downloadedLength = calculateDownloadedLength(url);
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Android")
                .header("Content-Type", "text/html; charset=utf-8;")
                .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                .url(url)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                LogClientUtils.e(tag, "文件下载失败 " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                doHttpResponse(response.body().byteStream(), response.body().contentLength(), downloadedLength, createFile(url));
            }
        });
    }

    /**
     * /**
     * 处理服务器返回数据
     */
    private void doHttpResponse(InputStream is, long contentLength, long downloadedLength, File file) {
        long downloading = 0;
        byte[] buf = new byte[2048];
        int len;
        RandomAccessFile randomAccessFile = null;
        try {
            if (downloadedLength == 0) {
                totalSize = contentLength;
            } else {
                totalSize = downloadedLength + contentLength;
            }
            if (totalSize == downloadedLength) {
                //已下载字节和文件总字节相等，说明下载已经完成了
//                sendCompletedMsg(file);
                if (listener != null) {
                    listener.onDownloadSuccess(file.getAbsolutePath());
                }
                return;
            }
            if (totalSize == 0) {
                if (downloadedLength == 0) {
                    LogClientUtils.d(tag, " 下载的文件大小为0");
                } else {
                    if (isBreakpoint()) {
                        if (listener != null) { // 下载完成
                            listener.onDownloadSuccess(file.getAbsolutePath());
                        }
                    } else {
                        file.delete();
                    }
                }
                return;
            }
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(downloadedLength);
            while ((len = is.read(buf)) != -1) {
//                if (isPause() || isCancel()) {
//                    break;
//                }
                randomAccessFile.write(buf, 0, len);
                downloading += len;
                //传递更新信息
                int percentage = (int) ((downloadedLength + downloading) * 100 / totalSize);
//                sendDownloadingMsg(totalSize, downSum, percentage);
                if (percentage % 10 == 0) {
                    if (percent != percentage) {
                        percent = percentage;
                        LogClientUtils.d(tag, "文件下载进度 " + percent + "%");
                    }
                }
//                LogClientUtils.d(tag, "当前下载进度 " + percentage + "%");
            }
            if (listener != null) { // 下载完成
                listener.onDownloadSuccess(file.getAbsolutePath());
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onDownloadFailed(e.getMessage());
            }
        } finally {
            setDownloading(false);
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                LogClientUtils.e(tag, " io流关闭失败 " + e.getMessage());
            }
            try {
                if (randomAccessFile != null)
                    randomAccessFile.close();
            } catch (IOException e) {
                LogClientUtils.e(tag, " io流关闭失败 " + e.getMessage());
            }
        }
    }


    public void setDownloading(boolean downloading) {
        this.isDownloading = downloading;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isBreakpoint() {
        return isBreakpoint;
    }

    public void setBreakpoint(boolean breakpoint) {
        isBreakpoint = breakpoint;
    }

    public DownloadUtil.OnDownloadListener getListener() {
        return listener;
    }

    public void setListener(DownloadUtil.OnDownloadListener listener) {
        this.listener = listener;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
