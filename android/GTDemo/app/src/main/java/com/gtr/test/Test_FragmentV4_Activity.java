package com.gtr.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.tencent.wstt.gtr.sdkdemo.R;

public class Test_FragmentV4_Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragmentv4);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Button button9  = (Button) findViewById(R.id.button9);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Test_FragmentV4_Activity.this,Test_FragmentV4_Switch_Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Test_FragmentV4_Activity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
