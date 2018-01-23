package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;

/**
 * Created by p_hongjcong on 2017/7/31.
 */

public class AppAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public AppAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    public void onCollectAppInfo(String packageName, String appName,
                                 String versionName, int versionCode,
                                 int gtrVersionCode, long startTestTime, int mainThreadId) {
        gtrAnalysisResult.packageName = packageName;
        gtrAnalysisResult.appName = appName;
        gtrAnalysisResult.versionName = versionName;
        gtrAnalysisResult.versionCode = versionCode;
        gtrAnalysisResult.gtrVersionCode = gtrVersionCode;
        gtrAnalysisResult.startTestTime = startTestTime;
        gtrAnalysisResult.mainThreadId = mainThreadId;
        GTRAnalysis.refreshAppInfo();
    }
}
