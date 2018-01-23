package com.tencent.wstt.gt.collector.monitor.yhook;

import android.graphics.Canvas;

import com.kunpeng.pit.HookAnnotation;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.collector.util.ViewDrawUtil;

/**
 * Created by p_xcli on 2018/1/20.
 */

public class ViewDrawHookWhiteList {
    private static final String TAG = "HookList_viewdraw";

    /** View绘制相关：**/
    @HookAnnotation(
            className = "android.view.View",
            methodName = "dispatchDraw",
            methodSig = "(Landroid/graphics/Canvas;)V")
    public static void dispatchDraw(Object thiz, Canvas canvas) {
        GTRLog.e(TAG,"View.dispatchDraw ");
        Object view = thiz;
        String drawClassName = view.getClass().getName();//当前绘制类名（包含包名）
        String objectHashCode = "" + view.hashCode();//当前绘制的对象名（包含包名和对象地址）
        ViewDrawUtil.onViewGroup_dispatchDraw_before(drawClassName, objectHashCode);
        dispatchDraw_backup(thiz, canvas);
        ViewDrawUtil.onViewGroup_dispatchDraw_after();
    }

    public static void dispatchDraw_backup(Object thiz, Canvas canvas) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void dispatchDraw_tmp(Object thiz, Canvas canvas) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }
}
