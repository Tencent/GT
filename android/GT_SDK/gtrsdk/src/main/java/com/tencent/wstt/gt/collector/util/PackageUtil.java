package com.tencent.wstt.gt.collector.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by elvis on 2016/10/20.
 */
public class PackageUtil {
    // 返回程序包名
    public static String getAPPName(String packageName, Context context) {
        if (packageName == null || packageName.equals("") || packageName.equals("null")) {
            return "未知应用";
        }

        PackageManager pm = context.getPackageManager();
        String pckName;
        try {
            pckName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
            return pckName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return packageName;
        }
    }

    // 返回程序版本名
    public static String getAppVersionName(String packageName, Context context) {
        String versionName;
        if (packageName == null || packageName.equals("") || packageName.equals("null")) {
            return "";
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 返回程序版本号
    public static int getAppVersionCode(String packageName, Context context) {
        int versionCode = -1;
        if (packageName == null || packageName.equals("") || packageName.equals("null")) {
            return -1;
        }

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }

        return versionCode;
    }
}
