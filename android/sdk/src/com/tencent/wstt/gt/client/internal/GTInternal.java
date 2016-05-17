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
package com.tencent.wstt.gt.client.internal;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.IService;
import com.tencent.wstt.gt.client.AbsGTParaLoader;
import com.tencent.wstt.gt.client.GT;
import com.tencent.wstt.gt.client.GTConnectListener;
import com.tencent.wstt.gt.client.communicate.ComImpl;
import com.tencent.wstt.gt.client.communicate.ICom;
import com.tencent.wstt.gt.client.internal.connect.ConnectedState;
import com.tencent.wstt.gt.client.internal.connect.ConnectingState;
import com.tencent.wstt.gt.client.internal.connect.DisconnectingState;
import com.tencent.wstt.gt.client.internal.connect.IConnState;
import com.tencent.wstt.gt.client.internal.connect.NotConnectedState;
import com.tencent.wstt.gt.client.internal.connect.NotInstalledState;
import com.tencent.wstt.gt.data.control.DataCacheController;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

/**
 * GT提供给宿主调用的总接口
 */
public class GTInternal {

	public static GTInternal INSTANCE = new GTInternal();

	public static GTInternal getInstance() {
		return INSTANCE;
	}

	private Context context;

	private IService gtService;

	private ICom gtCom;
	
	// 当前的数据缓存控制器，在Connecting和Connected两个状态才会调用其init方法
	// 实际使用可能要传service给它
	private DataCacheController dataCacheController;

	// 尚未连接到GT控制台
	IConnState CONNECT_STATE_NOT_CONNECTED;
	// 连接到GT控制台中
	IConnState CONNECT_STATE_CONNECTING;
	// 本次连接时GT控制台尚未安装
	IConnState CONNECT_STATE_NOT_INSTALLED;
	// 正在主动与GT控制台断连中
	IConnState CONNECT_STATE_DISCONNECTING;
	// 已连接到GT控制台
	IConnState CONNECT_STATE_CONNECTED;
	
	// 当前连接状态
	private IConnState curConnState;
	private GTConnectListener gTConnectListener;

	// 处理GT服务端应用状态通知的Handler
	private SplashHandler splashHandler;
	GTServiceConnection gtServiceConnection;

	private InParaManagerInternal inParaManager;
	private OutParaManagerInternal outParaManager;
	
	public static String GT_PACKAGE_NAME = "com.tencent.wstt.gt";
	public static final String ACTION = "com.tencent.wstt.gt.service";
	public static String smPara;
	
	public InParaManagerInternal getInParaManager()
	{
		return inParaManager;
	}
	
	public OutParaManagerInternal getOutParaManager()
	{
		return outParaManager;
	}
	
	private GTInternal() {
		inParaManager = new InParaManagerInternal();
		outParaManager = new OutParaManagerInternal();
		dataCacheController = new DataCacheController();
		CONNECT_STATE_NOT_CONNECTED = new NotConnectedState(dataCacheController);
		CONNECT_STATE_CONNECTING = new ConnectingState(dataCacheController);
		CONNECT_STATE_NOT_INSTALLED = new NotInstalledState(dataCacheController);
		CONNECT_STATE_DISCONNECTING = new DisconnectingState(dataCacheController);
		CONNECT_STATE_CONNECTED = new ConnectedState(dataCacheController);
		curConnState = CONNECT_STATE_NOT_CONNECTED;
	}

	void setGTService(IService gtService) {
		this.gtService = gtService;
	}
	
	public IService getGTService()
	{
		return gtService;
	}
	
	/**
	 * 当前是否可以尝试去连接GT
	 * @return
	 */
	public boolean isCanTryConnect()
	{
		if (curConnState == CONNECT_STATE_NOT_CONNECTED
				|| curConnState == CONNECT_STATE_NOT_INSTALLED)
		{
			return true;
		}
		return false;
	}

