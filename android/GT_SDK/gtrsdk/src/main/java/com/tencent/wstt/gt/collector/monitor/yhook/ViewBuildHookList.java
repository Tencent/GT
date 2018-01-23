package com.tencent.wstt.gt.collector.monitor.yhook;

import android.view.View;
import android.view.ViewGroup;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;
import com.tencent.wstt.gt.collector.GTRCollector;

import com.kunpeng.pit.HookAnnotation;


/**
 * Created by p_hongjcong on 2017/5/2.
 */
public class ViewBuildHookList {
    private static final String TAG = "HookList_viewbuild";

    /** View构建相关： **/
    @HookAnnotation(
            className = "android.view.LayoutInflater",
            methodName = "inflate",
            methodSig = "(ILandroid/view/ViewGroup;)Landroid/view/View;")
    public static View inflate(Object thiz, int resource, ViewGroup root) {
        GTRLog.e(TAG,"LayoutInflater.inflate");
        String resourceName = "未知";

        if (GTRCollector.applicationContext != null) {
            try {
                resourceName = GTRCollector.applicationContext.getResources().getResourceName(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long start = System.currentTimeMillis();
        View view = inflate_backup(thiz, resource, root);
        long end = System.currentTimeMillis();

        GTRClient.pushData(new StringBuilder()
                .append("LayoutInflater.inflate")
                .append(GTConfig.separator).append(resourceName)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
        return view;
    }

    public static View inflate_backup(Object thiz, int resource, ViewGroup root) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    public static View inflate_tmp(Object thiz, int resource, ViewGroup root) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }
}
