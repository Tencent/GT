package com.tencent.wstt.gt.datatool.obj;

import java.util.ArrayList;

/**
 * Created by p_hongjcong on 2017/5/3.
 */

public class FragmentInfo {

    public String activityClassName;
    public String activityHashCode;
    public String fragmentClassName;
    public String fragmentHashCode;
    public int startOrderId = 0;


    //生命周期信息：
    public ArrayList<FragmentLifecycleMethod> fragmentLifecycleMethodList = new ArrayList<>();
    public ArrayList<FragmentVisible> fragmentVisibleList = new ArrayList<>();

    public FragmentInfo() {
    }

    public FragmentInfo(String activityClassName, String activityHashCode, String fragmentClassName, String fragmentHashCode) {
        this.activityClassName = activityClassName;
        this.activityHashCode = activityHashCode;
        this.fragmentClassName = fragmentClassName;
        this.fragmentHashCode = fragmentHashCode;
    }


    public void addAttachInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONATTACH;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addCreateInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONCREATE;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addCreateViewInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONCREATEVIEW;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addActivityCreatedInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONACTIVITYCREATED;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addStartInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONSTART;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addResumeInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONRESUME;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addPauseInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONPAUSE;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addStopInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONSTOP;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addDestroyViewInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONDESTROYVIEW;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addDestroyInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONDESTROY;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addDetachInfo(long start, long end) {
        FragmentLifecycleMethod fragmentLifecycleMethod = new FragmentLifecycleMethod();
        fragmentLifecycleMethod.methodName = FragmentLifecycleMethod.ONDETACH;
        fragmentLifecycleMethod.methodStartTime = start;
        fragmentLifecycleMethod.methodEndTime = end;
        fragmentLifecycleMethodList.add(fragmentLifecycleMethod);
    }

    public void addVisibleInfo(long showStart, long shouEnd) {

        FragmentVisible fragmentVisible = new FragmentVisible();
        fragmentVisible.begin = showStart;
        fragmentVisible.end = shouEnd;
        fragmentVisibleList.add(fragmentVisible);


    }


}
