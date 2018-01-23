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
package com.tencent.wstt.gt.plugin.gps;

import com.tencent.wstt.gt.plugin.PluginManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GPSMockBroadcast extends BroadcastReceiver {
	public static final String GPS_START_RECORD = "com.tencent.wstt.gt.plugin.gps.startRecord";
	public static final String GPS_END_RECORD = "com.tencent.wstt.gt.plugin.gps.endRecord";
	public static final String GPS_START_REPLAY = "com.tencent.wstt.gt.plugin.gps.startReplay";
	public static final String GPS_END_REPLAY = "com.tencent.wstt.gt.plugin.gps.endReplay";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			if (action == null) return;
			if (action.equals(GPS_START_REPLAY)) {
				PluginManager.getInstance().getPluginControler(
						).startService(GTGPSReplayEngine.getInstance(), intent);
			}
			else if (action.equals(GPS_END_REPLAY)) {
				PluginManager.getInstance().getPluginControler(
						).stopService(GTGPSReplayEngine.getInstance());
			}
			else if (action.equals(GPS_START_RECORD)) {
				PluginManager.getInstance().getPluginControler(
						).startService(GTGPSRecordEngine.getInstance(), intent);
			}
			else if (action.equals(GPS_END_RECORD)) {
				PluginManager.getInstance().getPluginControler(
						).stopService(GTGPSRecordEngine.getInstance());
			}
		}
		catch (Exception e)
		{
			Log.e("GT", "error on GPSMockBroadcast.onReceive()...");
		}
	}
}
