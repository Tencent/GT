package com.tencent.wstt.gt.datatool.analysis;


import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.GCInfo;

import java.util.ArrayList;

/**
 * Created by p_hongjcong on 2017/8/2.
 */

public class GCAnalysis {

    ArrayList<GCInfo> allGCInfos;
    ArrayList<Integer> explicitGCs;

    public GCAnalysis(GTRAnalysis gtrAnalysis, ArrayList<GCInfo> allGCInfos, ArrayList<Integer> explicitGCs) {
        this.allGCInfos = allGCInfos;
        this.explicitGCs = explicitGCs;
    }


    public void onCollectGC(String gcTag, String gcString, long time) {
        GCInfo gcInfo = GCInfo.initGcInfo(gcTag, gcString, time);
        allGCInfos.add(gcInfo);

        //检测显示GC
        if (gcInfo.gcResult.contains("Explicit")) {//System.gc();引起
            explicitGCs.add(allGCInfos.size() - 1);
        }


    }

}
