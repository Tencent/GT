/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;


import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;


/**
 * 获取当前eclipse Preferences中设置的android sdklocation的位置
 *
 */
public class GetAdbPathUtil {
	private static final String ENV_PATH_KEY = "Path";
	private static final String SPECIAL_CHARACTER = "platform-tools";
	
	private static String getSDKPath()
	{
		IPreferencesService ips = Platform.getPreferencesService();
		return ips.getString("com.android.ide.eclipse.adt", "com.android.ide.eclipse.adt.sdk", "error", null);
	}
	
	public static boolean getAdbPath()
	{
		String sdkLocation = getSDKLocation();
		APTConsoleFactory.getInstance().APTPrint(sdkLocation);
		if(sdkLocation != null)
		{
			PCInfo.adbPath = "adb";
			PCInfo.hprofConv = "\"" + (sdkLocation + Constant.TOOLS_DIR_NAME) + File.separator + "hprof-conv\"";
		}
		else
		{
			sdkLocation = getSDKPath();
			APTConsoleFactory.getInstance().APTPrint("建议设置ADB的环境变量");
			APTConsoleFactory.getInstance().APTPrint(sdkLocation);
			if(sdkLocation.equalsIgnoreCase("error"))
			{
				return false;
			}
			if(PCInfo.OSName.toLowerCase().indexOf("window") != -1)
			{
				PCInfo.adbPath = "\"" + (sdkLocation + File.separator + Constant.ADB_DIR_NAME) + File.separator + "adb\"";
				PCInfo.hprofConv = "\"" + (sdkLocation + File.separator + Constant.TOOLS_DIR_NAME) + File.separator + "hprof-conv\"";
			}
			else
			{
				PCInfo.adbPath = (sdkLocation + File.separator + Constant.ADB_DIR_NAME) + File.separator + "adb";
				PCInfo.hprofConv = (sdkLocation + File.separator + Constant.TOOLS_DIR_NAME) + File.separator + "hprof-conv";
			}
		}		
		
		PCInfo.adbShell = PCInfo.adbPath + " shell";
		return true;
	}
	
	/**
	* @Description 检查adb 是否设置了环境变量 
	* @param @return   
	* @return boolean 
	* @throws
	 */
	private static String getSDKLocation()
	{
		//TODO 没有考虑linux、Mac
		String pathValue = System.getenv(ENV_PATH_KEY);
		if(pathValue == null)
		{
			return null;
		}
		
		String[] vals = pathValue.split(";");
		String sdkLocation = null;
		for(int i = 0; i < vals.length; i++)
		{
			//APTConsoleFactory.getInstance().APTPrint(vals[i]);
			File f1 = new File(vals[i] + File.separator + "adb");
			File f2 = new File(vals[i] + File.separator + "adb.exe");
			if(f1.exists() || f2.exists())
			{
				sdkLocation = vals[i].replaceAll(SPECIAL_CHARACTER, "");
				break;
			}
		}		
		return sdkLocation;
	}
	

}
