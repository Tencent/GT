package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.BlockInfo;
import com.tencent.wstt.gt.datatool.obj.FrontBackState;
import com.tencent.wstt.gt.datatool.obj.LowSMInfo;
import com.tencent.wstt.gt.datatool.obj.ScreenState;
import com.tencent.wstt.gt.datatool.obj.StackInfo;

import java.util.ArrayList;

public class BlockAnalysis {


    GTRAnalysis gtrAnalysis;
    ArrayList<BlockInfo> allBlockInfos;
    ArrayList<Integer> bigBlockInfos;
    ArrayList<LowSMInfo> lowSMInfos;
    ArrayList<Long> frames;

    public BlockAnalysis(GTRAnalysis gtrAnalysis, ArrayList<BlockInfo> allBlockInfos, ArrayList<Integer> bigBlockInfos, ArrayList<LowSMInfo> lowSMInfos, ArrayList<Long> frames) {
        this.gtrAnalysis = gtrAnalysis;
        this.allBlockInfos = allBlockInfos;
        this.bigBlockInfos = bigBlockInfos;
        this.lowSMInfos = lowSMInfos;
        this.frames = frames;
    }


    ArrayList<StackInfo> stackInfos = new ArrayList<>();//最近的Stack信息

    public void onCollectStack(long time, String stack) {
        StackInfo stackInfo = new StackInfo();
        stackInfo.time = time;
        stackInfo.stack = stack.replace("&&rn&&", "\r\n");
        stackInfos.add(stackInfo);
        while (stackInfos.size() > 300) {
            stackInfos.remove(0);
        }
    }


    int wori = 0;

    long lastFrameTime = 0;
    ArrayList<Long> framesInOneSecond = new ArrayList<>();//最近一秒内所有frame的时间
    boolean isInLowSM = false;
    long lowSMStartTime;

    public void onCollectFrame(long frameTime) {

        frames.add(frameTime);
        framesInOneSecond.add(frameTime);
        for (int i = 0; i < framesInOneSecond.size(); i++) {
            long tempFrameTime = framesInOneSecond.get(i);
            if (tempFrameTime < frameTime - 1000) {
                framesInOneSecond.remove(i);
                i--;
            }
        }

        if (lastFrameTime != 0 && isScreenOn(lastFrameTime, frameTime) && isFront(lastFrameTime, frameTime)) {
            BlockInfo thisBlockInfo = getBlockInfo(lastFrameTime, frameTime);
            if (thisBlockInfo.stackInfoList.size() > 0) {
                //所有卡顿：
                allBlockInfos.add(getBlockInfo(lastFrameTime, frameTime));
                //检测大卡顿：
                if (frameTime - lastFrameTime > 70) {
                    bigBlockInfos.add(allBlockInfos.size() - 1);
                }
            }
        }
        lastFrameTime = frameTime;


        //检测低流畅值区间:
        if (framesInOneSecond.size() < 40) {
            if (!isInLowSM) {
                lowSMStartTime = frameTime - 1000;
                isInLowSM = true;
            }
        } else {
            if (isInLowSM) {
                if (isScreenOn(lowSMStartTime, frameTime) && isFront(lowSMStartTime, frameTime)) {
                    lowSMInfos.add(getLowSMInfo(lowSMStartTime, frameTime));
                }
                isInLowSM = false;
            }
        }
    }

    private boolean isFront(long startTime, long endTime) {
        boolean isFront = true;
        for (FrontBackState frontBackState : gtrAnalysis.getFrontBackStates()) {
            if (frontBackState.time < startTime) {
                isFront = frontBackState.isFront;
            }
            if (frontBackState.time > startTime && frontBackState.time < endTime) {
                isFront = false;
                break;
            }
        }
        return isFront;
    }

    private boolean isScreenOn(long startTime, long endTime) {
        //根据ScreenCollect判断是否有效
        boolean isScreenOn = true;
        for (ScreenState screenInfo : gtrAnalysis.getScreenStates()) {
            if (screenInfo.time < startTime) {
                isScreenOn = screenInfo.isOn;
            }
            if (screenInfo.time > startTime && screenInfo.time < endTime) {
                isScreenOn = false;
                break;
            }
        }
        return isScreenOn;
    }

    private BlockInfo getBlockInfo(long startTime, long endTime) {
        BlockInfo bigBlockInfo = new BlockInfo();
        bigBlockInfo.startTime = startTime;
        bigBlockInfo.endTime = endTime;
        for (StackInfo stackInfo : stackInfos) {
            if (stackInfo.time > startTime && stackInfo.time < endTime) {
                bigBlockInfo.stackInfoList.add(stackInfo);
            }
        }
        return bigBlockInfo;
    }

    private LowSMInfo getLowSMInfo(long startTime, long endTime) {

        LowSMInfo lowSMInfo = new LowSMInfo();
        lowSMInfo.startTime = startTime;
        lowSMInfo.endTime = endTime;
        return lowSMInfo;

    }


}
