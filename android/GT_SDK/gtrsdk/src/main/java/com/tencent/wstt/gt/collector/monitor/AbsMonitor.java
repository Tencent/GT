package com.tencent.wstt.gt.collector.monitor;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by p_xcli on 2017/12/24.
 */

public class AbsMonitor {
    protected HandlerThread thread;
    protected Handler handler;
    int threadId = -1;

    volatile boolean started;

    public void start() {

    }

    public void start(Context context) {

    }

    public void stop() {

    }

    public void stop(Context context) {

    }

    public int getWorkThreadId() {
        return threadId;
    }

    public Handler getHandler() {
        return handler;
    }
}
