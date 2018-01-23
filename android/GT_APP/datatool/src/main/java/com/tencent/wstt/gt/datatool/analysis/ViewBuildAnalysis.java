package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.ViewBuildInfo;

import java.util.ArrayList;

public class ViewBuildAnalysis {


    ArrayList<ViewBuildInfo> viewBuildInfos;
    ArrayList<Integer> overViewBuilds;

    public ViewBuildAnalysis(GTRAnalysis gtrAnalysis, ArrayList<ViewBuildInfo> viewBuildInfos, ArrayList<Integer> overViewBuilds) {
        this.viewBuildInfos = viewBuildInfos;
        this.overViewBuilds = overViewBuilds;
    }

    public void onLayoutInflater_inflate(String resourceName, long start, long end) {
        ViewBuildInfo viewBuildInfo = new ViewBuildInfo();
        viewBuildInfo.method = ViewBuildInfo.INFLATE;
        viewBuildInfo.viewName = resourceName;
        viewBuildInfo.startTime = start;
        viewBuildInfo.endTime = end;
        viewBuildInfos.add(viewBuildInfo);

        checkOver(viewBuildInfo);
    }

//	public void onActivity_setContentView(String resourceName, long start, long end) {
//		ViewBuildInfo viewBuildInfo = new ViewBuildInfo();
//        viewBuildInfo.method = ViewBuildInfo.SETCONTENTVIEW;
//        viewBuildInfo.viewName = resourceName;
//        viewBuildInfo.startTime = start;
//        viewBuildInfo.endTime = end;
//        viewBuildInfos.add(viewBuildInfo);
//
//        checkOver(viewBuildInfo);
//	}


    void checkOver(ViewBuildInfo viewBuildInfo) {
        long loadTime = viewBuildInfo.endTime - viewBuildInfo.startTime;
        if (loadTime >= 10) {
            boolean isExists = false;
            for (int h = 0; h < overViewBuilds.size(); h++) {
                ViewBuildInfo overBuild = viewBuildInfos.get(overViewBuilds.get(h));
                if (overBuild.viewName.equals(viewBuildInfo.viewName)) {
                    isExists = true;
                    if (loadTime > overBuild.endTime - overBuild.startTime) {
                        overViewBuilds.remove(h);
                        overViewBuilds.add(viewBuildInfos.size() - 1);
                    }
                    break;
                }
            }
            if (!isExists) {
                overViewBuilds.add(viewBuildInfos.size() - 1);
            }
        }
    }


}
