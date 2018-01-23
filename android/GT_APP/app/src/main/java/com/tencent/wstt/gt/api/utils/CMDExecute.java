/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 * 
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 * 
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tencent.wstt.gt.api.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 该类用来执行命令行并返回执行结果
 * @author zoneguo
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
	 * 执行命令行;返回命令执行结果，每行数据以“\r\n”分隔
	 * 
	 * @param cmdString
	 * @return
	 * 执行命令失败返回null
	 */
	public static String runCMD(String cmdString) {
		//String result = "";
		StringBuilder sb = new StringBuilder();
		String line = "";
		Process process;
		
		try {
			process = Runtime.getRuntime().exec(new String[] { "su", "-c", cmdString});
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			while (((line = br.readLine())!= null)) {
				//去掉空白行数据
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
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	public static void doCmd(String cmd) throws Exception {
		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());

		os.writeBytes(cmd + "\n");

		os.writeBytes("exit\n");
		os.flush();
		os.close();

		process.waitFor();
	}
	
	public static void doCmds(List<String> cmds) throws Exception {
		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());

		for (String tmpCmd : cmds) {
			os.writeBytes(tmpCmd + "\n");
		}

		os.writeBytes("exit\n");
		os.flush();
		os.close();

		process.waitFor();
	}

	// 有人机交互的shell命令
	public static Process startSuCmdInteractive() throws Exception {
		Process process = Runtime.getRuntime().exec("su");
		return process;
	}

	// 人机交互的后续命令
	public static Process continueSuCmdInteractive(Process process, String cmd) throws Exception {
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		os.writeBytes(cmd + "\n");
		os.flush();
		return process;
	}
}
