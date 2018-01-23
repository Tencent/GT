package com.gtr.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.tencent.wstt.gtr.sdkdemo.R;

public class Test_Block_Activity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_block);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Test_Block_Activity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
