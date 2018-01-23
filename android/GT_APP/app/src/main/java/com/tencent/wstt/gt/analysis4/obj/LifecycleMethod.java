package com.tencent.wstt.gt.analysis4.obj;

/**
 *
 * Created by elvis on 2017/2/16.
 */

public class LifecycleMethod  {


    public static final String EXECSTART = "execStart";
    public static final String ONCREATE = "onCreate";
    public static final String ONSTART = "onStart";
    public static final String ONRESUME = "onResume";
    public static final String ONPAUSE = "onPause";
    public static final String ONSTOP = "onStop";

    public String methodName;
    public long methodStartTime;
    public long methodEndTime;
	
    
    
    
}
