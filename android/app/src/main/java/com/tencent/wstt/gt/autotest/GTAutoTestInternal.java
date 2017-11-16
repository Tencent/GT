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
package com.tencent.wstt.gt.autotest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.tencent.stat.StatService;
import com.tencent.wstt.gt.AidlEntry;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.activity.GTAUTFragment;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.engine.ProcPerfParaRunEngine;
import com.tencent.wstt.gt.internal.GTMemoryDaemonHelper;
import com.tencent.wstt.gt.log.GTGWInternal;
import com.tencent.wstt.gt.log.GTTimeInternal;
import com.tencent.wstt.gt.log.GWSaveEntry;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientFactory;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.SingleInstanceClientFactory;
import com.tencent.wstt.gt.utils.CommonString;
import com.tencent.wstt.gt.utils.FileUtil;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class GTAutoTestInternal {
	
	private static Map<ProOutParaQueryEntry, List<OutPara>> tempMap =
			new HashMap<ProOutParaQueryEntry, List<OutPara>>();

	public static final String INTENT_KEY_CPU = "cpu";
	public static final String INTENT_KEY_JIF = "jif";
	public static final String INTENT_KEY_PSS = "pss";
	public static final String INTENT_KEY_PRI = "pri";
	public static final String INTENT_KEY_NET = "net";
	public static final String INTENT_KEY_FPS = "fps"; // FPS弹su窗口卡住，所以建议用TowerRoot这样不弹框的

	/**
	 * 启动不指定被测应用的数据采集
	 */
	public static void startGlobalTest()
	{
		// 清理旧记录
		tempMap.clear();

		OpUIManager.gw_running = true; // 开启数据收集

		// 通知AUT页同步状态
		GTApp.getAUTHandler().sendEmptyMessage(0);
	}

	/**
	 * 启动指定应用的指定进程的数据采集
	 * @param pkgName 指定的被测应用
	 * @param pid 被测应用指定的被测进程，目前不使用，会同时采集应用的所有进程，请置-1
	 */
	public static void startProcTest(String pkgName, String verName, int pid)
	{
		ProcessUtils.initUidPkgCache();

		// 清理旧记录
		tempMap.clear();
		
		if (ClientManager.getInstance().getAUTClient() != null)
		{
			AUTManager.proNameIdMap.clear();
			
			AUTManager.proNameList.clear();
			AUTManager.proPidList.clear();
			// 清除旧的AUT_CLIENT
			ClientManager.getInstance().getAUTClient().clear();
			ClientManager.getInstance().removeClient(ClientManager.AUT_CLIENT);
		}

		// 新生的AUTClient
		ClientFactory cf = new SingleInstanceClientFactory();
		cf.orderClient(
				ClientManager.AUT_CLIENT, ClientManager.AUT_CLIENT.hashCode(), null, null);
		
		OpUIManager.gw_running = true; // 开启数据收集
		
		if (! ProcPerfParaRunEngine.getInstance().isStarted())
		{
			ProcPerfParaRunEngine.getInstance().start();
		}

		AUTManager.appstatus = "running";

		Env.CUR_APP_VER = verName == null ? "unknow" : verName;
		// 同之后的操作点击应用列表的操作
		if (pkgName != null)
		{
			AUTManager.pkn= pkgName;
			Env.CUR_APP_NAME = pkgName;
			PackageInfo pi = null;
			try {
				pi = GTApp.getContext().getPackageManager().getPackageInfo(pkgName, 0);
				AUTManager.apn = pi.applicationInfo.loadLabel(
						GTApp.getContext().getPackageManager()).toString();
				AUTManager.appic = pi.applicationInfo
						.loadIcon(GTApp.getContext().getPackageManager());

				// MTA记录选中的AUT
				Properties prop = new Properties();
				prop.setProperty("pkgName", AUTManager.pkn);
				StatService.trackCustomKVEvent(GTApp.getContext(), "Connected AUT", prop);

				// 复杂的过程，修改需谨慎
				AUTManager.findProcess();
			}
			catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		// 通知AUT页同步状态
		GTApp.getAUTHandler().sendEmptyMessage(0);
	}

	/**
	 * 停止所有数据采集
	 * @param saveFolderName 采集的被测数据保存目录名 sdcard/GT/GW/default/{saveFolderName}
	 */
	public static void endGlobalTest(String path, String desc, boolean clear)
	{
		endProcTest(null, -1, path, desc, clear);
	}

	/**
	 * 停止指定被测应用的数据采集
	 * @param pkgName 指定的被测应用，目前不使用
	 * @param pid 被测应用指定的被测进程，目前不使用，会同时采集应用的所有进程
	 * @param saveFolderName 采集的被测数据保存目录名 sdcard/GT/GW/default/{saveFolderName}
	 * @param clear 是否清理历史记录
	 */
	public static void endProcTest(String pkgName, int pid, String path, String desc, boolean clear)
	{
		// 结束统计，避免保存时候有风险
		OpUIManager.gw_running = false;

		// 保存之前要把关注的出参置为可保存的状态，因为保存前有些出参可能置为不记录状态
		for (List<OutPara> ops : tempMap.values())
		{
			if (ops != null)
			{
				for (OutPara op : ops)
				{
					op.setMonitor(true); // 只有monitor状态下才会保存，和手工操作一样
				}
			}
		}

		// 保存
		String path1 = null;
		String path2 = null;
		String path3 = null;

		if (path != null && ! path.isEmpty())
		{
			String[] paths = path.split(FileUtil.separator);
			if (paths.length > 2)
			{
				path1 = paths[paths.length - 3]; // 倒数第三级目录
				path2 = paths[paths.length - 2]; // 倒数第二级目录
				path3 = paths[paths.length - 1]; // 最后一级目录
			}
			else if (paths.length == 2)
			{
				path2 = paths[paths.length - 2]; // 倒数第二级目录
				path3 = paths[paths.length - 1]; // 最后一级目录
			}
			else
			{
				path3 = paths[0];
			}
		}

		if (null == path1 || path1.equals(""))
		{
			path1 = Env.CUR_APP_NAME;
		}
		if (null == path2 || path2.equals(""))
		{
			path2 = Env.CUR_APP_VER;
		}
		if (null == path3 || path3.equals(""))
		{
			path3 = GTGWInternal.getLastSaveFolder();
		}

		GWSaveEntry saveEntry = new GWSaveEntry(path1, path2, path3, desc);
		
		GTGWInternal.saveAllEnableGWData(saveEntry);

		if (clear)
		{
			// 1.清空老数据
			GTGWInternal.clearAllEnableGWData();
		}

		// 2.保存后要把所有出参置为非monitor状态
		for (List<OutPara> ops : tempMap.values())
		{
			if (ops != null)
			{
				for (OutPara op : ops)
				{
					op.setMonitor(false); // 只有monitor状态下才会保存，和手工操作一样
				}
			}
		}

		if (clear)
		{
			// 3.清理旧记录
			tempMap.clear();
		}

		// 通知AUT页同步状态
		GTApp.getAUTHandler().sendEmptyMessage(0);
	}
	
	/**
	 * 清理测试当前的测试数据，请保证先停止被测应用的数据采集，否则操作不会被执行
	 */
	public static void clearDatas()
	{
		if (OpUIManager.gw_running == true) return;
		
		// 清空老数据
		GTGWInternal.clearAllEnableGWData();
		
		// 清理旧记录
		tempMap.clear();
		
		// 通知AUT页同步状态
		GTApp.getAUTHandler().sendEmptyMessage(0);
	}

	/**
	 * 采集指定的指标
	 * @param pkgName 指定的被测应用
	 * @param pid 被测应用指定的被测进程，当pid为-1时，会采集应用所有进程的指定性能指标
	 * @param target 指定的采集指标，包括：
	 * <p>GTAutoTestInternal.INTENT_KEY_PSS</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_PRI</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_NET</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_JIF</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_CPU</p>
	 */
	public static void startSample(String pkgName, int pid, String target)
	{
		Client client = ClientManager.getInstance().getAUTClient();
		if (client == null)
		{
			return;
		}
		
		List<OutPara> opList = new ArrayList<OutPara>();

		if (target.equals(INTENT_KEY_PSS))
		{
			opList = GTAUTFragment.registerOutpara(AUTManager.SEQ_PSS, pid);
		}
		else if  (target.equals(INTENT_KEY_NET))
		{
			opList = GTAUTFragment.registerOutpara(AUTManager.SEQ_NET, -1);
		}
		else if (target.equals(INTENT_KEY_FPS))
		{
			// FPS从属的Client是DefaultClient
			OutPara opFps = ClientManager.getInstance().getDefaultClient().getOutPara(CommonString.FPS_key);
			// 将FPS挪到已关注出参中
			if (AidlEntry.DISPLAY_DISABLE == opFps.getDisplayProperty())
			{
				opFps.setMonitor(true);
				opFps.setDisplayProperty(AidlEntry.DISPLAY_NORMAL);
				OpUIManager.setItemToNormal(opFps);
			}
			// 如果FPS非观察状态，重置为观察状态
			if (! opFps.isMonitor())
			{
				opFps.setMonitor(true);
			}
			OpPerfBridge.registMonitor(opFps);
			opList.add(opFps);
		}
		else if (target.equals(INTENT_KEY_JIF))
		{
			opList = GTAUTFragment.registerOutpara(AUTManager.SEQ_JIF, pid);
		}
		else if (target.equals(INTENT_KEY_CPU))
		{
			opList = GTAUTFragment.registerOutpara(AUTManager.SEQ_CPU, pid);
		}
		else if (target.equals(INTENT_KEY_PRI))
		{
			opList = GTAUTFragment.registerOutpara(AUTManager.SEQ_PD, pid);
		}
		
		tempMap.put(new ProOutParaQueryEntry(pkgName, pid, target), opList);
		// 主动刷新出参页面的列表
		GTApp.getOpHandler().sendEmptyMessage(5);
		GTApp.getOpEditHandler().sendEmptyMessage(0);
	}

	/**
	 * 停止采集指定的指标
	 * @param pkgName 指定的被测应用
	 * @param pid 被测应用指定的被测进程号
	 * @param target 指定的采集指标，包括：
	 * <p>GTAutoTestInternal.INTENT_KEY_PSS</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_PRI</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_NET</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_JIF</p>
	 * <p>GTAutoTestInternal.INTENT_KEY_CPU</p>
	 */
	public static void stopSample(String pkgName, int pid, String target)
	{
		List<OutPara> ops = tempMap.get(new ProOutParaQueryEntry(pkgName, pid, target));
		if (null != ops)
		{
			for (OutPara op : ops)
			{
				op.setMonitor(false);
			}
		}
	}

	/**
	 * TODO 即时采集指定的当前指标，尚未支持
	 * @param pkgName
	 * @param pid
	 * @param target
	 */
	public static void sample(String pkgName, int pid, String target)
	{
		
	}

	/**
	 * 开始耗时统计
	 */
	public static void startTimeStatistics()
	{
		// UI需要隐藏save、delete、start，显示end
		if (!GTTimeInternal.isETStarted())
		{
			// 如果想开启，需要先校验
			if (!GTMemoryDaemonHelper.startGWOrProfValid())
			{
				return;
			}
			
			// TODO 页面UI最好进行同步

			// 这个属性需要交给控制器去做业务逻辑相关处理
			GTTimeInternal.setETStarted(true);
		}
	}

	/**
	 * 暂停耗时统计
	 */
	public static void stopTimeStatistics()
	{
		if (GTTimeInternal.isETStarted())
		{
			GTTimeInternal.setETStarted(false);
		}
	}

	/**
	 * 结束耗时统计并保存
	 * @param filename 保存的文件名
	 */
	public static void endTimeStatistics(String filename)
	{
		stopTimeStatistics();
		GTTimeInternal.saveTimeLog(filename);
		GTTimeInternal.cleartimeInfo();
	}

	public static void exitGT()
	{
		GTApp.exitGT();
	}
}
