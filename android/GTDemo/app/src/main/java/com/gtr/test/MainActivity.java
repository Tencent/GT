package com.gtr.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.wstt.gtr.sdkdemo.R;

//import com.tencent.smtt.sdk.WebStorage;
//import com.tencent.smtt.sdk.WebView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AsyncTask asyncTask = new AsyncTask() {
//            LinearLayout linearLayout;
//            @Override
//            protected Object doInBackground(Object[] params) {
//                Activity thisActivity = (Activity)params[0];
//                linearLayout = (LinearLayout) (thisActivity.findViewById(R.id.linear_main));
//                return params[0];
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//
//                linearLayout.addView(new Button((Activity)o));
//
//            }
//        };
//        asyncTask.execute(this);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Test_WebView_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.getApplicationContext().startActivity(intent);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Test_Memory_Activity.class);
                startActivity(intent);
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Test_FragmentV4_Activity.class);
                startActivity(intent);
            }
        });
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Test_Fragment_Activity.class);
                startActivity(intent);
            }
        });

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Test_DB_Activity.class);
                startActivity(intent);
            }
        });

        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Test_Test_Activity.class);
                startActivity(intent);
            }
        });

        Button button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Test_Input_Output_Activity.class);
                startActivity(intent);
            }
        });

      PermissionTool.applyPermissions(permissions,MainActivity.this);


    }

    //需要申请的权限：
    private static final String[] permissions = {
            //存储卡
            Manifest.permission.READ_EXTERNAL_STORAGE,//SDK>=16才可以使用
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
    };


    @RequiresApi(api = 23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        //申请所有权限的回调结果：
        if (requestCode == PermissionTool.APPLY_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//如果有权限被拒绝
                    Toast.makeText(this, "对不起，您未给予相应的权限，程序将退出。", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
            //如果全部都同意了就进行配置加载
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "测试1");
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "测试2");
        menu.add(Menu.NONE, Menu.FIRST + 3, 3, "测试3");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                break;
            case Menu.FIRST + 2:
                break;
            case Menu.FIRST + 3:
                break;


        }
        return false;
    }


}
