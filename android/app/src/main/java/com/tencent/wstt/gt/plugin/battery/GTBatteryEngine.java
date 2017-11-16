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
package com.tencent.wstt.gt.plugin.battery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.base.GTLog;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.plugin.PluginTaskExecutor;
import com.tencent.wstt.gt.utils.BrightnessUtils;
import com.tencent.wstt.gt.utils.FileUtil;

import android.os.Bundle;

public class GTBatteryEngine implements PluginTaskExecutor {
	// 唯一实例
	private static GTBatteryEngine INSTANCE;

	// 插件参数的管理使用全局客户端
	private Client globalClient;
	private static Timer timer;
	private static final String LOG_TAG = "Battery";
	
	// 观察者们，包括UI和自动化模块
	private List<BatteryPluginListener> listeners;
		
	// 插件没有业务模块，各CheckBox状态保存在静态变量里
	private boolean state_cb_I = true; // 默认电流开关是开的
	private boolean state_cb_U = false;
	private boolean state_cb_P = false;
	private boolean state_cb_T = false;

	// 保存当前的电池参数值
	private String I = ""; // 电流
	private String U = ""; // 电压
	private String POW = ""; // 电量
	private String TEMP = ""; // 温度
	private int INT_TEMP = -273; // 上一次采集的温度的值

	public static final String OPI = "Current"; // 电流出参key
	public static final String OPU = "Volt"; // 电压出参key
	public static final String OPPow = "Power"; // 电量出参key
	public static final String OPTemp = "Temperature"; // 温度出参key

	// 持久化key
	private static final String KEY_I = "battery_I";
	private static final String KEY_U = "battery_U";
	private static final String KEY_Pow = "battery_Pow";
	private static final String KEY_Temp = "battery_Temp";
	
	private long startBattry = -1; // 记录上一次电池变化的时间
	private String lastBatteryChangeTime = "";

	// 刷新频率
	private int refreshRate = 250; // 单位毫秒
		
	// 亮度，0-255
	private int brightness = -1;
	
	// 是否已经开始监控flag
	private boolean isStarted = false;
	
	private GTBatteryEngine()
	{
		globalClient = ClientManager.getInstance().getGlobalClient();
		listeners = new ArrayList<BatteryPluginListener>();
		
		state_cb_I = GTPref.getGTPref().getBoolean(KEY_I, true); // 默认关注电流值
		state_cb_U = GTPref.getGTPref().getBoolean(KEY_U, false);
		state_cb_P = GTPref.getGTPref().getBoolean(KEY_Pow, false);
		state_cb_T = GTPref.getGTPref().getBoolean(KEY_Temp, false);
	}

