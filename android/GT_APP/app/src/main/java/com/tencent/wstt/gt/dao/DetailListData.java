package com.tencent.wstt.gt.dao;

/**
 * Created by p_hongjcong on 2017/8/28.
 */

public class DetailListData {

    public static final int Normal =0;
    public static final int Warning =1;
    public static final int Error =2;

    public String string;
    public int type;

    public DetailListData(String string,int type){
        this.string = string;
        this.type = type;
    }


}
