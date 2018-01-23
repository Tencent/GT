package com.gtr.test;

/**
 * Created by Elvis on 2016/12/14.
 *
 */

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MyWakeLock {
    private WakeLock mWakeLock;
    private Context mContext;

    public MyWakeLock(Context context) {
        this.mContext = context;
    }

    public void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ZHENGYI.WZY");
            if (mWakeLock != null) {
                mWakeLock.acquire();
            }
        }
    }

    public void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }
}

//各种锁的类型对CPU 、屏幕、键盘的影响：
//        PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的。
//
//        SCREEN_DIM_WAKE_LOCK：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
//
//        SCREEN_BRIGHT_WAKE_LOCK：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
//
//        FULL_WAKE_LOCK：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
//
//        ACQUIRE_CAUSES_WAKEUP：正常唤醒锁实际上并不打开照明。相反，一旦打开他们会一直仍然保持(例如来世user的activity)。当获得wakelock，这个标志会使屏幕或/和键盘立即打开。一个典型的使用就是可以立即看到那些对用户重要的通知。
//
//        ON_AFTER_RELEASE：设置了这个标志，当wakelock释放时用户activity计时器会被重置，导致照明持续一段时间。如果你在wacklock条件中循环，这个可以用来减少闪烁


//我们创建WakeLock的时候，可以通过设置不同的标志来产生不同的行为，Android定义了以下标志：
//
//        PARTIAL_WAKE_LOCK                屏幕关，键盘灯关，不休眠
//
//        SCREEN_DIM_WAKE_LOCK             屏幕灰，键盘灯关，不休眠
//
//        SCREEN_BRIGHT_WEEK_LOCK          屏幕亮，键盘灯关，不休眠
//
//        FULL_WAKE_LOCK                   屏幕亮，键盘灯亮，不休眠
//
//下面这些标志可以结合使用：
//
//        ACQUIRE_CAUSES_WAKEUP            强制使屏幕亮起，这种锁主要针对一些必须通知用户的操作.
//
//        ON_AFTER_RELEASE                 当锁被释放时，保持屏幕亮起一段时间