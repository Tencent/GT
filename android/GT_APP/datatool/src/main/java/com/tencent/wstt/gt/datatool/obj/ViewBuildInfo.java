package com.tencent.wstt.gt.datatool.obj;


/**
 * View创建信息（new View;  inflate;  setContentView）
 * Created by elvis on 2017/2/10.
 */

public class ViewBuildInfo {


    public static final String NEWVIEW = "newView";
    public static final String INFLATE = "inflate";
    public static final String SETCONTENTVIEW = "setContentView";

    //页面信息：
    public String method = "";//构建的方式（newView;  inflate;  setContentView）
    public String viewName = "";//resourceName 或 View的className
    public long startTime = 0;
    public long endTime = 0;


}
