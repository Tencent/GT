package com.tencent.wstt.gt.analysis4.obj;

import java.util.ArrayList;

/**
 * Created by p_hongjcong on 2017/8/4.
 */

public class FrontBackInfo {


    public long frontTime = 0;
    public long frontCpuApp = 0;
    public long frontCpuTotal = 0;
    public ArrayList<Long> frontCpuArray = new ArrayList<>();
    public ArrayList<Long>  frontMemoryArray = new ArrayList<>();
    public long frontFlowUpload = 0;
    public long frontFlowDownload = 0;

    public long backTime = 0;
    public long backCpuApp = 0;
    public long backCpuTotal = 0;
    public ArrayList<Long> backCpuArray = new ArrayList<>();
    public ArrayList<Long>  backMemoryArray = new ArrayList<>();
    public long backFlowUpload = 0;
    public long backFlowDownload = 0;

}
