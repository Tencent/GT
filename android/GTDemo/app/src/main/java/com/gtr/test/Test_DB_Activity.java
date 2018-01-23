package com.gtr.test;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gtr.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.provider.BaseColumns._ID;

public class Test_DB_Activity extends Activity {

    private static final String TAG = "Test_DB_Activity";

    MySQLiteHelper mySQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mySQLiteHelper = new MySQLiteHelper(this.getApplicationContext());

        operateDB();
    }

    private void operateDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 清空数据库操作
                List<Person> list = mySQLiteHelper.quaryNameList();
                GTRLog.i(TAG, "list.size = " + list.size());
                if (list.size() != 0){
                    for (Person person : list) {
                        mySQLiteHelper.deleteData(String.valueOf(person.getId()), person.getName());
                    }
                }
                // 往数据库中插入数据
                GTRLog.i(TAG, "start insert");
                mySQLiteHelper.insertData("1", "aaaa");
                mySQLiteHelper.insertData("2", "bbbb");
                mySQLiteHelper.insertData("3", "cccc");
                // 更新数据库数据
                GTRLog.i(TAG, "start update");
                mySQLiteHelper.updateData("2","bbbb2");
                mySQLiteHelper.updateData("3","cccc3");
                // 删除数据库数据
                GTRLog.i(TAG, "start delete");
                mySQLiteHelper.deleteData("2","bbbb2");
                mySQLiteHelper.deleteData("3","cccc3");
            }
        }).start();
    }

    public class MySQLiteHelper extends SQLiteOpenHelper {

        public static final String DBName = "hero_info";

        //调用父类构造器
        public MySQLiteHelper(Context context) {
            super(context, DBName, null, 1);
        }

        /**
         * 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行.
         * 重写onCreate方法，调用execSQL方法创建表
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists " + DBName + "("
                    + "_id integer primary key autoincrement,"
                    + "p_id varchar(20),"
                    + "name varchar(20))");

        }

        //当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }


        /**
         * 增
         */
        public void insertData(String id, String name) {
            try {
                //设置属性：
                ContentValues dataValue = new ContentValues();
                dataValue.put("p_id", id);
                dataValue.put("name", name);
                //插入数据库：
                SQLiteDatabase databaseWrite = this.getWritableDatabase();
                databaseWrite.insert(DBName, null, dataValue);
                databaseWrite.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        /**
         * 删
         */
        public void deleteData(String id, String name) {
            SQLiteDatabase databaseWrite = this.getWritableDatabase();
            databaseWrite.delete(DBName, "p_id = ? and name = ?", new String[]{id, name});
            databaseWrite.close();
        }


        /**
         * 改
         */
        public void updateData(String id, String name) {
            SQLiteDatabase databaseWrite = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);//key为字段名，value为值
            databaseWrite.update(DBName, values, "p_id=?", new String[]{id});
            databaseWrite.close();
        }


        /**
         * 查
         */
        public ArrayList<Person> quaryNameList() {
            ArrayList<Person> nameList = new ArrayList<Person>();
            SQLiteDatabase database = this.getReadableDatabase();
            Cursor cursor = database.query(DBName, null, null, null, null, null, null);
            Person person = null;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        person = new Person();
                        person.setId(cursor.getString(cursor.getColumnIndex("p_id")));
                        person.setName(cursor.getString(cursor.getColumnIndex("name")));
                        nameList.add(person);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return nameList;
        }
    }

    public class Person {
        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySQLiteHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Test_DB_Activity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
