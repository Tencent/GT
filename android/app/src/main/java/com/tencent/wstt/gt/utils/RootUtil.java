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
package com.tencent.wstt.gt.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RootUtil {
	private static final Pattern PID_PATTERN = Pattern.compile("\\d+");
	private static final Pattern SPACES_PATTERN = Pattern.compile("\\s+");

	public static boolean rootJustNow = false;

	public static boolean isRooted() {
		BufferedReader reader;
		boolean flag = false;
		try {
			reader = terminal("ls /data/");
			if (reader.readLine() != null) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rootJustNow = flag;
		return flag;
	}

	private static BufferedReader terminal(String command) throws Exception {
		Process process = Runtime.getRuntime().exec("su");
		// 执行到这，Superuser会跳出来，选择是否允许获取最高权限
		OutputStream outstream = process.getOutputStream();
		DataOutputStream DOPS = new DataOutputStream(outstream);
		InputStream instream = process.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new DataInputStream(instream)));
		String temp = command + "\n";
		// 加回车
		DOPS.writeBytes(temp);
		// 执行
		DOPS.flush();
		// 刷新，确保都发送到outputstream
		DOPS.writeBytes("exit\n");
		// 退出
		DOPS.flush();
		process.waitFor();
		return br;
	}

	public static void destroy(Process process) {
		// stupid method for getting the pid, but it actually works
		Matcher matcher = PID_PATTERN.matcher(process.toString());
		matcher.find();
		int pid = Integer.parseInt(matcher.group());
		List<Integer> allRelatedPids = getAllRelatedPids(pid);
		for (Integer relatedPid : allRelatedPids) {
			destroyPid(relatedPid);
		}
	}

	private static void destroyPid(int pid) {

		Process suProcess = null;
		PrintStream outputStream = null;
		try {
			suProcess = Runtime.getRuntime().exec("su");
			outputStream = new PrintStream(new BufferedOutputStream(
					suProcess.getOutputStream(), 8192));
			outputStream.println("kill " + pid);
			outputStream.println("exit");
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			if (suProcess != null) {
				try {
					suProcess.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static List<Integer> getAllRelatedPids(final int pid) {
		List<Integer> result = new ArrayList<Integer>(Arrays.asList(pid));
		// use 'ps' to get this pid and all pids that are related to it (e.g.
		// spawned by it)
		try {

			final Process suProcess = Runtime.getRuntime().exec("su");

			new Thread(new Runnable() {

				@Override
				public void run() {
					PrintStream outputStream = null;
					try {
						outputStream = new PrintStream(
								new BufferedOutputStream(
										suProcess.getOutputStream(), 8192));
						outputStream.println("ps");
						outputStream.println("exit");
						outputStream.flush();
					} finally {
						if (outputStream != null) {
							outputStream.close();
						}
					}

				}
			}).run();

			if (suProcess != null) {
				try {
					suProcess.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(
						suProcess.getInputStream()), 8192);
				while (bufferedReader.ready()) {
					String[] line = SPACES_PATTERN.split(bufferedReader
							.readLine());
					if (line.length >= 3) {
						try {
							if (pid == Integer.parseInt(line[2])) {
								result.add(Integer.parseInt(line[1]));
							}
						} catch (NumberFormatException ignore) {
						}
					}
				}
			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return result;
	}
}
