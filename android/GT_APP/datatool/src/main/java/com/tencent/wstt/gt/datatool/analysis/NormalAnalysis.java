package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.FrontBackInfo;
import com.tencent.wstt.gt.datatool.obj.NormalInfo;

import java.util.ArrayList;


public class NormalAnalysis {

    GTRAnalysis gtrAnalysis;
    private ArrayList<NormalInfo> normalInfos;
    FrontBackInfo frontBackInfo;
    ArrayList<Integer> gtrThreadInfos;

    public NormalAnalysis(GTRAnalysis gtrAnalysis, ArrayList<NormalInfo> normalInfos, FrontBackInfo frontBackInfo, ArrayList<Integer> gtrThreadInfos) {
        this.gtrAnalysis = gtrAnalysis;
        this.normalInfos = normalInfos;
        this.frontBackInfo = frontBackInfo;
        this.gtrThreadInfos = gtrThreadInfos;
    }

    public void onCollectNormalInfo(long time, long cpuTotal, long cpuApp, String cpuThreads, int memory, long flowUpload, long flowDownload, String gtrThreads) {

        //gtrThreadInfos:
        gtrThreadInfos.clear();
        String[] gtrThs = gtrThreads.split(",");
        for (String temp : gtrThs) {
            gtrThreadInfos.add(Integer.parseInt(temp));
        }

        //去掉gtr线程带来的影响
        long cpuApp_noGTR = cpuApp;
        String[] cpuThs = cpuThreads.split(",");
        for (String s : cpuThs) {
            String[] sdsd = s.split(":");
            for (String temp : gtrThs) {
                if (sdsd[0].equals(temp)) {
                    cpuApp_noGTR = cpuApp_noGTR - Integer.parseInt(sdsd[1]);
                }
            }
        }


        NormalInfo normalInfo = new NormalInfo();
        normalInfo.time = time;
        normalInfo.cpuApp = cpuApp_noGTR;
        normalInfo.cpuTotal = cpuTotal;
        normalInfo.memory = memory / 1024;
        normalInfo.flowUpload = flowUpload / 1024;
        normalInfo.flowDownload = flowDownload / 1024;

        for (String s : cpuThs) {
            String[] sdsd = s.split(":");
            normalInfo.threadCpus.add(new NormalInfo.ThreadCpu(Integer.parseInt(sdsd[0]), sdsd[2].replace("@@@", ",").replace("%%%", ":"), Long.parseLong(sdsd[1])));
        }

        normalInfos.add(normalInfo);


        frontBackAnalysis(normalInfo);


    }


    /**
     * 前后台数据分析：
     */
    NormalInfo lastNormalInfo = null;

    void frontBackAnalysis(NormalInfo normalInfo) {
        if (lastNormalInfo != null) {
            //前台统计
            if (isFront(lastNormalInfo.time) && isFront(normalInfo.time)) {
                long frontTime = normalInfo.time - lastNormalInfo.time;
                long frontCpuApp = normalInfo.cpuApp - lastNormalInfo.cpuApp;
                long frontCpuTotal = normalInfo.cpuTotal - lastNormalInfo.cpuTotal;
                long frontMemory = normalInfo.memory;
                long frontFlowUpload = normalInfo.flowUpload - lastNormalInfo.flowUpload;
                long frontFlowDownload = normalInfo.flowDownload - lastNormalInfo.flowDownload;
                if (frontTime >= 0 //时间需要正向
                        && frontCpuApp >= 0 && frontCpuTotal >= 0 //cpu需要正向
                        && frontFlowUpload >= 0 && frontFlowDownload >= 0) {//流量需要正向
                    frontBackInfo.frontTime = frontBackInfo.frontTime + frontTime;
                    frontBackInfo.frontCpuApp = frontBackInfo.frontCpuApp + frontCpuApp;
                    frontBackInfo.frontCpuTotal = frontBackInfo.frontCpuTotal + frontCpuTotal;
                    frontBackInfo.frontCpuArray.add(frontCpuApp * 100L / frontCpuTotal);
                    frontBackInfo.frontMemoryArray.add(frontMemory);
                    frontBackInfo.frontFlowUpload = frontBackInfo.frontFlowUpload + frontFlowUpload;
                    frontBackInfo.frontFlowDownload = frontBackInfo.frontFlowDownload + frontFlowDownload;
                }
            }
            //后台统计
            if (!isFront(lastNormalInfo.time) && !isFront(normalInfo.time)) {
                long backTime = normalInfo.time - lastNormalInfo.time;
                long backCpuApp = normalInfo.cpuApp - lastNormalInfo.cpuApp;
                long backCpuTotal = normalInfo.cpuTotal - lastNormalInfo.cpuTotal;
                long backMemory = normalInfo.memory;
                long backFlowUpload = normalInfo.flowUpload - lastNormalInfo.flowUpload;
                long backFlowDownload = normalInfo.flowDownload - lastNormalInfo.flowDownload;
                if (backTime >= 0 //时间需要正向
                        && backCpuApp >= 0 && backCpuTotal >= 0 //cpu需要正向
                        && backFlowUpload >= 0 && backFlowDownload >= 0) {//流量需要正向
                    frontBackInfo.backTime = frontBackInfo.backTime + backTime;
                    frontBackInfo.backCpuApp = frontBackInfo.backCpuApp + backCpuApp;
                    frontBackInfo.backCpuTotal = frontBackInfo.backCpuTotal + backCpuTotal;
                    frontBackInfo.backCpuArray.add(backCpuApp * 100L / backCpuTotal);
                    frontBackInfo.backMemoryArray.add(backMemory);
                    frontBackInfo.backFlowUpload = frontBackInfo.backFlowUpload + backFlowUpload;
                    frontBackInfo.backFlowDownload = frontBackInfo.backFlowDownload + backFlowDownload;
                }
            }
        }

        lastNormalInfo = normalInfo;
    }


    boolean isFront(long time) {
        boolean isF = false;
        for (int i = 0; i < gtrAnalysis.getFrontBackStates().size(); i++) {
            if (gtrAnalysis.getFrontBackStates().get(i).time < time) {
                isF = gtrAnalysis.getFrontBackStates().get(i).isFront;
            }
        }
        return isF;
    }
}
