package com.tencent.wstt.gt.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.tencent.wstt.gt.GTApp;

import java.util.List;

/**
 * Created by p_hongjcong on 2017/8/15.
 */

public class GTRControllerServer {

    private static final String GTR_BROADCAST_ACTION = "GTR.GTRBroadcastReceiver";
    private static final String KEY_BEHAVIOR = "behavior";
    private static final String KEY_PARAM_1 = "param_1";
    private static final String KEY_PARAM_2 = "param_2";

    //强制杀死被测应用
    public static void killAppWithSDK(Context context,String packageName){
        Intent intent = new Intent();
        intent.setAction(GTR_BROADCAST_ACTION);
        intent.putExtra(KEY_BEHAVIOR,"kill");
        intent.putExtra(KEY_PARAM_1,packageName);
        context.sendBroadcast(intent);
    }

    public static void openApp(Context context,String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        PackageManager pManager = GTApp.getContext().getPackageManager();
        List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent,0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String startappName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            ComponentName cn = new ComponentName(startappName, className);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }



}
