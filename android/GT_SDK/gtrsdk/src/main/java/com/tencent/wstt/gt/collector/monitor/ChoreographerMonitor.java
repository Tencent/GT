package com.tencent.wstt.gt.collector.monitor;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Choreographer;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;

/**
 * Created by p_hongjcong on 2017/7/13.
 */
public class ChoreographerMonitor extends AbsMonitor {
    private static final String TAG = "ChoreographerMonitor";

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(Context context) {
        if (started) {
            return;
        }

        if (handler == null) {
            thread = new HandlerThread("GTRChoreographerMonitorThread");
            thread.start();
            handler = new Handler(thread.getLooper());
        }
        stop();

        Choreographer.getInstance().postFrameCallback(frameCallback);
        handler.postDelayed(stackCollectRunnable, interval);

        GTRLog.d(TAG, "monitor started");
        started = true;
    }

    @Override
    public void stop() {
        if (!started) {
            return;
        }

        if (handler != null) {
            handler.removeCallbacks(stackCollectRunnable);
        }
        Choreographer.getInstance().removeFrameCallback(frameCallback);

        GTRLog.d(TAG, "monitor stopped");
        started = false;
    }

    ////////////////////////////////////////////////////////////////////
    //                         栈采集相关                              //
    ////////////////////////////////////////////////////////////////////
    private static Thread uiThread = Looper.getMainLooper().getThread();

    // 采集间隔--30ms
    private static int interval = 30;

    private Runnable stackCollectRunnable = new Runnable() {
        @Override
        public void run() {
            if (threadId == -1) {
                threadId = android.os.Process.myTid();
            }

            StringBuilder stackStringBuilder = new StringBuilder();
            for (StackTraceElement stackTraceElement : uiThread.getStackTrace()) {
                stackStringBuilder.append(stackTraceElement.toString()).append("&&rn&&");
            }
            String stack = stackStringBuilder.toString();
            long time = System.currentTimeMillis();
            GTRClient.pushData(new StringBuilder()
                    .append("stackCollect")
                    .append(GTConfig.separator)
                    .append(stack)
                    .append(GTConfig.separator)
                    .append(time)
                    .toString());

            if (handler != null) {
                handler.postDelayed(stackCollectRunnable, interval);
            }
        }
    };

    ///////////////////////////////////////////////////////////////////
    //                      doFrame监控相关：                         //
    ///////////////////////////////////////////////////////////////////
    private Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {//系统绘帧回调
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void doFrame(long frameTimeNanos) {
            // 关闭栈采集
            handler.removeCallbacks(stackCollectRunnable);

            GTRClient.pushData(new StringBuilder()
                    .append("frameCollect")
                    .append(GTConfig.separator)
                    .append(System.currentTimeMillis())
                    .toString());

            // 开启下一个doFrame监控
            Choreographer.getInstance().postFrameCallback(frameCallback);

            // 重启栈采集
            if (handler != null) {
                handler.postDelayed(stackCollectRunnable, interval);
            }
        }
    };
}
