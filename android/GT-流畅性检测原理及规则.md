
# <font color=black>流畅度检测</font>


### <font color=#436EEE>流畅性检测维度：</font>
流畅性检测：简单的来讲就是APP的流畅程度，可以用来衡量的维度有：<u>流畅度检测</u>、<u>页面启动时长</u>、<u>Fragment启动时长</u>，这里主要说流畅度检测。


 Android系统在流畅的情况下绘帧速度是60帧/s(即：16.7ms一帧)。当绘帧间隔超过一定时长，我们就可以说此时掉帧了，也就会造成用户直接感官的卡顿。此模块可以统计一秒内绘帧次数（即：流畅度SM），并对丢帧的原因进行代码定位。
当然，有人会提出，通过检测主线程消息处理时长来定位当前的卡顿问题，此方法可以细致的定位引起卡顿的Message，但它只能定位消息的处理时长，不能定位主线程里的其他耗时操作，并且经过大量实验验证，此方法并不通用


### <font color=#436EEE>一、流畅度检测：</font>

**1.<u>实现原理</u>：**

首先Android的帧绘制流程是：CPU主线程图像处理->GPU进行光栅化->显示帧。APP产生掉帧的情况大多是由“CPU主线程图像处理”这一步超负载引起的，所以我们思考如何去监控主线程绘制情况。要检测CPU绘制帧的时间，就必须找到那个调用View.dispatchDraw的类，Choreographer类就是那个接受系统垂直同步信号，在每次接受时同步信号触发View的Input、Animation、Draw等操作。

所以我们可以向Choreographer类中加入自己的Callback来冒充View的Callback,通过此Callback我们可以获得View绘制的时间、可以统计一秒内帧绘制的能力（后面把此值称作“流畅值SM”，它能直观的代表当前时间段的流畅度）。之所以不用FPS来代表当前流畅度，是因为Android系统默认在前台页面静止时，FPS可能为0，FPS低无法直接代表当前处于卡顿。


>相关资料--渲染流程：[《深入Android渲染机制》](http://blog.csdn.net/ccj659/article/details/53219288)

>相关资料--垂直同步机制和双缓冲区：[《Android 4.4 Graphic系统详解（2） VSYNC的生成》](http://blog.csdn.net/michaelcao1980/article/details/43233765)

>相关资料--Choreographer源码分析：[Android之控制同步处理输入(Input)、动画(Animation)、绘制(Draw)三个UI操作的Choreographer源码分析](http://blog.csdn.net/zhangyongfeiyong/article/details/53556478)

第一步，代码实现：

		
	    long lastTime = 0;
	    long thisTime = 0;
	    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	    void startMonitor(){
	        Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {//系统绘帧回调
	            public void doFrame(long frameTimeNanos) {
	
	                thisTime = System.currentTimeMillis();
	                //累计流畅值
	                plusSM(thisTime);//当前秒的SM+1，如果当前秒数已经到下一秒，则将此SM值写入文件
	                //判别超时：
	                if (thisTime - lastTime > 40 && lastTime!=0){
	                    Log.e("DDDD","frame超时"+(thisTime - lastTime)+"ms: "+ lastTime +"-"+ thisTime);
	                    //saveBlockInfo(lastTime, thisTime);//此处保存卡顿信息
	                }
	                //设置下一帧绘制的回调
	                Choreographer.getInstance().postFrameCallback(this);//设置下次系统绘帧回调
	                lastTime = thisTime;
	            }
	        };
	        Choreographer.getInstance().postFrameCallback(frameCallback);
	    }
	
	    //保存当前SM值
	    private long nowTime =1;//当前的时间（ms）
	    private int sm = 1;
	    private void plusSM(long t){
	        if (nowTime ==1){
	            nowTime = t;
	        }
	        if (nowTime/1000 == t/1000){
	            sm++;
	        }else if (t/1000 - nowTime/1000 >=1){
	            //saveSMInfo(sm,t);//此处保存此时的流畅值SM
	            Log.e("DDDD","sm："+sm);
	            sm=1;
	            nowTime = t;
	        }
	    }


第二步，运行结果，我们可以在消息执行的过程中进行拉栈操作，可用于耗时代码定位：
		
		07-02 22:32:51.721 15931-15931/com.utest.elvistestapplication E/DDDD: frame超时113ms: 1499005971618-1499005971731
		07-02 22:32:51.781 15931-15931/com.utest.elvistestapplication E/DDDD: frame超时55ms: 1499005971731-1499005971786
		07-02 22:32:52.001 15931-15931/com.utest.elvistestapplication E/DDDD: sm：17
		07-02 22:32:53.011 15931-15931/com.utest.elvistestapplication E/DDDD: sm：60
		07-02 22:32:53.991 15931-15931/com.utest.elvistestapplication E/DDDD: sm：59
		07-02 22:32:54.991 15931-15931/com.utest.elvistestapplication E/DDDD: sm：60
		07-02 22:32:56.001 15931-15931/com.utest.elvistestapplication E/DDDD: sm：60
		......

**2.<u>评判规则</u>：**

可以从两个方向来分析数据：

1）<u>单次大卡顿</u>：当两次绘帧间隔大于70ms，相当于丢了4帧以上，建议开发人员对耗时的代码进行异步或拆分。

2）<u>低流畅值区间</u>：流畅值低于40帧/s的区间，导致低流畅值区间出现的原因有两类：“单次大卡顿”“连续小卡顿”，建议开发人员针对不同的场景进行优化。

下图是对流畅值的统计结果：


<div  align="center">   
<img src="https://ooo.0o0.ooo/2017/07/02/595908ff9f7b8.png" width = "80%" alt="图片名称" align=center />
</div>


下图是对卡顿区间的代码定位：

<div  align="center">   
<img src="https://ooo.0o0.ooo/2017/07/02/595908ff9fef8.png" width = "80%"  alt="图片名称" align=center />
</div>
