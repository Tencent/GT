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
package com.tencent.wstt.gt.manager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.api.utils.ProcessUtils.ProcessInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * 被测APP管理类，主要维护当前被测APP的状态，及测试的指标，目前是初步重构中间状态
 */
public class AUTManager {

	public static String pkn = null; // AUT包名
	public static String apn = null; // AUT名称
	public static Drawable appic = null; // 被测应用图片
	
	// 各个属性的序号
	public static final int SEQ_CPU = 0;
	public static final int SEQ_JIF = 1;
	public static final int SEQ_NET = 2;
	public static final int SEQ_PSS = 3;
	public static final int SEQ_PD = 4;
	
	public static Hashtable<String, List<String>> registOpTable = new Hashtable<String, List<String>>();
	public static HashMap<String, String> proNameIdMap = new HashMap<String, String>();
	public static String appstatus = "--"; // 标示被测应用是否已启动，未running的应用可从gt启动
	public static ArrayList<String> proPidList = new ArrayList<String>();
	public static ArrayList<String> proNameList = new ArrayList<String>();
	public static String[] pIds; // 与proPidList同步，使用数组是为了频繁操作提高效率
	public static String[] pNames; // 与proNameList同步，使用数组是为了频繁操作提高效率

	public synchronized static void removeProcess(int pid) {
		int pos = proPidList.indexOf(Integer.toString(pid));
		proPidList.remove(pos);
		proNameList.remove(pos);
		String[] temp = {};
		pNames = proNameList.toArray(temp);
		pIds = proPidList.toArray(temp);
	}

	// 因为Android5以上的实现要比Android4上低200倍，故将本方法强制拆成两套逻辑
	public static void findProcess() {
		if (Env.API < 21)
		{
			findProcess4x();
		}
		else
		{
			findProcess5x();
		}
	}

	public static void findProcess5x() {
		boolean isRoot = ProcessUtils.initUidPkgCache();
		List<ProcessInfo> mRunningProcess = ProcessUtils.getAllRunningAppProcessInfo();

		for (ProcessInfo pi : mRunningProcess) {
			if (AUTManager.pkn == null)
			{
				break;
			}

			if (isRoot)
			{
				int uid = pi.uid;
				String pkgName = ProcessUtils.getPackageByUid(uid);
				if (pkgName != null && pkgName.equals(AUTManager.pkn)) {
					// 1.pname存在否
					if (!AUTManager.proNameList.contains(pi.name)) {
						// 把包名同名的进程固定作为0号进程
						if (pi.name.equals(AUTManager.pkn))
						{
							AUTManager.proNameList.add(0, pi.name);
							AUTManager.proPidList.add(0, String.valueOf(pi.pid));
						}
						else
						{
							AUTManager.proNameList.add(pi.name);
							AUTManager.proPidList.add(String.valueOf(pi.pid));
						}
						AUTManager.proNameIdMap.put(pi.name, String.valueOf(pi.pid));

					}
					else
					{
						// 2.对应的pid变化否
						String oldPid = AUTManager.proNameIdMap.get(pi.name);
						if (!oldPid.equals(String.valueOf(pi.pid)))
						{
							// 3.如果变化了，则把记录中的pid项替换成新的
							int index = proPidList.indexOf(oldPid);
							proPidList.remove(index);
							AUTManager.proPidList.add(index, String.valueOf(pi.pid));
							AUTManager.proNameIdMap.put(pi.name, String.valueOf(pi.pid));
						}
					}
				}
			}
			// 手机未root的替代方案，对于进程命名中不包括包名的没有办法
			else if (pi.name.contains(AUTManager.pkn))
			{
				// 1.pname存在否
				if (!AUTManager.proNameList.contains(pi.name)) {
					// 把包名同名的进程固定作为0号进程
					if (pi.name.equals(AUTManager.pkn))
					{
						AUTManager.proNameList.add(0, pi.name);
						AUTManager.proPidList.add(0, String.valueOf(pi.pid));
					}
					else
					{
						AUTManager.proNameList.add(pi.name);
						AUTManager.proPidList.add(String.valueOf(pi.pid));
					}
					AUTManager.proNameIdMap.put(pi.name, String.valueOf(pi.pid));
				}
				else
				{
					// 2.对应的pid变化否
					String oldPid = AUTManager.proNameIdMap.get(pi.name);
					if (!oldPid.equals(String.valueOf(pi.pid)))
					{
						// 3.如果变化了，则把记录中的pid项替换成新的
						int index = proPidList.indexOf(oldPid);
						proPidList.remove(index);
						AUTManager.proPidList.add(index, String.valueOf(pi.pid));
						AUTManager.proNameIdMap.put(pi.name, String.valueOf(pi.pid));
					}
				}
			}
		}
		if (AUTManager.proPidList.size() == 0) {
			AUTManager.pIds = null;
			AUTManager.pNames = null;
		} else {
			AUTManager.pIds = new String[AUTManager.proNameIdMap.size()];
			AUTManager.pNames = new String[AUTManager.proNameIdMap.size()];
			String[] temp = {};
			AUTManager.pNames = AUTManager.proNameList.toArray(temp);
			for (int i = 0; i < AUTManager.proNameIdMap.size(); i++) {
				AUTManager.pIds[i] = AUTManager.proNameIdMap.get(AUTManager.pNames[i]);
			}
		}
	}


	public static void findProcess4x() {

		ActivityManager mActivityManager = (ActivityManager) GTApp.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager
				.getRunningAppProcesses();

		for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
			String[] pkgNameList = amProcess.pkgList; // 获得运行在该进程里的所有应用程序包
			// 输出所有应用程序的包名
			for (int x = 0; x < pkgNameList.length; x++) {
				String pkgName = pkgNameList[x];
				if (AUTManager.pkn == null) {
					break;
				}
				if (pkgName.equals(AUTManager.pkn)) {
					if (!AUTManager.proPidList.contains(String.valueOf(amProcess.pid))) {
						AUTManager.proPidList.add(String.valueOf(amProcess.pid));
						AUTManager.proNameIdMap.put(amProcess.processName,
								String.valueOf(amProcess.pid));
					}
					if (!AUTManager.proNameList.contains(amProcess.processName)) {
						AUTManager.proNameList.add(amProcess.processName);
					}
				}
			}
		}
		if (AUTManager.proPidList.size() == 0) {
			AUTManager.pIds = null;
			AUTManager.pNames = null;
		} else {
			AUTManager.pIds = new String[AUTManager.proNameIdMap.size()];
			AUTManager.pNames = new String[AUTManager.proNameIdMap.size()];
			String[] temp = {};
			AUTManager.pNames = AUTManager.proNameList.toArray(temp);
			for (int i = 0; i < AUTManager.proNameIdMap.size(); i++) {
				AUTManager.pIds[i] = AUTManager.proNameIdMap.get(AUTManager.pNames[i]);
			}
		}
	}

	public static void openApp(String packageName) {
		PackageInfo pi = null;
		try {
			pi = GTApp.getContext().getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);
		PackageManager pManager = GTApp.getContext().getPackageManager();
		List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent,
				0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			String startappName = ri.activityInfo.packageName;
			String className = ri.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn = new ComponentName(startappName, className);

			intent.setComponent(cn);
			GTApp.getContext().startActivity(intent);
		}
	}
}
