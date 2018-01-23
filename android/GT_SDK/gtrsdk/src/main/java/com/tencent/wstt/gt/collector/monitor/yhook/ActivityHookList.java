package com.tencent.wstt.gt.collector.monitor.yhook;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.collector.GTRCollector;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;

import com.kunpeng.pit.HookAnnotation;

/**
 * Created by p_hongjcong on 2017/5/2.
 */
public class ActivityHookList {
    private static final String TAG = "HookList_activity";

    /** Activity 的开始+生命周期: **/
    @HookAnnotation(
            className = "android.app.Instrumentation",
            methodName = "execStartActivity",
            methodSig = "(Landroid/content/Context;" +
                    "Landroid/os/IBinder;" +
                    "Landroid/os/IBinder;" +
                    "Landroid/app/Activity;" +
                    "Landroid/content/Intent;" +
                    "ILandroid/os/Bundle;)" +
                    "Landroid/app/Instrumentation$ActivityResult;"
    )
    public static ActivityResult execStartActivity(Object thiz, Context who,
                                                   IBinder contextThread, IBinder token,
                                                   Activity target, Intent intent,
                                                   int requestCode, Bundle options) {
        GTRLog.e(TAG,"Instrumentation.execStartActivity");
        long start = System.currentTimeMillis();
        Instrumentation.ActivityResult activityResult =
                execStartActivity_backup(thiz, who, contextThread, token, target, intent, requestCode, options);
        long end = System.currentTimeMillis();

        GTRClient.pushData(new StringBuilder()
                .append("Instrumentation.execStartActivity")
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());

        return activityResult;
    }

    public static ActivityResult execStartActivity_backup(Object thiz, Context who,
                                                          IBinder contextThread, IBinder token,
                                                          Activity target, Intent intent,
                                                          int requestCode, Bundle options) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }
    public static ActivityResult execStartActivity_tmp(Object thiz,Context who,
                                                       IBinder contextThread, IBinder token,
                                                       Activity target, Intent intent,
                                                       int requestCode, Bundle options) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    @HookAnnotation(
            className = "android.app.Instrumentation",
            methodName = "callActivityOnCreate",
            methodSig = "(Landroid/app/Activity;Landroid/os/Bundle;)V"
    )
    public static void callActivityOnCreate(Object thiz, Activity activity, Bundle icicle) {
        GTRLog.e(TAG,"Instrumentation.callActivityOnCreate");
        long start = System.currentTimeMillis();
        callActivityOnCreate_backup(thiz,activity, icicle);
        long end = System.currentTimeMillis();

        String activityClassName = activity.getClass().getName();
        String objectHashCode = "" + activity.hashCode();
        GTRClient.pushData(new StringBuilder()
                .append("Instrumentation.callActivityOnCreate")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(objectHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  callActivityOnCreate_backup(Object thiz, Activity activity, Bundle icicle) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }
    public static void  callActivityOnCreate_tmp(Object thiz, Activity activity, Bundle icicle) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.app.Instrumentation",
            methodName = "callActivityOnStart",
            methodSig = "(Landroid/app/Activity;)V"
    )
    public static void callActivityOnStart(Object thiz, Activity activity) {
        GTRLog.e(TAG,"Instrumentation.callActivityOnStart");
        long start = System.currentTimeMillis();
        callActivityOnStart_backup(thiz,activity);
        long end = System.currentTimeMillis();

        String activityClassName = activity.getClass().getName();
        String objectHashCode = "" + activity.hashCode();
        GTRClient.pushData(new StringBuilder()
                .append("Instrumentation.callActivityOnStart")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(objectHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  callActivityOnStart_backup(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void  callActivityOnStart_tmp(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.app.Instrumentation",
            methodName = "callActivityOnResume",
            methodSig = "(Landroid/app/Activity;)V"
    )
    public static void callActivityOnResume(Object thiz, Activity activity) {
        GTRCollector.collectChoreographer(true);
        GTRLog.e(TAG,"Instrumentation.callActivityOnResume");
        long start = System.currentTimeMillis();
        callActivityOnResume_backup(thiz, activity);
        long end = System.currentTimeMillis();

        String activityClassName = activity.getClass().getName();
        String objectHashCode = "" + activity.hashCode();
        GTRClient.pushData(new StringBuilder()
                .append("Instrumentation.callActivityOnResume")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(objectHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  callActivityOnResume_backup(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }
    public static void  callActivityOnResume_tmp(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.app.Instrumentation",
            methodName = "callActivityOnPause",
            methodSig = "(Landroid/app/Activity;)V"
    )
    public static void callActivityOnPause(Object thiz, Activity activity) {
        GTRCollector.collectChoreographer(false);
        GTRLog.e(TAG,"Instrumentation.callActivityOnPause");
        long start = System.currentTimeMillis();
        callActivityOnPause_backup(thiz, activity);
        long end = System.currentTimeMillis();

        String activityClassName = activity.getClass().getName();
        String objectHashCode = "" + activity.hashCode();
        GTRClient.pushData(new StringBuilder()
                .append("Instrumentation.callActivityOnPause")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(objectHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  callActivityOnPause_backup(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void  callActivityOnPause_tmp(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.app.Instrumentation",
            methodName = "callActivityOnStop",
            methodSig = "(Landroid/app/Activity;)V"
    )
    public static void callActivityOnStop(Object thiz, Activity activity) {
        GTRLog.e(TAG,"Instrumentation.callActivityOnStop");
        long start = System.currentTimeMillis();
        callActivityOnStop_backup(thiz,activity);
        long end = System.currentTimeMillis();

        String activityClassName = activity.getClass().getName();
        String objectHashCode = "" + activity.hashCode();
        GTRClient.pushData(new StringBuilder()
                .append("Instrumentation.callActivityOnStop")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(objectHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  callActivityOnStop_backup(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void  callActivityOnStop_tmp(Object thiz, Activity activity) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }
}
