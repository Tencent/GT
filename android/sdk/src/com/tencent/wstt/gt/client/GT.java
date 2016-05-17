/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 * 
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 * 
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tencent.wstt.gt.client;

import android.content.Context;
import android.os.Bundle;

import com.tencent.wstt.gt.client.internal.GTInternal;

/**
 * GT在被测APP中的接口类，提供给被测APP使用的所有接口都放在本类中作为静态方法提供。
 */
public class GT {
	// GT开关，调试版本可以打开该开关，发布版本请关闭该开关
	private static boolean ENABLE = true;
	
	/**
	 * 是否使用GT调试的开关
	 * @param flag
	 */
	public static void setEnable(boolean flag)
	{
		ENABLE = flag;
	}

	/**
	 * 获取当前GT调试的开关是否打开
	 */
	public static boolean isEnable()
	{
		return ENABLE;
	}

	/**
	 * 连接到GT控制台应用，如GT控制台未启动，则会被异步拉起。<p>
	 * 建议在被测App启动的Application或首个Activity的onCreat方法中调用。
	 * 该方法可以重复调用，以第一次调用为准。如放在首个Activity的onResume方法中调用。
	 * 示例：
	 * @par
	 * @code
	 * GT.connect(getApplicationContext(), new AbsGTParaLoader() {

				@Override
				public void loadInParas(InParaManager inPara) {
					// 定义入参：变量名、缩写名、入参默认值及备选值
					inPara.register("pkPlan", "PKPL","plan2","originalPlan", "plan1"); 
					inPara.register("超时时间", "RTO", "5", "2", "1","3");
					inPara.register("segmentSize", "SS", "2048", "1024");

					// 启动时默认放到GT控制台悬浮窗中展示的入参，如超过三个，只显示前三个
					inPara.defaultInParasInAC("pkPlan", "超时时间");
				}

				@Override
				public void loadOutParas(OutParaManager outPara) {
					// 定义出参：变量名、缩写名
					outPara.register("NetType", "NTPE");
					outPara.register("NetSpeed", "NSPD");
					outPara.register("SendFileSize", "SFS");
					outPara.register("发送成功率", "SSR");
					outPara.register("接收成功率", "RSR");

					// 启动时默认放到GT控制台悬浮窗中展示的出参，如超过三个，只显示前三个
					outPara.defaultOutParasInAC("发送成功率", "NetType", "SendFileSize");
				}
			});
	 * @endcode
	 * @param 
	 *     hostContext 被测应用的Context,因为GT模块会保持这个Context对象的引用，
	 *     所以为了避免内存泄漏，这个Context对象强烈建议使用应用的Application对象。
	 * @param
	 *     loader GT输入输出参数的注册器，需使用者自行实现，如何实现请参考上面示例
	 * @return 只有GT未安装会返回false
	 */
	public static boolean connect(Context hostContext, AbsGTParaLoader loader) {
		if (!ENABLE)
		{
			return false;
		}
		return GTInternal.INSTANCE.connect(hostContext, loader);
	}

	/**
	 * 连接到GT控制台应用，不注册任何的出参和入参
	 * @param hostContext
	 * @return
	 */
	public static boolean connect(Context hostContext) {
		if (!ENABLE)
		{
			return false;
		}
		return GTInternal.INSTANCE.connect(hostContext, new AbsGTParaLoader() {

			@Override
			public void loadInParas(InParaManager im) {

			}

			@Override
			public void loadOutParas(OutParaManager om) {

			}
		});
	}
	
	/**
	 * 与GT应用断连。建议在退出应用时调用。
	 * 
	 * @param hostContext
	 *            hostContext 被测应用的Context
	 */
	public static void disconnect(Context hostContext) {
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.disconnect(hostContext);
	}

