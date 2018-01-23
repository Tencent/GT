package com.tencent.wstt.gt.datatool;

import com.tencent.wstt.gt.datatool.analysis.BlockAnalysis;
import com.tencent.wstt.gt.datatool.analysis.GCAnalysis;
import com.tencent.wstt.gt.datatool.analysis.IOAnalysis;
import com.tencent.wstt.gt.datatool.analysis.LogAnalysis;
import com.tencent.wstt.gt.datatool.analysis.OperationAnalysis;
import com.tencent.wstt.gt.datatool.analysis.ScreenAnalysis;
import com.tencent.wstt.gt.datatool.obj.AppInfo;
import com.tencent.wstt.gt.datatool.obj.BlockInfo;
import com.tencent.wstt.gt.datatool.obj.DBActionInfo;
import com.tencent.wstt.gt.datatool.obj.DeviceInfo;
import com.tencent.wstt.gt.datatool.obj.DrawInfo;
import com.tencent.wstt.gt.datatool.obj.LogInfo;
import com.tencent.wstt.gt.datatool.obj.LowSMInfo;
import com.tencent.wstt.gt.datatool.obj.PageLoadState;
import com.tencent.wstt.gt.datatool.obj.ScreenState;
import com.tencent.wstt.gt.datatool.analysis.AppAnalysis;
import com.tencent.wstt.gt.datatool.analysis.DeviceAnalysis;
import com.tencent.wstt.gt.datatool.analysis.FragmentAnalysis;
import com.tencent.wstt.gt.datatool.analysis.NormalAnalysis;
import com.tencent.wstt.gt.datatool.analysis.PageLoadAnalysis;
import com.tencent.wstt.gt.datatool.analysis.ViewBuildAnalysis;
import com.tencent.wstt.gt.datatool.obj.DiskIOInfo;
import com.tencent.wstt.gt.datatool.obj.FileActionInfo;
import com.tencent.wstt.gt.datatool.obj.FragmentInfo;
import com.tencent.wstt.gt.datatool.obj.FrontBackInfo;
import com.tencent.wstt.gt.datatool.obj.FrontBackState;
import com.tencent.wstt.gt.datatool.obj.GCInfo;
import com.tencent.wstt.gt.datatool.obj.NormalInfo;
import com.tencent.wstt.gt.datatool.obj.OperationInfo;
import com.tencent.wstt.gt.datatool.obj.PageLoadInfo;
import com.tencent.wstt.gt.datatool.obj.ViewBuildInfo;

import java.util.ArrayList;

/**
 * Created by p_hongjcong on 2017/7/31.
 * 将数据路径传入，此解析器将数据装载为数据对象
 */

public class GTRAnalysis {
    public static final String separatorFile = "_&&GTRFile&_";

