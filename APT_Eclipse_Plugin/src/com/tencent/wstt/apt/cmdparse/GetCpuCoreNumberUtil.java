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
* @Description 通过解析/proc/cpuinfo获取手机的逻辑CPU核数 
* @date 2013年11月10日 下午5:45:13 
*
 */
public class GetCpuCoreNumberUtil {

	private final static String KEYWORD = "processor";
	private final static String CPUINFO_CMD = "cat /proc/cpuinfo";
	//public final static String GET_CPUINFO = "cat /proc/cpuinfo";
	//public static String CMD = "adb shell cat /proc/cpuinfo";

	public static int getCpuCoreNumber() {
		String str = CMDExecute.runCMD(PCInfo.adbShell + " " + CPUINFO_CMD );
		return parseCmdOutputStr(str);
	}

	private static int parseCmdOutputStr(String cmdOutputStr) {
		if (cmdOutputStr == null) {
			return Constant.FAILED;
		}
		int cpuCoreNumber = 0;
		
		String []rows = cmdOutputStr.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			return Constant.FAILED;
		}
		
		for(int i = 0; i < rows.length; i++)
		{
			if(rows[i].indexOf(KEYWORD)!=-1)
			{
				cpuCoreNumber++;
			}
		}
		if(cpuCoreNumber == 0)
		{
			return 1;
		}
		return cpuCoreNumber;
	}
}
