package com.tencent.wstt.gt.collector.monitor.yhook;

import android.os.Build;

/**
 * @author p_xcli
 * Created on 2018/1/20.
 */

public class WhiteList {
    /**
     * Whether the OS is powered by YunOS,
     * the list is still far from being completed.
     * @return
     */
    public static boolean isYunOS() {
        return Build.DISPLAY.startsWith("Flyme");
    }
}
