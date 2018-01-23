package com.tencent.wstt.gt;

import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Elvis on 2016/11/19.
 * 自定义Log，支持打印开关，支持本地存储开关
 */

public class GTRLog {
    private static String TAG = "GTR_TAG_";

    //是否要打开Log
    public static boolean isOpen = false;

    //是否要进行本地保存
    public static boolean isSaveLocal = false;

    public static void v(String tag, String msg) {
        if (!isOpen) {
            return;
        }
        Log.v(TAG + tag, msg);
        if (isSaveLocal) {
            saveLocal("verbose", tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (!isOpen) {
            return;
        }
        Log.d(TAG + tag, msg);
        if (isSaveLocal) {
            saveLocal("debug", tag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if (!isOpen) {
            return;
        }
        Log.i(TAG + tag, msg);
        if (isSaveLocal) {
            saveLocal("info", tag, msg);
        }
    }
    public static void w(String tag, String msg) {
        if (!isOpen) {
            return;
        }
        Log.w(TAG + tag, msg);
        if (isSaveLocal) {
            saveLocal("warn", tag, msg);
        }
    }
    public static void e(String tag, String msg) {
        if (!isOpen) {
            return;
        }
        Log.e(TAG + tag, msg);
        if (isSaveLocal) {
            saveLocal("error", tag, msg);
        }
    }

    /**
     * @deprecated
     * 本地存储相关：
     */
    private static void saveLocal(String level, String tag, String msg) {
        // TODO
    }
}
