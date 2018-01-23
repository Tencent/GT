package com.tencent.wstt.gt.analysis4.obj;

import java.util.ArrayList;


/**
 * 页面加载类:一次页面的打开显示
 * Created by elvis on 2017/2/10.
 */

public class PageLoadInfo   {

    //页面信息：
    public String activityClassName = "";//activity类名（包括包名）
    public String objectHashCode = "";//对象hashCode，页面的唯一标识。
    public int startOrderId = 0;//当前是第几个页面
    //生命周期信息：
    public ArrayList<LifecycleMethod> lifecycleMethodList = new ArrayList<>();//生命周期列表（从onCreate/onResume ----> 第一次onStop）
    //绘制信息：
    public int drawNumber = 0;;//已绘制次数
    public ArrayList<DrawInfo> drawInfoList = new ArrayList<>();;//绘制信息列表



    /**
     * 构造函数
     */
    public PageLoadInfo(){
    }
    public PageLoadInfo(String activityClassName,String objectHashCode){
        this.activityClassName = activityClassName;
        this.objectHashCode = objectHashCode;
    }


    /**  生命周期相关：------------------------------------------------------------------ **/
    public void addExecStartInfo(long start,long end){
        LifecycleMethod lifecycleMethod = new LifecycleMethod();
        lifecycleMethod.methodName = LifecycleMethod.EXECSTART;
        lifecycleMethod.methodStartTime = start;
        lifecycleMethod.methodEndTime = end;
        lifecycleMethodList.add(lifecycleMethod);
    }
    public void addCreateInfo(long start,long end){
        LifecycleMethod lifecycleMethod = new LifecycleMethod();
        lifecycleMethod.methodName = LifecycleMethod.ONCREATE;
        lifecycleMethod.methodStartTime = start;
        lifecycleMethod.methodEndTime = end;
        lifecycleMethodList.add(lifecycleMethod);
    }
    public void addStartInfo(long start, long end){
        LifecycleMethod lifecycleMethod = new LifecycleMethod();
        lifecycleMethod.methodName = LifecycleMethod.ONSTART;
        lifecycleMethod.methodStartTime = start;
        lifecycleMethod.methodEndTime = end;
        lifecycleMethodList.add(lifecycleMethod);
    }
    public void addResumeInfo(long start,long end){
        //tab会导致Activity执行了onCreate和onResume但不执行onStart方法，
        //所以如果onStart方法与onResume方法间隔超过300ms,将先清空之前的函数数据。
        long lastTime = 0;
        for (LifecycleMethod temp : lifecycleMethodList){
            if (temp.methodEndTime>lastTime){
                lastTime = temp.methodEndTime;
            }
        }
        if (lastTime!=0 && start-lastTime>300){
            lifecycleMethodList.clear();
        }
        LifecycleMethod lifecycleMethod = new LifecycleMethod();
        lifecycleMethod.methodName = LifecycleMethod.ONRESUME;
        lifecycleMethod.methodStartTime = start;
        lifecycleMethod.methodEndTime = end;
        lifecycleMethodList.add(lifecycleMethod);
    }
    public void addPauseInfo(long start,long end){
        LifecycleMethod lifecycleMethod = new LifecycleMethod();
        lifecycleMethod.methodName = LifecycleMethod.ONPAUSE;
        lifecycleMethod.methodStartTime = start;
        lifecycleMethod.methodEndTime = end;
        lifecycleMethodList.add(lifecycleMethod);
    }
    public void addStopInfo(long start,long end){
        LifecycleMethod lifecycleMethod = new LifecycleMethod();
        lifecycleMethod.methodName = LifecycleMethod.ONSTOP;
        lifecycleMethod.methodStartTime = start;
        lifecycleMethod.methodEndTime = end;
        lifecycleMethodList.add(lifecycleMethod);
    }

    public boolean isCold() {
        for(int i=0;i<lifecycleMethodList.size();i++){
            LifecycleMethod lifecycleMethod  = lifecycleMethodList.get(i);
            if (lifecycleMethod.methodName.equals(LifecycleMethod.ONCREATE)){
                return true;
            }
        }
        return false;
    }


    /**  绘制相关：------------------------------------------------------------------ **/
    //为本次加载添加绘制历史
    public void addDrawInfo(DrawInfo drawInfo){
        drawNumber++;
        drawInfo.drawOrderId = drawNumber;
        drawInfoList.add(drawInfo);
    }
    //判别是否属于本次加载的绘制
    public boolean isMyDraw(DrawInfo drawInfo){
        for (DrawInfo d:drawInfoList){
            if (d.objectHashCode.equals(drawInfo.objectHashCode)){
                return true;
            }
        }
        return false;
    }



}