    //APP信息：
    AppInfo appInfo = new AppInfo();//APP信息、进程信息、线程信息、SDK版本信息
    AppAnalysis appAnalysis = new AppAnalysis(this, appInfo);
    //Device信息：
    DeviceInfo deviceInfo = new DeviceInfo();
    DeviceAnalysis deviceAnalysis = new DeviceAnalysis(this, deviceInfo);
    //屏幕亮灭信息：
    ArrayList<ScreenState> screenStates = new ArrayList<>();//屏幕亮灭数据
    ScreenAnalysis screenAnalysis = new ScreenAnalysis(this, screenStates);
    //Activity生命周期+绘制数据：
    ArrayList<FrontBackState> frontBackStates = new ArrayList<>();//应用前后台数据(用于计算后台CPU损耗)
    ArrayList<PageLoadState> pageLoadStates = new ArrayList<>();//页面切换的时间区域(用于筛选卡顿信息)
    ArrayList<PageLoadInfo> pageLoadInfos = new ArrayList<>();
    ArrayList<Integer> overActivityInfos = new ArrayList<>();
    ArrayList<DrawInfo> overViewDraws = new ArrayList<>();
    PageLoadAnalysis pageLoadAnalysis = new PageLoadAnalysis(this, pageLoadInfos, overActivityInfos, overViewDraws, pageLoadStates, frontBackStates);
    //fragment生命周期：
    ArrayList<FragmentInfo> fragmentInfos = new ArrayList<>();
    ArrayList<Integer> overFragments = new ArrayList<>();
    FragmentAnalysis fragmentAnalysis = new FragmentAnalysis(this, fragmentInfos, overFragments);
    //基础数据：
    ArrayList<NormalInfo> normalInfos = new ArrayList<>();
    FrontBackInfo frontBackInfo = new FrontBackInfo();
    ArrayList<Integer> gtrThreadInfos = new ArrayList<>();
    NormalAnalysis normalAnalysis = new NormalAnalysis(this, normalInfos, frontBackInfo, gtrThreadInfos);
    //卡顿数据：
    ArrayList<Long> frames = new ArrayList<>();
    ArrayList<BlockInfo> allBlockInfos = new ArrayList<>();//所有有效卡顿信息（去掉黑屏时的卡顿数据）
    ArrayList<LowSMInfo> lowSMInfos = new ArrayList<>();
    ArrayList<Integer> bigBlockIDs = new ArrayList<>();
    BlockAnalysis blockAnalysis = new BlockAnalysis(this, allBlockInfos, bigBlockIDs, lowSMInfos, frames);
    //用户操作
    ArrayList<OperationInfo> operationInfos = new ArrayList<>();
    OperationAnalysis operationAnalysis = new OperationAnalysis(this, operationInfos);
    //View构建
    ArrayList<ViewBuildInfo> viewBuildInfos = new ArrayList<>();
    ArrayList<Integer> overViewBuilds = new ArrayList<>();
    ViewBuildAnalysis viewBuildAnalysis = new ViewBuildAnalysis(this, viewBuildInfos, overViewBuilds);
    //IO
    ArrayList<DiskIOInfo> diskIOInfos = new ArrayList<>();
    ArrayList<FileActionInfo> fileActionInfos = new ArrayList<>();
    ArrayList<Integer> fileActionInfosInMainThread = new ArrayList<>();
    ArrayList<DBActionInfo> dbActionInfos = new ArrayList<>();
    ArrayList<Integer> dbActionInfosInMainThread = new ArrayList<>();
    IOAnalysis ioAnalysis = new IOAnalysis(this, diskIOInfos, fileActionInfos, fileActionInfosInMainThread, dbActionInfos, dbActionInfosInMainThread);
    //GC
    ArrayList<GCInfo> allGCInfos = new ArrayList<>();
    ArrayList<Integer> explicitGCs = new ArrayList<>();
    GCAnalysis gcAnalysis = new GCAnalysis(this, allGCInfos, explicitGCs);
    //Logcat
    ArrayList<LogInfo> logInfos = new ArrayList<>();
    LogAnalysis logAnalysis = new LogAnalysis(this, logInfos, ioAnalysis, gcAnalysis);

    // flags
    ArrayList<Long> flagList = new ArrayList<>();

    public void clear() {
        //基础数据：
        normalInfos.clear();
        frontBackInfo.clear();
        gtrThreadInfos.clear();
        //卡顿数据：
        frames.clear();
        lowSMInfos.clear();
        allBlockInfos.clear();
        bigBlockIDs.clear();
        //用户操作
        operationInfos.clear();
        //Activity生命周期+绘制数据：
        pageLoadInfos.clear();
        overActivityInfos.clear();
        overViewDraws.clear();
        //fragment生命周期：
        fragmentInfos.clear();
        overFragments.clear();
        //View构建
        viewBuildInfos.clear();
        overViewBuilds.clear();
        //IO
        diskIOInfos.clear();
        fileActionInfos.clear();
        fileActionInfosInMainThread.clear();
        dbActionInfos.clear();
        dbActionInfosInMainThread.clear();
        //GC
        allGCInfos.clear();
        explicitGCs.clear();
        //logcat
        logInfos.clear();
    }


