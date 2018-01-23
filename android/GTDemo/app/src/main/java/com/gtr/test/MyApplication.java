package com.gtr.test;

import android.app.Application;

import com.tencent.wstt.gt.controller.GTRController;

/**
 * Created by elvis on 2016/11/19.
 * 自定义Application
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // GTRLog.isOpen = true;
        GTRController.init(getApplicationContext());
    }
}
