package com.tencent.wstt.gt.analysis4.analysis;

import android.os.Handler;
import android.os.HandlerThread;

import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.dao.DetailPointData;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.obj.FrontBackState;
import com.tencent.wstt.gt.analysis4.obj.ScreenState;
import com.tencent.wstt.gt.analysis4.obj.StackInfo;

import java.util.ArrayList;

public class BlockAnalysis {
	private Handler handler = null;

	//标记此时分析器对应的测试
	private String thisPackageName;

    //标记此时分析器对应的测试
	private long thisStartTime;

	private Runnable getSMChartDataRunnable = new Runnable() {
		@Override
		public void run() {
			if (thisPackageName == null) {
				thisPackageName = GTRAnalysis.packageName;
				thisStartTime = GTRAnalysis.startTestTime;
			}

			long time = System.currentTimeMillis();
			if (!isFront(time, time + 1)) {
				gtrAnalysisResult.nowSM = 0;
			}
			gtrAnalysisResult.allSMChartDatas.add(new DetailPointData((time - GTRAnalysis.startTestTime) / 1000, gtrAnalysisResult.nowSM));
			//call回调通知数据刷新
			GTRAnalysis.refreshSMInfo();

			if (thisPackageName != null && thisPackageName.equals(GTRAnalysis.packageName) && thisStartTime == GTRAnalysis.startTestTime) {
				handler.postDelayed(getSMChartDataRunnable, 1000);
			}
		}
	};

	GTRAnalysisResult gtrAnalysisResult = null;

	public BlockAnalysis(GTRAnalysisResult gtrAnalysisResult) {
		this.gtrAnalysisResult = gtrAnalysisResult;

        HandlerThread handlerThread = new HandlerThread("BlockAnalysisHandlerThread");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
		handler.postDelayed(getSMChartDataRunnable, 1000);
	}

	private ArrayList<FrontBackState> frontBackStates = new ArrayList<>();//前后台数据

	public void onInstrumentation_callActivityOnResume(String activityClassName, String objectHashCode, long start, long end) {
		// 如果有缓存后台数据，先处理，无论是否有效都制空
		if (pauseTime != -1 && start - pauseTime > 1500) {
			FrontBackState frontBackState = new FrontBackState(pauseTime, false);
			frontBackStates.add(frontBackState);
		}
		pauseTime = -1;
		//添加前台数据
		FrontBackState frontBackState = new FrontBackState(start, true);
		frontBackStates.add(frontBackState);
	}

	private long pauseTime = -1;

	public void onInstrumentation_callActivityOnPause(String activityClassName, String objectHashCode, long start, long end) {
		pauseTime = end;
	}

	private boolean isFront(long startTime, long endTime) {
		//如果有缓存后台数据，先处理，如果有效则制空
		if (pauseTime != -1 && startTime - pauseTime > 1500) {
			FrontBackState frontBackState = new FrontBackState(pauseTime, false);
			frontBackStates.add(frontBackState);
			pauseTime = -1;
		}
		boolean isFront = true;
		for (FrontBackState frontBackState : frontBackStates) {
			if (frontBackState.time < startTime) {
				isFront = frontBackState.isFront;
			}
			if (frontBackState.time > startTime && frontBackState.time < endTime) {
				isFront = true;
				break;
			}
		}
		return isFront;
	}

    // 屏幕亮灭数据
	private ArrayList<ScreenState> screenStates = new ArrayList<>();

	public void onCollectScreen(long time, boolean isOn) {
		ScreenState screenInfo = new ScreenState();
		screenInfo.time = time;
		screenInfo.isOn = isOn;
		screenStates.add(screenInfo);
	}

	private boolean isScreenOn(long startTime, long endTime) {
		// 根据ScreenCollect判断是否有效
		boolean isScreenOn = true;
		for (ScreenState screenInfo : screenStates) {
			if (screenInfo.time < startTime) {
				isScreenOn = screenInfo.isOn;
			}
			if (screenInfo.time > startTime && screenInfo.time < endTime) {
				isScreenOn = true;
				break;
			}
		}
		return isScreenOn;
	}

    // 最近的Stack信息
	private ArrayList<StackInfo> stackInfos = new ArrayList<>();

	public void onCollectStack(long time, String stack) {
		StackInfo stackInfo = new StackInfo();
		stackInfo.time = time;
		stackInfo.stack = stack.replace("&&rn&&", "\r\n");
		stackInfos.add(stackInfo);
		while (stackInfos.size() > 300) {
			stackInfos.remove(0);
		}
	}

    // 最近一秒内所有frame的时间
	private ArrayList<Long> framesInOneSecond = new ArrayList<>();
    private boolean isInLowSM = false;
    private long lastFrameTime = 0;
    private long lowSMStartTime;

	public void onCollectFrame(long frameTime) {
		framesInOneSecond.add(frameTime);
		for (int i = 0; i < framesInOneSecond.size(); i++) {
			long tempFrameTime = framesInOneSecond.get(i);
			if (tempFrameTime < frameTime - 1000) {
				framesInOneSecond.remove(i);
				i--;
			}
		}
		gtrAnalysisResult.nowSM = framesInOneSecond.size();

		//call回调通知数据刷新
		GTRAnalysis.refreshSMInfo();

		//检测大卡顿：
		if (lastFrameTime != 0) {
			if (frameTime - lastFrameTime > 70 &&
                    isScreenOn(lastFrameTime, frameTime) &&
                    isFront(lastFrameTime, frameTime)) {
				gtrAnalysisResult.bigBlockNum++;
				//call回调通知数据刷新
				GTRAnalysis.refreshBlockInfo();
			}
		}

		lastFrameTime = frameTime;

		//检测低流畅值区间：
		if (framesInOneSecond.size() < 40) {
			if (!isInLowSM) {
				lowSMStartTime = frameTime - 1000;
				isInLowSM = true;
			}
		} else {
			if (isInLowSM) {
				if (isScreenOn(lowSMStartTime, frameTime) && isFront(lowSMStartTime, frameTime)) {
					gtrAnalysisResult.lowSMNum++;
					//call回调通知数据刷新
					GTRAnalysis.refreshBlockInfo();
				}
				isInLowSM = false;
			}
		}
	}
}
