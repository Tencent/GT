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

import java.lang.reflect.Method;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Debug.MemoryInfo;

/**
 * 内存信息工具类。
 */
public class MemUtils {

	/**
	 * 获取内存信息：total、free、buffers、cached，单位MB
	 * 
	 * @return 内存信息
	 */
	public static long[] getMemInfo() {
		long memInfo[] = new long[4];
		try {
			Class<?> procClazz = Class.forName("android.os.Process");
			Class<?> paramTypes[] = new Class[] { String.class, String[].class,
					long[].class };
			Method readProclines = procClazz.getMethod("readProcLines",
					paramTypes);
			Object args[] = new Object[3];
			final String[] memInfoFields = new String[] { "MemTotal:",
					"MemFree:", "Buffers:", "Cached:" };
			long[] memInfoSizes = new long[memInfoFields.length];
			memInfoSizes[0] = 30;
			memInfoSizes[1] = -30;
			args[0] = new String("/proc/meminfo");
			args[1] = memInfoFields;
			args[2] = memInfoSizes;
			if (null != readProclines) {
				readProclines.invoke(null, args);
				for (int i = 0; i < memInfoSizes.length; i++) {
					memInfo[i] = memInfoSizes[i] / 1024;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return memInfo;
	}

	/**
	 * 获取空闲内存
	 * 
	 * @return 空闲内存
	 */
	public static String getFreeMem() {
		long[] memInfo = getMemInfo();
		return Long.toString(memInfo[1] + memInfo[2] + memInfo[3]) + "M";
	}

	/**
	 * 获取总内存
	 * 
	 * @return 总内存
	 */
	public static String getTotalMem() {
		long[] memInfo = getMemInfo();
		return Long.toString(memInfo[0]) + "M";
	}

	/**
	 * 获取空闲内存和总内存拼接字符串
	 * 
	 * @return 总内存
	 */
	public static String getFreeAndTotalMem() {
		long[] memInfo = getMemInfo();
		return Long.toString(memInfo[1] + memInfo[2] + memInfo[3]) + "M/"
				+ Long.toString(memInfo[0]) + "M";
	}

	/**
	 * 获取空闲内存和总内存拼接字符串 该方法引入的目的是一次不重复多次获取内存值，性能优化用
	 * 
	 * @return 总内存
	 */
	public static String trans2FreeAndTotalMem(long[] memInfo) {
		return Long.toString(memInfo[1] + memInfo[2] + memInfo[3]) + "M/"
				+ Long.toString(memInfo[0]) + "M";
	}

	/**
	 * 获取进程内存Private Dirty数据
	 * 
	 * @param context
	 * @param pid
	 *            进程ID
	 * @return nativePrivateDirty、dalvikPrivateDirty、 TotalPrivateDirty
	 */
	public static long[] getPrivDirty(Context context, int pid) {

		ActivityManager mAm = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		int[] pids = new int[1];
		pids[0] = pid;
		MemoryInfo[] memoryInfoArray = mAm.getProcessMemoryInfo(pids);
		MemoryInfo pidMemoryInfo = memoryInfoArray[0];
		long[] value = new long[3]; // Natvie Dalvik Total
		value[0] = pidMemoryInfo.nativePrivateDirty;
		value[1] = pidMemoryInfo.dalvikPrivateDirty;
		value[2] = pidMemoryInfo.getTotalPrivateDirty();
		return value;
	}

	/**
	 * 获取进程内存PSS数据
	 * 
	 * @param context
	 * @param pid
	 * @return nativePss、dalvikPss、TotalPss
	 */
	public static long[] getPSS(Context context, int pid) {
		long[] value = new long[3]; // Natvie Dalvik Total
		if (pid >= 0) {
			int[] pids = new int[1];
			pids[0] = pid;
			ActivityManager mAm = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo[] memoryInfoArray = mAm.getProcessMemoryInfo(pids);
			MemoryInfo pidMemoryInfo = memoryInfoArray[0];

			value[0] = pidMemoryInfo.nativePss;
			value[1] = pidMemoryInfo.dalvikPss;
			value[2] = pidMemoryInfo.getTotalPss();
		} else {
			value[0] = 0;
			value[1] = 0;
			value[2] = 0;
		}

		return value;
	}

	public static long[] getHeapNative() {
		int Native_HeapSize = 0;
		int Native_HeapAlloc = 1;
		long[] value = new long[2];
		value[Native_HeapSize] = Debug.getNativeHeapSize() >> 10;
		value[Native_HeapAlloc] = Debug.getNativeHeapAllocatedSize() >> 10;
		return value;
	}

	public static long[] getHeapDalvik() {
		int Total_HeapSize = 0;
		int Total_HeapAlloc = 1;

		long[] value_total = new long[2];
		value_total[Total_HeapSize] = Runtime.getRuntime().totalMemory() >> 10;
		value_total[Total_HeapAlloc] = (Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory()) >> 10;

		long[] value_native = getHeapNative();

		int Dalvik_HeapSize = 0;
		int Dalvik_HeapAlloc = 1;
		long[] value_dalvik = new long[2];
		value_dalvik[Dalvik_HeapSize] = value_total[Total_HeapSize]
				- value_native[0];
		value_dalvik[Dalvik_HeapAlloc] = value_total[Total_HeapAlloc]
				- value_native[1];

		return value_dalvik;
	}

	/**
	 * 获取堆内存数据，精确到KB Get VM Heap Size by calling:
	 * Runtime.getRuntime().totalMemory(); Get Allocated VM Memory by calling:
	 * Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	 * Get VM Heap Size Limit by calling: Runtime.getRuntime().maxMemory() Get
	 * Native Allocated Memory by calling: Debug.getNativeHeapAllocatedSize();
	 */
	public static long[] getVM() {
		long[] value = new long[5];
		value[0] = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
				.freeMemory()) >> 10;
		value[1] = Runtime.getRuntime().totalMemory() >> 10;

		value[2] = Debug.getNativeHeapAllocatedSize() >> 10;
		value[3] = Debug.getNativeHeapSize() >> 10;
		value[4] = Debug.getGlobalAllocSize() >> 10;
		return value;
	}
}
