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
package com.tencent.wstt.gt;

public interface Functions {
	// 设置出参
	public static final int SET_OUT_PARA = 1;
	
	// 设置入参
	public static final int SET_IN_PARA = 2;
	
	// 注册出参
	public static final int REGISTER_OUT_PARA = 3;
	
	// 注册入参
	public static final int  REGISTER_IN_PARA = 4;
	
	// 单点耗时性能统计类
	public static final int PERF_REDUCE_TIME = 5;

	// 双点耗时性能统计类，全局耗时统计
	public static final int PERF_START_TIME_GLOBAL = 6;
	public static final int PERF_END_TIME_GLOBAL = 7;

	/*
	 * 双点性能类，服务端线程内占位，目前在控制台也无实际关系逻辑，即控制台侧的
	 * start、end目前并不需要functionId
	 * 控制台侧也不需要跨进程的概念
	 */
	public static final int PERF_START_TIME_IN_THREAD = 8;
	public static final int PERF_END_TIME_IN_THREAD = 9;
	
	public static final int SET_PROFILER_ENABLE = 10;
	public static final int SET_FLOATVIEW_FRONT = 11;
	
	// 单点通用数字型性能统计类
	public static final int PERF_DIGITAL_NORMAL = 12;
	
	// 单点CPU性能统计类
	public static final int PERF_DIGITAL_CPU = 13;
	
	// 单点多维度性能统计类
	public static final int PERF_DIGITAL_MULT = 14;
	public static final int PERF_DIGITAL_MULT_MEM = 15; // 以kb为单位的内存值，需要转成mb
	
	// 单点通用非数字型性能统计类
	public static final int PERF_STRING_NORMAL = 16;
	
	// 双点通用数字型性能统计类
	public static final int PERF_START_DIGITAL_GLOBAL = 17;
	public static final int PERF_END_DIGITAL_GLOBAL = 18;

	// 电压数据，整数位单位是伏特，小数保持3位即可
	public static final int PERF_DIGITAL_VOLT = 0xFF + 1;
	
	
	// GT通用控制命令
	public static final String GT_COMMAND = "&gt_cmd_";
	public static final String GT_COMMAND_KEY = "&gt_cmd_k";
	public static final String GT_CMD_KEY_VERSION = "&gt_cmd_k_v";

	// 获取服务端内部版本号
	public static final int GT_CMD_GET_VERSION = 0;
	public static final int GT_CMD_START_PROCTEST = 1;
	public static final int GT_CMD_END_PROCTEST = 2;
	public static final int GT_CMD_START_SAMPLE = 3;
	public static final int GT_CMD_STOP_SAMPLE = 4;
	public static final int GT_CMD_SAMPLE = 5;

	public static final int GT_CMD_SET_SAMPLE_RATE = 6;

	public static final int GT_CMD_END_TEST_AND_CLEAR = 7;
	public static final int GT_CMD_TEST_DATA_CLEAR = 8;

	// 结束并保存清理耗时统计
	public static final int GT_CMD_END_ET_AND_CLEAR = 9;
}
