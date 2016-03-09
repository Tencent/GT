/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;


import java.util.Arrays;

import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;

/**
* @Description dumpsys meminfo pkg 命令解析工具类 
* @date 2013年11月10日 下午5:42:52 
*
 */
public class DumpsysMemInfoPkgParseUtil {

	//private static final String CMD = "adb shell dumpsys meminfo";
	private static final String DUMPSYS_MEM_CMD = "dumpsys meminfo";
	
	/**
	 * 4.x
	 */
	private static final String NATIVE = "Native";
	private static final String DALVIK = "Dalvik";
	private static final String TOTAL = "TOTAL";
	
	private static final int ITEM_COUNTS[] = {6, 8,8};
	private static final int PSS_INDEXS[] = {0,0};
	private static final int PRIV_INDEXS[] = {2,1};
	private static final int HEAP_ALLOC_INDEXS[] = {4,5};
	private static final int HEAP_SIZE_INDEXS[] = {3,4};
	
	private static final int ICS_KIKKAT = 0;
	private static final int KIKKAT = 1;
	private static final int X5 = 1;
	
	
	/**
	 * 2.x
	 */
	
	private static final String KEY_WORD1_2X = "allocated:";
	private static final String KEY_WORD2_2X = "(Pss):";
	private static final String KEY_WORD3_2X = "(priv dirty):";
	private static final String KEY_WORD4_2X = "size:";
	
	
	public static Integer[] run(String pkgName, int androidVersion)
	{
		try
		{
			String cmdstr = PCInfo.adbShell + " " + DUMPSYS_MEM_CMD + " " + pkgName;
			String result = CMDExecute.runCMD(cmdstr);
			if(androidVersion == Constant.ANDROID_4X)
			{
				return getMemValues4X(result, ICS_KIKKAT);
			}
			else if(androidVersion == Constant.ANDROID_KITKAT)
			{
				return getMemValues4X(result, KIKKAT);
			}
			else if(androidVersion == Constant.ANDROID_5X)
			{
				return getMemValues4X(result, X5);
			}
			else
			{
				return getMemValues2X(result);
			}
		}
		catch (Exception e)
		{
			return null;
		}
		
	}
	
	private static Integer[] getMemValues4X(String cmdResult, int ver)
	{
		Integer[] result = new Integer[Constant.ALL_MEM_KIND_COUNT];
		Arrays.fill(result, 0);
		
		if(cmdResult == null)
		{
			return result;
		}
		
		String[] rows = cmdResult.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			return result;
		}

		int valIndex = ver+1;
		for(int i = 0; i < rows.length; i++)
		{
			if(rows[i].indexOf(NATIVE) != -1)
			{
				rows[i] = rows[i].trim();
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= ITEM_COUNTS[ver])
				{
					result[Constant.PRIV_NATIVE_INDEX] = Integer.parseInt(columns[PRIV_INDEXS[ver]+valIndex].trim());
					result[Constant.PSS_NATIVE_INDEX] = Integer.parseInt(columns[PSS_INDEXS[ver]+valIndex].trim());
					result[Constant.HEAPALLOC_NATIVE_INDEX] = Integer.parseInt(columns[HEAP_ALLOC_INDEXS[ver]+valIndex].trim());
					result[Constant.HEAPSIZE_NATIVE_INDEX] = Integer.parseInt(columns[HEAP_SIZE_INDEXS[ver]+valIndex].trim());
					continue;
				}
			}
			else if(rows[i].indexOf(DALVIK) != -1)
			{
				rows[i] = rows[i].trim();
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= ITEM_COUNTS[ver])
				{
					result[Constant.PRIV_DALVIK_INDEX] = Integer.parseInt(columns[PRIV_INDEXS[ver]+valIndex].trim());
					result[Constant.PSS_DALVIK_INDEX] = Integer.parseInt(columns[PSS_INDEXS[ver]+valIndex].trim());
					result[Constant.HEAPALLOC_DALVIK_INDEX] = Integer.parseInt(columns[HEAP_ALLOC_INDEXS[ver]+valIndex].trim());
					result[Constant.HEAPSIZE_DALVIK_INDEX] = Integer.parseInt(columns[HEAP_SIZE_INDEXS[ver]+valIndex].trim());
					continue;
				}
			}
			else if(rows[i].indexOf(TOTAL) != -1)
			{
				rows[i] = rows[i].trim();
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= ITEM_COUNTS[ver])
				{
					result[Constant.PRIV_TOTAL_INDEX] = Integer.parseInt(columns[PRIV_INDEXS[ver]+1].trim());
					result[Constant.PSS_TOTAL_INDEX] = Integer.parseInt(columns[PSS_INDEXS[ver]+1].trim());
					result[Constant.HEAPSIZE_TOTAL_INDEX] = Integer.parseInt(columns[HEAP_SIZE_INDEXS[ver]+1].trim());
					result[Constant.HEAPALLOC_TOTAL_INDEX] = Integer.parseInt(columns[HEAP_ALLOC_INDEXS[ver]+1].trim());
					continue;
				}
			}
		}

		return result;
	}
	
	
	private static Integer[] getMemValues2X(String cmdResult)
	{
		Integer[] result = new Integer[Constant.ALL_MEM_KIND_COUNT];
		Arrays.fill(result, 0);
		
		if(cmdResult == null)
		{
			return result;
		}
		
		String[] rows = cmdResult.split(Constant.CMD_RESULT_SPLIT);
		if(rows == null)
		{
			return result;
		}
		
		for(int i = 0; i < rows.length; i++)
		{
			if(rows[i].indexOf(KEY_WORD1_2X) != -1)
			{
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= 5)
				{
					result[Constant.HEAPALLOC_NATIVE_INDEX] = Integer.parseInt(columns[1].trim());
					result[Constant.HEAPALLOC_DALVIK_INDEX] = Integer.parseInt(columns[2].trim());
					result[Constant.HEAPALLOC_TOTAL_INDEX] = Integer.parseInt(columns[4].trim());
					continue;
				}
			}
			else if(rows[i].indexOf(KEY_WORD2_2X) != -1)
			{
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= 5)
				{
					result[Constant.PSS_NATIVE_INDEX] = Integer.parseInt(columns[1].trim());
					result[Constant.PSS_DALVIK_INDEX] = Integer.parseInt(columns[2].trim());
					result[Constant.PSS_TOTAL_INDEX] = Integer.parseInt(columns[4].trim());
					continue;
				}
			}
			else if(rows[i].indexOf(KEY_WORD3_2X) != -1)
			{
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= 6)
				{
					result[Constant.PRIV_NATIVE_INDEX] = Integer.parseInt(columns[2].trim());
					result[Constant.PRIV_DALVIK_INDEX] = Integer.parseInt(columns[3].trim());
					result[Constant.PRIV_TOTAL_INDEX] = Integer.parseInt(columns[5].trim());
					continue;
				}
			}
			else if(rows[i].indexOf(KEY_WORD4_2X) != -1)
			{
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= 5)
				{
					result[Constant.HEAPSIZE_NATIVE_INDEX] = Integer.parseInt(columns[1].trim());
					result[Constant.HEAPSIZE_DALVIK_INDEX] = Integer.parseInt(columns[2].trim());
					result[Constant.HEAPSIZE_TOTAL_INDEX] = Integer.parseInt(columns[4].trim());
					continue;
				}
			}
		}

		return result;
	}
}
