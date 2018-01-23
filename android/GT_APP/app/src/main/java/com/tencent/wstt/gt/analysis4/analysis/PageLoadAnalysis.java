package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.analysis4.obj.LifecycleMethod;
import com.tencent.wstt.gt.dao.DetailListData;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.obj.DrawInfo;
import com.tencent.wstt.gt.analysis4.obj.PageLoadInfo;

import java.util.ArrayList;

public class PageLoadAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public PageLoadAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    private final Object lock = new Object();

    //启动页面的数量，用于赋值给startOrderId
    private int startNumber = 0;

    //execStart启动信息：当执行execStartActivity时，执行时间信息将保存在此
    //启动方法时间
    private long execStart_start = 0;

    //启动方法时间
    private long execStart_end = 0;

    //onCreate列表：一旦页面执行了onCreate，页面信息将保存在此列表中（并保存execStart信息）
    private ArrayList<PageLoadInfo> onCreateList = new ArrayList<>();

    //onStart列表：一旦页面执行了onStart，页面信息将保存在这里，并将此页面从onCreate列表中移除
    private ArrayList<PageLoadInfo> onStartList = new ArrayList<>();

    //page列表：
    // 1.onResume，此页面信息将从onStart列表移到页面列表中
    // 2.onPause时将页面信息写入文件
    // 3.onStop时从表中移除
    private ArrayList<PageLoadInfo> pageList = new ArrayList<>();

    //通过onResume来确定一下两个值，用于判断页面绘制归属那个页面
    private PageLoadInfo lastPage = null;
    private PageLoadInfo thisPage = null;

    public void onInstrumentation_execStartActivity(long start, long end) {
        synchronized (lock) {
            execStart_start = start;
            execStart_end = end;
        }
    }

    public void onInstrumentation_callActivityOnCreate(String activityClassName, String objectHashCode, long start, long end) {
        synchronized (lock) {
            PageLoadInfo pageLoadInfo = new PageLoadInfo(activityClassName, objectHashCode);
            if (execStart_start != 0 && execStart_end != 0) {
                pageLoadInfo.addExecStartInfo(execStart_start, execStart_end);
            }
            pageLoadInfo.addCreateInfo(start, end);
            onCreateList.add(pageLoadInfo);
            //清除启动信息
            execStart_start = 0;
            execStart_end = 0;
        }
    }

    public void onInstrumentation_callActivityOnStart(String activityClassName, String objectHashCode, long start, long end) {
        synchronized (lock) {
            //检查是否有OnCreate信息
            PageLoadInfo pageLoadInfo = null;
            for (PageLoadInfo p : onCreateList) {
                if (p.activityClassName.equals(activityClassName) && p.objectHashCode.equals(objectHashCode)) {
                    pageLoadInfo = p;
                    onCreateList.remove(p);
                    break;
                }
            }
            if (pageLoadInfo == null) {//页面从onStart开始
                pageLoadInfo = new PageLoadInfo(activityClassName, objectHashCode);
            }
            //保存onStart信息
            pageLoadInfo.addStartInfo(start, end);
            //加入页面列表中
            onStartList.add(pageLoadInfo);
        }
    }

    public void onInstrumentation_callActivityOnResume(String activityClassName, String objectHashCode, long start, long end) {
        synchronized (lock) {
            //检查是否有onStart信息
            PageLoadInfo pageLoadInfo = null;
            for (PageLoadInfo p : onStartList) {
                if (p.activityClassName.equals(activityClassName) && p.objectHashCode.equals(objectHashCode)) {
                    pageLoadInfo = p;
                    onStartList.remove(p);
                    break;
                }
            }
            if (pageLoadInfo == null) {//页面从onResume开始
                pageLoadInfo = new PageLoadInfo(activityClassName, objectHashCode);
            }
            //保存onResume信息
            pageLoadInfo.addResumeInfo(start, end);
            //startOrderId信息
            startNumber++;
            pageLoadInfo.startOrderId = startNumber;
            //加入onResume列表中
            pageList.add(pageLoadInfo);

            //设置以下两个值，用于View绘制归类判断
            lastPage = thisPage;
            thisPage = pageLoadInfo;

        }
    }

    public void onInstrumentation_callActivityOnPause(String activityClassName, String objectHashCode, long start, long end) {
        synchronized (lock) {
            //保存onPause信息,倒序遍历，赋值给第一个页面（原理和 @addDrawInfo 方法类似）
            for (int i = pageList.size() - 1; i >= 0; i--) {
                final PageLoadInfo pageLoadInfo = pageList.get(i);
                if (pageLoadInfo.activityClassName.equals(activityClassName) && pageLoadInfo.objectHashCode.equals(objectHashCode)) {
                    //保存onPause信息
                    pageLoadInfo.addPauseInfo(start, end);
                    //页面数
                    gtrAnalysisResult.pageNum++;
                    //超时页面数
                    long loadTime = getPageLoadStartFinishTime(pageLoadInfo) - getPageLoadStartTime(pageLoadInfo);
                    DetailListData detailListData;
                    if (pageLoadInfo.isCold() && loadTime > 500) { //排除热启动超时，如果应用从后台热启动，可能不绘制？？？？
                        gtrAnalysisResult.overPageNum++;
                        detailListData = new DetailListData(pageLoadInfo.startOrderId + "." + activityClassName + "\n启动耗时:" + loadTime + "ms", DetailListData.Error);
                    } else {
                        detailListData = new DetailListData(pageLoadInfo.startOrderId + "." + activityClassName + "\n启动耗时:" + loadTime + "ms", DetailListData.Normal);
                    }
                    gtrAnalysisResult.allActivityListData.add(detailListData);
                    //移除页面列表
                    pageList.remove(pageLoadInfo);
                    break;
                }
            }
            //call回调通知数据刷新
            GTRAnalysis.refreshPageLoadInfo();
        }
    }

    public void onInstrumentation_callActivityOnStop(String activityClassName, String objectHashCode, long start, long end) {
        //do nothing
    }

    /**
     * HOOK ViewGroup绘制
     */
    public void onViewGroup_dispatchDraw(String drawClassName, String objectHashCode, long start, long end, int drawDeep, String drawPath) {
        synchronized (lock) {
            DrawInfo drawInfo = new DrawInfo();
            drawInfo.drawClassName = drawClassName;
            drawInfo.objectHashCode = objectHashCode;
            drawInfo.drawBegin = start;
            drawInfo.drawEnd = end;
            drawInfo.drawDeep = drawDeep;
            drawInfo.drawPath = drawPath;
            //检查此绘制是否为前一个页面的绘制信息
            if (drawClassName.contains("com.android.internal.policy.DecorView")) {
                thisPage.addDrawInfo(drawInfo);
            } else {
                if (lastPage != null && lastPage.isMyDraw(drawInfo)) {
                    lastPage.addDrawInfo(drawInfo);
                } else {
                    thisPage.addDrawInfo(drawInfo);
                }
            }

            //检测是否绘制超时：
            DetailListData detailListData;
            gtrAnalysisResult.viewDrawNum++;
            long drawTime = end - start;
            if (drawTime > 5) {
                gtrAnalysisResult.overViewDrawNum++;
                detailListData = new DetailListData("所属Activity:" + thisPage.activityClassName + "\n绘制时长:" + drawTime + "ms", DetailListData.Error);
            } else {
                detailListData = new DetailListData("所属Activity:" + thisPage.activityClassName + "\n绘制时长:" + drawTime + "ms", DetailListData.Normal);
            }
            gtrAnalysisResult.allViewDrawListData.add(detailListData);
            //call回调通知数据刷新
            GTRAnalysis.refreshViewDrawInfo();
        }
    }

    private static long getPageLoadStartTime(PageLoadInfo pageLoadInfo) {
        long begin = 0;
        for (LifecycleMethod lifecycleMethod : pageLoadInfo.lifecycleMethodList) {
            if (begin == 0 || lifecycleMethod.methodStartTime < begin) {
                begin = lifecycleMethod.methodStartTime;
            }
        }
        return begin;
    }

    private static long getPageLoadStartFinishTime(PageLoadInfo pageLoadInfo) {
        long startFinishTime = 0;
        for (LifecycleMethod lifecycleMethod : pageLoadInfo.lifecycleMethodList) {
            if (lifecycleMethod.methodName.equals(LifecycleMethod.ONRESUME)) {
                startFinishTime = lifecycleMethod.methodEndTime;
            }
        }
        if (pageLoadInfo.drawInfoList != null && pageLoadInfo.drawInfoList.size() > 0) {
            startFinishTime = pageLoadInfo.drawInfoList.get(0).drawEnd;
        }
        return startFinishTime;
    }

    private static long getPageLoadEndTime(PageLoadInfo pageLoadInfo) {
        long end = 0;
        for (LifecycleMethod lifecycleMethod : pageLoadInfo.lifecycleMethodList) {
            if (end == 0 || lifecycleMethod.methodEndTime > end) {
                end = lifecycleMethod.methodEndTime;
            }
        }
        return end;
    }
}
