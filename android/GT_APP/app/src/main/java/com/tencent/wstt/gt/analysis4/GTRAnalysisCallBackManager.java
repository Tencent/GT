package com.tencent.wstt.gt.analysis4;

import java.util.ArrayList;

/**
 * Created by p_hongjcong on 2017/8/14.
 */

public class GTRAnalysisCallBackManager {
    private static final String TAG = "CallBackManager";

    /**
     * Callback列表，回调提示最新数据
     */
    private static ArrayList<GTRAnalysisCallback> callBacks = new ArrayList<>();

    public static void addCallBack(GTRAnalysisCallback callBack) {
        synchronized (callBacks) {
            callBacks.remove(callBack);
            callBacks.add(callBack);
        }
    }

    public static void removeCallBack(GTRAnalysisCallback callBack) {
        synchronized (callBacks) {
            callBacks.remove(callBack);
        }
    }

    public static void removeAllCallBack() {
        synchronized (callBacks) {
            callBacks.clear();
        }
    }

    /**
     * 调用回调函数：
     */
    public static void refreshPid() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshPid(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshAppInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshAppInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshDeviceInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshDeviceInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshNormalInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshNormalInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshFragmentInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshFragmentInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshPageLoadInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshPageLoadInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshViewDrawInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshViewDrawInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshViewBuildInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshViewBuildInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshSMInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshSMInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshBlockInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshBlockInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshIOInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshIOInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshGCInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshGCInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }

    public static void refreshLogInfo() {
        synchronized (callBacks) {
            for (int i = 0; i < callBacks.size(); i++) {
                callBacks.get(i).refreshLogInfo(GTRAnalysis.getGtrAnalysisResult());
            }
        }
    }
}
