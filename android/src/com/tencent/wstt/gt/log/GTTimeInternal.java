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
package com.tencent.wstt.gt.log;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.tencent.wstt.gt.PerfDigitalEntry;
import com.tencent.wstt.gt.internal.GTMemoryDaemonHelper;
import com.tencent.wstt.gt.plugin.PluginManager;
import com.tencent.wstt.gt.ui.model.GroupTimeEntry;
import com.tencent.wstt.gt.ui.model.NamedEntry;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;

public class GTTimeInternal {
	private static String lastSaveTimeLog = "GTTimeLog";
	private static String lastSaveTimeDetail = "GTTimeDetailLog";
	private static final NamedEntry[] EMPTY_NAMED_ENTRY = {};

	private static LogTimeController timeLogController = new LogTimeController();

	public static NamedEntry[] getEntrys() {
		List<GroupTimeEntry> root = timeLogController.getShowList();
		List<NamedEntry> result = new ArrayList<NamedEntry>();
		for (GroupTimeEntry gte : root)
		{
			List<TagTimeEntry> tagEntryList = gte.entrys();
			if (tagEntryList.size() > 0)
			{
				result.add(gte);
				for (TagTimeEntry tte : tagEntryList)
				{
					result.add(tte);
				}
			}
		}
		return result.toArray(EMPTY_NAMED_ENTRY);
	}

	public static GroupTimeEntry findGroupTimeEntry(String name) {
		return timeLogController.getGroupTimeEntry(name);
	}

	public static TagTimeEntry findTagTimeEntry(long tid, String parentName,
			String name) {
		return timeLogController.getTagTimeEntry(tid, parentName, name);
	}

	public static void saveTimeLog(String logFileName) {
		if (null != logFileName)
		{
			setLastSaveTimeLog(logFileName);
			timeLogController.saveAllCache(logFileName);
		}
	}

	public static void saveTimeLogDetail(String logFileName,
			TagTimeEntry tagTimeEntry) {
		setLastSaveTimeDetail(logFileName);
		timeLogController.saveCache(logFileName, tagTimeEntry);
	}

	public static String getLastSaveTimeLog() {
		return lastSaveTimeLog;
	}

	public static void setLastSaveTimeLog(String lastSaveTimeLog) {
		GTTimeInternal.lastSaveTimeLog = lastSaveTimeLog;
	}

	public static String getLastSaveTimeDetail() {
		return lastSaveTimeDetail;
	}

	public static void setLastSaveTimeDetail(String lastSaveTimeDetail) {
		GTTimeInternal.lastSaveTimeDetail = lastSaveTimeDetail;
	}

	public static boolean isETStarted() {
		return timeLogController.getState();
	}

	public static void setETStarted(boolean flag) {
		if (flag) // 如果想开启，需要先校验
		{
			if (!GTMemoryDaemonHelper.startGWOrProfValid())
			{
				return;
			}
		}
		timeLogController.setState(flag);
	}
	
	/**
	 * AIDL调用专用的方法。也会由tid区分是全局的还是线程内的
	 * @param entry
	 */
	public static void recordDigital(PerfDigitalEntry entry) {
		timeLogController.recordDigital(entry.getQueryEntry().tid,
				entry.getQueryEntry().group,
				entry.getQueryEntry().tag,
				entry.getDatas(),
				entry.getFunctionId());
	}

	public static void startDigital(PerfDigitalEntry entry) {
		timeLogController.startTime(entry.getQueryEntry().tid,
				entry.getQueryEntry().group,
				entry.getQueryEntry().tag,
				entry.getQueryEntry().exkey,
				entry.getData(),
				entry.getFunctionId());
	}

	public static long endDigital(PerfDigitalEntry entry) {
		return timeLogController.endTime(entry.getQueryEntry().tid,
				entry.getQueryEntry().group,
				entry.getQueryEntry().tag,
				entry.getQueryEntry().exkey,
				entry.getData(),
				entry.getFunctionId());
	}

	/**
	 * 清除屏幕上的日志
	 */
	public static void cleartimeInfo() {
		timeLogController.clearAllCache();
	}

	/**
	 * 分发客户端传递过来的插件，调用该方法需要保证sReceiver与Bundle有效
	 */
	public static void dispatchPiCommand(String sReceiver, Bundle bundle)
	{
		PluginManager.getInstance().dispatchCommand(sReceiver, bundle);
	}
	
	/**
	 * 分发客户端传递过来的插件，调用该方法需要保证sReceiver与Bundle有效
	 */
	public static void dispatchPiCommandSync(String sReceiver, Bundle bundle)
	{
		PluginManager.getInstance().dispatchCommandSync(sReceiver, bundle);
	}

	/**
	 * 这个方法是给mic的甘特图组件增加的
	 * @return
	 */
	public static List<GroupTimeEntry> getAllGroup() {
		return timeLogController.getShowList();
	}
}
