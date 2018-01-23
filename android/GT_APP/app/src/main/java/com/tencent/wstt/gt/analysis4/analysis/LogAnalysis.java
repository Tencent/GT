package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.obj.LogInfo;
import com.tencent.wstt.gt.analysis4.util.LogUtil;

public class LogAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public LogAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    public void onCollectLog(String log, long time) {
        LogInfo logInfo = LogUtil.onCollectLog(log, time);
        if (logInfo == null) {
            return;
        }
        if (logInfo.grade.contains("A") || logInfo.grade.contains("E")) {
            gtrAnalysisResult.errorLogNum++;
            GTRAnalysis.refreshLogInfo();
        }
    }
}
