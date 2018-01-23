package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.OperationInfo;

import java.util.ArrayList;

public class OperationAnalysis {


    ArrayList<OperationInfo> operationInfos;

    public OperationAnalysis(GTRAnalysis gtrAnalysis, ArrayList<OperationInfo> operationInfos) {
        this.operationInfos = operationInfos;
    }

    OperationInfo physicsOperation;

    public void onActivity_onKeyDown(String operationName, long time) {
        physicsOperation = new OperationInfo();
        physicsOperation.operationClassName = "物理键";
        physicsOperation.operationCode = operationName;
        physicsOperation.operationBegin = time;
    }


    public void onActivity_onKeyUp(String operationName, long time) {
        if (physicsOperation != null && physicsOperation.operationCode.equals(operationName)) {
            physicsOperation.operationEnd = time;
            operationInfos.add(physicsOperation);
        }
        physicsOperation = null;
    }

    OperationInfo viewOperation;

    public void onView_dispatchTouchEvent(String viewType, String viewName, String action, long time) {
        switch (action) {
            case "down":
                viewOperation = new OperationInfo();
                viewOperation.operationClassName = viewType;
                viewOperation.operationCode = viewName;
                viewOperation.operationBegin = time;
                break;
            case "up":
                if (viewOperation != null && viewOperation.operationCode.equals(viewName)) {
                    viewOperation.operationEnd = time;
                    operationInfos.add(viewOperation);
                }
                viewOperation = null;
                break;
            default:
                break;
        }
    }


}
