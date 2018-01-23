package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.dao.DetailListData;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.obj.FragmentInfo;
import com.tencent.wstt.gt.analysis4.obj.FragmentLifecycleMethod;

import java.util.ArrayList;

public class FragmentAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public FragmentAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    /**
     * 冷启动：从onAttach函数开始
     * 热启动：从onCreateView或onStart函数开始
     * 1.ViewPager的回退栈：会引起 onCreateView - onDestroyView 的生命周期
     * 2.Activity的回退栈：会引起 onStart - onStop 的生命周期
     */
    private final Object lock = new Object();

    //启动页面的数量，用于赋值给startOrderId
    private int startNumber = 0;

    //onAttach列表：加入列表中
    private ArrayList<FragmentInfo> onAttachList = new ArrayList<>();

    //onCreateView列表：
    private ArrayList<FragmentInfo> onCreateViewList = new ArrayList<>();

    //fragment列表：
    // 1.onStart，加入列表中
    // 2.onStop,从表中移除,写入文件
    private ArrayList<FragmentInfo> fragmentInfoList = new ArrayList<>();

    private final Object stateLock = new Object();

    //Fragment状态列表：
    // 1.onAttach时加入
    // 2.onDetach时移除列表
    private ArrayList<FragmentState> fragmentStateList = new ArrayList<>();

    public void onFragment_onAttach(String activityClassName, String activityHashCode,
                                    String fragmentClassName, String fragmentHashCode,
                                    long start, long end) {
        synchronized (lock) {
            FragmentInfo fragmentInfo = new FragmentInfo(activityClassName, activityHashCode, fragmentClassName, fragmentHashCode);
            fragmentInfo.addAttachInfo(start, end);
            onAttachList.add(fragmentInfo);
        }
    }

    public void onFragment_performCreate(String activityClassName, String activityHashCode,
                                         String fragmentClassName, String fragmentHashCode,
                                         long start, long end) {
        synchronized (lock) {
            for (FragmentInfo fragmentInfo : onAttachList) {
                if (fragmentInfo.activityClassName.equals(activityClassName)
                        && fragmentInfo.activityHashCode.equals(activityHashCode)
                        && fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentInfo.addCreateInfo(start, end);
                    break;
                }
            }
        }
    }

    public void onFragment_performCreateView(String activityClassName, String activityHashCode,
                                             String fragmentClassName, String fragmentHashCode,
                                             long start, long end) {
        synchronized (lock) {
            FragmentInfo fragmentHasAttach = null;
            for (FragmentInfo fragmentInfo : onAttachList) {
                if (fragmentInfo.activityClassName.equals(activityClassName)
                        && fragmentInfo.activityHashCode.equals(activityHashCode)
                        && fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentHasAttach = fragmentInfo;
                    onAttachList.remove(fragmentInfo);
                    break;
                }
            }
            if (fragmentHasAttach == null) {
                fragmentHasAttach = new FragmentInfo(activityClassName, activityHashCode, fragmentClassName, fragmentHashCode);
            }
            fragmentHasAttach.addCreateViewInfo(start, end);
            onCreateViewList.add(fragmentHasAttach);
        }
    }

    public void onFragment_performActivityCreated(String activityClassName, String activityHashCode,
                                                  String fragmentClassName, String fragmentHashCode,
                                                  long start, long end) {
        synchronized (lock) {
            for (FragmentInfo fragmentInfo : onCreateViewList) {
                if (fragmentInfo.activityClassName.equals(activityClassName)
                        && fragmentInfo.activityHashCode.equals(activityHashCode)
                        && fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentInfo.addActivityCreatedInfo(start, end);
                    break;
                }
            }
        }
    }

    public void onFragment_performStart(String activityClassName, String activityHashCode,
                                        String fragmentClassName, String fragmentHashCode,
                                        long start, long end) {
        synchronized (lock) {
            FragmentInfo fragmentHasCreateView = null;
            for (FragmentInfo fragmentInfo : onCreateViewList) {
                if (fragmentInfo.activityClassName.equals(activityClassName)
                        && fragmentInfo.activityHashCode.equals(activityHashCode)
                        && fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentHasCreateView = fragmentInfo;
                    onCreateViewList.remove(fragmentInfo);
                    break;
                }
            }
            if (fragmentHasCreateView == null) {
                fragmentHasCreateView = new FragmentInfo(activityClassName, activityHashCode, fragmentClassName, fragmentHashCode);
            }
            fragmentHasCreateView.addStartInfo(start, end);
            fragmentInfoList.add(fragmentHasCreateView);
        }
    }

    public void onFragment_performResume(String activityClassName, String activityHashCode,
                                         String fragmentClassName, String fragmentHashCode,
                                         long start, long end) {
        synchronized (lock) {
            for (FragmentInfo fragmentInfo : fragmentInfoList) {
                if (fragmentInfo.activityClassName.equals(activityClassName)
                        && fragmentInfo.activityHashCode.equals(activityHashCode)
                        && fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentInfo.addResumeInfo(start, end);
                    startNumber++;
                    fragmentInfo.startOrderId = startNumber;
                    break;
                }
            }
        }
        synchronized (stateLock) {
            FragmentState fragmentToShow = null;
            for (FragmentState fragmentState : fragmentStateList) {
                if (fragmentState.fragmentClassName.equals(fragmentClassName)
                        && fragmentState.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentToShow = fragmentState;
                    break;
                }
            }
            if (fragmentToShow == null) {
                fragmentToShow = new FragmentState(fragmentClassName, fragmentHashCode);
                fragmentStateList.add(fragmentToShow);
            }
            fragmentToShow.hasResumed = true;
            if (fragmentToShow.isShow()) {
                fragmentToShow.showStart = System.currentTimeMillis();
            }
        }
    }

    public void onFragment_performPause(String activityClassName, String activityHashCode,
                                        String fragmentClassName, String fragmentHashCode,
                                        long start, long end) {
        synchronized (lock) {
            for (FragmentInfo fragmentInfo : fragmentInfoList) {
                if (fragmentInfo.activityClassName.equals(activityClassName)
                        && fragmentInfo.activityHashCode.equals(activityHashCode)
                        && fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentInfo.addPauseInfo(start, end);
                    break;
                }
            }
        }
        synchronized (stateLock) {
            FragmentState fragmentToShow = null;
            for (FragmentState fragmentState : fragmentStateList) {
                if (fragmentState.fragmentClassName.equals(fragmentClassName)
                        && fragmentState.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentToShow = fragmentState;
                    break;
                }
            }
            if (fragmentToShow == null) {
                fragmentToShow = new FragmentState(fragmentClassName, fragmentHashCode);
                fragmentStateList.add(fragmentToShow);
            }
            if (fragmentToShow.isShow() && fragmentToShow.showStart != 0) {//匹配fragment的显示时间
                addFragmentVisibleInfo(fragmentClassName, fragmentHashCode, fragmentToShow.showStart, System.currentTimeMillis());
            }
            fragmentToShow.hasResumed = false;
            fragmentToShow.showStart = 0;
        }
    }

    public void onFragment_performStop(String activityClassName, String activityHashCode,
                                       String fragmentClassName, String fragmentHashCode,
                                       long start, long end) {
        synchronized (lock) {
            for (FragmentInfo fragmentInfo : fragmentInfoList) {
                if (fragmentInfo.activityClassName.equals(activityClassName)
                        && fragmentInfo.activityHashCode.equals(activityHashCode)
                        && fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentInfo.addStopInfo(start, end);
                    // 写入文件
                    if (fragmentInfo.startOrderId != 0) {
                        //等于零 表示没有执行onResume函数
                        gtrAnalysisResult.fragmentNum++;
                        //记录超时
                        long loadTime = getFragmentStartFinishTime(fragmentInfo) - getFragmentStartTime(fragmentInfo);
                        DetailListData detailListData;
                        if (loadTime > 300) {
                            gtrAnalysisResult.overFragmentNum++;
                            detailListData = new DetailListData(fragmentInfo.startOrderId + "." + fragmentClassName + "\n启动耗时:" + loadTime + "ms", DetailListData.Error);
                        } else {
                            detailListData = new DetailListData(fragmentInfo.startOrderId + "." + fragmentClassName + "\n启动耗时:" + loadTime + "ms", DetailListData.Normal);
                        }
                        gtrAnalysisResult.allFragmentListData.add(detailListData);
                    }
                    //移除队列
                    fragmentInfoList.remove(fragmentInfo);
                    break;
                }
            }
            //call回调通知数据刷新
            GTRAnalysis.refreshFragmentInfo();
        }
    }

    public void onFragment_performDestroyView(String activityClassName, String activityHashCode,
                                              String fragmentClassName, String fragmentHashCode,
                                              long start, long end) {
        //do nothing
    }

    public void onFragment_performDestroy(String activityClassName, String activityHashCode,
                                          String fragmentClassName, String fragmentHashCode,
                                          long start, long end) {
        //do nothing
    }

    public void onFragment_performDetach(String activityClassName, String activityHashCode,
                                         String fragmentClassName, String fragmentHashCode,
                                         long start, long end) {
        //do nothing
        synchronized (stateLock) {
            for (FragmentState fragmentState : fragmentStateList) {
                if (fragmentState.fragmentClassName.equals(fragmentClassName)
                        && fragmentState.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentStateList.remove(fragmentState);
                    break;
                }
            }
        }
    }

    public void onFragment_onHiddenChanged(String activityClassName, String activityHashCode,
                                           String fragmentClassName, String fragmentHashCode,
                                           long time, boolean hidden) {
        synchronized (stateLock) {
            FragmentState fragmentToShow = null;
            for (FragmentState fragmentState : fragmentStateList) {
                if (fragmentState.fragmentClassName.equals(fragmentClassName)
                        && fragmentState.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentToShow = fragmentState;
                    break;
                }
            }
            if (fragmentToShow == null) {
                fragmentToShow = new FragmentState(fragmentClassName, fragmentHashCode);
                fragmentStateList.add(fragmentToShow);
            }
            if (!hidden) {
                fragmentToShow.hasShow = true;
                if (fragmentToShow.isShow()) {
                    fragmentToShow.showStart = System.currentTimeMillis();
                }
            } else {
                if (fragmentToShow.isShow() && fragmentToShow.showStart != 0) {//匹配fragment的显示时间
                    addFragmentVisibleInfo(fragmentClassName, fragmentHashCode, fragmentToShow.showStart, System.currentTimeMillis());
                }
                fragmentToShow.hasShow = false;
                fragmentToShow.showStart = 0;
            }
        }
    }

    public void onFragment_setUserVisibleHint(String activityClassName, String activityHashCode,
                                              String fragmentClassName, String fragmentHashCode,
                                              long time, boolean isVisibleToUser) {
        synchronized (stateLock) {
            FragmentState fragmentToShow = null;
            for (FragmentState fragmentState : fragmentStateList) {
                if (fragmentState.fragmentClassName.equals(fragmentClassName)
                        && fragmentState.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentToShow = fragmentState;
                    break;
                }
            }
            if (fragmentToShow == null) {
                fragmentToShow = new FragmentState(fragmentClassName, fragmentHashCode);
                fragmentStateList.add(fragmentToShow);
            }
            if (isVisibleToUser) {
                fragmentToShow.hasVisible = true;
                if (fragmentToShow.isShow()) {
                    fragmentToShow.showStart = System.currentTimeMillis();
                }
            } else {
                if (fragmentToShow.isShow() && fragmentToShow.showStart != 0) {//匹配fragment的显示时间
                    addFragmentVisibleInfo(fragmentClassName, fragmentHashCode, fragmentToShow.showStart, System.currentTimeMillis());
                }
                fragmentToShow.hasVisible = false;
                fragmentToShow.showStart = 0;
            }
        }
    }

    private void addFragmentVisibleInfo(String fragmentClassName, String fragmentHashCode, long showStart, long shouEnd) {
        synchronized (lock) {
            for (FragmentInfo fragmentInfo : fragmentInfoList) {
                if (fragmentInfo.fragmentClassName.equals(fragmentClassName)
                        && fragmentInfo.fragmentHashCode.equals(fragmentHashCode)) {
                    fragmentInfo.addVisibleInfo(showStart, shouEnd);
                    break;
                }
            }
        }
    }

    private static class FragmentState {
        public String fragmentClassName = "";
        public String fragmentHashCode = "";

        public boolean hasResumed = false; //此值由Fragment.onResume 和 Fragment.onPause 函数决定。
        public boolean hasShow = true;//此值由onHiddenChanged函数决定
        public boolean hasVisible = true; //此值由setUserVisibleHint函数决定

        public long showStart = 0;

        public FragmentState(String fragmentClassName, String fragmentHashCode) {
            this.fragmentClassName = fragmentClassName;
            this.fragmentHashCode = fragmentHashCode;
        }

        public String getState() {
            if (hasResumed && hasShow && hasVisible) {
                return fragmentClassName + "," + fragmentHashCode + "," + "显示";
            } else {
                return fragmentClassName + "," + fragmentHashCode + "," + "隐藏";
            }
        }

        public boolean isShow() {
            if (hasResumed && hasShow && hasVisible) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static long getFragmentStartTime(FragmentInfo fragmentInfo) {
        long start = 0;
        for (FragmentLifecycleMethod lifecycleMethod : fragmentInfo.fragmentLifecycleMethodList) {
            if (start == 0 || lifecycleMethod.methodStartTime < start) {
                start = lifecycleMethod.methodStartTime;
            }
        }
        return start;
    }

    private static long getFragmentStartFinishTime(FragmentInfo fragmentInfo) {
        long startFinish = 0;
        for (FragmentLifecycleMethod lifecycleMethod : fragmentInfo.fragmentLifecycleMethodList) {
            if (startFinish == 0 || lifecycleMethod.methodName.equals(FragmentLifecycleMethod.ONRESUME)) {
                startFinish = lifecycleMethod.methodEndTime;
                return startFinish;
            }
        }
        return startFinish;
    }
}
