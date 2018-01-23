# <font color=black>页面启动时长检测原理及规则</font>

### <font color=#436EEE>一、Activity启动时长：</font>


Activity启动时长就是唤醒Activityy到Activity在前台进行第一次绘制的时间，配合“绘帧检测”中定位的掉帧区间，可以直观的展示卡顿问题。



**1.<u>实现原理</u>：**

1)<u>对Activity生命周期的监控</u>： Android 4.0以上的版本可以利用ActivityLifecycleCallbacks来实现对生命周期的监听，但此方法无法得到每个生命周期函数的执行时长。因此我们采用Hook的方式来监控Activity生命周期，这里介绍一下最佳Hook节点：
		
		所有Hook节点：
		hook android.app.Instrumentation.execStartActivity函数    	:startActivity函数
		hook android.app.Instrumentation.callActivityOnCreate函数  	:onCreate函数
		hook android.app.Instrumentation.callActivityOnStart函数		:onStart函数
		hook android.app.Instrumentation.callActivityOnResume函数		:onResume函数
		hook android.app.Instrumentation.callActivityOnPause函数		:onPause函数
		hook android.app.Instrumentation.callActivityOnStop函数		:onStop函数

		举个例子：
	    @HookAnnotation(className = "android.app.Instrumentation")
	    public void callActivityOnStart(Activity activity) {
	        LogUtil.e(TAG,"Instrumentation.callActivityOnStart");
	        long start = System.currentTimeMillis();
	        KHookManager.getInstance().callOriginalMethod("android.app.Instrumentation.callActivityOnStart", this, activity);
	        long end = System.currentTimeMillis();
	        ActivityCollector.onInstrumentation_callActivityOnStart(activity,start,end);//PageLoad模块
	    }
		

Hook数据的结果：
		
		07-03 10:39:39.922 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnCreate
		07-03 10:39:40.092 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnStart
		07-03 10:39:40.102 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnResume
		07-03 10:39:51.192 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.execStartActivity
		07-03 10:39:51.222 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnPause
		07-03 10:39:51.242 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnCreate
		07-03 10:39:51.652 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnStart
		07-03 10:39:51.652 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnResume
		07-03 10:39:52.042 28363-28363/com.utest.pdm.example E/_PDM_HookList_activity: Instrumentation.callActivityOnStop
		......

关于数据的整理：我们都知道，页面分为冷启动和热启动（页面从startActivity开始则是冷启动，如果从onStart或onResume开始，则是热启动），我们可以维护一个页面列表pageList,然后通过hashCode和生命周期函数的执行时间来归类数据，并可以对页面的冷热启动进行分析。


2)<u>对View绘制的监控</u>：

对View绘制的监控，只需要Hook ViewGroup的dispatchDraw方法即可。

	    @HookAnnotation(className = "android.view.ViewGroup")
	    protected void dispatchDraw(Canvas canvas) {
	        LogUtil.e(TAG,"ViewGroup.dispatchDraw");
	        Object view = this;
	        ViewDrawCollector.onViewGroup_dispatchDraw_before((View) view);//PageLoad模块
	        KHookManager.getInstance().callOriginalMethod("android.view.ViewGroup.dispatchDraw", this, canvas);
	        ViewDrawCollector.onViewGroup_dispatchDraw_after((View) view);//PageLoad模块
	    }

Hook数据的结果：

		07-03 10:39:59.002 28363-28363/com.utest.pdm.example E/_PDM_HookList_viewdraw: ViewGroup.dispatchDraw
		07-03 10:39:59.002 28363-28363/com.utest.pdm.example E/_PDM_HookList_viewdraw: ViewGroup.dispatchDraw
		07-03 10:39:59.002 28363-28363/com.utest.pdm.example E/_PDM_HookList_viewdraw: ViewGroup.dispatchDraw
		07-03 10:39:59.002 28363-28363/com.utest.pdm.example E/_PDM_HookList_viewdraw: ViewGroup.dispatchDraw
		07-03 10:39:59.002 28363-28363/com.utest.pdm.example E/_PDM_HookList_viewdraw: ViewGroup.dispatchDraw
		......

