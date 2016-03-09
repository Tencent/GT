/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.tencent.wstt.apt.console.APTConsoleFactory;

/**
* @Description 
* @date 2013年11月10日 下午5:03:48 
*
 */
public class AdbDeviceListener implements AndroidDebugBridge.IDeviceChangeListener{

	//静态锁，相等于类锁
	//这里可以保证，所有该类的对象中的所有方法都是同步执行的
	public static final Object sLock = new Object();
	public IDevice mCurrentDevice = null;
	@Override
	public void deviceChanged(IDevice arg0, int arg1) {
		
	}

	@Override
	public void deviceConnected(IDevice device) {
		APTConsoleFactory.getInstance().APTPrint("Device Connected");
		synchronized (sLock) {
			this.mCurrentDevice = device;
			sLock.notify();
		}
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		APTConsoleFactory.getInstance().APTPrint("Device Disconnected");
		synchronized (sLock) {
			this.mCurrentDevice = null;
			sLock.notify();
		}
	}

}
