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
import com.tencent.wstt.apt.data.DeviceInfo;
import com.tencent.wstt.apt.data.PCInfo;
import com.tencent.wstt.apt.data.PkgInfo;
import com.tencent.wstt.apt.data.TestSence;

/**
* @Description dumpsys cpuinfo命令的解析工具类 
* @date 2013年11月10日 下午5:42:12 
*
 */
public class DumpsysCpuInfoParseUtil {


	public static final int CPU_VALUE_INDEX = 0;
	private static final int PID_INDEX = 1;
	//public static final String CMD = "adb shell dumpsys cpuinfo";
	public static final String DUMPSYS_CPU_CMD = "dumpsys cpuinfo";
	

	/**
	 * 通过解析dumpsys cpuinfo 来获取pkglist中对应pkg的CPU占用百分比
	* @Title: getCpuValues  
	* @Description:   
	* @param pkgList
	* @return 
	* List<Float> 
	* 不存在对应进程名的时候返回0
	* @throws
	 */
	public static Float[] getCpuValues(String[] pkgNames)
	{
		if(pkgNames == null || pkgNames.length == 0)
		{
			return null;
		}
		int pkgNumber = pkgNames.length;
		Float[] result = new Float[pkgNumber];
		Arrays.fill(result, 0f);
		
		String cmdResultStr = CMDExecute.runCMD(PCInfo.adbShell + " " + DUMPSYS_CPU_CMD);
		if(cmdResultStr == null)
		{
			return result;
		}
		String[] rows = cmdResultStr.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			return result;
		}
		

		for(int i = 0; i < pkgNumber; i++)
		{
			for(int j = 0; j < rows.length; j++)
			{
				
				if(rows[j].indexOf(pkgNames[i]) != -1)
				{
					rows[j] = rows[j].trim();
					String[] columns = rows[j].split(Constant.BLANK_SPLIT);
					if(columns == null)
					{
						return result;
					}
					
					int firstIndex = columns[1].indexOf("/");
					if(firstIndex == -1)
					{
						firstIndex = 0;
					}
					String pkgName = columns[1].substring(firstIndex + 1, columns[1].length()-1);

					if(pkgName.equalsIgnoreCase(pkgNames[i]))
					{
						String temp =  columns[CPU_VALUE_INDEX].trim().substring(0, columns[CPU_VALUE_INDEX].length()-1);
						Float valueCPU = Float.parseFloat(temp);
						result[i] = valueCPU/DeviceInfo.getInstance().cpuCoreNumber;
						String pidInStr = columns[PID_INDEX].trim();
						TestSence.getInstance().pkgInfos.get(i).contents[PkgInfo.PID_INDEX] = pidInStr.substring(0, pidInStr.indexOf("/"));
						break;
					}
				}
			}	
		}

		return result;
	}
}
