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
package com.tencent.wstt.gt.plugin.smtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.tencent.wstt.gt.api.utils.ProcessUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * SM新版临时支持自动化的广播
 */
public class SMBroadcast extends BroadcastReceiver {
	public static final String ACTION_START_TEST = "com.tencent.wstt.gt.plugin.sm.startTest";
	public static final String ACTION_END_TEST = "com.tencent.wstt.gt.plugin.sm.endTest";
	public static final String ACTION_CHECK = "com.tencent.wstt.gt.plugin.sm.check";
	public static final String ACTION_MODIFY = "com.tencent.wstt.gt.plugin.sm.modify";
	public static final String ACTION_RESUME = "com.tencent.wstt.gt.plugin.sm.resume";
	public static final String ACTION_RESTART = "com.tencent.wstt.gt.plugin.sm.restart";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (null == action) {
			return;
		}
		if (action.equals(ACTION_CHECK)) {
			String cmd = "getprop debug.choreographer.skipwarning";
			ProcessBuilder execBuilder = new ProcessBuilder("sh", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				Process p = execBuilder.start();
				InputStream is = p.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					if (line.compareTo("1") == 0) {
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (action.equals(ACTION_MODIFY)) {
			String cmd = "setprop debug.choreographer.skipwarning 1";
			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				execBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (action.equals(ACTION_RESUME)) {
			String cmd = "setprop debug.choreographer.skipwarning 30";
			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				execBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (action.equals(ACTION_RESTART)) {
			String cmd = "setprop ctl.restart surfaceflinger; setprop ctl.restart zygote";
			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				execBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (action.equals(ACTION_START_TEST)) {
			String procName = intent.getStringExtra("procName");
			int pid = -1;
			if (procName != null)
			{
				pid = ProcessUtils.getProcessPID(procName);
			}
			if (-1 == pid || null == procName) {
				return;
			}
//			SMServiceHelper.stopBackgroundServiceIfRunning(context);
			SMServiceHelper.getInstance().startBackgroundService(context, pid, procName);
		} else if (action.equals(ACTION_END_TEST)) {
			SMServiceHelper.getInstance().stopBackgroundServiceIfRunning(context);
		}
	}
}