	public static GTBatteryEngine getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new GTBatteryEngine();
		}
		return INSTANCE;
	}
	
	public boolean isStarted()
	{
		return isStarted;
	}

	@Override
	public void execute(Bundle bundle) {
		String cmd = bundle.getString("cmd");
		if (cmd != null && cmd.equals("start")) {
			doStart(250, 100);
		} else if (cmd != null && cmd.equals("stop")) {
			doStop();
		}
	}
	
	public void doStart(int in_refreshRate, int in_brightness)
	{
		if (isStarted()) return;

		try
		{
			if (state_cb_I)
			{
				globalClient.registerOutPara(OPI, "I");
				globalClient.setOutparaMonitor(OPI, true);
				OpPerfBridge.startProfier(
						globalClient.getOutPara(OPI), Functions.PERF_DIGITAL_NORMAL, "", "mA");
			}
			if (state_cb_U)
			{
				globalClient.registerOutPara(OPU, "U");
				globalClient.setOutparaMonitor(OPU, true);
				OpPerfBridge.startProfier(
						globalClient.getOutPara(OPU), Functions.PERF_DIGITAL_NORMAL, "", "mV");
			}
			if (state_cb_P)
			{
				globalClient.registerOutPara(OPPow, "POW");
				globalClient.setOutparaMonitor(OPPow, true);
				OpPerfBridge.startProfier(
						globalClient.getOutPara(OPPow), Functions.PERF_DIGITAL_NORMAL, "", "%");
			}
			if (state_cb_T)
			{
				globalClient.registerOutPara(OPTemp, "TEMP");
				globalClient.setOutparaMonitor(OPTemp, true);
				OpPerfBridge.startProfier(
						globalClient.getOutPara(OPTemp), Functions.PERF_DIGITAL_NORMAL, "", "℃");
			}

			if (in_refreshRate < 100)
			{
				for (BatteryPluginListener listener : listeners)
				{
					listener.onBatteryException(GTApp.getContext(
							).getString(R.string.pi_battery_sample_tip2));
				}
				
				return;
			}
			else
			{
				this.refreshRate = in_refreshRate;
			}

			timer = new Timer(true);
			timer.schedule(new ReadPowerTimerTask(), refreshRate, refreshRate);
			
			// 处理亮度
			if (in_brightness >= 0 && in_brightness <= 255)
			{
				setBrightness(in_brightness);
				BrightnessUtils.setManualMode();
				BrightnessUtils.setScreenBrightness(in_brightness);
				BrightnessUtils.saveBrightness(in_brightness);
			}

			isStarted = true;
			
			for (BatteryPluginListener listener : listeners)
			{
				listener.onBatteryStart();
			}
		}
		catch (Exception e)
		{
			for (BatteryPluginListener listener : listeners)
			{
				listener.onBatteryException(
						GTApp.getContext().getString(R.string.pi_battery_sample_tip));
			}
		}
	}

	public void doStop()
	{
		if (! isStarted()) return;
		
		if (timer != null)
		{
			timer.cancel();
			timer.purge();
			timer = null;
		}
		isStarted = false;
		
		for (BatteryPluginListener listener : listeners)
		{
			listener.onBatteryStop();
		}
	}

	public boolean isState_cb_I() {
		return state_cb_I;
	}

	public boolean isState_cb_U() {
		return state_cb_U;
	}

	public boolean isState_cb_P() {
		return state_cb_P;
	}

	public boolean isState_cb_T() {
		return state_cb_T;
	}
	
	public void updateI(boolean isChecked)
	{
		if (isChecked)
		{
			globalClient.registerOutPara(GTBatteryEngine.OPI, "I");
			globalClient.setOutparaMonitor(GTBatteryEngine.OPI, true);
		}
		else
		{
			globalClient.unregisterOutPara(GTBatteryEngine.OPI);
		}
		state_cb_I = isChecked;
		GTPref.getGTPref().edit().putBoolean(GTBatteryEngine.KEY_I, isChecked).commit();

		for (BatteryPluginListener listener : listeners)
		{
			listener.onUpdateI(isChecked);
		}
	}

	public void updateU(boolean isChecked)
	{
		if (isChecked)
		{
			globalClient.registerOutPara(OPU, "U");
			globalClient.setOutparaMonitor(GTBatteryEngine.OPU, true);
		}
		else
		{
			globalClient.unregisterOutPara(OPU);
		}
		state_cb_U = isChecked;
		GTPref.getGTPref().edit().putBoolean(KEY_U, isChecked).commit();

		for (BatteryPluginListener listener : listeners)
		{
			listener.onUpdateU(isChecked);
		}
	}

	public void updateP(boolean isChecked)
	{
		if (isChecked)
		{
			globalClient.registerOutPara(OPPow, "POW");
			globalClient.setOutparaMonitor(GTBatteryEngine.OPPow, true);
		}
		else
		{
			globalClient.unregisterOutPara(OPPow);
		}
		state_cb_P = isChecked;
		GTPref.getGTPref().edit().putBoolean(KEY_Pow, isChecked).commit();

		for (BatteryPluginListener listener : listeners)
		{
			listener.onUpdateP(isChecked);
		}
	}

	public void updateT(boolean isChecked)
	{
		if (isChecked)
		{
			globalClient.registerOutPara(OPTemp, "TEMP");
			globalClient.setOutparaMonitor(GTBatteryEngine.OPTemp, true);
		}
		else
		{
			globalClient.unregisterOutPara(OPTemp);
		}
		state_cb_T = isChecked;
		GTPref.getGTPref().edit().putBoolean(KEY_Temp, isChecked).commit();

		for (BatteryPluginListener listener : listeners)
		{
			listener.onUpdateT(isChecked);
		}
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public synchronized void addListener(BatteryPluginListener listener)
	{
		listeners.add(listener);
	}

	public synchronized void removeListener(BatteryPluginListener listener)
	{
		listeners.remove(listener);
	}

	class ReadPowerTimerTask extends TimerTask {
		File f = new File("/sys/class/power_supply/battery/uevent");
		// 如华为系统,Battery目录是大写的。。
		File f2 = new File("/sys/class/power_supply/Battery/uevent");
		boolean isHuawei = false;
		boolean isLGg3 = false; // 但实际上LG g3的数据应该不够准确，静默都是1500mA左右

		public ReadPowerTimerTask()
		{
			if (!f.exists() && f2.exists())
				
			{
				f = f2;
				isHuawei = true;
			}

			if (DeviceUtils.getDevModel().startsWith("LG")
					&& DeviceUtils.getHardware().equals("g3"))
			{
				isLGg3 = true;
			}
		}

		@Override
		public void run() {
			
			BufferedReader br = null;
			try {
				FileReader fr = new FileReader(f);
				br = new BufferedReader(fr);
				String line = "";
				while((line = br.readLine()) != null){
					
					int found = 0;
					if (line.startsWith("POWER_SUPPLY_VOLTAGE_NOW="))
					{
						U = line.substring(line.lastIndexOf("=") + 1);
						// since 2.1.1 从μV转成mV
						long volt = Long.parseLong(U) / 1000;
						globalClient.setOutPara(OPU, volt + "mV");
						
						OutPara op = globalClient.getOutPara(OPU);
						if (null != op)
						{
							OpPerfBridge.addHistory(op, U, volt);
						}
						
						found++;
					}
					if (line.startsWith("POWER_SUPPLY_CURRENT_NOW="))
					{
						I = line.substring(line.lastIndexOf("=") + 1);
						// since 2.1.1 从μA转成mA since 2.2.4 华为本身就是mA
						long current = Long.parseLong(I);
						if (isHuawei)
						{
							current = -current;
						}
						else if (isLGg3)
						{
							current = current >> 1; // 经验值估算LG g3的数据除以2后比较接近真实
						}
						else
						{
							current = current / 1000;
						}
						globalClient.setOutPara(OPI, current + "mA");
						
						OutPara op = globalClient.getOutPara(OPI);
						if (null != op)
						{
							OpPerfBridge.addHistory(op, I, current);
						}
						
						found++;
					}
					if (line.startsWith("POWER_SUPPLY_CAPACITY="))
					{
						String lastBattery = POW;
						POW =  line.substring(line.lastIndexOf("=") + 1);
						if (! lastBattery.equals(POW)) // 电池百分比变化了
						{
							if (startBattry != -1)
							{
								lastBatteryChangeTime = (System.currentTimeMillis() - startBattry)/1000 + "s";
								String tempValue = POW + "% | -1% time:" + lastBatteryChangeTime;
								globalClient.setOutPara(OPPow, tempValue);
								GTLog.logI(LOG_TAG, tempValue);
								// 将电量加入历史记录
								OutPara op = globalClient.getOutPara(OPPow);
								if (null != op)
								{
									OpPerfBridge.addHistory(op, tempValue, Long.parseLong(POW));
								}
							}
							
							startBattry = System.currentTimeMillis();
						}
						
						globalClient.setOutPara(OPPow, POW + "% | -1% time:" + lastBatteryChangeTime);
						found++;
					}
					if (line.startsWith("POWER_SUPPLY_TEMP="))
					{
						TEMP = line.substring(line.lastIndexOf("=") + 1);
						int iTemp = Integer.parseInt(TEMP);
						iTemp = iTemp/10;
						if (iTemp > -273)
						{
							TEMP = iTemp + "℃";
						}
						
						globalClient.setOutPara(OPTemp, TEMP);
						
						OutPara op = globalClient.getOutPara(OPTemp);
						if (null != op && iTemp != INT_TEMP)
						{
							OpPerfBridge.addHistory(op, TEMP, iTemp);
							GTLog.logI(LOG_TAG, TEMP);
							INT_TEMP = iTemp;
						}

						found++;
					}
					if (found >= 4)
					{
						return;
					}
					
				}
			} catch (Exception e) {
				doStop();
			}
			finally
			{
				FileUtil.closeReader(br);
			}
		}
	};
}
