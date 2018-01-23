package com.gtr.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.wstt.gtr.sdkdemo.R;

import java.util.ArrayList;

public class Test_Memory_Activity extends Activity {


    ArrayList<Object> objects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long start = System.currentTimeMillis();
        setContentView(R.layout.activity_test_memory);
        long end = System.currentTimeMillis();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i("ASSSSASAS","View构建耗时："+(end-start));

//        Looper.getMainLooper().setMessageLogging(new Printer() {
//            @Override
//            public void println(String x) {
//
//            }
//        });

        Button button = (Button) findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<999999;i++){
                    objects.add(new Object());
                }
            }
        });
        Button button2 = (Button) findViewById(R.id.button6);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                System.runFinalization();
                Runtime.getRuntime().gc();
                Toast.makeText(Test_Memory_Activity.this,"haha",Toast.LENGTH_LONG).show();
            }
        });


//        WebView webView = (WebView) findViewById(R.id.sssss);
//        webView.loadUrl("http://fanyi.baidu.com/?aldtype=16047#zh/en/%E9%97%B4%E9%9A%94");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Test_Memory_Activity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
