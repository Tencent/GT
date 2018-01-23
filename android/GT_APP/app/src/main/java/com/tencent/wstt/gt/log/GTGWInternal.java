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

import com.tencent.wstt.gt.api.utils.NetUtils;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.utils.GTUtils;

public class GTGWInternal {
	private static String lastSaveFolder = "";

	public static void saveGWDataForSM(GWSaveEntry saveEntry,
			TagTimeEntry tagTimeEntry) {
		setLastSaveFolder(saveEntry.path3);
		String now = GTUtils.getSaveDate();
		saveEntry.setNow(now);
		LogUtils.writeGWDataForSM(saveEntry, tagTimeEntry);
		LogUtils.writeGWDesc(saveEntry, tagTimeEntry);
	}

	public static void saveGWData(GWSaveEntry saveEntry,
			TagTimeEntry tagTimeEntry) {
		setLastSaveFolder(saveEntry.path3);
		String now = GTUtils.getSaveDate();
		saveEntry.setNow(now);
		LogUtils.writeGWData(saveEntry, tagTimeEntry);
		LogUtils.writeGWDesc(saveEntry, tagTimeEntry);
	}
	
	public static void saveAllGWData(GWSaveEntry saveEntry) {
		setLastSaveFolder(saveEntry.path3);
		String now = GTUtils.getSaveDate();
		saveEntry.setNow(now);
		TagTimeEntry[] ttes = OpPerfBridge.getAllProfilerData();
		for (TagTimeEntry tte : ttes)
		{
			if (null != tte && tte.getAlias().equals("SM"))
			{
				LogUtils.writeGWDataForSM(saveEntry, tte);
			}
			else
			{
				LogUtils.writeGWData(saveEntry, tte);
			}
		}
		LogUtils.writeGWDesc(saveEntry, ttes);
	}

	public static void saveAllEnableGWData(GWSaveEntry saveEntry) {
		setLastSaveFolder(saveEntry.path3);
		String now = GTUtils.getSaveDate();
		saveEntry.setNow(now);
		TagTimeEntry[] ttes = OpPerfBridge.getAllEnableProfilerData();
		for (TagTimeEntry tte : ttes)
		{
			if (null != tte && tte.getAlias().equals("SM"))
			{
				LogUtils.writeGWDataForSM(saveEntry, tte);
			}
			else
			{
				LogUtils.writeGWData(saveEntry, tte);
			}
		}
		LogUtils.writeGWDesc(saveEntry, ttes);
	}
	
	public static void clearAllGWData()
	{
		TagTimeEntry[] ttes = OpPerfBridge.getAllProfilerData();
		for (TagTimeEntry tte : ttes)
		{
			tte.clear();
			// 如果是流量数据，还要同时reset
			String key = tte.getName();
			NetUtils.clearNetValue(key);
		}
	}
	
	public static void clearAllEnableGWData()
	{
		TagTimeEntry[] ttes = OpPerfBridge.getAllEnableProfilerData();
		for (TagTimeEntry tte : ttes)
		{
			tte.clear();
			// 如果是流量数据，还要同时reset
			String key = tte.getName();
			NetUtils.clearNetValue(key);
		}
	}

	public static String getLastSaveFolder() {
		return lastSaveFolder;
	}

	public static void setLastSaveFolder(String lastSaveFolder) {
		GTGWInternal.lastSaveFolder = lastSaveFolder;
	}
}
