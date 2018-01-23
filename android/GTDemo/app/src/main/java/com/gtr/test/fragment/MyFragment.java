package com.gtr.test.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by p_hongjcong on 2017/4/20.
 *
 */

public abstract class MyFragment extends Fragment {

    boolean isCreateView = false;//表示Fragment此时是否完成onCreateView
    boolean isVisible = false;//表示Fragment此时是否显示在前台，取决于getUserVisibleHint();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        isCreateView = true;
        if (isVisible && isCreateView){
            onVisable();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        isCreateView = false;
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            if (isVisible && isCreateView){
                onVisable();
            }
        } else {
            isVisible = false;
            onInVisable();
        }
    }

    public abstract void onVisable();
    public abstract void onInVisable();

}
