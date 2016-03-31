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

import java.util.Arrays;

public class DumpsysMemInfoPkgParseUtil {

	private static final String DUMPSYS_MEM_CMD = "dumpsys meminfo";
	
	/**
	 * 4.x
	 */
	private static final String KEY_WORD1 = "Native";
	private static final String KEY_WORD2 = "Dalvik";
	private static final String KEY_WORD3 = "TOTAL";
	private static final int ITEM_COUNT = 7;
	private static final int PSS_INDEX = 1;
	private static final int PRIV_INDEX = 3;
	private static final int HEAP_ALLOC_INDEX = 5;
	
	/**
	 * 2.x
	 */
	
	private static final String KEY_WORD1_2X = "allocated:";
	private static final String KEY_WORD2_2X = "(Pss):";
	private static final String KEY_WORD3_2X = "(priv dirty):";
	
	
	public static Integer[] run(String pkgName, int androidVersion)
	{
		String cmdstr = DUMPSYS_MEM_CMD + " " + pkgName;
		String result = CMDExecute.runCMD(cmdstr);
		if(androidVersion == Constant.ANDROID_4X)
		{
			return getMemValues4X(result);
		}
		else
		{
			return getMemValues2X(result);
		}
	}
	
	private static Integer[] getMemValues4X(String cmdResult)
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
			if(rows[i].indexOf(KEY_WORD1) != -1)
			{
				rows[i] = rows[i].trim();
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length == ITEM_COUNT)
				{
					result[Constant.PRIV_NATIVE_INDEX] = Integer.parseInt(columns[PRIV_INDEX].trim());
					result[Constant.PSS_NATIVE_INDEX] = Integer.parseInt(columns[PSS_INDEX].trim());
					result[Constant.HEAPALLOC_NATIVE_INDEX] = Integer.parseInt(columns[HEAP_ALLOC_INDEX].trim());
					continue;
				}
			}
			else if(rows[i].indexOf(KEY_WORD2) != -1)
			{
				rows[i] = rows[i].trim();
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length == ITEM_COUNT)
				{
					result[Constant.PRIV_DALVIK_INDEX] = Integer.parseInt(columns[PRIV_INDEX].trim());
					result[Constant.PSS_DALVIK_INDEX] = Integer.parseInt(columns[PSS_INDEX].trim());
					result[Constant.HEAPALLOC_DALVIK_INDEX] = Integer.parseInt(columns[HEAP_ALLOC_INDEX].trim());
					continue;
				}
			}
			else if(rows[i].indexOf(KEY_WORD3) != -1)
			{
				rows[i] = rows[i].trim();
				String[] columns = rows[i].split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length == ITEM_COUNT)
				{
					result[Constant.PRIV_TOTAL_INDEX] = Integer.parseInt(columns[PRIV_INDEX].trim());
					result[Constant.PSS_TOTAL_INDEX] = Integer.parseInt(columns[PSS_INDEX].trim());
					result[Constant.HEAPALLOC_TOTAL_INDEX] = Integer.parseInt(columns[HEAP_ALLOC_INDEX].trim());
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
		}

		return result;
	}
}
