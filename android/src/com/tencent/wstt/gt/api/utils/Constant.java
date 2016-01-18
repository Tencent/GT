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

import java.util.HashMap;
import java.util.Map;

/**
 * APT中用到的一些常量数据
 */
public final class Constant {
	
	/**
	 * 写文件相关常量
	 */
	public final static String APTLOG_FILENAME_PREFIX = "APT";
	public final static String APTLOG_FILENAME_SPLIT = "_";
	public final static String APTLOG_FILENAME_SUFFIX = ".log";
	public static final String APTLOG_FILECONTENT_SPLIT = ",";
	public static final String APTLOG_FILECONTENT_NEWLINE = "\r\n";
	
	/**
	 * APTlog文件解析
	 */
	public static final String APTLOG_PKGNAME = "PKG_NAME";
	public static final String APTLOG_TESTITEM = "TESTITEM";
	public static final String APTLOG_COLUMN_NAME = "COLUMN_NAME";
	public static final String APTLOG_KEYVALUE_SPLIT = "=";
	
	
	/**
	 * 解析adb shell命令中用到的常量
	 */
	public static final String CMD_RESULT_SPLIT = "\r\n";
	public static final String BLANK_SPLIT = "\\s+";
	public static final String DATAITEM_SPLIT = ";";
	
	
	/**
	 * JFreechart相关常量
	 */
	
	public static final float LINE_WIDTH = 2.0f;
	
	/**
	 * CPU测试相关
	 */
	
	public final static int ALL_CPU_KIND_COUNT = 2;

	public final static int CPU_PERSENT_INDEX = 0;
	public final static int CPU_JIFFIES_INDEX = 1;

	public final static String CPU_ITEM_TITLES[] = { "CPU%", "Jiffies" };

	/**
	 * CPU百分比获取的两种方式
	 */
	public final static int CPU_TESTMETHOD_NUMBER = 2;
	public final static int TOP_INDEX = 0;
	public final static int DUMPSYS_CPUINFO_INDEX = 1;
	public final static String[] CPU_TESTMETHOD_TITLES = {"top", "dumpsys cpuinfo"};
	
	public final static Map<String, Integer> CPU_ITEM_MAPS = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;

		{
			put("top", 0);
			put("dumpsys cpuinfo", 1);
		}

	};
	
	/**
	 * CPU采样频率建议值()
	 */
	public final static int TOP_UPDATE_PERIOD = 3;
	
	/**
	 * 内存测试相关
	 */
	public final static int ALL_MEM_KIND_COUNT = 9;

	public final static int PRIV_NATIVE_INDEX = 0;
	public final static int PRIV_DALVIK_INDEX = 1;
	public final static int PRIV_TOTAL_INDEX = 2;

	public final static int PSS_NATIVE_INDEX = 3;
	public final static int PSS_DALVIK_INDEX = 4;
	public final static int PSS_TOTAL_INDEX = 5;

	public final static int HEAPALLOC_NATIVE_INDEX = 6;
	public final static int HEAPALLOC_DALVIK_INDEX = 7;
	public final static int HEAPALLOC_TOTAL_INDEX = 8;

	public final static String MEM_ITEM_TITLES[] = { "PrivNative", "PrivDalvik",
			"PrivTotal", "PSSNative", "PSSDalvik", "PSSTotal",
			"HeapAllocNative", "HeapAllocDalvik", "HeapAllocTotal" };
	
	/**
	 * 获取的内存数据中包括下面9中数据
	 */
	public final static Map<String, Integer> MEM_ITEM_MAPS = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;

		{
			put("PrivNative", 0);
			put("PrivDalvik", 1);
			put("PrivTotal", 2);
			put("PSSNative", 3);
			put("PSSDalvik", 4);
			put("PSSTotal", 5);
			put("HeapAllocNative", 6);
			put("HeapAllocDalvik", 7);
			put("HeapAllocTotal", 8);
		}

	};
	
	/**
	 * 目前APT提供CPU、jiffies和内存这三方面的测试数据	
	 */
	public final static int TEST_ITEM_COUNT = 2;
	public final static int CPU_INDEX = 0;
	public final static int MEM_INDEX = 1;
	public final static String TEXT_ITEM_TITLES[] = {"CPU", "Memory"};
	
	
	/**
	 * 系统版本
	 */
	public final static int ANDROID_4X = 0;
	public final static int ANDROID_2X = 1;

	/**
	 * Return code
	 */
	
	public enum PhoneState
	{
		STATE_OK,STATE_NOT_ADB,STATE_NOT_FOUND_PHONE,STATE_MULTI_FOUND_PHONE
	}
	public final static int FAILED = 10;
	
	
	/**
	 * UI相关
	 */
	public final static int MARGIN_WIDTH = 10;
	public final static int MARGIN_WIDTH_NARROW = 5;
	public final static int VIEW_MARGIN_WIDTH = 5;
	
	/**
	 * 其他
	 */
	public final static String APT_OFFICIAL_WEBSITE = "http://pub.code.oa.com/project/home?projectName=APT";
	public final static String APT_SERVER_URL = "http://10.6.222.155:8080/apt/upload/";
	//支持的最大测试进程数量
	public final static int MAX_PKG_NUMBER = 3;
	
	//手动指定的进程的不存在时，pid值
	public final static String PID_NOT_EXSIT = "-1";
	
	//ADT PluginId
	public final static String ADT_PLUGIN_ID = "com.android.ide.eclipse.adt";
	public final static String ADT_PLUGIN_PREFERENCES_SDK = "com.android.ide.eclipse.adt.sdk";
	public final static String ADB_DIR_NAME = "platform-tools";
}