首先是对View绘制数据是杂乱无章的，由于ViewGroup的执行是递归的，所以我们发明了一种递归压栈归类法（将当前绘制节点进行压栈和弹栈操作），而且通过最大栈深可以得知View的绘制深度：

	    private static int stackSize = 0;//dispatchDraw的栈深，每当栈弹光，说明一次绘制完成
		public static DrawInfo drawInfo;//代表一次绘制对象

	    public static synchronized void onViewGroup_dispatchDraw_before(View view) {
			//绘制开始--创建对象
	        if (stackSize == 0) {
	            drawInfo = new DrawInfo();
			}
			//将draw数据保存在drawInfo中
			//...省略n行.... 
	        stackSize++;
			if(stackSize>drawInfo.drawDeep){
	            drawInfo.drawDeep = stackSize;//保存最大绘制深度
	        }
	    }

	    public static synchronized void onViewGroup_dispatchDraw_after(View view) {
	        stackSize--;
			//绘制栈弹光，则说明当前进行了一次完整的递归绘制，保存drawInfo数据
	        if (stackSize == 0 && drawInfo != null) {
	            drawInfo.drawEnd = System.currentTimeMillis();
	            for (CallBack callBack : callBackArrayList){
	                callBack.onDrawFinish(drawInfo);
	            }
	        }
	    }

其次是将viewDraw信息匹配给Activity：

		//原理：前面说过，将页面对象数据存成一个pageList,当一次绘制完成后，我们先检查此绘制是否为前
		//一个页面的绘制信息，是则将此绘制数据add到之前页面对象中，否则该绘制信息是新页面的绘制信息。
        public void onDrawFinish(DrawInfo drawInfo) {
            synchronized (lock){
                //检查此绘制是否为前一个页面的绘制信息
                if (pageList.size()>=2 && pageList.get(pageList.size()-2).isMyDraw(drawInfo)){
                    pageList.get(pageList.size()-2).addDrawInfo(drawInfo);
                    LogUtil.e("Draw","绘制赋值:"+drawInfo.drawClassName +" -> "+pageList.get(pageList.size()-2).activityClassName);
                }else if(pageList.size()>=1){
                    pageList.get(pageList.size()-1).addDrawInfo(drawInfo);
                    LogUtil.e("Draw","绘制赋值:"+drawInfo.drawClassName +" -> "+ pageList.get(pageList.size()-1).activityClassName);
                }
            }
        }
		//判别是否属于前一个页面的绘制信息：判断此View的hashCode是否在前一个页面显示的时候绘制过。
	    public boolean isMyDraw(DrawInfo drawInfo){
	        for (DrawInfo d:drawInfoList){
	            if (d.objectHashCode.equals(drawInfo.objectHashCode)){
	                return true;
	            }
	        }
	        return false;
	    }





**2.<u>评判规则</u>：**

下图展示每个页面详细的启动数据，包含了Activity生命周期、绘制信息、卡顿信息、页面平均流畅值，启动时长。页面启动时长就是唤醒Activity到Activity在前台进行第一次绘制的时间。以下三种情况则可认为页面启动卡顿或启动超时：

1）启动时长超过250ms

2）页面1秒内卡顿超过300ms

3）页面5秒内卡顿超过500ms

<div  align="center">   
<img src="https://ooo.0o0.ooo/2017/07/03/5959bb5a322c5.png" width = "80%" alt="图片名称" align=center />
</div>





### <font color=#436EEE>二、Fragment启动时长：</font>

Fragment启动时长就是唤醒Fragment到Fragment执行onResume的完成时间。

**1.<u>实现原理</u>：**

