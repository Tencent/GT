package com.tencent.wstt.gt.collector.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/**
 * Created by elvis on 2017/2/24.
 */

public class ProcessUtil {
    /**
     * 判断进程是否包含Application主线程
     * @param context
     * @param pid
     * @return
     */
    public static boolean isUIProcess(Context context, int pid) {
        String processName = null;
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }

        String packageName = context.getPackageName();
        return processName != null && processName.equals(packageName);
    }
}
