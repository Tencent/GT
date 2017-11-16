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
package com.tencent.wstt.gt.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.autotest.GTAutoTestInternal;

/**
 * 通过广播方式启动具体进程的性能采集，类似GT SDK的方式，主要用于自动化测试中关注性能参数的场景。
 * FPS支持的不好，因为目前Andorid手机的root权限需要用户手动确认，如果起FPS会卡UI
 * @author yoyoqin
 *
 */
public class BaseCommandReceiver extends BroadcastReceiver {
	
	public static final String TAG = "BaseCommandReceiver";
	
	// 采集数据
	public static final String ACTION_SAMPLE = "com.tencent.wstt.gt.baseCommand.sampleData";
	
	// 开始测试，需要参数应用名和进程ID
	public static final String ACTION_START_TEST = "com.tencent.wstt.gt.baseCommand.startTest";
	
	// 结束测试，需要参数应用名和进程ID
	public static final String ACTION_END_TEST = "com.tencent.wstt.gt.baseCommand.endTest";

	// 关闭GT
	public static final String ACTION_EXIT_GT = "com.tencent.wstt.gt.baseCommand.exitGT";

	public static final String INTENT_KEY_SAVE_FOLDER = "saveFolderName";
	public static final String INTENT_KEY_SAVE_DESC = "desc";
	
	public static final String INTENT_KEY_PNAME = "pkgName";
	public static final String INTENT_KEY_PVERNAME = "verName";
	public static final String INTENT_KEY_PID = "procId";

	private static int pid = -1; 
	private static String pkgName = null;
	private static String verName = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		try
		{
			String action = intent.getAction();
			if (null == action) {
				return;
			}
			if (action.equals(ACTION_START_TEST))
			{
				pkgName = intent.getStringExtra(INTENT_KEY_PNAME);
				verName = intent.getStringExtra(INTENT_KEY_PVERNAME);
				pid = intent.getIntExtra(INTENT_KEY_PID, -1);
				GTAutoTestInternal.startProcTest(pkgName, verName, pid);
			}
			else if (action.equals(ACTION_SAMPLE)) {
				
				// PSS的命令字
				int startMem = intent.getIntExtra(GTAutoTestInternal.INTENT_KEY_PSS, -1);
				if (startMem != -1 && pkgName != null)
				{
					if (startMem == 1)
					{
						GTAutoTestInternal.startSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_PSS);
					}
					else if (startMem == 0)
					{
						GTAutoTestInternal.stopSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_PSS);
					}
				}
				
				// PRI的命令字
				int startPri = intent.getIntExtra(GTAutoTestInternal.INTENT_KEY_PRI, -1);
				if (startPri != -1 && pkgName != null)
				{
					if (startPri == 1)
					{
						GTAutoTestInternal.startSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_PRI);
					}
					else if (startPri == 0)
					{
						GTAutoTestInternal.stopSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_PRI);
					}
				}
				
				// CPU命令字
				int startCpu = intent.getIntExtra(GTAutoTestInternal.INTENT_KEY_CPU, -1);
				if (startCpu != -1 && pkgName != null)
				{
					if (startCpu == 1)
					{
						GTAutoTestInternal.startSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_CPU);
					}
					else if (startCpu == 0)
					{
						GTAutoTestInternal.stopSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_CPU);
					}
				}
				
				// jiffies命令字
				int startJif = intent.getIntExtra(GTAutoTestInternal.INTENT_KEY_JIF, -1);
				if (startJif != -1 && pkgName != null)
				{
					if (startJif == 1)
					{
						GTAutoTestInternal.startSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_JIF);
					}
					else if (startJif == 0)
					{
						GTAutoTestInternal.stopSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_JIF);
					}
				}
				
				// NET命令字
				int startNet = intent.getIntExtra(GTAutoTestInternal.INTENT_KEY_NET, -1);
				if (startNet != -1 && pkgName != null)
				{
					if (startNet == 1)
					{
						GTAutoTestInternal.startSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_NET);
					}
					else if (startNet == 0)
					{
						GTAutoTestInternal.stopSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_NET);
					}
				}
				
				// 4.x以上的FPS
				int startFps = intent.getIntExtra(GTAutoTestInternal.INTENT_KEY_FPS, -1);
				if (startFps != -1)
				{
					if (startFps == 1)
					{
						GTAutoTestInternal.startSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_FPS);
					}
					else if (startFps == 0)
					{
						GTAutoTestInternal.stopSample(pkgName, pid, GTAutoTestInternal.INTENT_KEY_FPS);
					}
				}

				// 通知AUT页和出参页同步状态
				GTApp.getAUTHandler().sendEmptyMessage(0);
				GTApp.getOpHandler().sendEmptyMessage(5);
				GTApp.getOpEditHandler().sendEmptyMessage(0);
			}
			else if (action.equals(ACTION_END_TEST))
			{
				String folderName = intent.getStringExtra(INTENT_KEY_SAVE_FOLDER);
				String desc = intent.getStringExtra(INTENT_KEY_SAVE_DESC);
				// 当folderName==null时，会保存到上一次保存的目录，如果没有上一次的目录，会保存到默认目录GW_DATA下
				GTAutoTestInternal.endGlobalTest(folderName, desc, true);
			}
			else if (action.equals(ACTION_EXIT_GT))
			{
				/*
				 * 修复金刚系统检查出的漏洞：
				 * 在应用首次启动时动态加载功能代码等情况，可能会出现未完全加载类或某些前置环境未满足，
				 * 即接收到以上消息来启动它，导致classNotFoundException等异常，使得应用崩溃
				 */
				try
				{
					GTAutoTestInternal.exitGT();
				}
				catch(Exception e)
				{
					
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
