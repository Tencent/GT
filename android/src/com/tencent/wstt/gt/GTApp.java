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
package com.tencent.wstt.gt;

import java.lang.ref.WeakReference;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tencent.wstt.gt.activity.GTEntrance;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.NetUtils;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.api.utils.SignalUtils;
import com.tencent.wstt.gt.engine.DefaultParaRunEngine;
import com.tencent.wstt.gt.engine.ProcPerfParaRunEngine;
import com.tencent.wstt.gt.internal.DaemonHandler;
import com.tencent.wstt.gt.internal.GTDaemonThreadManager;
import com.tencent.wstt.gt.internal.GTMemoryDaemonThread;
import com.tencent.wstt.gt.log.GTLogInternal;
import com.tencent.wstt.gt.manager.ClientFactory;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.SingleInstanceClientFactory;
import com.tencent.wstt.gt.plugin.PluginManager;
import com.tencent.wstt.gt.plugin.battery.BatteryPluginItem;
import com.tencent.wstt.gt.plugin.gps.GTGPSPluginItem;
import com.tencent.wstt.gt.plugin.gps.GTGPSReplayEngine;
import com.tencent.wstt.gt.plugin.internal.PluginService;
import com.tencent.wstt.gt.plugin.memfill.GTMemFillPluginItem;
import com.tencent.wstt.gt.plugin.screenlock.ScreenlockPluginItem;
import com.tencent.wstt.gt.plugin.smtools.SMToolsPluginItem;
import com.tencent.wstt.gt.plugin.tcpdump.TcpdumpPluginItem;
import com.tencent.wstt.gt.utils.GTUtils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class GTApp extends Application {
	private static Context mContext;
	private static boolean isAppRunned = false;
	public static DaemonHandler daemonHandler;

	static Handler emptyHandler = new Handler();

	// 是否在GT的UI中
	private static boolean isInGT = false;

	public static boolean isInGT() {
		return isInGT;
	}

	public static void setInGT(boolean inGT) {
		isInGT = inGT;
	}

	public GTApp() {
		super();
		if (null == daemonHandler) {
			daemonHandler = new DaemonHandler();
		}
		if (!GTDaemonThreadManager.getInstance().contains(GTMemoryDaemonThread.key)) {
			GTDaemonThreadManager.getInstance().put(GTMemoryDaemonThread.key, new GTMemoryDaemonThread());
			GTDaemonThreadManager.getInstance().start(GTMemoryDaemonThread.key);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();

		Env.init();

		// 设置主线程的未捕获异常记录
//		GTUtils.setGTUncaughtExceptionHandler();
		CrashReport.initCrashReport(mContext, "900010910", false);

		// 初始化适配Android4.x和5.x以上的ProcessUtils
		ProcessUtils.init();
		// App启动的时候，把该拷贝的资源文件拷贝了
		propareResourceFile();
		// 信号值获取的模块需要在这里初始化
		SignalUtils.init();
		// 网络流量在这里初始化，从GT开始启动时统计
		NetUtils.initNetValue();
		// 在GT启动时，把默认提供的出参注册了
		registerGTDefaultOutParas();

		// 初始化全局客户端
		ClientFactory cf = new SingleInstanceClientFactory();
		cf.orderClient(ClientManager.GLOBAL_CLIENT, ClientManager.GLOBAL_CLIENT.hashCode(), null, null);

		// 加载插件，这句要在初始化全局客户端之后执行
		loadPlugins();

		loadEnvInfo();

		// 使用MAT平台进行统计上报
		// 第二个参数是null，标识读取manifest里配置的appKey
		// 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
		StatConfig.setDebugEnable(false); // 不打印MTA类日志
		// 是否WIFI网络下实时上报，如果是false，则在GT第二次启动上报
		StatConfig.setEnableSmartReporting(true);
		try {
			StatService.startStatService(this, null, com.tencent.stat.common.StatConstants.VERSION);
		} catch (MtaSDkException e) {
			// MTA初始化失败
			Log.e("gt_mta", "MTA start failed.");
		}
	}

	private void loadEnvInfo() {
		Env.CUR_APP_VER = DeviceUtils.getSDKVersion(); // Android版本名
	}

	private void loadPlugins() {
		PluginManager pm = PluginManager.getInstance();
		// 启动PluginService
		Intent intent = new Intent(this, PluginService.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startService(intent);

		// 插件管理器初始化
		pm.register(new BatteryPluginItem());
		pm.register(new TcpdumpPluginItem());
		pm.register(new ScreenlockPluginItem());
		pm.register(new GTMemFillPluginItem());
		pm.register(new SMToolsPluginItem());
		pm.register(new GTGPSPluginItem());
	}

	public static Context getContext() {
		return mContext;
	}

	public static void setContext(Context context) {
		mContext = context;
	}

	public static void setGTRunStatus(boolean runned) {
		isAppRunned = runned;
	}

	public static boolean getGTRunStatus() {
		return isAppRunned;
	}

	private void propareResourceFile() {
		/*
		 * 作为内置应用时， 第一次运行要拷贝so到data/data/com.tencent.wstt.gt/files下并加载
		 */
//		GTUtils.copySoToDest(mContext);

		GTUtils.copyTcpdump(mContext);
		GTUtils.copyalarm(mContext);
	}

	/**
	 * 注册GT提供的默认输出参数
	 */
	private void registerGTDefaultOutParas() {

		DefaultParaRunEngine mDefaultParaRunEngine = new DefaultParaRunEngine();
		mDefaultParaRunEngine.start();
		ProcPerfParaRunEngine ppEngine = ProcPerfParaRunEngine.getInstance();
		ppEngine.start();
	}

	/**
	 * 关闭GT开启的service, 关闭GT应用
	 */
	public static void exitGT() {
		// 将缓存中的日志信息保存到文件
		GTLogInternal.endAllLog();

		// 如果有在模拟位置，要即时清理状态
		GTGPSReplayEngine.getInstance().stopMockLocation();

		// 这个一定要放在最后，因为里面有杀进程的动作
		GTEntrance.GTclose(mContext);
	}

	// 控制AUT界面展示状态的Handler
	private static WeakReference<Handler> autHandler;

	public static void setAUTHandler(Handler handler) {
		autHandler = new WeakReference<Handler>(handler);
	}

	public static Handler getAUTHandler() {
		Handler result = null;
		if (autHandler != null) {
			result = autHandler.get();
		}

		if (result == null) {
			result = emptyHandler;
		}
		return result;
	}

	// 控制出参界面展示状态的Handler
	private static WeakReference<Handler> opHandler;

	public static void setOpHandler(Handler handler) {
		opHandler = new WeakReference<Handler>(handler);
	}

	public static Handler getOpHandler() {
		Handler result = null;
		if (opHandler != null) {
			result = opHandler.get();
		}

		if (result == null) {
			result = emptyHandler;
		}
		return result;
	}

	// 控制出参编辑界面展示状态的Handler
	private static WeakReference<Handler> opEditHandler;

	public static void setOpEditHandler(Handler handler) {
		opEditHandler = new WeakReference<Handler>(handler);
	}

	public static Handler getOpEditHandler() {
		Handler result = null;
		if (opEditHandler != null) {
			result = opEditHandler.get();
		}

		if (result == null) {
			result = emptyHandler;
		}
		return result;
	}

	// 控制入参界面展示状态的Handler
	private static WeakReference<Handler> ipHandler;

	public static void setIpHandler(Handler handler) {
		ipHandler = new WeakReference<Handler>(handler);
	}

	public static Handler getIpHandler() {
		Handler result = null;
		if (ipHandler != null) {
			result = ipHandler.get();
		}

		if (result == null) {
			result = emptyHandler;
		}
		return result;
	}

	// 控制入参编辑界面展示状态的Handler
	private static WeakReference<Handler> ipEditHandler;

	public static void setIpEditHandler(Handler handler) {
		ipEditHandler = new WeakReference<Handler>(handler);
	}

	public static Handler getIpEditHandler() {
		Handler result = null;
		if (ipEditHandler != null) {
			result = ipEditHandler.get();
		}

		if (result == null) {
			result = emptyHandler;
		}
		return result;
	}
}
