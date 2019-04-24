package com.diuchen.updateutil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Author: 刘晨
 * Date: 2019/4/22 16:55
 */
public class Util {
    /**
     * 检查安装包 包名是否正确 是否大于当前版本
     *
     * @param context        上下文
     * @param apkFilePath    安装包路径
     * @param newVersionCode 最新的版本号 如果为0 则以context提供的版本号为准
     * @return 是否符合要求
     */
    public static boolean isNewApk(Context context, String apkFilePath, long newVersionCode) {
        PackageManager packageManager = context.getPackageManager();

        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null) return false;
        String myPackageName = context.getPackageName();
        PackageInfo myPackageInfo = null;
        long versionCode;
        long myVersionCode = 0;
        try {
            myPackageInfo = packageManager.getPackageInfo(myPackageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //api28(9.0)中PackageInfo.versionCode被废弃了 改用中PackageInfo.getLongVersionCode()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            versionCode = packageInfo.getLongVersionCode();
            if (myPackageInfo != null) myVersionCode = myPackageInfo.getLongVersionCode();
        } else {
            versionCode = packageInfo.versionCode;
            if (myPackageInfo != null) myVersionCode = myPackageInfo.versionCode;
        }
        return packageInfo.packageName.equals(myPackageName) &&
                (newVersionCode > 0 ? versionCode == newVersionCode : versionCode > myVersionCode);
    }

    public static void installApp(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = context.getPackageName() + ".fileprovider";
            data = FileProvider.getUriForFile(context, authority, file);
        }
        intent.setDataAndType(data, type);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
