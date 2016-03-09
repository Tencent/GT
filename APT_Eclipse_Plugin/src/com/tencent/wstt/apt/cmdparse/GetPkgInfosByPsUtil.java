/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;

import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;
import com.tencent.wstt.apt.data.PkgInfo;

/**
* @Description 通过解析ps命令获取当前的进程列表 
* @date 2013年11月10日 下午5:48:05 
*
 */
public class GetPkgInfosByPsUtil {
	private static final String PS_CMD = "ps";
	private static final int TARGETLINEITEMNUMBER = 9;
	private static final int USER_INDEX = 0;
	private static final int PID_INDEX = 1;
	private static final int NAME_INDEX = 8;
	
	private static final String FILTER_4x = "u0_";
	private static final String FILTER_2x = "app_";
	private static final String FILTER_SYSTEM = "system";
	private static final String FILTERS[] = new String[]{FILTER_4x, FILTER_2x, FILTER_SYSTEM};
	
	
	public static List<PkgInfo> getPkgInfos()
	{
		APTConsoleFactory.getInstance().APTPrint("GetPkgInfosByPsUtil.getPkgInfos start");
		
		//TODO 性能问题：每次都new一个list对象
		List<PkgInfo> result = new ArrayList<PkgInfo>();
		String cmdOutputStr = CMDExecute.runCMD(PCInfo.adbShell + " " + PS_CMD);
		if(cmdOutputStr == null)
		{
			APTConsoleFactory.getInstance().APTPrint("GetPkgInfosByPsUtil.getPkgInfos cmdOutputStr == null");
			
			return null;
		}
		String []rows = cmdOutputStr.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			APTConsoleFactory.getInstance().APTPrint("GetPkgInfosByPsUtil.getPkgInfos rows == null");
			
			return null;
		}
		
		for(int i = 0; i < rows.length; i++)
		{
			String []columns = rows[i].split(Constant.BLANK_SPLIT);
			if(columns!=null && columns.length==TARGETLINEITEMNUMBER)
			{
				for(String filter : FILTERS)
				{
					if(columns[USER_INDEX].indexOf(filter) != -1)
					{
						PkgInfo item = new PkgInfo();
						item.contents[PkgInfo.NAME_INDEX] = columns[NAME_INDEX];
						item.contents[PkgInfo.PID_INDEX] = columns[PID_INDEX];
						result.add(item);
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	public static String getPid(String pkgName)
	{
		String cmdOutputStr = CMDExecute.runCMD(PCInfo.adbShell + " " + PS_CMD);
		if(cmdOutputStr == null)
		{
			return null;
		}
		String []rows = cmdOutputStr.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			APTConsoleFactory.getInstance().APTPrint("rows == null");
			return null;
		}
		
		for(int i = 0; i < rows.length; i++)
		{
			String []columns = rows[i].split(Constant.BLANK_SPLIT);
			if(columns!=null && columns.length==TARGETLINEITEMNUMBER)
			{
				for(String filter : FILTERS)
				{
					if(columns[USER_INDEX].indexOf(filter) != -1)
					{
						if(columns[NAME_INDEX].equalsIgnoreCase(pkgName))
						{
							return columns[PID_INDEX];
						}
					}
				}
			}
		}
		
		return null;
	}

}
