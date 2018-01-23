package com.tencent.wstt.gt.collector.util;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by p_hongjcong on 2017/6/27.
 */
public class HookUtil {
    public static int getDBHashCode(SQLiteStatement sqLiteStatement) {
        //获取SQLiteDatabaseConfiguration对象：
        int hashCode = -1;
        try {
            Class SQLiteProgramClass = Class.forName("android.database.sqlite.SQLiteProgram");
            Method[] methods = SQLiteProgramClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals("getDatabase")) {
                    method.setAccessible(true);
                    SQLiteDatabase property = (SQLiteDatabase) method.invoke(sqLiteStatement);
                    if (property != null) {
                        hashCode = property.hashCode();
                    }
                }
            }
        } catch ( IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return hashCode;
    }

    public static String getSQL(SQLiteStatement sqLiteStatement) {
        // 获取SQLiteDatabaseConfiguration对象：
        String sql = null;
        try {
            Class SQLiteProgramClass = Class.forName("android.database.sqlite.SQLiteProgram");
            Method[] methods = SQLiteProgramClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals("getSql")) {
                    method.setAccessible(true);
                    sql = (String) method.invoke(sqLiteStatement);
                    if (sql == null) {
                        sql = "";
                    }
                }
            }
        } catch ( IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return sql;
    }
}
