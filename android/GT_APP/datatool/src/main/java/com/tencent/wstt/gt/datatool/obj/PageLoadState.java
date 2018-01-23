package com.tencent.wstt.gt.datatool.obj;

/**
 * Created by p_hongjcong on 2017/7/31.
 * <p>
 * 代表页面切换的区间
 */

public class PageLoadState {

    public long time;
    public boolean isLoad;

    public PageLoadState() {
    }

    public PageLoadState(long time, boolean isLoad) {
        this.time = time;
        this.isLoad = isLoad;
    }


}
