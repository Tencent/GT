package com.tencent.wstt.gt.analysis4;

/**
 * Created by p_hongjcong on 2017/8/14.
 */

public class UITest {
    public static void testStart() {
        //  GTRAnalysis.start(context,"com.elvis.test");
    }

    public static void testStop() {
        GTRAnalysis.addCallBack(new GTRAnalysisCallback(){
            @Override
            public void refreshNormalInfo(GTRAnalysisResult gtrAnalysisResult) {

            }

            @Override
            public void refreshDeviceInfo(GTRAnalysisResult gtrAnalysisResult) {

            }
        });
    }



}
