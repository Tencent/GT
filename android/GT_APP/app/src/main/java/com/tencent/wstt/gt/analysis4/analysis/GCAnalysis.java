package com.tencent.wstt.gt.analysis4.analysis;


import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.analysis4.obj.LogInfo;
import com.tencent.wstt.gt.analysis4.util.LogUtil;
import com.tencent.wstt.gt.dao.DetailListData;

/**
 * Created by p_hongjcong on 2017/8/2.
 */

public class GCAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public GCAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    public void onCollectLog(String log, long time) {
        LogInfo logInfo = LogUtil.onCollectLog(log, time);
        if (logInfo == null) {
            return;
        }
        if ((logInfo.tag.contains("dalvikvm") || logInfo.tag.contains("art"))
                && logInfo.logContent.contains("GC")
                && logInfo.logContent.contains("freed")
                && logInfo.logContent.contains("paused")) {
            onCollectGC(logInfo.tag, logInfo.logContent, logInfo.time);
        }
    }

    private void onCollectGC(String gcTag, String gcString, long time) {
        gtrAnalysisResult.gcNum++;
        DetailListData detailListData;
        //检测显示GC
        if (gcString.contains("Explicit")) {
            // System.gc();引起
            gtrAnalysisResult.explicitGCNum++;
            detailListData = new DetailListData("TAG:" + gcTag + "\nLog:" + gcString, DetailListData.Error);
        } else {
            detailListData = new DetailListData("TAG:" + gcTag + "\nLog:" + gcString, DetailListData.Normal);
        }
        gtrAnalysisResult.allGCListData.add(detailListData);
        //call回调通知数据刷新
        GTRAnalysis.refreshGCInfo();
    }
}