	/**
	 * 设置耗时统计开关状态，主要用于耗时统计开关随GT启动开启的设置
	 * 该方法在connect方法之后调用，即可保证GT控制台启动时即开启耗时统计开关
	 * @param flag 耗时统计开关是否开启
	 * @since 1.1
	 */
	public static void setProfilerEnable(boolean flag)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.setProfilerEnable(flag);
	}
	
	/**
	 * 控制GT控制台开打或关闭悬浮框窗口的命令方法
	 * @param flag
	 * @since 1.1
	 */
	public static void setFloatViewFront(boolean flag)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.setFloatViewFront(flag);
	}
	
	/**
	 * 向GT控制台的插件发出命令，驱动GT的插件做事情。主要插件要设置为能接收并解析SDK发出的命令。
	 * @param receiver 插件标识
	 * @param bundle 封装了命令数据
	 * @since 1.1
	 */
	public static void setCommand(String receiver, Bundle bundle)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.setCommand(receiver, bundle);
	}
	//=======================================关于输出参数===========================
	/**
	 * 设置输出参数的值，更新的输出参数值会在GT的输出参数界面显示（若该参数被放到了悬浮窗中，则悬浮窗中也会显示）。<P>
	 * 注意使用该方法前，对应ParaName的参数要先在connect方法中注册。
	 * 具体请参考demo与使用说明。
	 * 
	 * @param paraName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值，支持任意类型，显示时都会被作为字符串处理
	 * @param extras
	 *            扩展参数，如果extras[0]是boolean型，则标明该参数是否是Global类型的
	 */
	public static void setOutPara(String paraName, Object value, Object...extras){
		if (!ENABLE)
		{
			return;
		}
		if(null != value){
			if (null != extras && extras.length > 0)
			{
				if (extras[0] instanceof Boolean)
				{
					boolean isGlobal = Boolean.TRUE.equals(extras[0]);
					GTInternal.INSTANCE.setOutPara(paraName, value.toString(), isGlobal);
				}
			}
			else
			{
				GTInternal.INSTANCE.setOutPara(paraName, value.toString(), false);
			}
		}
	}

	/**
	 * 同名方法的无扩展参数版，主要是为了规避java5编译器，无法正确处理Object...extras这种参数的bug
	 * java6编译器不需要此方法
	 */
	public static void setOutPara(String paraName, Object value){
		if (!ENABLE)
		{
			return;
		}
		if(null != value){
			GTInternal.INSTANCE.setOutPara(paraName, value.toString(), false);
		}
	}

	//=======================================关于输入参数===========================
	/**
	 * 设置输入参数的值，更新的输入参数值会在GT的输入参数界面显示（若该参数被放到了悬浮窗中，则悬浮窗中也会显示）。<P>
	 * 注意使用该方法前，对应paraName的参数要先在connect方法中加载。
	 * <p>
	 * (*)该方法不常用。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param newValue
	 *            输入参数的值，支持任意类型，内部会被转成字符串处理
	 * @param inlog
	 *            是否要在控制台打印
	 * @deprecated 该方法不能保证时序性，测试逻辑也不应改变原有被测逻辑的时序
	 * 
	 * @since 2.1 去掉是否在控制台打印日志的参数，因为以前版本中该参数的tag不好确定，沦为无用的鸡肋了
	 */
	public static void setInPara(String paraName, Object newValue, Object...extras) {
		if (!ENABLE)
		{
			return;
		}
		if(null != newValue){
			if (null != extras && extras.length > 0)
			{
				if (extras[0] instanceof Boolean)
				{
					boolean isGlobal = Boolean.TRUE.equals(extras[0]);
					GTInternal.INSTANCE.setInPara(paraName, newValue.toString(), isGlobal);
				}
			}
			else
			{
				GTInternal.INSTANCE.setInPara(paraName, newValue.toString(), false);
			}
		}
	}

	/**
	 * 设置输入参数的值，更新的输入参数值会在GT的输入参数界面显示（若该参数被放到了悬浮窗中，则悬浮窗中也会显示）。<P>
	 * 注意使用该方法前，对应paraName的参数要先在connect方法中加载。
	 * <p>
	 * (*)该方法不常用。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param newValue
	 *            输入参数的值，支持任意类型，内部会被转成字符串处理
	 * @deprecated 该方法不能保证时序性，测试逻辑也不应改变原有被测逻辑的时序
	 */
	public static void setInPara(String paraName, Object newValue) {
		if (!ENABLE)
		{
			return;
		}
		if(null != newValue){
			// 入参目前不支持注册成全局的，只会在一个APP内有效
			GTInternal.INSTANCE.setInPara(paraName, newValue.toString(), false);
		}
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是String的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static String getInPara(String paraName, String origVal){
		if (!ENABLE)
		{
			return "";
		}
		if(null != paraName && null != origVal){
			// 入参目前不支持注册成全局的，只会在一个APP内有效
			return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
		}
		return "";
	}


	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是boolean的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static boolean getInPara(String paraName, boolean origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是int的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static int getInPara(String paraName, int origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是double的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static double getInPara(String paraName, double origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是float的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static float getInPara(String paraName, float origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是long的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static long getInPara(String paraName, long origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是short的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static short getInPara(String paraName, short origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是byte的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static byte getInPara(String paraName, byte origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 获取GT中存储的该输入参数的值，输入参数类型是char的接口。
	 * <P>
	 * 对应paraName的参数要先在connect方法中加载。如果对应paraName的参数未在connect方法中加载，
	 * 或GT控制台未安装，或未正常启动，则直接返回origVal。
	 * 
	 * @param paraName
	 *            输入参数的名称
	 * @param origVal
	 *            此处建议使用原被测代码中的对应变量的原始值
	 * 
	 * @return 该入参的当前值
	 */
	public static char getInPara(String paraName, char origVal){
		if (!ENABLE)
		{
			return origVal;
		}
		return GTInternal.INSTANCE.getInPara(paraName, origVal, false);
	}

	/**
	 * 打印日志，INFO级别。
	 * 被打印的日志会在GT控制台的日志界面显示。
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public static void logI(String tag, String msg) {
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.logI(tag, msg);
	}
	
	/**
	 * 打印日志，DEBUG级别。
	 * 被打印的日志会在GT控制台的日志界面显示。
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public static void logD(String tag, String msg) {
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.logD(tag, msg);
	}
	
	/**
	 * 打印日志，WARNING级别。
	 * 被打印的日志会在GT控制台的日志界面显示。
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public static void logW( String tag, String msg) {
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.logW(tag, msg);
	}
	
	/**
	 * 打印日志，ERROR级别。
	 * 被打印的日志会在GT控制台的日志界面显示。
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public static void logE(String tag, String msg) {
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.logE(tag, msg);
	}
	
	/**
	 * 开始一次线程内耗时的统计。（要保证与endTimeInThread成对出现）。<P>
	 * 需要注意，如在递归体中使用，请联系GT开发咨询使用方法。
	 * 
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @param exkeys 额外的扩展匹配键数组，特殊用途
	 */
	public static void startTimeInThread(String group, String tag, int...exkeys)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.startTimeInThread(group, tag, exkeys);
	}
	
	/**
	 * 结束一次线程内耗时的统计。
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @param exkeys 额外的扩展匹配键数组，特殊用途
	 * @return 本次统计的差值，数值精度是纳秒。
	 */
	public static long endTimeInThread(String group, String tag, int...exkeys)
	{
		if (!ENABLE)
		{
			return -1;
		}
		return GTInternal.INSTANCE.endTimeInThread(group, tag, exkeys);
	}
	
	/**
	 * 开始一次进程内可跨线程的耗时统计（要保证与endTime成对出现）。
	 * <P>
	 * 需要注意，如在递归体中使用，请联系GT开发咨询使用方法。
	 * 
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @param exkeys 额外的扩展匹配键数组，特殊用途
	 */
	public static void startTime(String group, String tag, int...exkeys)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.startTime(group, tag, exkeys);
	}
	
	/**
	 * 结束一次进程内可跨线程耗时统计（结束计时）。
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @param exkeys 额外的扩展匹配键数组，特殊用途
	 * @return 本次统计的差值，数值精度是纳秒。
	 */
	public static long endTime(String group, String tag, int...exkeys)
	{
		if (!ENABLE)
		{
			return -1;
		}
		return GTInternal.INSTANCE.endTime(group, tag, exkeys);
	}
	
	/**
	 * 开始一次可跨进程的耗时统计（要保证与endTimeGlobal成对出现）。
	 * 
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @param exkeys 额外的扩展匹配键数组，特殊用途
	 * @since 1.1
	 */
	public static void startTimeGlobal(String group, String tag, int...exkeys)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.startTimeGlobal(group, tag, exkeys);
	}
	
	/**
	 * 结束一次可跨进程耗时统计（结束计时）。
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @param exkeys 额外的扩展匹配键数组，特殊用途
	 * @since 1.1
	 */
	public static void endTimeGlobal(String group, String tag, int...exkeys)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.endTimeGlobal(group, tag, exkeys);
	}
	
	/**
	 * @deprecated
	 * @return 连接的GT控制台版本,如未成功连接到控制台，会返回null
	 */
	public static String getGTConsoleVersion()
	{
		if (!ENABLE)
		{
			return null;
		}
		return GTInternal.INSTANCE.getGTConsoleVersion();
	}

	/**
	 * 设置当前的GT连接监听对象
	 * @param listener
	 * @since 1.2
	 */
	public static void setGTConnectedListener(GTConnectListener listener)
	{
		if (!ENABLE)
		{
			return;
		}
		GTInternal.INSTANCE.setGTConnectListener(listener);
	}
}
