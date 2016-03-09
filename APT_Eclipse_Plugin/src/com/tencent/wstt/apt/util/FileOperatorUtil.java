/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;

import java.util.Arrays;
import java.util.List;

import com.tencent.wstt.apt.cmdparse.CMDExecute;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;


/**
* @Description 操作手机上文件相关的工具类 
* @date 2013年11月10日 下午6:17:00 
*
 */
public class FileOperatorUtil {
	
	/**
	* @Description 把手机上的apt拷贝到PC上对应的目录 
	* @param @param name
	* @param @param to   
	* @return void 
	* @throws
	 */
	public static void pullLogFileWithNameFromSDCard(String name, String to)
	{
        String cmdStr = PCInfo.adbPath + " pull " + Constant.LOG_FOLDER_ON_PHONE + name + " " + to;
        System.out.println(cmdStr);
		String outputStr = CMDExecute.runCMD(cmdStr); 
		System.out.println(outputStr);
	}

	/**
	* @Description 获取手机上APT目录下面的文件列表 
	* @param @return   
	* @return List<String> 
	* @throws
	 */
	public static List<String> getLogFilesOnPhone()
	{
		String result = "";
        String cmdstr = "adb shell ls " + Constant.LOG_FOLDER_ON_PHONE;
		result = com.tencent.wstt.apt.cmdparse.CMDExecute.runCMD(cmdstr);
		if(result == null)
		{
			return null;
		}
		String[] files = parseLsResult2FileNames(result);
		if(files == null)
		{
			return null;
		}
		List<String> list = Arrays.asList(files);
        return list;
	}
	

	private static String[] parseLsResult2FileNames(String content)
	{
		if(content.indexOf("No such file or directory") != -1 || content.equals(""))
		{
			return null;
		}
		//System.out.println(":" + content + ":");
		String[] files = content.toLowerCase().split(Constant.BLANK_SPLIT);
		return files;
	}
}
