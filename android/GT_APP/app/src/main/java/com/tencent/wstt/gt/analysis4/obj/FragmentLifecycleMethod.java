package com.tencent.wstt.gt.analysis4.obj;

/**
 *
 * Created by elvis on 2017/2/16.
 */

public class FragmentLifecycleMethod  {


    public static final String ONATTACH  = "onAttach";
    public static final String ONCREATE = "onCreate";
    public static final String ONCREATEVIEW = "onCreateView";
    public static final String ONACTIVITYCREATED = "onActivityCreated";
    public static final String ONSTART = "onStart";
    public static final String ONRESUME = "onResume";
    public static final String ONPAUSE = "onPause";
    public static final String ONSTOP = "onStop";
    public static final String ONDESTROYVIEW = "onDestroyView";
    public static final String ONDESTROY = "onDestroy";
    public static final String ONDETACH = "onDetach";

    public String methodName;
    public long methodStartTime;
    public long methodEndTime;
	

}
