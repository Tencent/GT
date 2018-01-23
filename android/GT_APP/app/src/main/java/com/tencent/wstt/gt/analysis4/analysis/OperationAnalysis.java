package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.analysis4.obj.OperationInfo;

public class OperationAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public OperationAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    OperationInfo physicsOperation;

    public void onActivity_onKeyDown(String operationName, long time) {

    }


    public void onActivity_onKeyUp(String operationName, long time) {

    }

    OperationInfo viewOperation;

    public void onView_dispatchTouchEvent(String viewType, String viewName, String action, long time) {

    }
}