1)<u>对Fragment生命周期的监控</u>： 我们同样采用Hook的方式来监控Fragment生命周期，这里介绍一下最佳Hook节点：


		//android.app.Fragment包:
		hook android.app.Fragment.onAttach					:onAttach
		hook android.app.Fragment.performCreate				:onCreate
		hook android.app.Fragment.performCreateView			:onCreateView
		hook android.app.Fragment.performActivityCreated	:onActivityCreated
		hook android.app.Fragment.performStart				:onStart
		hook android.app.Fragment.performResume				:onResume
		hook android.app.Fragment.performPause				:onPause
		hook android.app.Fragment.performStop				:onStop
		hook android.app.Fragment.performDestroyView		:onDestoryView
		hook android.app.Fragment.performDestroy			:onDestory
		hook android.app.Fragment.performDetach				:onDetach
		hook android.app.Fragment.onHiddenChanged			:onHiddenChanged
		hook android.app.Fragment.setUserVisibleHint		:setUserVisibleHint
	
	
		//android.support.v4.app.Fragment包：
		hook android.support.v4.app.Fragment.onAttach					:onAttach
		hook android.support.v4.app.Fragment.performCreate				:onCreate
		hook android.support.v4.app.Fragment.performCreateView			:onCreateView
		hook android.support.v4.app.Fragment.performActivityCreated		:onActivityCreated
		hook android.support.v4.app.Fragment.performStart				:onStart
		hook android.support.v4.app.Fragment.performResume				:onResume
		hook android.support.v4.app.Fragment.performPause				:onPause
		hook android.support.v4.app.Fragment.performStop				:onStop
		hook android.support.v4.app.Fragment.performDestroyView			:onDestoryView
		hook android.support.v4.app.Fragment.performDestroy				:onDestory
		hook android.support.v4.app.Fragment.performDetach				:onDetach
		hook android.support.v4.app.Fragment.onHiddenChanged			:onHiddenChanged
		hook android.support.v4.app.Fragment.setUserVisibleHint			:setUserVisibleHint

		//举个例子：	
		@HookAnnotation(className = "android.app.Fragment")
		void performCreate(Bundle savedInstanceState) {
	        LogUtil.e(TAG,"performCreate");
	        long start = System.currentTimeMillis();
	        KHookManager.getInstance().callOriginalMethod("android.app.Fragment.performCreate", this, savedInstanceState);
	        long end = System.currentTimeMillis();
	        String activityClassName = "";
	        String activityHashCode = "";
	        String fragmentClassName = "";
	        String fragmentHashCode = "";
	        Object fragment = this;
	        if (fragment instanceof android.app.Fragment){
	            fragmentClassName =  ((android.app.Fragment)fragment).getClass().getName();
	            fragmentHashCode = ""+this.hashCode();
	            Activity activity = ((android.app.Fragment) fragment).getActivity();
	            if (activity!=null){
	                activityClassName  = activity.getClass().getName();
	                activityHashCode = ""+activity.hashCode();
	            }
	        }
	        FragmentCollector.onFragment_performCreate(activityClassName, activityHashCode,fragmentClassName,fragmentHashCode,start,end);//Fragment模块
	    }

Hook数据的结果：

		07-03 15:21:02.831 22305-22305/com.utest.pdm.example E/_PDM_PDMHookList_Fragment: onAttach
		07-03 15:21:02.831 22305-22305/com.utest.pdm.example E/_PDM_PDMHookList_Fragment: performCreate
		07-03 15:21:02.831 22305-22305/com.utest.pdm.example E/_PDM_PDMHookList_Fragment: performCreateView
		07-03 15:21:02.841 22305-22305/com.utest.pdm.example E/_PDM_PDMHookList_Fragment: performActivityCreated
		07-03 15:21:02.841 22305-22305/com.utest.pdm.example E/_PDM_PDMHookList_Fragment: performStart
		07-03 15:21:02.841 22305-22305/com.utest.pdm.example E/_PDM_PDMHookList_Fragment: performResume



**2.<u>评判规则</u>：**

下图展示每个页面详细的启动数据，包含了Fragment生命周期、卡顿信息、页面平均流畅值，启动时长Fragment启动时长就是唤醒Fragment到Fragment执行onResume完成的时间。启动时长超过150ms则可认为页面启动卡顿或启动超时：


<div  align="center">   
<img src="https://ooo.0o0.ooo/2017/07/03/5959f169dff6f.png" width = "80%" alt="图片名称" align=center />
</div>



