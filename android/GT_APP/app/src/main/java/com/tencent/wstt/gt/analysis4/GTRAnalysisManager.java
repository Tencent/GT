package com.tencent.wstt.gt.analysis4;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.analysis4.analysis.AppAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.BlockAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.DeviceAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.FragmentAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.GCAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.IOAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.LogAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.NormalAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.OperationAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.PageLoadAnalysis;
import com.tencent.wstt.gt.analysis4.analysis.ViewBuildAnalysis;

/**
 * Created by p_hongjcong on 2017/8/10.
 */

public class GTRAnalysisManager {
    //APP信息：
    private AppAnalysis appAnalysis;

    //Device信息：
    private DeviceAnalysis deviceAnalysis;

    //Activity生命周期+绘制数据：
    private PageLoadAnalysis pageLoadAnalysis;

    //fragment生命周期：
    private FragmentAnalysis fragmentAnalysis;

    //基础数据：
    private NormalAnalysis normalAnalysis;

    //卡顿数据：
    private BlockAnalysis blockAnalysis;

    //用户操作
    private OperationAnalysis operationAnalysis;

    //View构建
    private ViewBuildAnalysis viewBuildAnalysis;

    //IO
    private IOAnalysis ioAnalysis;

    //GC
    private GCAnalysis gcAnalysis;

    //Logcat
    private LogAnalysis logAnalysis;

    public GTRAnalysisManager(GTRAnalysisResult gtrAnalysisResult) {
        appAnalysis = new AppAnalysis(gtrAnalysisResult);
        deviceAnalysis = new DeviceAnalysis(gtrAnalysisResult);
        normalAnalysis = new NormalAnalysis(gtrAnalysisResult);
        fragmentAnalysis = new FragmentAnalysis(gtrAnalysisResult);
        pageLoadAnalysis = new PageLoadAnalysis(gtrAnalysisResult);
        blockAnalysis = new BlockAnalysis(gtrAnalysisResult);
        operationAnalysis = new OperationAnalysis(gtrAnalysisResult);
        viewBuildAnalysis = new ViewBuildAnalysis(gtrAnalysisResult);
        ioAnalysis = new IOAnalysis(gtrAnalysisResult);
        gcAnalysis = new GCAnalysis(gtrAnalysisResult);
        logAnalysis = new LogAnalysis(gtrAnalysisResult);
    }

    public void analysisData(String data) {
        try {
            distribute(data.split(GTConfig.separator));
        } catch (Exception e) {

        }
    }

