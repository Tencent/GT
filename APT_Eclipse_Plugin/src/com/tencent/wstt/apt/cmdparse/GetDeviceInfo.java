/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;

import com.tencent.wstt.apt.data.DeviceInfo;
import com.tencent.wstt.apt.data.Constant.PhoneState;


/**
 * @Description 获取手机信息 该类主要有三个作用 （1）获取当前设备数量 （2）获取系统版本 （3）获取当前进程列表
 * @date 2013年11月10日 下午5:45:28
 * 
 */
public class GetDeviceInfo {

	/**
	 * @return 返回当前进程列表，如果不存在不合法设备返回错误码
	 */
	public static void getDeviceInfo()
	{
		//返回当前设备数量
		PhoneState state = AdbDevicesParseUtil.run();
		DeviceInfo.getInstance().state = state;
		if(state != PhoneState.STATE_OK)
		{
			return;
		}
		
		//获取当前系统版本
		// TODO这里可以进行优化；优化为只获取一次androidversion
		DeviceInfo.getInstance().androidVersion = GetAndroidVersionUtil.getAndroidVersion();
		//APTConsoleFactory.getInstance().APTPrint("version=" + DeviceInfo.getInstance().androidVersion);
		DeviceInfo.getInstance().androidVersionStr = GetAndroidVersionUtil.getAndroidVersionStr();
		
		
		//获取CPU核数
		DeviceInfo.getInstance().cpuCoreNumber = GetCpuCoreNumberUtil.getCpuCoreNumber();
		
		DeviceInfo.getInstance().pkgList = GetPkgInfosByPsUtil.getPkgInfos();
	
	}
}
