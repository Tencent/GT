package com.tencent.wstt.gt.collector.monitor;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.kunpeng.pit.HookMain;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.collector.monitor.yhook.WhiteList;

/**
 * Created by p_hongjcong on 2017/7/13.
 */
public class HookMonitor extends AbsMonitor {
    private static final String TAG = "HookMonitor";

    @Override
    public void start() {
        start(null);
    }

    private static void startJava_YAHFAHook() {
        Class<?> hookClazz;
        try {
            hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.ActivityHookList");
            HookMain.doHookDefault(hookClazz);
            hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.DBHookList");
            HookMain.doHookDefault(hookClazz);
            hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.FragmentHookList");
            HookMain.doHookDefault(hookClazz);
            hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.FragmentHookList_v4");
            HookMain.doHookDefault(hookClazz);
            hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.OperationHookList");
            HookMain.doHookDefault(hookClazz);
            hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.ViewBuildHookList");
            HookMain.doHookDefault(hookClazz);

            if (WhiteList.isYunOS()) {
                hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.ViewDrawHookWhiteList");
            } else {
                hookClazz = Class.forName("com.tencent.wstt.gt.collector.monitor.yhook.ViewDrawHookList");
            }

            HookMain.doHookDefault(hookClazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Context context) {
        startJava_YAHFAHook();
        GTRLog.d(TAG, "monitor started");
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public int getWorkThreadId() {
        return super.getWorkThreadId();
    }

    @Override
    public Handler getHandler() {
        return super.getHandler();
    }
}
