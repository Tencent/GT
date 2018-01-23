package com.tencent.wstt.gt.analysis4.obj;

/**
 * Created by p_hongjcong on 2017/7/31.
 */

public class FrontBackState {

    public long time;
    public boolean isFront;

    public FrontBackState(){
    }

    public FrontBackState(long time,boolean isFront){
        this.time = time;
        this.isFront = isFront;
    }


}
