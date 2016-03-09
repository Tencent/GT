/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;

import java.util.Arrays;

import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;
import com.tencent.wstt.apt.data.PkgInfo;
import com.tencent.wstt.apt.data.TestSence;


/**
* @Description top命令解析工具类 
* @date 2013年11月10日 下午5:44:43 
*
 */
public class GetCpuByTopCmdUtil {
	//private static final String CMD = "adb shell top -n 1 -d " + Constant.TOP_UPDATE_PERIOD;
	
	private static final String TOP_CMD = "top -n 1 -d " + Constant.TOP_UPDATE_PERIOD;
	
	private static final int CPU_INDEX_4X = 2;
	private static final int PKGNAME_INDEX_4X = 9;
	private static final int ITEM_COUNT_TOP_4X = 10;
	
	private static final int CPU_INDEX_2X = 1;
	private static final int PKGNAME_INDEX_2X = 8;
	private static final int ITEM_COUNT_TOP_2X = 9;
	
	private static final int PID_INDEX = 0;
	/**
	 * 获取进程pkgName的CPU占用率
	 * @param pkgName
	 * @param androidVersion
	 * @return -1：解析命令出错 -2：top命令中未检查到pkgName对应的进程
	 */
	public static Integer[] getPkgCpuByTopCmd(String[] pkgNames, int androidVersion)
	{
		String cmdOutputStr = CMDExecute.runCMD(PCInfo.adbShell + " " + TOP_CMD);
		if(androidVersion == Constant.ANDROID_4X || androidVersion == Constant.ANDROID_KITKAT || androidVersion == Constant.ANDROID_5X)//5.0命令也一样
		{
			return parseTopCmd4X(cmdOutputStr, pkgNames);
		}
		else
		{
			return parseTopCmd2X(cmdOutputStr, pkgNames);
		}
	}

	private static Integer[] parseTopCmd2X(String cmdOutputStr, String[] pkgNames)
	{
		if(cmdOutputStr == null || pkgNames == null || pkgNames.length == 0)
		{
			return null;
		}
		
		
		String rows[] = cmdOutputStr.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			return null;
		}
		int pkgNumber = pkgNames.length;
		Integer []result = new Integer[pkgNumber];
		Arrays.fill(result, 0);
		
		for(int i = 0; i < pkgNumber; i++)
		{
			//每次强制使用新的PID
			//TestSence.getInstance().pkgInfos.get(i).contents[PkgInfo.PID_INDEX] = "-1";
			for(int j = 0; j < rows.length; j++)
			{
				String []columns = rows[j].split(Constant.BLANK_SPLIT);
				if(columns == null || columns.length != ITEM_COUNT_TOP_2X)
				{
					continue;
				}
				
				if(columns[PKGNAME_INDEX_2X].trim().equals(pkgNames[i]))
				{
					String cpuStr = columns[CPU_INDEX_2X];
					result[i] = Integer.parseInt(cpuStr.substring(0, cpuStr.length()-1));
					TestSence.getInstance().pkgInfos.get(i).contents[PkgInfo.PID_INDEX] = columns[PID_INDEX];
					break;
				}
			}	
		}
		
		return result;
	}
	
	
	private static Integer[] parseTopCmd4X(String cmdOutputStr, String[] pkgNames)
	{
		if(cmdOutputStr == null || pkgNames == null || pkgNames.length == 0)
		{
			return null;
		}
		
		
		String rows[] = cmdOutputStr.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			return null;
		}
		int pkgNumber = pkgNames.length;
		Integer []result = new Integer[pkgNumber];
		Arrays.fill(result, 0);
		
		for(int i = 0; i < pkgNumber; i++)
		{
			for(int j = 0; j < rows.length; j++)
			{
				String []columns = rows[j].split(Constant.BLANK_SPLIT);
				if(columns == null || columns.length != ITEM_COUNT_TOP_4X)
				{
					continue;
				}
				
				if(columns[PKGNAME_INDEX_4X].trim().equals(pkgNames[i]))
				{
					String cpuStr = columns[CPU_INDEX_4X];
					result[i] = Integer.parseInt(cpuStr.substring(0, cpuStr.length()-1));
					TestSence.getInstance().pkgInfos.get(i).contents[PkgInfo.PID_INDEX] = columns[PID_INDEX];
				}
			}	
		}
		
		return result;
	}
}