    private void distribute(String[] data) throws Exception {
        // 分类处理
        String activityClassName;
        String drawClassName;
        String objectHashCode;
        long time;
        long start;
        long end;
        int drawDeep;
        String drawPath;
        long cpuTotal;
        long cpuApp;
        int memory;
        long flowUpload;
        long flowDownload;
        String stack;
        boolean isOn;
        String operationName;
        String viewType;
        String viewName;
        String action;
        String resourceName;
        String fragmentClassName;
        String fragmentHashCode;
        String activityHashCode;
        boolean isVisibleToUser;
        boolean hidden;
        long startTestTime;
        int dbHashCode;
        String threadName;
        int threadId;
        String sql;
        String path;
        String log;
        int mainThreadId;
        String cpuThreads;
        String gtrThreads;

        switch (data[2]) {
            case "Instrumentation.execStartActivity":
                start = Long.parseLong(data[3]);
                end = Long.parseLong(data[4]);
                normalAnalysis.onFront(start);//前台
                pageLoadAnalysis.onInstrumentation_execStartActivity(start, end);
                break;
            case "Instrumentation.callActivityOnCreate":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                normalAnalysis.onFront(start);//前台
                pageLoadAnalysis.onInstrumentation_callActivityOnCreate(activityClassName, objectHashCode, start, end);
                break;
            case "Instrumentation.callActivityOnStart":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                normalAnalysis.onFront(start);//前台
                pageLoadAnalysis.onInstrumentation_callActivityOnStart(activityClassName, objectHashCode, start, end);
                break;
            case "Instrumentation.callActivityOnResume":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                normalAnalysis.onFront(start);//前台
                pageLoadAnalysis.onInstrumentation_callActivityOnResume(activityClassName, objectHashCode, start, end);
                blockAnalysis.onInstrumentation_callActivityOnResume(activityClassName, objectHashCode, start, end);
                break;
            case "Instrumentation.callActivityOnPause":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                normalAnalysis.onBack(end);//后台
                pageLoadAnalysis.onInstrumentation_callActivityOnPause(activityClassName, objectHashCode, start, end);
                blockAnalysis.onInstrumentation_callActivityOnPause(activityClassName, objectHashCode, start, end);
                break;
            case "Instrumentation.callActivityOnStop":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                pageLoadAnalysis.onInstrumentation_callActivityOnStop(activityClassName, objectHashCode, start, end);
                break;
            case "SQLiteDatabase.beginTransaction":
                dbHashCode = Integer.parseInt(data[3]);
                threadName = data[4];
                threadId = Integer.parseInt(data[5]);
                start = Long.parseLong(data[6]);
                end = Long.parseLong(data[7]);
                ioAnalysis.onSQLiteDatabase_beginTransaction(dbHashCode, threadName, threadId, start, end);
                break;
            case "SQLiteDatabase.endTransaction":
                dbHashCode = Integer.parseInt(data[3]);
                threadName = data[4];
                threadId = Integer.parseInt(data[5]);
                start = Long.parseLong(data[6]);
                end = Long.parseLong(data[7]);
                ioAnalysis.onSQLiteDatabase_endTransaction(dbHashCode, threadName, threadId, start, end);
                break;
            case "SQLiteDatabase.enableWriteAheadLogging":
                dbHashCode = Integer.parseInt(data[3]);
                threadName = data[4];
                threadId = Integer.parseInt(data[5]);
                start = Long.parseLong(data[6]);
                end = Long.parseLong(data[7]);
                ioAnalysis.onSQLiteDatabase_enableWriteAheadLogging(dbHashCode, threadName, threadId, start, end);
                break;
            case "SQLiteDatabase.openDatabase":
                dbHashCode = Integer.parseInt(data[3]);
                path = data[4];
                threadName = data[5];
                threadId = Integer.parseInt(data[6]);
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                ioAnalysis.onSQLiteDatabase_openDatabase(dbHashCode, path, threadName, threadId, start, end);
                break;
            case "SQLiteDatabase.rawQueryWithFactory":
                dbHashCode = Integer.parseInt(data[3]);
                sql = data[4];
                threadName = data[5];
                threadId = Integer.parseInt(data[6]);
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                ioAnalysis.onSQLiteDatabase_rawQueryWithFactory(dbHashCode, sql, threadName, threadId, start, end);
                break;
            case "SQLiteStatement.execute":
                dbHashCode = Integer.parseInt(data[3]);
                sql = data[4];
                threadName = data[5];
                threadId = Integer.parseInt(data[6]);
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                ioAnalysis.onSQLiteStatement_execute(dbHashCode, sql, threadName, threadId, start, end);
                break;
            case "SQLiteStatement.executeInsert":
                dbHashCode = Integer.parseInt(data[3]);
                sql = data[4];
                threadName = data[5];
                threadId = Integer.parseInt(data[6]);
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                ioAnalysis.onSQLiteStatement_executeInsert(dbHashCode, sql, threadName, threadId, start, end);
                break;
            case "SQLiteStatement.executeUpdateDelete":
                dbHashCode = Integer.parseInt(data[3]);
                sql = data[4];
                threadName = data[5];
                threadId = Integer.parseInt(data[6]);
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                ioAnalysis.onSQLiteStatement_executeUpdateDelete(dbHashCode, sql, threadName, threadId, start, end);
                break;
            case "Fragment.onAttach":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_onAttach(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performCreate":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performCreate(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performCreateView":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performCreateView(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performActivityCreated":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performActivityCreated(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performStart":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performStart(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performResume":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performResume(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performPause":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performPause(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performStop":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performStop(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performDestroyView":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performDestroyView(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performDestroy":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performDestroy(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.performDetach":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performDetach(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "Fragment.onHiddenChanged":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                time = Long.parseLong(data[7]);
                hidden = Boolean.parseBoolean(data[8]);
                fragmentAnalysis.onFragment_onHiddenChanged(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, time, hidden);
                break;
            case "Fragment.setUserVisibleHint":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                time = Long.parseLong(data[7]);
                isVisibleToUser = Boolean.parseBoolean(data[8]);
                fragmentAnalysis.onFragment_setUserVisibleHint(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, time, isVisibleToUser);
                break;
            case "FragmentV4.onAttach":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_onAttach(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performCreate":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performCreate(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performCreateView":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performCreateView(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performActivityCreated":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performActivityCreated(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performStart":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performStart(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performResume":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performResume(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performPause":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performPause(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performStop":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performStop(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performDestroyView":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performDestroyView(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performDestroy":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performDestroy(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.performDetach":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                start = Long.parseLong(data[7]);
                end = Long.parseLong(data[8]);
                fragmentAnalysis.onFragment_performDetach(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, start, end);
                break;
            case "FragmentV4.onHiddenChanged":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                time = Long.parseLong(data[7]);
                hidden = Boolean.parseBoolean(data[8]);
                fragmentAnalysis.onFragment_onHiddenChanged(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, time, hidden);
                break;
            case "FragmentV4.setUserVisibleHint":
                activityClassName = data[3];
                activityHashCode = data[4];
                fragmentClassName = data[5];
                fragmentHashCode = data[6];
                time = Long.parseLong(data[7]);
                isVisibleToUser = Boolean.parseBoolean(data[8]);
                fragmentAnalysis.onFragment_setUserVisibleHint(activityClassName, activityHashCode, fragmentClassName,
                        fragmentHashCode, time, isVisibleToUser);
                break;
            case "Activity.onKeyDown":
                operationName = data[3];
                time = Long.parseLong(data[4]);
                operationAnalysis.onActivity_onKeyDown(operationName, time);
                break;
            case "Activity.onKeyUp":
                operationName = data[3];
                time = Long.parseLong(data[4]);
                operationAnalysis.onActivity_onKeyUp(operationName, time);
                break;
            case "View.dispatchTouchEvent":
                viewType = data[3];
                viewName = data[4];
                action = data[5];
                time = Long.parseLong(data[6]);
                operationAnalysis.onView_dispatchTouchEvent(viewType, viewName, action, time);
                break;
            case "LayoutInflater.inflate":
                resourceName = data[3];
                start = Long.parseLong(data[4]);
                end = Long.parseLong(data[5]);
                viewBuildAnalysis.onLayoutInflater_inflate(resourceName, start, end);
                break;
//            case "Activity.setContentView"://此函数的Hook已关闭
//                resourceName = data[3];
//                start = Long.parseLong(data[4]);
//                end = Long.parseLong(data[5]);
//                viewBuildAnalysis.onActivity_setContentView(resourceName, start, end);
//                break;
            case "ViewGroup.dispatchDraw":
                drawClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                drawDeep = Integer.parseInt(data[7]);
                drawPath = data[8];
                pageLoadAnalysis.onViewGroup_dispatchDraw(drawClassName, objectHashCode, start, end, drawDeep, drawPath);
                break;
            case "stackCollect":
                stack = data[3];
                time = Long.parseLong(data[4]);
                blockAnalysis.onCollectStack(time, stack);
                break;
            case "frameCollect":
                time = Long.parseLong(data[3]);
                blockAnalysis.onCollectFrame(time);
                break;
            case "logcatCollect":
                log = data[3];
                time = Long.parseLong(data[4]);
                logAnalysis.onCollectLog(log, time);
                gcAnalysis.onCollectLog(log, time);
                ioAnalysis.onCollectLog(log, time);
                break;
            case "normalCollect":
                time = Long.parseLong(data[3]);
                cpuTotal = Long.parseLong(data[4]);
                cpuApp = Long.parseLong(data[5]);
                cpuThreads = data[6];
                memory = Integer.parseInt(data[7]);
                flowUpload = Long.parseLong(data[8]);
                flowDownload = Long.parseLong(data[9]);
                gtrThreads = data[10];
                normalAnalysis.onCollectNormalInfo(time, cpuTotal, cpuApp, cpuThreads, memory, flowUpload, flowDownload, gtrThreads);
                break;
            case "appCollect":
                String packageName;
                String appName;
                String versionName;
                int versionCode;
                int gtrVersionCode;
                packageName = data[3];
                appName = data[4];
                versionName = data[5];
                versionCode = Integer.parseInt(data[6]);
                gtrVersionCode = Integer.parseInt(data[7]);
                startTestTime = Long.parseLong(data[8]);
                mainThreadId = Integer.parseInt(data[9]);
                appAnalysis.onCollectAppInfo(packageName, appName, versionName, versionCode, gtrVersionCode, startTestTime, mainThreadId);
                break;
            case "deviceCollect":
                String vendor;
                String model;
                String sdkName;
                int sdkInt;
                vendor = data[3];
                model = data[4];
                sdkInt = Integer.parseInt(data[5]);
                sdkName = data[6];
                deviceAnalysis.onCollectDeviceInfo(vendor, model, sdkName, sdkInt);
                break;
            case "screenCollect":
                isOn = Boolean.parseBoolean(data[3]);
                time = Long.parseLong(data[4]);
                blockAnalysis.onCollectScreen(time, isOn);
                break;
            default:
                System.out.println("遗漏数据：" + data[2]);
                break;
        }
    }
}
