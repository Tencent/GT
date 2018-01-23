package com.gtr.test.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.wstt.gtr.sdkdemo.R;

/**
 * Created by p_hongjcong on 2017/4/20.
 */

public class FragmentDemo1_V4 extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        return view;
    }




}
