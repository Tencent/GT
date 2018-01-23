package com.tencent.wstt.gt.collector;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;
import com.tencent.wstt.gt.collector.monitor.MonitorManager;
import com.tencent.wstt.gt.collector.util.ProcessUtil;

public class GTRCollector {
    private static final String TAG = "GTRCollector";

    public static int mainThreadId;

    // 作为当次测试的ID
    private static long startTestTime = System.currentTimeMillis();

    public static Context applicationContext;

    // 监控器状态
    private static boolean isRunning = false;

    public static boolean init(Context context) {
        // 防止重复开启
        if (isRunning) {
            return true;
        }

        // 支持SDK版本21（5.0）以上
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }

        applicationContext = context.getApplicationContext();

        // 只采集应用UI进程数据
        if (!ProcessUtil.isUIProcess(applicationContext, android.os.Process.myPid())) {
            return false;
        }

        mainThreadId = android.os.Process.myTid();

        GTRClient.init(applicationContext);
        initMonitors();
        isRunning = true;

        if (!GTRClient.tryConnectServer(applicationContext)) {
            GTRLog.w(TAG, "No GT service is available, waiting for the GT app.");
        }

        return isRunning;
    }

    private static void initMonitors() {
        MonitorManager.getInstance().setContext(applicationContext);
        MonitorManager.startMonitors();
    }

    public static void startMonitorsIfNeeded() {
        if (!isRunning) {
            MonitorManager.startMonitors();
        }
    }

    public static long getStartTime() {
        return startTestTime;
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static Runnable stopDelayRunnable = new Runnable() {
        @Override
        public void run() {
            stopChoreographerCollect();
        }
    };

    private static void startChoreographerCollect() {
        // 获取卡顿数据（计算流畅值和拉栈）：(这个必须在最后开启，防止捕获到GTR的卡顿)
        MonitorManager.getMonitor(MonitorManager.CHORE_MONITOR).start();
    }

    private static void stopChoreographerCollect() {
        //获取卡顿数据（计算流畅值和拉栈）：(这个必须在最后开启，防止捕获到GTR的卡顿)
        MonitorManager.getMonitor(MonitorManager.CHORE_MONITOR).stop();
    }

    /**
     * 开启或者关闭choreographer的数据采集
     * @param ifStart true if want to start collect choreographer data,
     *                false if to stop
     */
    public static void collectChoreographer(boolean ifStart) {
        if (isRunning && ifStart) {
            handler.removeCallbacks(stopDelayRunnable);
            startChoreographerCollect();
        } else if (isRunning) {
            handler.postDelayed(stopDelayRunnable,1500);
        }
    }
}
