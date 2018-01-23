#<font color=black>性能测试--布局检测</font>


###<font color=#436EEE>布局检测维度：</font>

**1.<u>View构建时长</u>：**View在使用之前需要进行Infalte操作，此操作在主线程执行且耗时严重，通常是造成卡顿的直接原因。

**2.<u>View绘制深度</u>：**View的绘制深度决定着当前视图的复杂度，复杂度越高，越容易引起卡顿。建议开发人员对复杂度高的视图进行优化



###<font color=#436EEE>一、View构建时长：</font>


**1.<u>实现原理</u>：**

View构建是通过调用Inflate函数实现的，setContentView的原理也是通过Inflate函数构建View，这里介绍一下最佳Hook节点：

		//hook的函数:
		hook android.view.LayoutInflater.inflate		:inflate
		hook android.app.Activity.setContentView		:setContentView

	    //举个例子：
	    @HookAnnotation(className = "android.view.LayoutInflater")
	    public View inflate(int resource, ViewGroup root) {
	        LogUtil.e(TAG,"LayoutInflater.inflate");
	        String resourceName="未知";
	        if (PDM.getContext()!=null){
	            try{
	                resourceName = PDM.getContext().getResources().getResourceName(resource);
	            }catch (Exception e){ }
	        }
	        long start = System.currentTimeMillis();
	        View  view = KHookManager.getInstance().callOriginalMethod("android.view.LayoutInflater.inflate", this, resource,root);
	        long end = System.currentTimeMillis();
	        ViewBuildCollector.onLayoutInflater_inflate(resourceName,start,end);
	        return view;
	    }

Hook数据的结果：

		07-03 15:53:47.731 1644-1644/? E/_PDM_HookList_viewbuild: Activity.setContentView
		07-03 15:53:47.741 1644-1644/? E/_PDM_HookList_viewbuild: LayoutInflater.inflate
		07-03 15:53:47.761 1644-1644/? E/_PDM_HookList_viewbuild: LayoutInflater.inflate

**2.<u>评判规则</u>：**
	
下图展示了View构建的数据，图上包含了当前页面的生命周期、卡顿信息、View构建等信息。当View构建时长超过30ms,则可认为此View构建超时：


<div  align="center">   
<img src="https://ooo.0o0.ooo/2017/07/03/5959f89c544e7.png" width = "80%" alt="图片名称" align=center />
</div>



###<font color=#436EEE>二、View绘制深度：</font>


**1.<u>实现原理</u>：**

View绘制深度相关的知识点在[《性能检测--流畅度检测》]()中提过，只需要hook ViewGroup.dispatchDraw方法，然后通过本人提出的递归压栈归类法即可计算View的绘制深度：

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

**2.<u>评判规则</u>：**


下图展示了Activity的绘制情况，图上包含了当前页面的生命周期、卡顿信息、View构建等信息。以下情况则可认为View绘制超时或View过于复杂：

1）当View绘制深度大于8则可认为view绘制过于复杂

2）当绘制时长超过10ms,则可认为此View绘制超时


<div  align="center">   
<img src="https://ooo.0o0.ooo/2017/07/03/5959fbf295c5b.png" width = "80%" alt="图片名称" align=center />
</div>
