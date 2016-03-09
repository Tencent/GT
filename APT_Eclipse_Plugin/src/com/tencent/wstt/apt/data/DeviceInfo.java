/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.data;

import java.util.List;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant.PhoneState;

/**
* @Description 设备信息结构体 
* @date 2013年11月10日 下午5:54:56 
*
 */
public class DeviceInfo {

	private static DeviceInfo instance = null;
	private DeviceInfo()
	{
		
	}
	
	public static DeviceInfo getInstance()
	{
		if(instance == null)
		{
			instance = new DeviceInfo();
		}
		return instance;
	}
	
	public void print()
	{
		String str = "当前手机信息：\r\n";
		str += "手机系统版本：" + androidVersionStr + "\r\n";
		str += "CPU核数：" + cpuCoreNumber + "\r\n";
		APTConsoleFactory.getInstance().APTPrint(str);
	}
	
	public PhoneState state;
	public int androidVersion;
	public String androidVersionStr;
	public int cpuCoreNumber;
	public List<PkgInfo> pkgList;
}
