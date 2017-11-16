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
package com.tencent.wstt.gt.api.base;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.PerfDigitalEntry;
import com.tencent.wstt.gt.QueryPerfEntry;
import com.tencent.wstt.gt.log.GTTimeInternal;
import android.os.Process;

/**
 * 控制台本身及工具使用的耗时统计服务入口类。
 */
public class GTTime {
	
	/**
	 * 耗时统计开关-开
	 */
	public static void enable()
	{
		GTTimeInternal.setETStarted(true);
	}
	
	/**
	 * 耗时统计开关-关
	 */
	public static void disable()
	{
		GTTimeInternal.setETStarted(false);
	}
	
	/**
	 * 获取统计开关是否打开
	 * @return
	 */
	public static boolean isEnable() {
		return GTTimeInternal.isETStarted();
	}
	
	private static PerfDigitalEntry creatPerfDigitalEntry(int functionId,
			String group, String tag, long pid, int[] exKeys)
	{
		PerfDigitalEntry entry = new PerfDigitalEntry();
		entry.setData(System.nanoTime());
		entry.setFunctionId(functionId);
		entry.setLogTime(System.currentTimeMillis());
		
		int exKey = 0;
		if (null != exKeys && exKeys.length > 0)
		{
			exKey = exKeys[0];
		}
		QueryPerfEntry queryPerfEntry = new QueryPerfEntry(group, tag, pid, exKey);
		entry.setQueryEntry(queryPerfEntry);
		
		return entry;
	}
	
	/**
	 * 开始一次全局耗时统计。不区分被测程序侧和控制台侧，所以可以进行跨应用统计。
	 * 需要注意，
	 * 1.startTime和endTime对不建议在递归体中使用；
	 * 2.如果需要高精度统计，startTime和endTime对不建议嵌套使用，
	 * 即在一对startTime和endTime结束统计后，再进行下一对的startTime和endTime。
	 * 
	 * @param group 统计分组
	 * @param tag 统计标签
	 */
	public static void startTime(String group, String tag, int[] exKeys) {
		
		PerfDigitalEntry entry = creatPerfDigitalEntry(
				Functions.PERF_START_TIME_GLOBAL, group, tag, 0, exKeys);
		GTTimeInternal.startDigital(entry);
	}

	/**
	 * 结束一次全局耗时统计，注意控制台侧不需要跨进程的概念。
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @return 本次统计的差值，精度是纳秒
	 */
	public static long endTime(String group, String tag, int[] exKeys) {
		PerfDigitalEntry entry = creatPerfDigitalEntry(
				Functions.PERF_END_TIME_GLOBAL, group, tag, 0, exKeys);
		return GTTimeInternal.endDigital(entry);
	}
	
	/**
	 * 开始一次线程内耗时统计。注意这里是控制台侧的统计，所以线程号取负数与被测端区别
	 * 注意项同startTime方法
	 * @param group 统计分组
	 * @param tag 统计标签
	 */
	public static void startTimeInThread(String group, String tag, int[] exKeys) {
		PerfDigitalEntry entry = creatPerfDigitalEntry(
				Functions.PERF_START_TIME_IN_THREAD,
				group, tag, 0 - Process.myTid(), exKeys);
		GTTimeInternal.startDigital(entry); // TODO 也许应该和客户端一样用reduceTime
	}

	/**
	 * 结束一次线程内耗时统计。注意这里是控制台侧的统计，所以线程号取负数与被测端区别
	 * @param group 统计分组
	 * @param tag 统计标签
	 * @return 本次统计的差值，精度是纳秒
	 */
	public static long endTimeInThread(String group, String tag, int[] exKeys) {
		PerfDigitalEntry entry = creatPerfDigitalEntry(
				Functions.PERF_END_TIME_IN_THREAD, group, tag, 0, exKeys);
		return GTTimeInternal.endDigital(entry);
	}
}
