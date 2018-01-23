package com.tencent.wstt.gt.datatool.obj;

/**
 * 视图的一次完整绘制（包含了子视图的绘制）
 * Created by elvis on 2017/1/22.
 */

public class DrawInfo {


    //绘制属性
    public String drawClassName = "";//当前绘制类名（包含包名）
    public String objectHashCode = "";//当前绘制的对象名（包含包名和对象地址）
    public long drawBegin = 0;
    public long drawEnd = 0;
    public int drawDeep = 0;
    public String drawPath = "";
    //Activty相关
    public int drawOrderId = 0;//当前是页面的第几次绘制


}
