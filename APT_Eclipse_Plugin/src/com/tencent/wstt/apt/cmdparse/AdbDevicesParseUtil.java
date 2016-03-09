/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;

import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.Constant.PhoneState;
import com.tencent.wstt.apt.data.PCInfo;


/**
* @Description 执行adb devices；来判断当前是否存在一个合法的设备 
* @date 2013年11月10日 下午5:39:10 
*
 */
public class AdbDevicesParseUtil {

	private static final String LIST_DEVICES_CMD = "devices";
	private static final String KEYWORD = "device";
	
	/**
	* @Description 获取当前的手机状态 
	* @param @return   
	* @return PhoneState 
	* @throws
	 */
	public static PhoneState run()
	{
		//APTConsoleFactory.getInstance().APTPrint("PhoneState_run");
		//String result = CMDExecute.runCMD(PCInfo.adbPath + " " + LIST_DEVICES_CMD);
		//APTConsoleFactory.getInstance().APTPrint(PCInfo.adbPath + " " + LIST_DEVICES_CMD);
		String cmdStr = PCInfo.adbPath + " " + LIST_DEVICES_CMD;
		String result = CMDExecute.runCMD(cmdStr);
		//APTConsoleFactory.getInstance().APTPrint("PhoneState_run_over");
		return getDeviceCount(result);
	}
	
	private static PhoneState getDeviceCount(String result)
	{
		if(result == null)
		{
			return PhoneState.STATE_NOT_ADB;
		}
		
		int deviceCount = 0;
		
		String[] rows = result.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			return PhoneState.STATE_NOT_FOUND_PHONE;
		}
		
		for(int i = 1; i < rows.length; i++)
		{
			String[] columns = rows[i].split(Constant.BLANK_SPLIT);
			if(columns == null)
			{
				continue;
			}
			if(columns.length == 2 && columns[1].equals(KEYWORD))
			{
				deviceCount++;
			}	
		}
		if(deviceCount == 0)
		{
			return PhoneState.STATE_NOT_FOUND_PHONE;
		}
		else if(deviceCount == 1)
		{
			return PhoneState.STATE_OK;
		}
		else
		{
			return PhoneState.STATE_MULTI_FOUND_PHONE;
		}
	}
}
