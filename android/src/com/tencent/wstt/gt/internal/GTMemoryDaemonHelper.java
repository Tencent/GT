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
package com.tencent.wstt.gt.internal;

import java.util.List;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.api.base.GTTime;
import com.tencent.wstt.gt.log.GTTimeInternal;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.ui.model.GroupTimeEntry;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.utils.ToastUtil;

public class GTMemoryDaemonHelper {
	
	public static boolean startGWOrProfValid()
	{
		/*
		 * 判断GW和Prof的总数是否超上限，超上限则不允许checked，提示用户
		 */
		int total = 0;
		TagTimeEntry[] opPerfDatas = OpPerfBridge.getAllProfilerData();
		for (TagTimeEntry tte : opPerfDatas)
		{
			int single = 0;
			if (tte.hasChild())
			{
				single = tte.getChildren()[0].getRecordSize();
				// 总值计算时，复数要乘倍数
				total += single * tte.getChildren().length;
			}
			else
			{
				single = tte.getRecordSize();
				total += single;
			}
			
			
		}
		
		if (GTTime.isEnable())
		{
			List<GroupTimeEntry> timeGroupList = GTTimeInternal.getAllGroup();
			for (GroupTimeEntry gte : timeGroupList)
			{
				for (TagTimeEntry tte : gte.entrys())
				{
					int single = tte.getRecordSize();
					total += single;
				}
			}
		}
		
		if (total >= GTMemoryDaemonThread.topLevelLimit)
		{
			ToastUtil.ShowLongToast(GTApp.getContext(),
					"More than " + GTMemoryDaemonThread.topLevelLimit + " GW or Prof records."
					+ "You should save and clear records first.");
			
			return false;
		}
		
		return true;
	}
}
