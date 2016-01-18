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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.tencent.wstt.gt.utils.DoubleUtils;
import com.tencent.wstt.gt.utils.FileUtil;

/**
 * CPU相关工具类。
 */
public class CpuUtils {
	public static Map<String, CpuUtils> cpuInfoMap = new HashMap<String, CpuUtils>();
	private static boolean initCpu = true;
	private static double o_cpu = 0.0;
	private static double o_idle = 0.0;

	private double p_jif = 0.0;
	private double pCpu = 0.0;
	private double aCpu = 0.0;
	private double o_pCpu = 0.0;
	private double o_aCpu = 0.0;
	
	/**
	 * 获取进程的CPU使用时间片
	 * 
	 * @return 进程的CPU使用时间片
	 */
	public long getJif() {
		return (long)p_jif;
	}

	/**
	 * 获取CPU使用率
	 * 
	 * @return CPU使用率
	 */
	public static double getCpuUsage() {
		double usage = 0.0;
		if (initCpu) {
			initCpu = false;
			RandomAccessFile reader = null;
			try {
				reader = new RandomAccessFile("/proc/stat",
						"r");
				String load;
				load = reader.readLine();
				String[] toks = load.split(" ");
				o_idle = Double.parseDouble(toks[5]);
				o_cpu = Double.parseDouble(toks[2])
						+ Double.parseDouble(toks[3])
						+ Double.parseDouble(toks[4])
						+ Double.parseDouble(toks[6])
						+ Double.parseDouble(toks[8])
						+ Double.parseDouble(toks[7]);
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally
			{
				FileUtil.closeRandomAccessFile(reader);
			}
		} else {
			RandomAccessFile reader = null;
			try {
				reader = new RandomAccessFile("/proc/stat", "r");
				String load;
				load = reader.readLine();
				String[] toks = load.split(" ");
				double c_idle = Double.parseDouble(toks[5]);
				double c_cpu = Double.parseDouble(toks[2])
						+ Double.parseDouble(toks[3])
						+ Double.parseDouble(toks[4])
						+ Double.parseDouble(toks[6])
						+ Double.parseDouble(toks[8])
						+ Double.parseDouble(toks[7]);
				if (0 != ((c_cpu + c_idle) - (o_cpu + o_idle))) {
					// double value = (100.00 * ((c_cpu - o_cpu) ) / ((c_cpu +
					// c_idle) - (o_cpu + o_idle)));
					usage = DoubleUtils.div((100.00 * ((c_cpu - o_cpu))),
							((c_cpu + c_idle) - (o_cpu + o_idle)), 2);
					// Log.d("CPU", "usage: " + usage);
					if (usage < 0) {
						usage = 0;
					}
					else if (usage > 100)
					{
						usage = 100;
					}
					// BigDecimal b = new BigDecimal(Double.toString(value));

					// usage = b.setScale(2,
					// BigDecimal.ROUND_HALF_UP).doubleValue();
					// Log.d("CPU", "usage: " + usage);
				}
				o_cpu = c_cpu;
				o_idle = c_idle;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				FileUtil.closeRandomAccessFile(reader);
			}
		}
		return usage;
	}

	public static double getCpuUsage0() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();
			String[] toks = load.split(" ");
			double idle1 = Double.parseDouble(toks[5]);
			double cpu1 = Double.parseDouble(toks[2])
					+ Double.parseDouble(toks[3]) + Double.parseDouble(toks[4])
					+ Double.parseDouble(toks[6]) + Double.parseDouble(toks[8])
					+ Double.parseDouble(toks[7]);
			// 2:user 3:nice 4:system 6:iowait 7:irq 8:softirq
			try {
				Thread.sleep(360);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reader.seek(0);
			load = reader.readLine();
			reader.close();
			toks = load.split(" ");
			double idle2 = Double.parseDouble(toks[5]);
			double cpu2 = Double.parseDouble(toks[2])
					+ Double.parseDouble(toks[3]) + Double.parseDouble(toks[4])
					+ Double.parseDouble(toks[6]) + Double.parseDouble(toks[8])
					+ Double.parseDouble(toks[7]);
			double value = DoubleUtils.div((100.00 * ((cpu2 - cpu1))),
					(cpu2 + idle2) - (cpu1 + idle1), 2);
			// BigDecimal b = new BigDecimal(Double.toString(value));

			// double res = b.setScale(2,
			// BigDecimal.ROUND_HALF_UP).doubleValue();

			return value;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public static double getCpuUsage1() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();
			String[] toks = load.split(" ");

			double user1 = Double.parseDouble(toks[2]);
			double system1 = Double.parseDouble(toks[4]);
			double irq1 = Double.parseDouble(toks[7]);
			double idle1 = Double.parseDouble(toks[5]);
			try {
				Thread.sleep(360);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reader.seek(0);
			load = reader.readLine();
			reader.close();
			toks = load.split(" ");
			double user2 = Double.parseDouble(toks[2]);
			double system2 = Double.parseDouble(toks[4]);
			double irq2 = Double.parseDouble(toks[7]);
			double idle2 = Double.parseDouble(toks[5]);

			double user_pass = user2 - user1;
			double system_pass = system2 - system1;
			double irq_pass = irq2 - irq1;
			double idle_pass = idle2 - idle1;
			double usage = (user_pass + system_pass + irq_pass) * 100.00
					/ (user_pass + irq_pass + system_pass + idle_pass);
			BigDecimal b = new BigDecimal(usage);
			double res = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			return res;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return 0;

	}

	public String[] getProcessCpuAction(int pid) {
		String cpuPath = "/proc/" + pid + "/stat";
		String cpu = "";
		String[] result = new String[3];

		File f = new File(cpuPath);
		if (!f.exists() || !f.canRead())
		{
			/*
			 * 进程信息可能无法读取，
			 * 同时发现此类进程的PSS信息也是无法获取的，用PS命令会发现此类进程的PPid是1，
			 * 即/init，而其他进程的PPid是zygote,
			 * 说明此类进程是直接new出来的，不是Android系统维护的
			 */
			return result;
		}

		FileReader fr = null;
		BufferedReader localBufferedReader = null;

		try {
			fr = new FileReader(f);
			localBufferedReader = new BufferedReader(fr, 8192);
			cpu = localBufferedReader.readLine();
			if (null != cpu) {
				String[] cpuSplit = cpu.split(" ");
				result[0] = cpuSplit[1];
				result[1] = cpuSplit[13];
				result[2] = cpuSplit[14];
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		FileUtil.closeReader(localBufferedReader);
		return result;
	}

	public String[] getCpuAction() {
		String cpuPath = "/proc/stat";
		String cpu = "";
		String[] result = new String[7];
		
		File f = new File(cpuPath);
		if (!f.exists() || !f.canRead())
		{
			return result;
		}

		FileReader fr = null;
		BufferedReader localBufferedReader = null;

		try {
			fr = new FileReader(f);
			localBufferedReader = new BufferedReader(fr, 8192);
			cpu = localBufferedReader.readLine();
			if (null != cpu) {
				result = cpu.split(" ");

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileUtil.closeReader(localBufferedReader);
		return result;
	}

	public CpuUtils() {
		initCpuData();
	}

	private void initCpuData() {
		pCpu = o_pCpu = 0.0;
		aCpu = o_aCpu = 0.0;

	}

	public String getProcessCpuUsage(int pid) {

		String result = "";
		String[] result1 = null;
		String[] result2 = null;
		if (pid >= 0) {

			result1 = getProcessCpuAction(pid);
			if (null != result1) {
				pCpu = Double.parseDouble(result1[1])
						+ Double.parseDouble(result1[2]);
			}
			result2 = getCpuAction();
			if (null != result2) {
				aCpu = 0.0;
				for (int i = 2; i < result2.length; i++) {

					aCpu += Double.parseDouble(result2[i]);
				}
			}
			double usage = 0.0;
			if ((aCpu - o_aCpu) != 0) {
				usage = DoubleUtils.div(((pCpu - o_pCpu) * 100.00),
						(aCpu - o_aCpu), 2);
				if (usage < 0) {
					usage = 0;
				}
				else if (usage > 100)
				{
					usage = 100;
				}

			}
			o_pCpu = pCpu;
			o_aCpu = aCpu;
			result = String.valueOf(usage) + "%";
		}
		p_jif = pCpu;
		return result;
	}

	public static void getCpuUsageByCmd() {
		Process process;
		StringBuilder sb = new StringBuilder();
		String line = "";
		String cmd = "dumpsys cpuinfo";
		try {
			process = Runtime.getRuntime().exec(
					new String[] { "sh", "-c", cmd });
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			while (((line = br.readLine()) != null)) {
				// 去掉空白行数据
				line = line.trim();
				if (line.equals("")) {
					continue;
				}
				sb.append(line);
				sb.append("\r\n");
			}
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
