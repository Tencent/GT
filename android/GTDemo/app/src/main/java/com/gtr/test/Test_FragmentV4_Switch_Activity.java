package com.gtr.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.gtr.test.fragment.FragmentDemo1_V4;
import com.gtr.test.fragment.FragmentDemo2_V4;
import com.gtr.test.fragment.FragmentDemo3_V4;
import com.gtr.test.fragment.FragmentDemo4_V4;
import com.gtr.test.fragment.FragmentDemo5_V4;
import com.tencent.wstt.gtr.sdkdemo.R;

import java.util.ArrayList;

public class Test_FragmentV4_Switch_Activity extends FragmentActivity {


    //Fragment sss = new FragmentDemo1_V4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragmentv4_switch);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();
        titles.add("1");
        fragments.add(new FragmentDemo1_V4());
        titles.add("2");
        fragments.add(new FragmentDemo2_V4());
        titles.add("2");
        fragments.add(new FragmentDemo3_V4());
        titles.add("2");
        fragments.add(new FragmentDemo4_V4());
        titles.add("2");
        fragments.add(new FragmentDemo5_V4());

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),titles,fragments);


        getFragmentManager();


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setCurrentItem(0);//设置当前显示标签页为第一页

    }




    public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();

        public MyFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<String> titles,ArrayList<Fragment> fragments) {
            super(fragmentManager);
            this.titles = titles;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments!=null && position<fragments.size()){
                return fragments.get(position);
            }else {
                return null;
            }
        }

        @Override
        public int getCount() {
            if (fragments != null){
                return fragments.size();
            }else {
                return 0;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (titles!=null && position<titles.size()){
                return titles.get(position);
            }else {
                return "";
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Test_FragmentV4_Switch_Activity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
