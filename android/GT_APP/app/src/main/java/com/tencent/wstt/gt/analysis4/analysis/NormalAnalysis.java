package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.dao.DetailPointData;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.obj.FrontBackState;

import java.util.ArrayList;

public class NormalAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public NormalAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    private long lastTime = 0;
    private long lastCPUApp = 0;
    private long lastCPUTotal = 0;
    private long lastFlowUpload = 0;
    private long lastFlowDownload = 0;

    public void onCollectNormalInfo(long time, long cpuTotal, long cpuApp,
                                    String cpuThreads, int memory, long flowUpload,
                                    long flowDownload, String gtrThreads) {
        // 去掉gtr线程带来的影响
        long cpuApp_noGTR = cpuApp;
        String[] gtrThs = gtrThreads.split(",");
        String[] cpuThs = cpuThreads.split(",");
        for (String s : cpuThs) {
            String[] sdsd = s.split(":");
            for (String temp : gtrThs) {
                if (sdsd[0].equals(temp)) {
                    cpuApp_noGTR = cpuApp_noGTR - Integer.parseInt(sdsd[1]);
                }
            }
        }

        if (lastTime != 0) {
            gtrAnalysisResult.nowCPU = (cpuApp_noGTR - lastCPUApp) * 100L / (cpuTotal - lastCPUTotal);
            gtrAnalysisResult.nowMemory = memory / 1024;
            gtrAnalysisResult.nowFlow = gtrAnalysisResult.nowFlow + (flowUpload + flowDownload - lastFlowUpload - lastFlowDownload);
            gtrAnalysisResult.nowFlowSpeed = (flowUpload + flowDownload - lastFlowUpload - lastFlowDownload) * 1000 / 1024 / (time - lastTime);


            gtrAnalysisResult.allCPUChartDatas.add(new DetailPointData((time - GTRAnalysis.startTestTime) / 1000, gtrAnalysisResult.nowCPU));
            gtrAnalysisResult.allMemoryChartDatas.add(new DetailPointData((time - GTRAnalysis.startTestTime) / 1000, gtrAnalysisResult.nowMemory));
            gtrAnalysisResult.allFlowChartDatas.add(new DetailPointData((time - GTRAnalysis.startTestTime) / 1000, gtrAnalysisResult.nowFlow / 1024));

            long thisCpuApp = cpuApp_noGTR - lastCPUApp;
            long thisCpuTotal = cpuTotal - lastCPUTotal;

            // 前台统计
            if (isFront(lastTime) && isFront(time)) {
                long frontTime = time - lastTime;
                long frontFlowUpload = flowUpload - lastFlowUpload;
                long frontFlowDownload = flowDownload - lastFlowDownload;
                if (frontTime >= 0 //时间需要正向
                        && thisCpuApp >= 0 && thisCpuTotal >= 0 //cpu需要正向
                        && frontFlowUpload >= 0 && frontFlowDownload >= 0) {//流量需要正向
                    gtrAnalysisResult.frontTime += frontTime;
                    gtrAnalysisResult.frontCpuApp += thisCpuApp;
                    gtrAnalysisResult.frontCpuTotal += thisCpuTotal;

                    if (gtrAnalysisResult.frontCpuMax < thisCpuApp * 100L / thisCpuTotal) {
                        gtrAnalysisResult.frontCpuMax = thisCpuApp * 100L / thisCpuTotal;
                    }

                    if (gtrAnalysisResult.frontMemoryMax < (long) memory / 1024) {
                        gtrAnalysisResult.frontMemoryMax = (long) memory / 1024;
                    }

                    gtrAnalysisResult.frontMemoryAverage_Sum += (long) memory / 1024;
                    gtrAnalysisResult.frontMemoryAverage_Num++;
                    gtrAnalysisResult.frontFlowUpload += frontFlowUpload;
                    gtrAnalysisResult.frontFlowDownload += frontFlowDownload;
                }
            }

            // 后台统计
            if (!isFront(lastTime) && !isFront(time)) {
                long backTime = time - lastTime;
                long backFlowUpload = flowUpload - lastFlowUpload;
                long backFlowDownload = flowDownload - lastFlowDownload;

                if (backTime >= 0 //时间需要正向
                        && thisCpuApp >= 0 && thisCpuTotal >= 0 //cpu需要正向
                        && backFlowUpload >= 0 && backFlowDownload >= 0) {//流量需要正向
                    gtrAnalysisResult.backTime += backTime;
                    gtrAnalysisResult.backCpuApp += thisCpuApp;
                    gtrAnalysisResult.backCpuTotal += thisCpuTotal;

                    if (gtrAnalysisResult.backCpuMax < thisCpuApp * 100L / thisCpuTotal) {
                        gtrAnalysisResult.backCpuMax = thisCpuApp * 100L / thisCpuTotal;
                    }

                    if (gtrAnalysisResult.backMemoryMax < (long) memory / 1024) {
                        gtrAnalysisResult.backMemoryMax = (long) memory / 1024;
                    }

                    gtrAnalysisResult.backMemoryAverage_Sum += (long) memory / 1024;
                    gtrAnalysisResult.backMemoryAverage_Num++;
                    gtrAnalysisResult.backFlowUpload += backFlowUpload;
                    gtrAnalysisResult.backFlowDownload += backFlowDownload;
                }
            }
        }

        lastTime = time;
        lastCPUApp = cpuApp_noGTR;
        lastCPUTotal = cpuTotal;
        lastFlowUpload = flowUpload;
        lastFlowDownload = flowDownload;
        GTRAnalysis.refreshNormalInfo();
    }

    private ArrayList<FrontBackState> frontBackStates = new ArrayList<>();

    public void onFront(long time) {
        frontBackStates.add(new FrontBackState(time, true));
    }

    public void onBack(long time) {
        frontBackStates.add(new FrontBackState(time, false));
    }

    private boolean isFront(long time) {
        boolean isF = false;
        for (int i = 0; i < frontBackStates.size(); i++) {
            if (frontBackStates.get(i).time < time) {
                isF = frontBackStates.get(i).isFront;
            }
        }
        return isF;
    }
}
