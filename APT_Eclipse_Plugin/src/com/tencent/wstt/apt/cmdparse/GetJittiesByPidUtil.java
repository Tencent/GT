/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;


import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.Jiffies;
import com.tencent.wstt.apt.data.PCInfo;

/**
* @Description 获取对应pid的jiffies值 
* @date 2013年11月10日 下午5:47:26 
*
 */
public class GetJittiesByPidUtil {
	
	private final static int UTIME_INDEX = 13;
	private final static int STIME_INDEX = 14;
	
	private final static String CMD1 = "cat /proc/";
	private final static String CMD2 = "/stat";
	
	/**
	 * 获取pid对应的进程的utime和stime
	* @Title: getJitties  
	* @Description:   
	* @param pid
	* @return 
	* Jitties 
	* @throws
	 */
	public static Jiffies getJitties(String pid)
	{
		String cmdStr = PCInfo.adbShell + " " + CMD1 + pid + CMD2;
		String resultCmd = CMDExecute.runCMD(cmdStr);
		
		if(resultCmd == null)
		{
			return new Jiffies(0, 0);
		}
		
		String []columns = resultCmd.split(Constant.BLANK_SPLIT);
		if(columns == null || columns.length < 15)
		{
			return new Jiffies(0, 0);
		}
		return new Jiffies(Long.parseLong(columns[UTIME_INDEX]), Long.parseLong(columns[STIME_INDEX]));
	}
}
