package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.dao.DetailListData;

public class ViewBuildAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public ViewBuildAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    public void onLayoutInflater_inflate(String resourceName, long start, long end) {
        gtrAnalysisResult.viewBuildNum++;
        DetailListData detailListData;
        long buildTime = end - start;
        if (buildTime >= 10) {
            gtrAnalysisResult.overViewBuildNum++;
            detailListData = new DetailListData("资源ID:" + resourceName + "\n构建耗时:" + buildTime + "ms", DetailListData.Error);
        } else {
            detailListData = new DetailListData("资源ID:" + resourceName + "\n构建耗时:" + buildTime + "ms", DetailListData.Normal);
        }
        gtrAnalysisResult.allViewBuildListData.add(detailListData);
        //call回调通知数据刷新
        GTRAnalysis.refreshViewBuildInfo();
    }
}
