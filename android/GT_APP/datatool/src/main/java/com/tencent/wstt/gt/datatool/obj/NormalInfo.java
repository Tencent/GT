package com.tencent.wstt.gt.datatool.obj;

import java.util.ArrayList;

/**
 * Created by Elvis on 2017/1/4.
 * Email:elvis@21kunpeng.com
 * 用于保存后台定时采集的数据（流量、内存、CPU、电量）
 */

public class NormalInfo {


    //(cpuApp - lastCpuApp) * 100L / (cpuTotal-lastCpuTotal);//计算cpu占用率

    public long time;//时间
    public long memory;
    public long flowUpload;
    public long flowDownload;
    public long cpuApp;
    public long cpuTotal;


    public ArrayList<ThreadCpu> threadCpus = new ArrayList<>();//所有线程的CPU占用


    public static class ThreadCpu {
        public ThreadCpu() {

        }

        public ThreadCpu(int threadId, String threadName, long threadCpu) {
            this.threadId = threadId;
            this.threadName = threadName;
            this.threadCpu = threadCpu;
        }

        public int threadId;
        public String threadName;
        public long threadCpu;
    }


}
