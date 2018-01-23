package com.tencent.wstt.gt.collector.monitor.yhook;

import android.graphics.Canvas;

import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.collector.util.ViewDrawUtil;

import com.kunpeng.pit.HookAnnotation;


/**
 * Created by p_hongjcong on 2017/5/2.
 */
public class ViewDrawHookList {
    private static final String TAG = "HookList_viewdraw";

    /** View绘制相关：**/
    @HookAnnotation(
            className = "android.view.ViewGroup",
            methodName = "dispatchDraw",
            methodSig = "(Landroid/graphics/Canvas;)V")
    public static void dispatchDraw(Object thiz, Canvas canvas) {
        GTRLog.e(TAG,"ViewGroup.dispatchDraw");
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