	/**
	 * 加载GT主应用程序
	 * 
	 * @param hostContext
	 *            宿主应用的Context
	 * @return 只有GT未安装会返回false
	 */
	public boolean connect(Context hostContext, AbsGTParaLoader loader) {
		if (hostContext == null) return false;
		Context app = hostContext.getApplicationContext();
		if (isCanTryConnect())
		{
			setHostContext(app);
			setConnState(CONNECT_STATE_CONNECTING);

			// 获取GT应用Context的一个镜像对象，用于判断GT应用是否已安装
			if (!isGTInstalled(app)) {
				// 如GT未安装，各模块采用空实现，清理connect过程的缓存
				setConnState(CONNECT_STATE_NOT_INSTALLED);
				return false;
			}
			// 初始化Handler，在Handler中绑定GT服务并拉起GT应用
			splashHandler = new SplashHandler(app);
			gtServiceConnection = new GTServiceConnection(splashHandler);
			
			outParaManager = new OutParaManagerInternal();
			inParaManager = new InParaManagerInternal();

			loader.loadInParas(inParaManager.getUserInterface());
			loader.loadOutParas(outParaManager.getUserInterface());

			// 可以将出入参放入aidl任务缓存队列了
			curConnState.registerInParas(inParaManager.getAndClearTempParas());
			curConnState.registerOutParas(outParaManager.getAndClearTempParas());

			Intent gtIntent = new Intent();
			gtIntent.setAction(GTInternal.ACTION);
			gtIntent.setPackage(GTInternal.GT_PACKAGE_NAME);

			Message msg = Message.obtain();
			msg.what = SplashHandler.MSG_START_CONNECT_GT;
			msg.obj = gtIntent;

			splashHandler.sendMessage(msg);
		}
		return true;
	}
	
	public void setHostContext(Context hostContext){
		context = hostContext;
	}
	
	public Context getHostContext(){
		return context;
	}
	
	/**
	 * 通过被测应用的Context查找GT主应用是否存在，
	 *  如果存在，则返回一个GT应用的Context对象，注意该Context并非GT应用中的对象，
	 * 只是GT应用Context的一个镜像
	 * 
	 * @param hostContext 被测应用的Context
	 * 
	 * @return GT应用是否已安装
	 */
	private boolean isGTInstalled(Context hostContext) {
		return getGTContext(hostContext) == null ? false : true;
	}
	
	private static Context getGTContext(Context hostContext)
	{
		try {
			Context context = hostContext.createPackageContext(
					GTInternal.GT_PACKAGE_NAME, Context.CONTEXT_INCLUDE_CODE
					| Context.CONTEXT_IGNORE_SECURITY);
			return context;
		} catch (NameNotFoundException e) {
			Log.d(GTInternal.GT_PACKAGE_NAME, "GT is uninstall.");
		}
		return null;
	}

	public void initComImpl() {
		gtCom = new ComImpl(gtService);
	}
	
