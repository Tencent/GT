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
package com.tencent.wstt.gt.proInfo.floatView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.tencent.wstt.gt.api.utils.Env;


public class MemInfoByCMD {
	
	public static MemInfo getMemInfo(String packageName)
	{
		MemInfo result = null;
		String resultString = null;
		try {
			resultString = runCMD("dumpsys meminfo " + packageName);
		} catch (Exception e) {
			e.printStackTrace();
			return MemInfo.EMPTY;
		}

		if(Env.API < 14)
		{
			result = parseMemInfoFrom2x(resultString);
		}
		else if (Env.API < 19)
		{
			result = parseMemInfoFrom4x(resultString);
		}
		else
		{
			result = parseMemInfoFrom44(resultString);
		}

		return result;
	}
	
	private static MemInfo parseMemInfoFrom2x(String resultString)
	{
		String[] rows = null;

		MemInfo mi = new MemInfo();

		if (null == resultString) {
			return mi;
		}

		rows = resultString.split("\r\n");

		for (int i = 4; i < rows.length; ++i) {

			rows[i] = rows[i].trim();
			if (rows[i].indexOf("size") != -1) {
				mi.nativeHeapSize = Long.parseLong(rows[i].split("\\s+")[1]);
				mi.dalvikHeapSize = Long.parseLong(rows[i].split("\\s+")[2]);
			}

			if (rows[i].indexOf("allocated") != -1) {
				mi.nativeAllocated = Long.parseLong(rows[i].split("\\s+")[1]);
				mi.dalvikAllocated = Long.parseLong(rows[i].split("\\s+")[2]);
			}

			if (rows[i].indexOf("(Pss):") != -1) {
				mi.pss_total= Long.parseLong(rows[i].split("\\s+")[4]);
			}
			
			if (rows[i].indexOf("(priv") != -1) {
				mi.private_dirty= Long.parseLong(rows[i].split("\\s+")[5]);
				break;
			}
		}
		return mi;

	}
	
	private static MemInfo parseMemInfoFrom4x(String resultString)
	{
		String[] rows = null;
		boolean nativeIsFind = false;
		boolean dalvikIsFind = false;
		boolean ashemIsFind = false;
		boolean ohterDevIsFind = false;
		boolean unknownIsFind = false;
		
		MemInfo mi = new MemInfo();
		try
		{
			rows = resultString.split("\r\n");
			
			for(int i = 7;i < rows.length;++i){
				
				rows[i] = rows[i].trim();
				
				if (!nativeIsFind && rows[i].indexOf("Native") != -1) {
					nativeIsFind = true;
					mi.pss_Native = Long.parseLong(rows[i].split("\\s+")[1]);
					mi.nativeHeapSize = Long.parseLong(rows[i].split("\\s+")[4]);
					mi.nativeAllocated = Long.parseLong(rows[i].split("\\s+")[5]);
					continue;
				}
				else if (!dalvikIsFind && rows[i].indexOf("Dalvik") != -1) {
					dalvikIsFind = true;
					mi.pss_Dalvik = Long.parseLong(rows[i].split("\\s+")[1]);
					mi.dalvikHeapSize = Long.parseLong(rows[i].split("\\s+")[4]);
					mi.dalvikAllocated = Long.parseLong(rows[i].split("\\s+")[5]);
					continue;
				}
				else if (!ashemIsFind && rows[i].indexOf("Ashmem") != -1) {
					ashemIsFind = true;
					mi.pss_Ashmem = Long.parseLong(rows[i].split("\\s+")[1]);
					continue;
					
				}
				else if (!ohterDevIsFind && rows[i].indexOf("Other dev") != -1) {
					ohterDevIsFind = true;
					mi.pss_OtherDev = Long.parseLong(rows[i].split("\\s+")[2]); // 注意这行从2开始，Other dev中间有个空格
					i += 6; // to Unknow
					continue;
				}
				else if (!unknownIsFind && rows[i].indexOf("Unknown") != -1) {
					unknownIsFind = true;
					mi.pss_UnKnown = Long.parseLong(rows[i].split("\\s+")[1]);
					continue;
				}
				if (rows[i].indexOf("TOTAL") != -1) {
					mi.pss_total = Long.parseLong(rows[i].split("\\s+")[1]);
					mi.private_dirty = Long.parseLong(rows[i].split("\\s+")[3]);
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return mi;
	}

	private static MemInfo parseMemInfoFrom44(String resultString)
	{
		String[] rows = null;
		boolean nativeIsFind = false;
		boolean dalvikIsFind = false;
		boolean ohterDevIsFind = false;
		boolean graphicsIsFind = false; // since Android4.4
		boolean glIsFind = false; // since Android4.4
		boolean unknownIsFind = false;
		
		MemInfo mi = new MemInfo();
		
		try
		{
			rows = resultString.split("\r\n");
			
			for(int i = 7;i < rows.length;++i){
				
				rows[i] = rows[i].trim();
				
				if (!nativeIsFind && rows[i].indexOf("Native") != -1) {
					nativeIsFind = true;
					mi.pss_Native = Long.parseLong(rows[i].split("\\s+")[2]);
					mi.nativeHeapSize = Long.parseLong(rows[i].split("\\s+")[6]);
					mi.nativeAllocated = Long.parseLong(rows[i].split("\\s+")[7]);
					continue;
				}
				else if (!dalvikIsFind && rows[i].indexOf("Dalvik") != -1) {
					dalvikIsFind = true;
					mi.pss_Dalvik = Long.parseLong(rows[i].split("\\s+")[2]);
					mi.dalvikHeapSize = Long.parseLong(rows[i].split("\\s+")[6]);
					mi.dalvikAllocated = Long.parseLong(rows[i].split("\\s+")[7]);
					continue;
				}
				else if (!ohterDevIsFind && rows[i].indexOf("Other dev") != -1) {
					ohterDevIsFind = true;
					mi.pss_OtherDev = Long.parseLong(rows[i].split("\\s+")[2]); // 注意这行从2开始，Other dev中间有个空格
					i += 5; // to Graphics
					continue;
				}
				else if (!graphicsIsFind && rows[i].indexOf("Graphics") != -1) {
					graphicsIsFind = true;
					mi.pss_graphics = Long.parseLong(rows[i].split("\\s+")[1]);
					continue;
				}
				else if (!glIsFind && rows[i].indexOf("GL") != -1) {
					glIsFind = true;
					mi.pss_gl = Long.parseLong(rows[i].split("\\s+")[1]);
					continue;
				}
				else if (!unknownIsFind && rows[i].indexOf("Unknown") != -1) {
					unknownIsFind = true;
					mi.pss_UnKnown = Long.parseLong(rows[i].split("\\s+")[1]);
					continue;
				}
				if (rows[i].indexOf("TOTAL") != -1) {
					mi.pss_total = Long.parseLong(rows[i].split("\\s+")[1]);
					mi.private_dirty = Long.parseLong(rows[i].split("\\s+")[2]);
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return mi;
	}

	public static String runCMD(String cmdString) {
		
		ProcessBuilder execBuilder = null;
		execBuilder = new ProcessBuilder("su", "-c", cmdString);
		//execBuilder.redirectErrorStream(true);

		Process exec = null;
		try {
			exec = execBuilder.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream is = exec.getInputStream();
		
		String result = "";
		String line = "";
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					is));
			while ((line = br.readLine()) != null) {
				result += line;
				result += "\r\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
