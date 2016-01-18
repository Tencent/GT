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
package com.tencent.wstt.gt.client.internal.connect;

import android.os.Bundle;
import android.os.Process;

import com.tencent.wstt.gt.AidlTask;
import com.tencent.wstt.gt.BooleanEntry;
import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.PerfDigitalEntry;
import com.tencent.wstt.gt.QueryPerfEntry;
import com.tencent.wstt.gt.data.control.DataCacheController;
import com.tencent.wstt.gt.data.local.LocalEndTimePerfEntry;
import com.tencent.wstt.gt.data.local.LocalNumberDataPerfEntry;
import com.tencent.wstt.gt.data.local.LocalStartTimePerfEntry;

/**
 * 需要缓存的连接态抽象类，放connecting和connected态的公共实现
 */
public class AbsDataCachedConnState extends AbsConnState {
	
	protected DataCacheController dataCacheController;
	
	public AbsDataCachedConnState(DataCacheController dataCacheController)
	{
		this.dataCacheController = dataCacheController;
	}
	
	private void log(int level, String tag, String msg) {
		long pid = Process.myPid();
		String[] content = new String[4];
		content[0] = String.valueOf(pid);
		content[1] = String.valueOf(level);
		content[2] = tag;
		content[3] = msg;
		
		dataCacheController.putLog(content);
	}

	@Override
	public void logI(String tag, String msg) {
		log(LOG_INFO, tag, msg);
	}

	@Override
	public void logD(String tag, String msg) {
		log(LOG_DEBUG, tag, msg);
	}

	@Override
	public void logW(String tag, String msg) {
		log(LOG_WARNING, tag, msg);
	}

	@Override
	public void logE(String tag, String msg) {
		log(LOG_ERROR, tag, msg);
	}
	
	@Override
	public void startTimeInThread(String group, String tag, int... exKeys) {
		int exKey = 0;
		if (null != exKeys && exKeys.length > 0)
		{
			exKey = exKeys[0]; // 为简单起见，QueryPerfEntry只接受一个额外key
		}
		QueryPerfEntry queryPerfEntry =
				new QueryPerfEntry(group, tag, Process.myTid(), exKey);

		LocalNumberDataPerfEntry entry = new LocalStartTimePerfEntry(queryPerfEntry);
		entry.setLogTime(System.currentTimeMillis());
		entry.setData(System.nanoTime());

		dataCacheController.putPerfData(entry);
	}

	@Override
	public long endTimeInThread(String group, String tag, int... exKeys) {
		int exKey = 0;
		if (null != exKeys && exKeys.length > 0)
		{
			exKey = exKeys[0]; // 为简单起见，QueryPerfEntry只接受一个额外key
		}
		QueryPerfEntry queryPerfEntry =
				new QueryPerfEntry(group, tag, Process.myTid(), exKey);

		LocalNumberDataPerfEntry entry = new LocalEndTimePerfEntry(queryPerfEntry);
		entry.setLogTime(System.currentTimeMillis());
		entry.setData(System.nanoTime());

		AidlTask resultTask = dataCacheController.profilerData(entry);
		if (null != resultTask && resultTask instanceof PerfDigitalEntry)
		{
			return ((PerfDigitalEntry)resultTask).getData();
		}

		return -1;
	}

	@Override
	public void startTime(String group, String tag, int... exKeys) {
		int exKey = 0;
		if (null != exKeys && exKeys.length > 0)
		{
			exKey = exKeys[0]; // 为简单起见，QueryPerfEntry只接受一个额外key
		}
		QueryPerfEntry queryPerfEntry = new QueryPerfEntry(group, tag, 0, exKey);

		LocalNumberDataPerfEntry entry = new LocalStartTimePerfEntry(queryPerfEntry);
		entry.setLogTime(System.currentTimeMillis());
		entry.setData(System.nanoTime());

		dataCacheController.putPerfData(entry);
	}

	@Override
	public long endTime(String group, String tag, int... exKeys) {
		int exKey = 0;
		if (null != exKeys && exKeys.length > 0)
		{
			exKey = exKeys[0]; // 为简单起见，QueryPerfEntry只接受一个额外key
		}
		QueryPerfEntry queryPerfEntry = new QueryPerfEntry(group, tag, 0, exKey);

		LocalNumberDataPerfEntry entry = new LocalEndTimePerfEntry(queryPerfEntry);
		entry.setLogTime(System.currentTimeMillis());
		entry.setData(System.nanoTime());
		entry.setQueryEntry(queryPerfEntry);

		AidlTask resultTask = dataCacheController.profilerData(entry);
		if (null != resultTask && resultTask instanceof PerfDigitalEntry)
		{
			return ((PerfDigitalEntry)resultTask).getData();
		}

		return -1;
	}
	
