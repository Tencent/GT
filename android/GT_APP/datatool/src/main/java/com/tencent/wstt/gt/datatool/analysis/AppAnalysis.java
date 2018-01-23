package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.AppInfo;

/**
 * Created by p_hongjcong on 2017/7/31.
 */

public class AppAnalysis {

    AppInfo appInfo;

    public AppAnalysis(GTRAnalysis gtrAnalysis, AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public void onCollectAppInfo(String packageName, String appName, String versionName, int versionCode, int gtrVersionCode, long startTestTime, int mainThreadId) {

        appInfo.packageName = packageName;
        appInfo.appName = appName;
        appInfo.versionName = versionName;
        appInfo.versionCode = versionCode;
        appInfo.gtrVersionCode = gtrVersionCode;
        appInfo.startTestTime = startTestTime;
        appInfo.mainThreadId = mainThreadId;
    }


}
