package com.gtr.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.tencent.wstt.gtr.sdkdemo.R;

public class Test_Operation_Activity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_operation);

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
