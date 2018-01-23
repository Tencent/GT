package com.gtr.test.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.wstt.gtr.sdkdemo.R;

/**
 * Created by p_hongjcong on 2017/4/20.
 */

public class FragmentDemo2 extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, null);

        return view;
    }
}
