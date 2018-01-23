package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.obj.ScreenState;
import com.tencent.wstt.gt.datatool.GTRAnalysis;

import java.util.ArrayList;

/**
 * Created by p_hongjcong on 2017/7/31.
 */

public class ScreenAnalysis {


    ArrayList<ScreenState> screenStates = new ArrayList<>();

    public ScreenAnalysis(GTRAnalysis gtrAnalysis, ArrayList<ScreenState> screenStates) {
        this.screenStates = screenStates;
    }

    public void onCollectScreen(long time, boolean isOn) {
        ScreenState screenInfo = new ScreenState();
        screenInfo.time = time;
        screenInfo.isOn = isOn;
        screenStates.add(screenInfo);
    }
}
