package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;

/**
 * Created by p_hongjcong on 2017/8/1.
 */

public class DeviceAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public DeviceAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    public void onCollectDeviceInfo(String vendor, String model, String sdkName, int sdkInt) {
        gtrAnalysisResult.vendor = vendor;
        gtrAnalysisResult.model = model;
        gtrAnalysisResult.sdkName = sdkName;
        gtrAnalysisResult.sdkInt = sdkInt;
        GTRAnalysis.refreshDeviceInfo();
    }
}
