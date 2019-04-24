package com.diuchen.updateutil;

import android.os.SystemClock;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: 刘晨
 * Date: 2019/4/18 17:29
 * <p>
 */
public class OkHttpDownload {
    private String url;
    private String savePath;
    private String fileName;
    private DownloadListener downloadListener;
    private int progressInterval;
    private long lastProgressTime = 0;

    private OkHttpDownload(Builder builder) {
        this.url = builder.url;
        this.savePath = builder.savePath;
        this.fileName = builder.fileName;
        this.downloadListener = builder.downloadListener;
        this.progressInterval = builder.progressInterval;
    }

    public void start() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (downloadListener != null) downloadListener.downloadFail(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() == null) {
                            if (downloadListener != null)
                                downloadListener.downloadFail("response.body() == null");
                            return;
                        }
                        if (fileName == null) {
                            fileName = getNameFromUrl(url);
                        }
                        File saveDir = new File(savePath);
                        saveDir.mkdirs();
                        File saveFile = new File(saveDir, fileName);

                        long length = response.body().contentLength();
                        if (downloadListener != null)
                            downloadListener.downloadStart(length);

                        InputStream is = response.body().byteStream();
                        OutputStream os = null;
                        byte data[] = new byte[1024 * 8];
                        int len;
                        long allLen = 0;
                        try {
                            os = new BufferedOutputStream(new FileOutputStream(saveFile));
                            while ((len = is.read(data)) != -1) {
                                os.write(data, 0, len);
                                allLen += len;
                                if (downloadListener != null) {
                                    long time = SystemClock.elapsedRealtime();
                                    if (time - lastProgressTime > progressInterval || allLen == length) {
                                        lastProgressTime = time;
                                        downloadListener.downloadProgress((int) ((allLen / (double) length) * 100));
                                    }
                                }
                            }
                            //必须先关闭流再回调下载完成 否则无法获取安装包信息
                            is.close();
                            os.close();
                            if (downloadListener != null)
                                downloadListener.downloadComplete(saveFile.getAbsolutePath());
                        } catch (IOException e) {
                            if (downloadListener != null)
                                downloadListener.downloadFail(e.getMessage());
                            e.printStackTrace();
                        } finally {
                            try {
                                is.close();
                                if (os != null) {
                                    os.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static final class Builder {
        public static final int DEFAULT_PROGRESS_INTERVAL = 500/* millis **/;
        private String url;
        private String savePath;
        private String fileName;
        private DownloadListener downloadListener;
        private int progressInterval = DEFAULT_PROGRESS_INTERVAL;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setDownloadListener(DownloadListener downloadListener) {
            this.downloadListener = downloadListener;
            return this;
        }

        public Builder setProgressInterval(int progressInterval) {
            this.progressInterval = progressInterval;
            return this;
        }

        public OkHttpDownload build() {
            if (url == null) throw new IllegalStateException("下载路径不能为空");
            else if (savePath == null) throw new IllegalStateException("保存路径不能为空");
            return new OkHttpDownload(this);
        }
    }

    public interface DownloadListener {
        /**
         * 开始下载
         */
        void downloadStart(long max);

        /**
         * 下载进度
         */
        void downloadProgress(int progress);

        /**
         * 下载完成
         */
        void downloadComplete(String path);

        /**
         * 请求失败
         */
        void downloadFail(String message);
    }
}
