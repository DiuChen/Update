package com.diuchen.updateutil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

/**
 * Author: 刘晨
 * Date: 2019/4/18 17:20
 */
public class UpdateUtil {
    private static final String TAG = "UpdateUtil";
    private static String APK_NAME = "newApp.apk";
    private Context context;
    private String url;
    private long newVersionCode;
    private boolean showNotification;
    private UpdateListener updateListener;
    private String savePath;
    private UpdateServer updateServer;
    private ServiceConnection serviceConnection;

    private UpdateUtil(Builder builder) {
        this.context = builder.context;
        this.url = builder.url;
        this.newVersionCode = builder.newVersionCode;
        this.showNotification = builder.showNotification;
        this.updateListener = builder.updateListener;
    }

    /**
     * 开始进行更新
     */
    public void start() {
        if (context.getExternalFilesDir(null) != null) {
            File upDateDir = new File(context.getExternalFilesDir(null), "upDate");
            savePath = upDateDir.getAbsolutePath();
            File apkFile = new File(upDateDir, APK_NAME);
            if (apkFile.exists()) {
                if (Util.isNewApk(context, apkFile.getAbsolutePath(), newVersionCode)) {
                    Util.installApp(context, apkFile);
                    return;
                }
            }
            apkDownload();
        } else {
            Log.e(TAG, "start: 外部存储不可用");
        }
    }

    /**
     * 下载apk文件
     */
    private void apkDownload() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                UpdateServer.MyBinder binder = (UpdateServer.MyBinder) service;
                updateServer = binder.getService();
                updateServer.startDownload(url, savePath, APK_NAME, newVersionCode, showNotification, new UpdateListener() {
                    @Override
                    public void upDateStart(long max) {
                        if (updateListener != null) updateListener.upDateStart(max);
                    }

                    @Override
                    public void upDateProgress(int progress) {
                        if (updateListener != null) updateListener.upDateProgress(progress);
                    }

                    @Override
                    public void upDateComplete(String path) {
                        if (updateListener != null) updateListener.upDateComplete(path);
                        context.unbindService(serviceConnection);
                    }

                    @Override
                    public void upDateFail(String message) {
                        if (updateListener != null) updateListener.upDateFail(message);
                        context.unbindService(serviceConnection);
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(context, UpdateServer.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static final class Builder {
        public static final boolean DEFAULT_SHOW_NOTIFICATION = true;
        private Context context;
        private String url;
        private long newVersionCode;
        private boolean showNotification = DEFAULT_SHOW_NOTIFICATION;
        private UpdateListener updateListener;

        public Builder setActivity(Activity context) {
            this.context = context;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setNewVersionCode(long newVersionCode) {
            this.newVersionCode = newVersionCode;
            return this;
        }

        public Builder setShowNotification(boolean showNotification) {
            this.showNotification = showNotification;
            return this;
        }

        public Builder setUpdateListener(UpdateListener updateListener) {
            this.updateListener = updateListener;
            return this;
        }

        public UpdateUtil build() {
            if (context == null) throw new IllegalStateException("context == null");
            else if (url == null) throw new IllegalStateException("url == null");
            return new UpdateUtil(this);
        }
    }

    public interface UpdateListener {
        /**
         * 开始下载
         */
        void upDateStart(long max);

        /**
         * 下载进度
         */
        void upDateProgress(int progress);

        /**
         * 下载完成
         */
        void upDateComplete(String path);

        /**
         * 请求失败
         */
        void upDateFail(String message);
    }
}
