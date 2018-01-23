package com.gtr.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.wstt.gtr.sdkdemo.R;

public class Test_ViewBuild_Activity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_viewbuild);

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_main,null);

    }
}
