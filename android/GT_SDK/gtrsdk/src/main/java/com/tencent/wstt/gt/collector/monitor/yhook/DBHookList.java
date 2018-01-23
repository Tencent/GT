package com.tencent.wstt.gt.collector.monitor.yhook;

import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.CancellationSignal;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.collector.util.HookUtil;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;

import com.kunpeng.pit.HookAnnotation;


/**
 * Created by p_hongjcong on 2017/6/26.
 */

public class DBHookList {
    private static final String TAG = "HookList_DB";

    /* 对事务的执行进行监控 */
    @HookAnnotation(
            className = "android.database.sqlite.SQLiteDatabase",
            methodName = "beginTransaction",
            methodSig = "()V" )
    public static void beginTransaction(Object thiz) {
        GTRLog.e(TAG,"SQLiteDatabase.beginTransaction");
        long start = System.currentTimeMillis();
        beginTransaction_backup(thiz);
        long end = System.currentTimeMillis();
        int dbHashCode = thiz.hashCode();
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteDatabase.beginTransaction")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());

    }

    public static void beginTransaction_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void beginTransaction_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.database.sqlite.SQLiteDatabase",
            methodName = "endTransaction",
            methodSig ="()V")
    public static void endTransaction(Object thiz) {
        GTRLog.e(TAG,"SQLiteDatabase.endTransaction");
        long start = System.currentTimeMillis();
        endTransaction_backup(thiz);
        long end = System.currentTimeMillis();
        int dbHashCode = thiz.hashCode();
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteDatabase.endTransaction")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void endTransaction_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void endTransaction_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    /* 监测是否打开同步写开关（用内存换时间的做法）*/
    @HookAnnotation(
            className = "android.database.sqlite.SQLiteDatabase",
            methodName = "enableWriteAheadLogging",
            methodSig = "()Z")
    public static boolean enableWriteAheadLogging(Object thiz) {
        GTRLog.e(TAG,"SQLiteDatabase.enableWriteAheadLogging");
        long start = System.currentTimeMillis();
        boolean result = enableWriteAheadLogging_backup(thiz);
        long end = System.currentTimeMillis();
        int dbHashCode = thiz.hashCode();
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteDatabase.enableWriteAheadLogging")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
        return result;
    }

    public static boolean enableWriteAheadLogging_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }

    public static boolean enableWriteAheadLogging_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return true;
    }

    /* 检测数据库打开的时长*/
    @HookAnnotation(
            className = "android.database.sqlite.SQLiteDatabase",
            methodName = "openDatabase",
            methodSig ="(Ljava/lang/String;" +
                    "Landroid/database/sqlite/SQLiteDatabase$CursorFactory;" +
                    "ILandroid/database/DatabaseErrorHandler;)" +
                    "Landroid/database/sqlite/SQLiteDatabase;" )
    public static SQLiteDatabase openDatabase(String path, SQLiteDatabase.CursorFactory factory, int flags,DatabaseErrorHandler errorHandler) {
        GTRLog.e(TAG,"SQLiteDatabase.openDatabase");
        long start = System.currentTimeMillis();
        SQLiteDatabase result = openDatabase_backup(path,factory,flags,errorHandler);
        long end = System.currentTimeMillis();
        int dbHashCode = result.hashCode();
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteDatabase.openDatabase")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(path)
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
        return result;
    }

    public static SQLiteDatabase openDatabase_backup(String path, SQLiteDatabase.CursorFactory factory, int flags,DatabaseErrorHandler errorHandler) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    public static SQLiteDatabase openDatabase_tmp(String path, SQLiteDatabase.CursorFactory factory, int flags,DatabaseErrorHandler errorHandler) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    /* 对sql的执行函数进行监控 */
    @HookAnnotation(
            className = "android.database.sqlite.SQLiteDatabase",
            methodName = "rawQueryWithFactory",
            methodSig = "(Landroid/database/sqlite/SQLiteDatabase$CursorFactory;" +
                    "Ljava/lang/String;" +
                    "[Ljava/lang/String;" +
                    "Ljava/lang/String;" +
                    "Landroid/os/CancellationSignal;)" +
                    "Landroid/database/Cursor;")
    public static Cursor rawQueryWithFactory(Object thiz, SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs,
            String editTable, CancellationSignal cancellationSignal) {
        GTRLog.e(TAG,"SQLiteDatabase.rawQueryWithFactory");
        long start = System.currentTimeMillis();
        Cursor result =rawQueryWithFactory_backup(thiz, cursorFactory, sql, selectionArgs, editTable, cancellationSignal);
        long end = System.currentTimeMillis();
        int dbHashCode = thiz.hashCode();
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteDatabase.rawQueryWithFactory")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(sql.replace("\n",""))
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
        return result;
    }

    public static Cursor rawQueryWithFactory_backup(Object thiz, SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs,
                                                     String editTable, CancellationSignal cancellationSignal) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    public static Cursor rawQueryWithFactory_tmp(Object thiz, SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs,
                                                  String editTable, CancellationSignal cancellationSignal) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    /* 对sql的执行函数进行监控 */
    @HookAnnotation(
            className = "android.database.sqlite.SQLiteStatement",
            methodName = "execute",
            methodSig ="()V" )
    public static void execute(Object thiz) {
        GTRLog.e(TAG,"SQLiteStatement.execute");
        long start = System.currentTimeMillis();
        execute_backup(thiz);
        long end = System.currentTimeMillis();
        int dbHashCode = HookUtil.getDBHashCode((SQLiteStatement)(Object)thiz);
        String sql = HookUtil.getSQL((SQLiteStatement)(Object)thiz);
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteStatement.execute")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(sql.replace("\n",""))
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void execute_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void execute_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.database.sqlite.SQLiteStatement",
            methodName = "executeInsert",
            methodSig = "()J")
    public static long executeInsert(Object thiz) {
        GTRLog.e(TAG,"SQLiteStatement.executeInsert" );
        long start = System.currentTimeMillis();
        long result = executeInsert_backup(thiz);
        long end = System.currentTimeMillis();
        int dbHashCode = HookUtil.getDBHashCode((SQLiteStatement)(Object)thiz);
        String sql = HookUtil.getSQL((SQLiteStatement)(Object)thiz);
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteStatement.executeInsert")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(sql.replace("\n",""))
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
        return result;
    }

    public static long executeInsert_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return 0;
    }

    public static long executeInsert_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return 0;
    }

    @HookAnnotation(
            className = "android.database.sqlite.SQLiteStatement",
            methodName = "executeUpdateDelete",
            methodSig = "()I")
    public static int executeUpdateDelete(Object thiz) {
        GTRLog.e(TAG,"SQLiteStatement.executeUpdateDelete");
        long start = System.currentTimeMillis();
        int result = executeUpdateDelete_backup(thiz);
        long end = System.currentTimeMillis();
        int dbHashCode = HookUtil.getDBHashCode((SQLiteStatement)(Object)thiz);
        String sql = HookUtil.getSQL((SQLiteStatement)(Object)thiz);
        String threadName = Thread.currentThread().getName();
        long threadID = android.os.Process.myTid();
        GTRClient.pushData(new StringBuilder()
                .append("SQLiteStatement.executeUpdateDelete")
                .append(GTConfig.separator).append(dbHashCode)
                .append(GTConfig.separator).append(sql.replace("\n",""))
                .append(GTConfig.separator).append(threadName)
                .append(GTConfig.separator).append(threadID)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
        return result;
    }

    public static int executeUpdateDelete_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return 0;
    }

    public static int executeUpdateDelete_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return 0;
    }
}
