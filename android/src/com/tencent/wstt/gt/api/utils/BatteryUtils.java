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
package com.tencent.wstt.gt.api.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryUtils {

	private int temperature;
	public String batteryHealth;
	
	public BatteryUtils(Context context){
		context.registerReceiver(new BatteryBroadcastReceiver(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		temperature = 0;
		batteryHealth = "未知状况";
	}
	
	public int getBatteryTemp(){
		return temperature;
	}
	
	public String getBatteryHealth(){
		return batteryHealth;
	}
	
	class BatteryBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
				temperature = intent.getIntExtra("temperature", 0);
			}
			switch(intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)){
			case BatteryManager.BATTERY_HEALTH_UNKNOWN:
				batteryHealth = "未知状况";
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:  
				batteryHealth = "状态良好";  
				break;  
			case BatteryManager.BATTERY_HEALTH_DEAD:  
				batteryHealth = "电池没有电";  
				break;  
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:  
				batteryHealth = "电池电压过高";  
				break;  
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:  
				batteryHealth =  "电池过热";  
				break;  
			}
		}
	};
}
