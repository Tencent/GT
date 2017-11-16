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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.content.Intent;

public class SMServiceHelper {
	private static SMServiceHelper INSTANCE;
	
	BlockingQueue<Integer> dataQueue = new LinkedBlockingQueue<Integer>();
	
	// 观察者们，包括UI和自动化模块
	private List<SMPluginListener> listeners;
	public synchronized void addListener(SMPluginListener listener)
	{
		listeners.add(listener);
	}

	public synchronized void removeListener(SMPluginListener listener)
	{
		listeners.remove(listener);
	}

	boolean start = false;

	boolean isStarted() {
		return start;
	}

	void setStarted(boolean isStarted) {
		start = isStarted;
	}

	private SMServiceHelper()
	{
		listeners = new ArrayList<SMPluginListener>();
	}

	public static SMServiceHelper getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new SMServiceHelper();
		}
		return INSTANCE;
	}

	synchronized void stopBackgroundServiceIfRunning(Context context) {

		if (isStarted())
		{
			context.stopService(new Intent(context, SMLogService.class));
			context.stopService(new Intent(context, SMDataService.class));
			setStarted(false);
			for (SMPluginListener listener : listeners)
			{
				listener.onSMStop();
			}
		}
	}

	synchronized void startBackgroundService(Context context, Integer pid, String pkgName) {
		if (! isStarted())
		{
			setStarted(true);
			
			Intent intent = new Intent(context, SMLogService.class);
			intent.putExtra("pid", pid.toString());
			intent.putExtra("pkgName", pkgName);
			context.startService(intent);
			
			Intent intent2 = new Intent(context, SMDataService.class);
			intent2.putExtra("pid", pid.toString());
			intent2.putExtra("pkgName", pkgName);
			context.startService(intent2);
			
			for (SMPluginListener listener : listeners)
			{
				listener.onSMStart();
			}
		}
	}
}
