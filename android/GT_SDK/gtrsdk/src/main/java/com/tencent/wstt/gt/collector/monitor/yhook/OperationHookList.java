package com.tencent.wstt.gt.collector.monitor.yhook;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;

import com.kunpeng.pit.HookAnnotation;

/**
 * Created by p_hongjcong on 2017/5/2.
 *
 */
public class OperationHookList {
    private static final String TAG = "HookList_operation";

    /** 屏幕事件监听相关： **/
    @HookAnnotation(
            className = "android.app.Activity",
            methodName = "onKeyDown",
            methodSig = "(ILandroid/view/KeyEvent;)Z")
    public static boolean onKeyDown(Object thiz, int keyCode, KeyEvent event) {
        GTRLog.e(TAG,"Activity.onKeyDown");
        String buttonName = "";
        long time = System.currentTimeMillis();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                buttonName = "返回键";
                break;
            case KeyEvent.KEYCODE_HOME:
                buttonName = "Home键";
                break;
            case KeyEvent.KEYCODE_MENU:
                buttonName = "菜单键";
                break;
        }

        GTRClient.pushData(new StringBuilder()
                .append("Activity.onKeyDown")
                .append(GTConfig.separator).append(buttonName)
                .append(GTConfig.separator).append(time)
                .toString());

        return onKeyDown_backup(thiz, keyCode, event) ;
    }

    public static boolean onKeyDown_backup(Object thiz, int keyCode, KeyEvent event) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }

    public static boolean onKeyDown_tmp(Object thiz, int keyCode, KeyEvent event) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }

    @HookAnnotation(
            className = "android.app.Activity",
            methodName = "onKeyUp",
            methodSig ="(ILandroid/view/KeyEvent;)Z")
    public static boolean onKeyUp(Object thiz, int keyCode, KeyEvent event) {
        GTRLog.e(TAG,"Activity.onKeyUp");
        String buttonName = "";
        long time = System.currentTimeMillis();

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                buttonName = "返回键";
                break;
            case KeyEvent.KEYCODE_HOME:
                buttonName = "Home键";
                break;
            case KeyEvent.KEYCODE_MENU:
                buttonName = "菜单键";
                break;
        }

        GTRClient.pushData(new StringBuilder()
                .append("Activity.onKeyUp")
                .append(GTConfig.separator).append(buttonName)
                .append(GTConfig.separator).append(time)
                .toString());
        return  onKeyUp_backup(thiz, keyCode, event);
    }

    public static boolean onKeyUp_backup(Object thiz, int keyCode, KeyEvent event) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }

    public static boolean onKeyUp_tmp(Object thiz, int keyCode, KeyEvent event) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }

    @HookAnnotation(
            className = "android.view.View",
            methodName = "dispatchTouchEvent",
            methodSig = "(Landroid/view/MotionEvent;)Z")
    public static boolean dispatchTouchEvent(Object thiz, MotionEvent event) {
        //true表示已经被本层视图消费了
        boolean  result = dispatchTouchEvent_backup(thiz,event);
        if (result) {
            GTRLog.e(TAG,"View.dispatchTouchEvent:" + thiz.getClass().getName() + ":" + event.getAction() + "消费了此事件！");
            Object view  = thiz;
            String viewType;
            String viewName;

            if (view instanceof Button) {
                viewType = "Button";
                viewName = ((TextView)view).getText().toString().replace("\n"," ");
            } else if (view instanceof TextView) {
                viewType =" TextView";
                viewName = ((TextView)view).getText().toString().replace("\n"," ");
            } else {
                String[] ss = view.getClass().getName().split("\\.");
                viewType = ss[ss.length-1];
                viewName = "" + view.hashCode();
            }

            String action = "";
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    action = "down";
                    break;
                case MotionEvent.ACTION_UP:
                    action = "up";
                    break;
            }

            long time = System.currentTimeMillis();
            GTRClient.pushData(new StringBuilder()
                    .append("View.dispatchTouchEvent")
                    .append(GTConfig.separator).append(viewType)
                    .append(GTConfig.separator).append(viewName)
                    .append(GTConfig.separator).append(action)
                    .append(GTConfig.separator).append(time)
                    .toString());
        } else {
           GTRLog.e(TAG,"View.dispatchTouchEvent:" + thiz.getClass().getName() + ":" + event.getAction() + "没消费此事件！");
        }
        return result;
    }

    public static boolean dispatchTouchEvent_backup(Object thiz, MotionEvent event) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }

    public static boolean dispatchTouchEvent_tmp(Object thiz, MotionEvent event) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }
}
