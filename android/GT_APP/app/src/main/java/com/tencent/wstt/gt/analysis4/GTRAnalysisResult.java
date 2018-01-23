package com.tencent.wstt.gt.analysis4;


import com.tencent.wstt.gt.dao.DetailListData;
import com.tencent.wstt.gt.dao.DetailPointData;

import java.util.ArrayList;

/**
 * GTR分析结果数据类
 * Created by p_hongjcong on 2017/8/10.
 */

public class GTRAnalysisResult {
    public int pId = -1;

    //APP信息
    public String appName = null;
    public String packageName = null;
    public String versionName = null;
    public int versionCode = -1;
    public int gtrVersionCode = -1;
    public long startTestTime = -1;
    public int mainThreadId = -1;

    //Device信息：
    public String vendor = null;
    public String model = null;
    public String sdkName = null;
    public int sdkInt = -1;

    //基础数据：
    public long nowCPU = 0;
    public long nowMemory = 0;
    public long nowFlow = 0;//总流量kb
    public long nowFlowSpeed = 0;//三秒内流量均速
    public long frontTime = 0;
    public long frontCpuApp = 0;
    public long frontCpuTotal = 0;
    public long frontCpuMax = 0;
    public long frontMemoryAverage_Sum = 0;
    public long frontMemoryAverage_Num = 0;
    public long frontMemoryMax = 0;
    public long frontFlowUpload = 0;
    public long frontFlowDownload = 0;
    public long backTime = 0;
    public long backCpuApp = 0;
    public long backCpuTotal = 0;
    public long backCpuMax = 0;
    public long backMemoryAverage_Sum = 0;
    public long backMemoryAverage_Num = 0;
    public long backMemoryMax = 0;
    public long backFlowUpload = 0;
    public long backFlowDownload = 0;
    public ArrayList<DetailPointData> allCPUChartDatas = new ArrayList<>();
    public ArrayList<DetailPointData> allMemoryChartDatas = new ArrayList<>();
    public ArrayList<DetailPointData> allFlowChartDatas = new ArrayList<>();
    //fragment测速：
    public int fragmentNum = 0;
    public int overFragmentNum = 0;
    public ArrayList<DetailListData> allFragmentListData = new ArrayList<>();
    //Activity测速：
    public int pageNum = 0;
    public int overPageNum = 0;
    public ArrayList<DetailListData> allActivityListData = new ArrayList<>();
    //View绘制：
    public int viewDrawNum = 0;
    public int overViewDrawNum = 0;
    public ArrayList<DetailListData> allViewDrawListData = new ArrayList<>();
    //View构建
    public int viewBuildNum = 0;
    public int overViewBuildNum = 0;//总的超时次数
    public ArrayList<DetailListData> allViewBuildListData = new ArrayList<>();
    //SM数据：
    public int nowSM = 0;
    public ArrayList<DetailPointData> allSMChartDatas = new ArrayList<>();
    //卡顿数据：
    public int lowSMNum = 0;
    public int bigBlockNum = 0;
    //IO
    public int dbIONum = 0;
    public int mainThreadDBIONum = 0;
    public ArrayList<DetailListData> allDBIOListData = new ArrayList<>();
    //GC
    public int gcNum = 0;
    public int explicitGCNum = 0;
    public ArrayList<DetailListData> allGCListData = new ArrayList<>();
    //Log
    public int errorLogNum = 0;
}