	/**
	 * 当前是否可以尝试去连接GT
	 * @return
	 */
	public boolean isCanTryDisconnect()
	{
		if (curConnState == CONNECT_STATE_CONNECTED)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 与GT应用断连
	 * 
	 * @param hostContext
	 *            宿主应用的Context
	 */
	public void disconnect(Context hostContext) {
		if (isCanTryDisconnect())
		{
			setConnState(CONNECT_STATE_DISCONNECTING);
			boolean result = disconnectGT(hostContext.getPackageName());
			if(result){
				if (null != splashHandler)
				{
					splashHandler.sendEmptyMessage(SplashHandler.MSG_START_DISCONNECT_GT);
				}
			}
		}
	}
	
	public void applyDisconnect(Context hostContext){
		Intent intent = new Intent("com.tencent.wstt.gt.disconnected");
		intent.putExtra("param_clean_ParaList", false);
		intent.putExtra("param_remove_client", hostContext.getPackageName());
		context.sendBroadcast(intent);
		
		if(null != splashHandler){
			splashHandler.sendEmptyMessage(SplashHandler.MSG_START_DISCONNECT_GT);
		}
	}
	
	public int checkIsCanConnect(String cur_pkgName){
		return gtCom.checkIsCanConnect(cur_pkgName, GTConfig.INTERVAL_VID);
	}
	
	public void initConnectGT(String pkgName, int pid){
		gtCom.initConnectGT(pkgName, pid);
	}

	public boolean disconnectGT(String cur_pkgName){
		if(null != gtCom){
			return gtCom.disconnectGT(cur_pkgName);
		}else{
			return false;
		}	
	}

	/**
	 * 打印日志，INFO级别
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public void logI(String tag, String msg) {
		if (null != curConnState)
		{
			curConnState.logI(tag, msg);
		}
	}

	/**
	 * 打印日志，DEBUG级别
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public void logD(String tag, String msg) {
		if (null != curConnState)
		{
			curConnState.logD(tag, msg);
		}
	}
	
	/**
	 * 打印日志，WARNING级别
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public void logW( String tag, String msg) {
		if (null != curConnState)
		{
			curConnState.logW(tag, msg);
		}
	}
	
	/**
	 * 打印日志，ERROR级别
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public void logE(String tag, String msg) {
		if (null != curConnState)
		{
			curConnState.logE(tag, msg);
		}
	}

	/**
	 * 开始一次线程内耗时统计。
	 * @param group 统计分组
	 * @param tag 统计标签
	 */
	public void startTimeInThread(String group, String tag, int...exkeys)
	{
		if (null != curConnState)
		{
			curConnState.startTimeInThread(group, tag, exkeys);
		}
	}
	
	/**
	 * 结束一次线程内耗时统计。
	 * @param group 统计分组
	 * @param tag 统计标签
	 */
	public long endTimeInThread(String group, String tag, int...exkeys)
	{
		if (null != curConnState)
		{
			return curConnState.endTimeInThread(group, tag, exkeys);
		}
		return -1;
	}
	
	/**
	 * 开始一次全局耗时统计。不区分被测程序侧和控制台侧，所以可以进行跨应用统计
	 * @param group 统计分组
	 * @param tag 统计标签
	 */
	public void startTime(String group, String tag, int...exkeys)
	{
		if (null != curConnState)
		{
			curConnState.startTime(group, tag, exkeys);
		}
	}
	
	/**
	 * 结束一次全局耗时统计。
	 * @param group 统计分组
	 * @param tag 统计标签
	 */
	public long endTime(String group, String tag, int...exkeys)
	{
		if (null != curConnState)
		{
			return curConnState.endTime(group, tag, exkeys);
		}
		return -1;
	}
	
	public void startTimeGlobal(String group, String tag, int...exkeys)
	{
		if (null != curConnState)
		{
			curConnState.startTimeAcrossProcess(group, tag, exkeys);
		}
	}
	
	public void endTimeGlobal(String group, String tag, int...exkeys)
	{
		if (null != curConnState)
		{
			curConnState.endTimeAcrossProcess(group, tag, exkeys);
		}
	}

	/**
	 * 为当前线程设置未捕获异常的处理器
	 */
	public void setDefaultUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new GTUncaughtExceptionHandler());
	}
	
	//=======================================关于输出参数=======================================================
	public void setOutPara(String paraName, Object value, boolean isGlobal) {
		if (null == paraName || null == value)
		{
			return;
		}
		if(null != curConnState){
			curConnState.setOutPara(paraName, value, isGlobal);
		}
	}

	//=======================================关于输入参数=======================================================
	public void setInPara(String key, String newValue, boolean isGlobal) {
		if(null != curConnState){
			curConnState.setInPara(key, newValue, isGlobal);
		}
	}
	
	public String getInPara(String ParaName, String origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public boolean getInPara(String ParaName, boolean origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public int getInPara(String ParaName, int origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public double getInPara(String ParaName, double origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public float getInPara(String ParaName, float origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public long getInPara(String ParaName, long origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public short getInPara(String ParaName, short origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public byte getInPara(String ParaName, byte origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}
	
	public void setProfilerEnable(boolean flag)
	{
		curConnState.setProfilerEnable(flag);
	}
	
	public void setFloatViewFront(boolean flag)
	{
		curConnState.setFloatViewFront(flag);
	}
	
	public void setCommand(String receiver, Bundle bundle)
	{
		curConnState.setCommand(receiver, bundle);
	}
	
	public char getInPara(String ParaName, char origVal, boolean isGlobal) {
		return curConnState.getInPara(ParaName, origVal, isGlobal);
	}

	/*
	 * modify on 2015.3.5 状态切换加同步保护
	 */
	synchronized public void setConnState(IConnState state)
	{
		if (this.curConnState != null)
		{
			this.curConnState.finish();
		}
		Log.w("setConnState", "Pre State:" + this.curConnState.getClass().getName());
		
		state.init(this.curConnState);
		state.init(this.curConnState, gtService);// 只有ConnectedState方法需要
		this.curConnState = state;
		
		Log.w("setConnState", "Now State:" + this.curConnState.getClass().getName());
	}
	
	public String getGTConsoleVersion()
	{
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_GET_VERSION);
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
				return null;
			}
			
			String version = bundle.getString(Functions.GT_CMD_KEY_VERSION);
			return version;
		}
		return null;
	}
	
	/**
	 * 设置当前的GT连接监听对象
	 * @param listener
	 */
	public void setGTConnectListener(GTConnectListener listener)
	{
		this.gTConnectListener = listener;
	}
	
	/**
	 * 获取当前的GT连接监听对象
	 * @return
	 */
	public GTConnectListener getGTConnectListener()
	{
		return gTConnectListener;
	}
	
	/**
	 * 启动GT进行当前进程的数据采集，执行该命令前GT应已连接
	 */
	public void startProcTest(int pid, String pkgName)
	{
		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_START_PROCTEST);

			bundle.putString("pkgName", pkgName);
			bundle.putInt("pid", pid);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 停止GT对当前进程数据的采集，执行该命令前GT应已连接
	 * 
	 * @param saveFolderName
	 *            保存目录的名称，此目录会保存在/sdcard/GT/default/下，
	 *            如果目录名为null，默认的目录名是GW_DATA
	 */
	public void endProcTest(int pid, String saveFolderName)
	{
		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_END_PROCTEST);
			
			String pkgName = context.getPackageName();
			bundle.putString("saveFolderName", saveFolderName);
			bundle.putString("pkgName", pkgName);
			bundle.putInt("pid", pid);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止GT对当前进程数据的采集，并清理测试数据，执行该命令前GT应已连接
	 * 
	 * @param saveFolderName
	 *            保存目录的名称，此目录会保存在/sdcard/GT/default/下，
	 *            如果目录名为null，默认的目录名是GW_DATA，
	 *            每次保存后，GT会把缓存的本次测试数据清空。
	 */
	public void endTestAndClear(int pid, String saveFolderName)
	{
		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_END_TEST_AND_CLEAR);
			
			String pkgName = context.getPackageName();
			bundle.putString("saveFolderName", saveFolderName);
			bundle.putString("pkgName", pkgName);
			bundle.putInt("pid", pid);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 清理测试数据，执行该命令前必须先执行过endProcTest方法，否则清理动作无效
	 */
	public void clearTestDatas()
	{
		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_TEST_DATA_CLEAR);
			
			String pkgName = context.getPackageName();
			bundle.putString("pkgName", pkgName);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void startSample(int pid, String target)
	{
		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_START_SAMPLE);
			
			String pkgName = context.getPackageName();
			bundle.putString("target", target);
			bundle.putString("pkgName", pkgName);
			bundle.putInt("pid", pid);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopSample(int pid, String target)
	{
		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_STOP_SAMPLE);
			
			String pkgName = context.getPackageName();
			bundle.putString("target", target);
			bundle.putString("pkgName", pkgName);
			bundle.putInt("pid", pid);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void sample(int pid, String target)
	{
		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_SAMPLE);
			
			String pkgName = context.getPackageName();
			bundle.putString("target", target);
			bundle.putString("pkgName", pkgName);
			bundle.putInt("pid", pid);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPerfDataSampleRate(int ms) {
		Bundle bundle = new Bundle();
		bundle.putString(Functions.GT_COMMAND, "");
		bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_SET_SAMPLE_RATE);

		bundle.putInt("sampleRate", ms);
		
		curConnState.setCommand("", bundle);
	}

	/**
	 * 直接采集某一指标
	 * @param target 指标标识，要与控制台对应
	 * @since 2.2.6.3
	 */
	public static void sample(String target)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.sample(-1, target);
	}

	/**
	 * 开始耗时统计
	 * @since 2.2.6.3
	 */
	public void startTimeStatistics()
	{
		this.setProfilerEnable(true);
	}

	/**
	 * 暂停耗时统计
	 * @since 2.2.6.3
	 */
	public void stopTimeStatistics()
	{
		this.setProfilerEnable(false);
	}

	/**
	 * 结束耗时统计并保存
	 * @param filename 保存的文件名
	 */
	public void endTimeStatistics(String filename)
	{
		if (null == filename)
		{
			return;
		}

		// 不走状态，直接调同步命令的服务
		if (null != gtService)
		{
			Bundle bundle = new Bundle();
			bundle.putString(Functions.GT_COMMAND, "");
			bundle.putInt(Functions.GT_COMMAND_KEY, Functions.GT_CMD_END_ET_AND_CLEAR);
			bundle.putString("filename", filename);
			
			try {
				gtService.setCommondSync(bundle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