    /**
     * 根据数据形式将数据分发给各模块的解析器
     *
     * @param data
     */
    public void distribute(String[] data) throws Exception {
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
                pageLoadAnalysis.onInstrumentation_execStartActivity(start, end);
                break;
            case "Instrumentation.callActivityOnCreate":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                pageLoadAnalysis.onInstrumentation_callActivityOnCreate(activityClassName, objectHashCode, start, end);
                break;
            case "Instrumentation.callActivityOnStart":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                pageLoadAnalysis.onInstrumentation_callActivityOnStart(activityClassName, objectHashCode, start, end);
                break;
            case "Instrumentation.callActivityOnResume":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                pageLoadAnalysis.onInstrumentation_callActivityOnResume(activityClassName, objectHashCode, start, end);
                break;
            case "Instrumentation.callActivityOnPause":
                activityClassName = data[3];
                objectHashCode = data[4];
                start = Long.parseLong(data[5]);
                end = Long.parseLong(data[6]);
                pageLoadAnalysis.onInstrumentation_callActivityOnPause(activityClassName, objectHashCode, start, end);
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
                screenAnalysis.onCollectScreen(time, isOn);
                break;
            case "GTRFlagRecord":
                time = Long.parseLong(data[3]);
                flagList.add(time);
                break;
            default:
                System.out.println("遗漏数据：" + data[2]);
                break;
        }
    }


    public AppInfo getAppInfo() {
        return appInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public ArrayList<ScreenState> getScreenStates() {
        return screenStates;
    }

    public ArrayList<FrontBackState> getFrontBackStates() {
        return frontBackStates;
    }

    public ArrayList<PageLoadState> getPageLoadStates() {
        return pageLoadStates;
    }

    public ArrayList<PageLoadInfo> getPageLoadInfos() {
        return pageLoadInfos;
    }


    public ArrayList<Integer> getOverActivityInfos() {
        return overActivityInfos;
    }

    public ArrayList<DrawInfo> getOverViewDraws() {
        return overViewDraws;
    }

    public ArrayList<FragmentInfo> getFragmentInfos() {
        return fragmentInfos;
    }

    public ArrayList<Integer> getOverFragments() {
        return overFragments;
    }


    public ArrayList<NormalInfo> getNormalInfos() {
        return normalInfos;
    }

    public ArrayList<Integer> getGtrThreadInfos() {
        return gtrThreadInfos;
    }

    public FrontBackInfo getFrontBackInfo() {
        return frontBackInfo;
    }

    public ArrayList<Long> getFrames() {
        return frames;
    }

    public ArrayList<BlockInfo> getAllBlockInfos() {
        return allBlockInfos;
    }

    public ArrayList<LowSMInfo> getLowSMInfos() {
        return lowSMInfos;
    }

    public void setLowSMInfos(ArrayList<LowSMInfo> lowSMInfos) {
        this.lowSMInfos = lowSMInfos;
    }

    public ArrayList<Integer> getBigBlockIDs() {
        return bigBlockIDs;
    }

    public ArrayList<OperationInfo> getOperationInfos() {
        return operationInfos;
    }

    public ArrayList<ViewBuildInfo> getViewBuildInfos() {
        return viewBuildInfos;
    }

    public ArrayList<Integer> getOverViewBuilds() {
        return overViewBuilds;
    }

    public ArrayList<DBActionInfo> getDbActionInfos() {
        return dbActionInfos;
    }

    public ArrayList<Integer> getDbActionInfosInMainThread() {
        return dbActionInfosInMainThread;
    }

    public ArrayList<DiskIOInfo> getDiskIOInfos() {
        return diskIOInfos;
    }

    public ArrayList<FileActionInfo> getFileActionInfos() {
        return fileActionInfos;
    }

    public ArrayList<Integer> getFileActionInfosInMainThread() {
        return fileActionInfosInMainThread;
    }

    public ArrayList<GCInfo> getAllGCInfos() {
        return allGCInfos;
    }

    public ArrayList<Integer> getExplicitGCs() {
        return explicitGCs;
    }

    public ArrayList<LogInfo> getLogInfos() {
        return logInfos;
    }

    public ArrayList<Long> getFlagList() {
        return flagList;
    }
}
