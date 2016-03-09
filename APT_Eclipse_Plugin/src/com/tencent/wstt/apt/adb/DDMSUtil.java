/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Client;
import com.android.ddmlib.ClientData;
import com.tencent.wstt.apt.console.APTConsoleFactory;


/**
* @Description  dump相关的工具类
* @date 2013年11月10日 下午5:06:20 
*
 */
public class DDMSUtil {
	
	private static AdbDeviceListener deviceListener = null;
	private static AdbHProfDumpListener hprofListener = null;
	private static boolean isInit = false;
	
	/**
	* @Description 初始化ddmlib 
	* @param    
	* @return void 
	* @throws
	 */
	public static void init()
	{
		if(!isInit)
		{
			deviceListener = new AdbDeviceListener();
			hprofListener = new AdbHProfDumpListener();
			AndroidDebugBridge.addDeviceChangeListener(deviceListener);
			ClientData.setHprofDumpHandler(hprofListener);
			AndroidDebugBridge.init(true);
			AndroidDebugBridge.createBridge();
			isInit = true;
		}
	}
	
	/**
	* @Description 执行dump操作 
	* @param  pkgName执行dump操作对应的进程
	* @return boolean dump操作是否成功
	* @throws
	 */
	public static boolean dump(String pkgName)
	{
		synchronized (AdbDeviceListener.sLock) {
			if (deviceListener.mCurrentDevice == null) {
				APTConsoleFactory.getInstance().APTPrint("等待设备连接");
				try {
					AdbDeviceListener.sLock.wait(5000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					APTConsoleFactory.getInstance().APTPrint(e.getMessage());
				}
			}
		}

		if (deviceListener.mCurrentDevice == null) {
			APTConsoleFactory.getInstance().APTPrint(
					"当前没有设备连接, dump hprof failed");
			return false;
		}

		Client targetClient = deviceListener.mCurrentDevice.getClient(pkgName);

		if (targetClient == null) {
			APTConsoleFactory.getInstance().APTPrint(
					"进程连接失败:pkgName=" + pkgName);
			APTConsoleFactory.getInstance().APTPrint("1.首先保证系统或者被测应用是可调试的");
			APTConsoleFactory.getInstance().APTPrint("2.其次保证APT先于DDMS启动（打开APT透视图，重启eclipse即可）");
			return false;
		}

		targetClient.dumpHprof();
		return true;
	}
	
	/**
	* @Description 触发GC，并获取一次内存数据 
	* @param @param pkgName
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean gc(String pkgName)
	{
		synchronized (AdbDeviceListener.sLock) {
			if (deviceListener.mCurrentDevice == null) {
				APTConsoleFactory.getInstance().APTPrint("等待设备连接");
				try {
					AdbDeviceListener.sLock.wait(5000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					APTConsoleFactory.getInstance().APTPrint(e.getMessage());
				}
			}
		}

		if (deviceListener.mCurrentDevice == null) {
			APTConsoleFactory.getInstance().APTPrint(
					"当前没有设备连接, dump hprof failed");
			return false;
		}

		Client targetClient = deviceListener.mCurrentDevice.getClient(pkgName);

		if (targetClient == null) {
			APTConsoleFactory.getInstance().APTPrint(
					"进程连接失败:pkgName=" + pkgName);
			APTConsoleFactory.getInstance().APTPrint("1.首先保证系统或者被测应用是可调试的");
			APTConsoleFactory.getInstance().APTPrint("2.其次保证APT先于DDMS启动（打开APT透视图，重启eclipse即可）");
			return false;
		}

		targetClient.executeGarbageCollector();
		APTConsoleFactory.getInstance().APTPrint("GC Done");
		return true;
	}
}
