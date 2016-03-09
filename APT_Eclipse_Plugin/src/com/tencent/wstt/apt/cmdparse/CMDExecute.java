/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;







import com.tencent.wstt.apt.console.APTConsoleFactory;

/**
* @Description 执行命令行并获取对应输出
* @date 2013年11月10日 下午5:40:08 
*
 */
public final class CMDExecute {
	/**
	 * 私有构造函数，防止被new
	 */
	private CMDExecute()
	{
		
	}
	
	/**
	* @Description 执行命令行;返回命令执行结果，每行数据以“\r\n”分隔 
	* @param  cmdString
	* @param    
	* @return String 
	* @throws
	 */
	public static String runCMD(String cmdString) {
		StringBuilder sb = new StringBuilder();
		String line = "";
		
		try {
			Process process = Runtime.getRuntime().exec(cmdString);	
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					is));	
			while (((line = br.readLine())!= null)) {
				line = line.trim();
				if(line.equals(""))
				{
					continue;
				}
				sb.append(line);
				sb.append("\r\n");
			}
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
				APTConsoleFactory.getInstance().APTPrint("执行CMD wait操作失败");
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			APTConsoleFactory.getInstance().APTPrint("执行CMD失败");
			APTConsoleFactory.getInstance().APTPrint(e.getMessage());
			return null;
		}
		return sb.toString();
	}
}
