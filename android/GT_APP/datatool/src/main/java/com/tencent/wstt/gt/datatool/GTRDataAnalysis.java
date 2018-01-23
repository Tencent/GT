package com.tencent.wstt.gt.datatool;

public class GTRDataAnalysis {
//
//	PageLoadAnalysis pageLoadAnalysis;
//	NormalAnalysis normalAnalysis;
//	AppInfo appInfo;
//	BlockAnalysis blockAnalysis;
//	OperationAnalysis operationAnalysis;
//	ViewBuildAnalysis viewBuildAnalysis;
//	FragmentAnalysis fragmentAnalysis;
//	IOAnalysis ioAnalysis;
//	LogAnalysis logAnalysis;
//
//	public GTRDataAnalysis(ArrayList<PageLoadInfo> pageLoadInfos, ArrayList<NormalInfo> normalInfos, AppInfo appInfo,
//                           ArrayList<BlockInfo> lowSMBlockInfos, ArrayList<BlockInfo> bigBlockInfos, ArrayList<Long> frames, ArrayList<OperationInfo> operationInfos,
//                           ArrayList<ViewBuildInfo> viewBuildInfos, ArrayList<FragmentInfo> fragmentInfos, ArrayList<DBInfo> dbInfos, ArrayList<FileInfo> fileInfos, ArrayList<LogInfo> logInfos) {
//		pageLoadAnalysis = new PageLoadAnalysis(pageLoadInfos);
//		normalAnalysis = new NormalAnalysis(normalInfos,appInfo);
//		blockAnalysis = new BlockAnalysis(lowSMBlockInfos,bigBlockInfos, frames);
//		operationAnalysis = new OperationAnalysis(operationInfos);
//		viewBuildAnalysis = new ViewBuildAnalysis(viewBuildInfos);
//		fragmentAnalysis = new FragmentAnalysis(fragmentInfos);
//		ioAnalysis = new IOAnalysis(dbInfos, appInfo);
//		logAnalysis = new LogAnalysis(logInfos,fileInfos);
//	}
//
//	public void analysisData(String[] data) {
//		// 分类处理
//		String activityClassName;
//		String drawClassName;
//		String objectHashCode;
//		long time;
//		long start;
//		long end;
//		int drawDeep;
//		String drawPath;
//		long cpuTotal;
//		long cpuApp;
//		int memory;
//		long flowUpload;
//		long flowDownload;
//		String stack;
//		boolean isOn;
//		String operationName;
//		String viewType;
//		String viewName;
//		String action;
//		String resourceName;
//		String fragmentClassName;
//		String fragmentHashCode;
//		String activityHashCode;
//		boolean isVisibleToUser;
//		boolean hidden;
//		long startTestTime;
//		int dbHashCode;
//		String threadName;
//		int threadId;
//		String sql;
//		String path;
//		String log;
//		int mainThreadId;
//
//
//		switch (data[2]) {
//		case "Instrumentation.execStartActivity":
//			start = Long.parseLong(data[3]);
//			end = Long.parseLong(data[4]);
//			pageLoadAnalysis.onInstrumentation_execStartActivity(start, end);
//			break;
//		case "Instrumentation.callActivityOnCreate":
//			activityClassName = data[3];
//			objectHashCode = data[4];
//			start = Long.parseLong(data[5]);
//			end = Long.parseLong(data[6]);
//			pageLoadAnalysis.onInstrumentation_callActivityOnCreate(activityClassName, objectHashCode, start, end);
//			break;
//		case "Instrumentation.callActivityOnStart":
//			activityClassName = data[3];
//			objectHashCode = data[4];
//			start = Long.parseLong(data[5]);
//			end = Long.parseLong(data[6]);
//			pageLoadAnalysis.onInstrumentation_callActivityOnStart(activityClassName, objectHashCode, start, end);
//			break;
//		case "Instrumentation.callActivityOnResume":
//			activityClassName = data[3];
//			objectHashCode = data[4];
//			start = Long.parseLong(data[5]);
//			end = Long.parseLong(data[6]);
//			pageLoadAnalysis.onInstrumentation_callActivityOnResume(activityClassName, objectHashCode, start, end);
//			break;
//		case "Instrumentation.callActivityOnPause":
//			activityClassName = data[3];
//			objectHashCode = data[4];
//			start = Long.parseLong(data[5]);
//			end = Long.parseLong(data[6]);
//			pageLoadAnalysis.onInstrumentation_callActivityOnPause(activityClassName, objectHashCode, start, end);
//			break;
//		case "Instrumentation.callActivityOnStop":
//			activityClassName = data[3];
//			objectHashCode = data[4];
//			start = Long.parseLong(data[5]);
//			end = Long.parseLong(data[6]);
//			pageLoadAnalysis.onInstrumentation_callActivityOnStop(activityClassName, objectHashCode, start, end);
//			break;
//		case "SQLiteDatabase.beginTransaction":
//			dbHashCode = Integer.parseInt(data[3]);
//			threadName = data[4];
//			threadId = Integer.parseInt(data[5]);
//			start = Long.parseLong(data[6]);
//			end = Long.parseLong(data[7]);
//			ioAnalysis.onSQLiteDatabase_beginTransaction(dbHashCode,threadName,threadId,start,end);
//			break;
//		case "SQLiteDatabase.endTransaction":
//			dbHashCode = Integer.parseInt(data[3]);
//			threadName = data[4];
//			threadId = Integer.parseInt(data[5]);
//			start = Long.parseLong(data[6]);
//			end = Long.parseLong(data[7]);
//			ioAnalysis.onSQLiteDatabase_endTransaction(dbHashCode, threadName, threadId, start, end);
//			break;
//		case "SQLiteDatabase.enableWriteAheadLogging":
//			dbHashCode = Integer.parseInt(data[3]);
//			threadName = data[4];
//			threadId = Integer.parseInt(data[5]);
//			start = Long.parseLong(data[6]);
//			end = Long.parseLong(data[7]);
//			ioAnalysis.onSQLiteDatabase_enableWriteAheadLogging(dbHashCode, threadName, threadId, start, end);
//			break;
//		case "SQLiteDatabase.openDatabase":
//			dbHashCode = Integer.parseInt(data[3]);
//			path = data[4];
//			threadName = data[5];
//			threadId = Integer.parseInt(data[6]);
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			ioAnalysis.onSQLiteDatabase_openDatabase(dbHashCode, path, threadName, threadId, start, end);
//			break;
//		case "SQLiteDatabase.rawQueryWithFactory":
//			dbHashCode = Integer.parseInt(data[3]);
//			sql = data[4];
//			threadName = data[5];
//			threadId = Integer.parseInt(data[6]);
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			ioAnalysis.onSQLiteDatabase_rawQueryWithFactory(dbHashCode, sql, threadName, threadId, start, end);
//			break;
//		case "SQLiteStatement.execute":
//			dbHashCode = Integer.parseInt(data[3]);
//			sql = data[4];
//			threadName = data[5];
//			threadId = Integer.parseInt(data[6]);
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			ioAnalysis.onSQLiteStatement_execute(dbHashCode, sql, threadName, threadId, start, end);
//			break;
//		case "SQLiteStatement.executeInsert":
//			dbHashCode = Integer.parseInt(data[3]);
//			sql = data[4];
//			threadName = data[5];
//			threadId = Integer.parseInt(data[6]);
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			ioAnalysis.onSQLiteStatement_executeInsert(dbHashCode, sql, threadName, threadId, start, end);
//			break;
//		case "SQLiteStatement.executeUpdateDelete":
//			dbHashCode = Integer.parseInt(data[3]);
//			sql = data[4];
//			threadName = data[5];
//			threadId = Integer.parseInt(data[6]);
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			ioAnalysis.onSQLiteStatement_executeUpdateDelete(dbHashCode, sql, threadName, threadId, start, end);
//			break;
//		case "Fragment.onAttach":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_onAttach(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performCreate":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performCreate(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performCreateView":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performCreateView(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performActivityCreated":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performActivityCreated(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performStart":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performStart(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performResume":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performResume(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performPause":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performPause(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performStop":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performStop(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performDestroyView":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performDestroyView(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performDestroy":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performDestroy(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.performDetach":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performDetach(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "Fragment.onHiddenChanged":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			time = Long.parseLong(data[7]);
//			hidden = Boolean.parseBoolean(data[8]);
//			fragmentAnalysis.onFragment_onHiddenChanged(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, time, hidden);
//			break;
//		case "Fragment.setUserVisibleHint":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			time = Long.parseLong(data[7]);
//			isVisibleToUser = Boolean.parseBoolean(data[8]);
//			fragmentAnalysis.onFragment_setUserVisibleHint(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, time, isVisibleToUser);
//			break;
//		case "FragmentV4.onAttach":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_onAttach(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performCreate":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performCreate(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performCreateView":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performCreateView(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performActivityCreated":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performActivityCreated(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performStart":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performStart(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performResume":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performResume(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performPause":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performPause(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performStop":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performStop(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performDestroyView":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performDestroyView(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performDestroy":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performDestroy(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.performDetach":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			start = Long.parseLong(data[7]);
//			end = Long.parseLong(data[8]);
//			fragmentAnalysis.onFragment_performDetach(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, start, end);
//			break;
//		case "FragmentV4.onHiddenChanged":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			time = Long.parseLong(data[7]);
//			hidden = Boolean.parseBoolean(data[8]);
//			fragmentAnalysis.onFragment_onHiddenChanged(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, time, hidden);
//			break;
//		case "FragmentV4.setUserVisibleHint":
//			activityClassName = data[3];
//			activityHashCode = data[4];
//			fragmentClassName = data[5];
//			fragmentHashCode = data[6];
//			time = Long.parseLong(data[7]);
//			isVisibleToUser = Boolean.parseBoolean(data[8]);
//			fragmentAnalysis.onFragment_setUserVisibleHint(activityClassName, activityHashCode, fragmentClassName,
//					fragmentHashCode, time, isVisibleToUser);
//			break;
//		case "Activity.onKeyDown":
//			operationName = data[3];
//			time = Long.parseLong(data[4]);
//			operationAnalysis.onActivity_onKeyDown(operationName, time);
//			break;
//		case "Activity.onKeyUp":
//			operationName = data[3];
//			time = Long.parseLong(data[4]);
//			operationAnalysis.onActivity_onKeyUp(operationName, time);
//			break;
//		case "View.dispatchTouchEvent":
//			viewType = data[3];
//			viewName = data[4];
//			action = data[5];
//			time = Long.parseLong(data[6]);
//			operationAnalysis.onView_dispatchTouchEvent(viewType, viewName, action, time);
//			break;
//		case "LayoutInflater.inflate":
//			resourceName = data[3];
//			start = Long.parseLong(data[4]);
//			end = Long.parseLong(data[4]);
//			viewBuildAnalysis.onLayoutInflater_inflate(resourceName, start, end);
//			break;
//		case "Activity.setContentView":
//			resourceName = data[3];
//			start = Long.parseLong(data[4]);
//			end = Long.parseLong(data[4]);
//			viewBuildAnalysis.onActivity_setContentView(resourceName, start, end);
//			break;
//		case "ViewGroup.dispatchDraw":
//			drawClassName = data[3];
//			objectHashCode = data[4];
//			start = Long.parseLong(data[5]);
//			end = Long.parseLong(data[6]);
//			drawDeep = Integer.parseInt(data[7]);
//			drawPath = data[8];
//			pageLoadAnalysis.onViewGroup_dispatchDraw(drawClassName, objectHashCode, start, end, drawDeep, drawPath);
//			break;
//		case "stackCollect":
//			stack = data[3];
//			time = Long.parseLong(data[4]);
//			blockAnalysis.onCollectStack(time, stack);
//			break;
//		case "frameCollect":
//			time = Long.parseLong(data[3]);
//			blockAnalysis.onCollectFrame(time);
//			break;
//		case "logcatCollect":
//			log = data[3];
//			time = Long.parseLong(data[4]);
//			logAnalysis.onCollectLog(log,time);
//			break;
//		case "normalCollect":
//			time = Long.parseLong(data[3]);
//			cpuTotal = Long.parseLong(data[4]);
//			cpuApp = Long.parseLong(data[5]);
//			memory = Integer.parseInt(data[6]);
//			flowUpload = Long.parseLong(data[7]);
//			flowDownload = Long.parseLong(data[8]);
//			normalAnalysis.onCollectNormalInfo(time, cpuTotal, cpuApp, memory, flowUpload, flowDownload);
//			break;
//		case "appCollect":
//			String packageName;
//			String appName;
//			String versionName;
//			int versionCode;
//			int gtrVersionCode;
//			packageName = data[3];
//			appName = data[4];
//			versionName = data[5];
//			versionCode = Integer.parseInt(data[6]);
//			gtrVersionCode = Integer.parseInt(data[7]);
//			startTestTime = Long.parseLong(data[8]);
//			mainThreadId = Integer.parseInt(data[9]);
//			normalAnalysis.onCollectAppInfo(packageName, appName, versionName, versionCode, gtrVersionCode,startTestTime,mainThreadId);
//			break;
//		case "screenCollect":
//			isOn = Boolean.parseBoolean(data[3]);
//			time = Long.parseLong(data[4]);
//			blockAnalysis.onCollectScreen(time, isOn);
//			break;
//		default:
//			System.out.println("遗漏数据：" + data[2]);
//			break;
//		}
//	}

}