	@Override
	public void startTimeAcrossProcess(String group, String tag, int... exKeys) {
		int exKey = 0;
		if (null != exKeys && exKeys.length > 0)
		{
			exKey = exKeys[0]; // 为简单起见，QueryPerfEntry只接受一个额外key
		}
		QueryPerfEntry queryPerfEntry = new QueryPerfEntry(group, tag, 0, exKey);

		PerfDigitalEntry entry = new PerfDigitalEntry();
		entry.setFunctionId(Functions.PERF_START_TIME_GLOBAL);
		entry.setQueryEntry(queryPerfEntry);
		entry.setLogTime(System.currentTimeMillis());
		entry.setData(System.nanoTime());

		dataCacheController.putPerfTask(entry);
	}

	@Override
	public void endTimeAcrossProcess(String group, String tag, int... exKeys) {
		int exKey = 0;
		if (null != exKeys && exKeys.length > 0)
		{
			exKey = exKeys[0]; // 为简单起见，QueryPerfEntry只接受一个额外key
		}
		QueryPerfEntry queryPerfEntry = new QueryPerfEntry(group, tag, 0, exKey);

		PerfDigitalEntry entry = new PerfDigitalEntry();
		entry.setFunctionId(Functions.PERF_END_TIME_GLOBAL);
		entry.setQueryEntry(queryPerfEntry);
		entry.setLogTime(System.currentTimeMillis());
		entry.setData(System.nanoTime());

		dataCacheController.putPerfTask(entry);
	}
	
	@Override
	public void setProfilerEnable(boolean flag) {
		BooleanEntry entry = new BooleanEntry();
		entry.setFunctionId(Functions.SET_PROFILER_ENABLE);
		entry.setData(flag);
		dataCacheController.putBooleanTask(entry);
	}

	@Override
	public void setFloatViewFront(boolean flag) {
		BooleanEntry entry = new BooleanEntry();
		entry.setFunctionId(Functions.SET_FLOATVIEW_FRONT);
		entry.setData(flag);
		dataCacheController.putBooleanTask(entry);
	}
	
	@Override
	public void setCommand(String receiver, Bundle bundle) {
		bundle.putString(Functions.GT_COMMAND, receiver);
		dataCacheController.putCommandTask(bundle);
	}
	
	protected boolean matchInParaType(String str, String type){
		boolean result = false;
		if(type.equals("int")){
			result = determineInParaType(str, 0);
		}
		if(type.equals("boolean")){
			result = determineInParaType(str, 1);
		}
		if(type.equals("long")){
			result = determineInParaType(str, 2);
		}
		if(type.equals("double")){
			result = determineInParaType(str, 3);
		}
		if(type.equals("float")){
			result = determineInParaType(str, 4);
		}
		if(type.equals("short")){
			result = determineInParaType(str, 5);
		}
		if(type.equals("byte")){
			result = determineInParaType(str, 6);
		}
		return result;
	}
	
	private boolean determineInParaType(String str, int type){
		boolean result = true;
		
		switch(type){
		case 0:
			char[] cs = str.toCharArray();
			for(int i = 0 ; i < cs.length ; i++){
				int ascii = (int)cs[i];
				if(ascii < 48 || ascii > 57){
					if(ascii != 45){
						result = false;
						break;
					}
				}
			}
			break;
		case 1:
			if(!str.equals("true") && !str.equals("false")){
				result = false;
			}
			break;
		case 2:
			char[] cs_long = str.toCharArray();
			for(int i = 0 ; i < cs_long.length ; i++){
				int ascii = (int)cs_long[i];
				if(ascii < 48 || ascii > 57){
					result = false;
					break;
				}
			}
			break;
		case 3:
			char[] cs_double = str.toCharArray();
			for(int i = 0 ; i < cs_double.length ; i++){
				int ascii = (int)cs_double[i];
				if(ascii < 48 || ascii > 57){
					if(ascii != 46 && ascii != 45){
						result = false;
						break;
					}
				}
			}
			break;
		case 4:
			char[] cs_float = str.toCharArray();
			for(int i = 0 ; i < cs_float.length ; i++){
				int ascii = (int)cs_float[i];
				if(ascii < 48 || ascii > 57){
					if(ascii != 46 && ascii != 45){
						result = false;
						break;
					}
				}
			}
			break;
		case 5:
			char[] cs_short = str.toCharArray();
			for(int i = 0 ; i < cs_short.length ; i++){
				int ascii = (int)cs_short[i];
				if(ascii < 48 || ascii > 57){
					if(ascii != 46 && ascii != 45){
						result = false;
						break;
					}
				}
			}
			break;
		case 6:
			char[] cs_byte = str.toCharArray();
			for(int i = 0 ; i < cs_byte.length ; i++){
				int ascii = (int)cs_byte[i];
				if(ascii < 48 || ascii > 57){
					result = false;
					break;
				}
			}
			break;
		}
			
		return result;
	}
}
