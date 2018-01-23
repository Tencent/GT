package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.obj.DBActionInfo;
import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.DiskIOInfo;
import com.tencent.wstt.gt.datatool.obj.FileActionInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class IOAnalysis {
    GTRAnalysis gtrAnalysis;


    //所有磁盘IO：
    ArrayList<DiskIOInfo> diskIOInfos;

    //文件操作：
    ArrayList<FileActionInfo> fileActionInfos;//通过路径名过滤掉系统IO
    //主线程的文件操作：
    ArrayList<Integer> fileActionInfosInMainThread;


    //数据库操作：
    ArrayList<DBActionInfo> dbActionInfos;
    ArrayList<Integer> dbActionInfosInMainThread;


    public IOAnalysis(GTRAnalysis gtrAnalysis,
                      ArrayList<DiskIOInfo> diskIOInfos,
                      ArrayList<FileActionInfo> fileActionInfos,
                      ArrayList<Integer> fileActionInfosInMainThread,
                      ArrayList<DBActionInfo> dbActionInfos,
                      ArrayList<Integer> dbActionInfosInMainThread) {
        this.gtrAnalysis = gtrAnalysis;
        this.diskIOInfos = diskIOInfos;
        this.fileActionInfos = fileActionInfos;
        this.fileActionInfosInMainThread = fileActionInfosInMainThread;
        this.dbActionInfos = dbActionInfos;
        this.dbActionInfosInMainThread = dbActionInfosInMainThread;
    }


    private HashMap<Integer, String> threadNames = new HashMap<>();//线程ID与线程名的对应关系
    private HashMap<Integer, String> fileNames = new HashMap<>();//文件ID与文件名的对应关系
    private HashMap<Integer, String> filePaths = new HashMap<>();//文件ID与文件路径的对应关系

    public void onFileOpen(int tid, int fd, String path, long start, long end) {

        String[] tempStringArray = path.split("/");
        filePaths.put(fd, path);
        fileNames.put(fd, tempStringArray[tempStringArray.length - 1]);

        DiskIOInfo diskIOInfo = new DiskIOInfo();
        diskIOInfo.fd = fd;
        diskIOInfo.fileName = fileNames.get(fd) == null ? "unknow" : fileNames.get(fd);
        diskIOInfo.filePath = filePaths.get(fd) == null ? "unknow" : filePaths.get(fd);
        diskIOInfo.actionName = DiskIOInfo.OPEN;
        diskIOInfo.actionStart = start;
        diskIOInfo.actionEnd = end;
        diskIOInfo.actionSize = 0;
        diskIOInfo.threadID = tid;
        diskIOInfo.threadName = threadNames.get(tid) == null ? "unknow" : threadNames.get(tid);

        addFileInfo(diskIOInfo);

    }

    public void onFileWrite(int tid, int fd, int size, long start, long end) {

        DiskIOInfo diskIOInfo = new DiskIOInfo();
        diskIOInfo.fd = fd;
        diskIOInfo.fileName = fileNames.get(fd) == null ? "unknow" : fileNames.get(fd);
        diskIOInfo.filePath = filePaths.get(fd) == null ? "unknow" : filePaths.get(fd);
        diskIOInfo.actionName = DiskIOInfo.WRITE;
        diskIOInfo.actionStart = start;
        diskIOInfo.actionEnd = end;
        diskIOInfo.actionSize = size;
        diskIOInfo.threadID = tid;
        diskIOInfo.threadName = threadNames.get(tid) == null ? "unknow" : threadNames.get(tid);

        addFileInfo(diskIOInfo);

    }

    public void onFileRead(int tid, int fd, int size, long start, long end) {

        DiskIOInfo diskIOInfo = new DiskIOInfo();
        diskIOInfo.fd = fd;
        diskIOInfo.fileName = fileNames.get(fd) == null ? "unknow" : fileNames.get(fd);
        diskIOInfo.filePath = filePaths.get(fd) == null ? "unknow" : filePaths.get(fd);
        diskIOInfo.actionName = DiskIOInfo.READ;
        diskIOInfo.actionStart = start;
        diskIOInfo.actionEnd = end;
        diskIOInfo.actionSize = size;
        diskIOInfo.threadID = tid;
        diskIOInfo.threadName = threadNames.get(tid) == null ? "unknow" : threadNames.get(tid);

        addFileInfo(diskIOInfo);

    }

    public void addFileInfo(DiskIOInfo diskIOInfo) {
        //TODO 对系统IO进行剔除：
        if (diskIOInfo.filePath == null) {
            return;
        }
        //diskIOInfos:
        diskIOInfos.add(diskIOInfo);
        //fileActionInfos:
        int local = -1;
        if (diskIOInfo.actionName.equals(DiskIOInfo.OPEN)) {//如果是open函数，那么就是新的文件操作
            FileActionInfo fileActionInfo = new FileActionInfo();
            fileActionInfo.addDiskIOInfo(diskIOInfo, gtrAnalysis.getAppInfo().mainThreadId);
            fileActionInfos.add(fileActionInfo);
            local = fileActionInfos.size() - 1;
        } else {//寻找最近的fd文件操作，如果不存在，则说明文件的open函数没有hook到。
            boolean isExists = false;
            for (int i = fileActionInfos.size() - 1; i >= 0; i--) {
                if (fileActionInfos.get(i).fd == diskIOInfo.fd) {
                    isExists = true;
                    fileActionInfos.get(i).addDiskIOInfo(diskIOInfo, gtrAnalysis.getAppInfo().mainThreadId);
                    local = i;
                    break;
                }
            }
            if (!isExists) {
                FileActionInfo fileActionInfo = new FileActionInfo();
                fileActionInfo.addDiskIOInfo(diskIOInfo, gtrAnalysis.getAppInfo().mainThreadId);
                fileActionInfos.add(fileActionInfo);
                local = fileActionInfos.size() - 1;
            }
        }
        //fileActionInfosInMainThread:
        if (local == -1) {//TODO ERROR
            System.out.println("fileActionInfosInMainThread ERROR");
        }
        if (local != -1 && diskIOInfo.threadID == gtrAnalysis.getAppInfo().mainThreadId) {
            boolean isExists = false;
            for (Integer integer : fileActionInfosInMainThread) {
                if (integer == local) {
                    isExists = true;
                    break;
                }
            }
            if (!isExists) {
                fileActionInfosInMainThread.add(local);
            }
        }


    }


    private HashMap<Integer, String> dbNames = new HashMap<>();//DB ID与DB名字的对应关系
    private HashMap<Integer, String> dbPaths = new HashMap<>();//DB ID与DB路径的对应关系

    public void onSQLiteDatabase_beginTransaction(int dbHashCode, String threadName, int threadId, long start, long end) {

        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "beginTransaction";
        dbActionInfo.sql = "";
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);

    }

    public void onSQLiteDatabase_endTransaction(int dbHashCode, String threadName, int threadId, long start, long end) {
        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "endTransaction";
        dbActionInfo.sql = "";
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);
    }

    public void onSQLiteDatabase_enableWriteAheadLogging(int dbHashCode, String threadName, int threadId, long start, long end) {
        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "enableWriteAheadLogging";
        dbActionInfo.sql = "";
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);
    }

    public void onSQLiteDatabase_openDatabase(int dbHashCode, String path, String threadName, int threadId, long start, long end) {

        String[] tempStringArray = path.split("/");
        dbPaths.put(dbHashCode, path);
        dbNames.put(dbHashCode, tempStringArray[tempStringArray.length - 1]);

        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "openDatabase";
        dbActionInfo.sql = "";
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);
    }

    public void onSQLiteDatabase_rawQueryWithFactory(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "Query";
        dbActionInfo.sql = sql;
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);
    }

    public void onSQLiteStatement_execute(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "Execute";
        dbActionInfo.sql = sql;
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);
    }

    public void onSQLiteStatement_executeInsert(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "Insert";
        dbActionInfo.sql = sql;
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);
    }

    public void onSQLiteStatement_executeUpdateDelete(int dbHashCode, String sql, String threadName, int threadId, long start, long end) {
        DBActionInfo dbActionInfo = new DBActionInfo();
        dbActionInfo.dbHashCode = dbHashCode;
        dbActionInfo.dbPath = dbPaths.get(dbHashCode) == null ? "" : dbPaths.get(dbHashCode);
        dbActionInfo.dbName = dbNames.get(dbHashCode) == null ? "" : dbNames.get(dbHashCode);
        dbActionInfo.actionName = "UpdateDelete";
        dbActionInfo.sql = sql;
        dbActionInfo.startTime = start;
        dbActionInfo.endTime = end;
        dbActionInfo.threadName = threadName;
        dbActionInfo.threadId = threadId;

        addDBActionInfo(dbActionInfo);
    }


    private void addDBActionInfo(DBActionInfo dbActionInfo) {
        dbActionInfos.add(dbActionInfo);
        if (dbActionInfo.threadId == gtrAnalysis.getAppInfo().mainThreadId) {
            dbActionInfosInMainThread.add(dbActionInfos.size() - 1);
        }
    }


}
