package com.tencent.wstt.gt.analysis4.analysis;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.dao.DetailListData;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;
import com.tencent.wstt.gt.analysis4.obj.DiskIOInfo;
import com.tencent.wstt.gt.analysis4.obj.LogInfo;
import com.tencent.wstt.gt.analysis4.util.LogUtil;

import java.util.HashMap;

public class IOAnalysis {
    GTRAnalysisResult gtrAnalysisResult = null;

    public IOAnalysis(GTRAnalysisResult gtrAnalysisResult) {
        this.gtrAnalysisResult = gtrAnalysisResult;
    }

    public void onCollectLog(String log, long time) {
        LogInfo logInfo = LogUtil.onCollectLog(log, time);
        if (logInfo == null) {
            return;
        }

        if (logInfo.tag.contains("GTR_DATA_TAG")) {
            String[] data = logInfo.logContent.split(GTConfig.separatorFile);
            int tid;
            int fd;
            int size;
            long timeStart;
            long timeEnd;
            String path;

            if (data[0].contains("file_open")) {
                tid = Integer.parseInt(data[1]);
                fd = Integer.parseInt(data[2]);
                path = data[3];
                timeStart = Long.parseLong(data[4]);
                timeEnd = Long.parseLong(data[5]);
                onFileOpen(tid, fd, path, timeStart, timeEnd);
            } else if (data[0].contains("file_write") ||
                    data[0].contains("file_pwrite64")) {
                tid = Integer.parseInt(data[1]);
                fd = Integer.parseInt(data[2]);
                size = Integer.parseInt(data[3]);
                timeStart = Long.parseLong(data[4]);
                timeEnd = Long.parseLong(data[5]);
                onFileRead(tid, fd, size, timeStart, timeEnd);
            } else if (data[0].contains("file_read") ||
                    data[0].contains("file_pread64")) {
                tid = Integer.parseInt(data[1]);
                fd = Integer.parseInt(data[2]);
                size = Integer.parseInt(data[3]);
                timeStart = Long.parseLong(data[4]);
                timeEnd = Long.parseLong(data[5]);
                onFileWrite(tid, fd, size, timeStart, timeEnd);
            }
        }
    }

    private HashMap<Integer, String> threadNames = new HashMap<>();//线程ID与线程名的对应关系
    private HashMap<Integer, String> fileNames = new HashMap<>();//文件ID与文件名的对应关系
    private HashMap<Integer, String> filePaths = new HashMap<>();//文件ID与文件路径的对应关系

    public void onFileOpen(int tid, int fd, String path, long start, long end) {

    }

    public void onFileWrite(int tid, int fd, int size, long start, long end) {

    }

    public void onFileRead(int tid, int fd, int size, long start, long end) {
    }

    public void addFileInfo(DiskIOInfo diskIOInfo) {
    }


    private HashMap<Integer, String> dbNames = new HashMap<>();//DB ID与DB名字的对应关系
    private HashMap<Integer, String> dbPaths = new HashMap<>();//DB ID与DB路径的对应关系

    public void onSQLiteDatabase_beginTransaction(int dbHashCode, String threadName, int threadId, long start, long end) {
        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = "beginTransaction";
        checkOnMainThread(dbName, action, takeTime, threadId);
    }

    public void onSQLiteDatabase_endTransaction(int dbHashCode, String threadName, int threadId, long start, long end) {
        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = "endTransaction";
        checkOnMainThread(dbName, action, takeTime, threadId);
    }

    public void onSQLiteDatabase_enableWriteAheadLogging(int dbHashCode, String threadName, int threadId, long start, long end) {
        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = "enableWriteAheadLogging";
        checkOnMainThread(dbName, action, takeTime, threadId);
    }

    public void onSQLiteDatabase_openDatabase(int dbHashCode, String path, String threadName, int threadId, long start, long end) {
        String[] tempStringArray = path.split("/");
        dbPaths.put(dbHashCode, path);
        dbNames.put(dbHashCode, tempStringArray[tempStringArray.length - 1]);

        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = "openDatabase";
        checkOnMainThread(dbName, action, takeTime, threadId);
    }

    public void onSQLiteDatabase_rawQueryWithFactory(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = sql;
        checkOnMainThread(dbName, action, takeTime, threadId);
    }

    public void onSQLiteStatement_execute(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = sql;
        checkOnMainThread(dbName, action, takeTime, threadId);
    }

    public void onSQLiteStatement_executeInsert(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = sql;
        checkOnMainThread(dbName, action, takeTime, threadId);
    }

    public void onSQLiteStatement_executeUpdateDelete(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        long takeTime = end - start;
        String dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        String action = sql;
        checkOnMainThread(dbName, action, takeTime, threadId);
    }


    private void checkOnMainThread(String dbName, String action, long takeTime, int threadId) {
        gtrAnalysisResult.dbIONum++;
        DetailListData detailListData;
        if (gtrAnalysisResult.mainThreadId != -1 && gtrAnalysisResult.mainThreadId == threadId) {
            gtrAnalysisResult.mainThreadDBIONum++;
            detailListData = new DetailListData("数据库：" + dbName + "\n操作:" + action + "\n耗时:" + takeTime + "ms\n线程:主线程", DetailListData.Error);
        } else {
            detailListData = new DetailListData("数据库：" + dbName + "\n操作:" + action + "\n耗时:" + takeTime + "ms\n线程:" + threadId, DetailListData.Normal);
        }
        gtrAnalysisResult.allDBIOListData.add(detailListData);

        GTRAnalysis.refreshIOInfo();
    }
}
