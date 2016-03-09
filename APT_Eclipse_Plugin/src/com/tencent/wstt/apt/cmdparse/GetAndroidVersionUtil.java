/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;

import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;

/**
* @Description 获取当前手机系统版本 
* @date 2013年11月10日 下午5:43:51 
*
 */
public class GetAndroidVersionUtil {

	//用下面的这个命令解析可能会更好，后续可以进行优化
	//public final static String GET_ANDROID_VERSION = "getprop ro.build.version.sdk";
	//public final static String GET_ANDROID_VERSION = "getprop ro.build.version.release";
	//public static String CMD = "adb shell getprop ro.build.version.release";
	
	private static String ANDROID_VERSION_CMD = "getprop ro.build.version.release";
	public static int getAndroidVersion()
	{
		return parseStr2Int(getAndroidVersionStr());
	}
	
	public static String getAndroidVersionStr()
	{
		String str =  CMDExecute.runCMD(PCInfo.adbShell + " " + ANDROID_VERSION_CMD );
		if(str == null)
		{
			return null;
		}
		String rows[] = str.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null || rows.length < 1)
		{
			return null;
		}
		return rows[0];
	}
	
	private static int parseStr2Int(String cmdOutputStr)
	{
		if(cmdOutputStr == null || cmdOutputStr.length() < 1)
		{
			return Constant.FAILED;
		}
		if(cmdOutputStr.substring(0, 1).equals("4"))
		{
			if(cmdOutputStr.charAt(2) == '4')
			{
				return Constant.ANDROID_KITKAT;
			}
			return Constant.ANDROID_4X;
		}
		else if(cmdOutputStr.substring(0, 1).equals("5"))
		{
			return Constant.ANDROID_5X;
		}
		else if(cmdOutputStr.substring(0, 1).equals("2"))
		{
			return Constant.ANDROID_2X;
		}
		else
		{
			return Constant.FAILED;
		}
	}
	
	

}
