package com.diuchen.updateutil;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;

/**
 * Author: 刘晨
 * Date: 2019/4/23 10:20
 */
public class UpdateServer extends Service {
    private static final String TAG = "UpdateServer";
    private static final String UPDATE_CHANNEL = "UpaDate";
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        //创建通知渠道
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(UPDATE_CHANNEL,
                    "应用更新", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("下载通知");
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
        //初始化通知
        builder = new NotificationCompat.Builder(this, UPDATE_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_launcher_round);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    public void startDownload(String url, String savePath, final String fileName, final UpdateUtil.UpdateListener updateListener) {
        new OkHttpDownload.Builder()
                .setUrl(url)
                .setSavePath(savePath)
                .setFileName(fileName)
                .setDownloadListener(new OkHttpDownload.DownloadListener() {
                    @Override
                    public void downloadStart(long max) {
                        updateListener.upDateStart(max);
                        builder.setContentTitle("下载安装包")
                                .setContentText("准备下载");
                        notificationManager.notify(1, builder.build());
                    }

                    @Override
                    public void downloadProgress(int progress) {
                        updateListener.upDateProgress(progress);
                        builder.setContentTitle("正在下载安装包")
                                .setContentText(progress + "%")
                                .setProgress(100, progress, false);
                        notificationManager.notify(1, builder.build());
                    }

                    @Override
                    public void downloadComplete(String path) {
                        updateListener.upDateComplete(path);
                        notificationManager.cancel(1);
                        File apkFile = new File(path);
                        if (Util.isNewApk(getApplicationContext(), apkFile.getAbsolutePath())) {
                            Util.installApp(getApplicationContext(), apkFile);
                        } else {
                            builder.setContentTitle("下载完成")
                                    .setContentText("安装包校验失败")
                                    .setProgress(0, 0, false);
                            notificationManager.notify(1, builder.build());
                        }
                    }

                    @Override
                    public void downloadFail(String message) {
                        updateListener.upDateFail(message);
                        builder.setContentTitle("下载安装包")
                                .setContentText("下载失败")
                                .setProgress(0, 0, false);
                        notificationManager.notify(1, builder.build());
                    }
                })
                .build()
                .start();
    }

    class MyBinder extends Binder {
        UpdateServer getService() {
            return UpdateServer.this;
        }
    }
}
