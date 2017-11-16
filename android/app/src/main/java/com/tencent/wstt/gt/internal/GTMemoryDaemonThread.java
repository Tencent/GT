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

import android.os.Message;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.api.base.GTTime;
import com.tencent.wstt.gt.log.GTTimeInternal;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.ui.model.GroupTimeEntry;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;

/**
 * 1.限制历史数据和耗时统计数据总数在50万条以下（java堆20M以下）
 * 2.限制单条历史数据在15万条以下，停止监视并通知一次（只一次）
 * 3.40万条和45万条时，缩小两次监视的间隔，弹toast或通知进行告警
 * 4.超过50万条时，停止记录历史数据，并弹对话框提示用户保存和清空
 */
public class GTMemoryDaemonThread extends GTDaemonThread {
	
	public final static int topLevelLimit = 500000;
	public final static int secondLevelLimit = 450000;
	public final static int thirdLevelLimit = 400000;
	
	public final static int singleLimit = 150000;
	public final static int topInterval = 60 * 1000; // 一级警戒的监控间隔是1分钟
	public final static int secondInterval = 3 * 60 * 1000; // 二级警戒的监控间隔是3分钟
	public final static int thirdInterval = 5 * 60 * 1000; // 二级警戒的监控间隔是5分钟 
	
	public static final String key = "MEM_DAEMON";
	
	private boolean isCrossSecondLevel = false; // 已经越过2级告警的标识
	private boolean isCrossThirdLevel = false;

	DaemonHandler handler = null;
	
	public GTMemoryDaemonThread()
	{
		super();
		setName(this.getClass().getSimpleName());
		curInterval = secondInterval;
		handler = GTApp.daemonHandler;
	}

	@Override
	void doTask() {
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
			
			validSingleLimit(single, tte);
		}
		
		if (! validTopLimit(total)) // gw部分已经超一级警戒了，prof部分就不需要看了
		{
			return;
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
		
		if (! validTopLimit(total)) // 先判断一级警戒，如果没有过一级警戒，才会判断二级警戒
		{
			return;
		}
		
		if (!validSecendLimit(total)) // 先判断二级警戒，如果没有过二级警戒，才会判断三级警戒
		{
			return;
		}
		
		validThirdLimit(total);
	}

	private boolean validTopLimit(int curSize)
	{
		if (curSize >= topLevelLimit && (OpUIManager.gw_running || GTTime.isEnable()))
		{
			// 停止一切历史记录活动
			GTApp.getOpHandler().sendEmptyMessage(1); // 驱动GW采集停止
			GTTime.disable();

			// 发消息提示用户，弹对话框
			Message msg = Message.obtain(handler, DaemonHandler.MEM_TOP_WARNING_FLAG);
			handler.sendMessage(msg);
			
			return false;
		}
		
		return true;
	}
	
	private boolean validSecendLimit(int curSize)
	{
		if (curSize >= secondLevelLimit && curSize < topLevelLimit && !isCrossSecondLevel
				&& (OpUIManager.gw_running || GTTime.isEnable()))
		{
			isCrossSecondLevel = true; // 越过2级警戒后，不要再次2级警戒了
			
			// 发消息提示用户，通知
			Message msg = Message.obtain(handler, DaemonHandler.MEM_SECOND_WARNING_FLAG);
			handler.sendMessage(msg);
			
			// 缩短监视间隔
			curInterval = topInterval;
			return false;
		}
		else if (curSize < secondLevelLimit
				&& curSize >= thirdLevelLimit
				&& (OpUIManager.gw_running || GTTime.isEnable()))
		{
			// 恢复为较长的监视间隔
			isCrossSecondLevel = false;
			curInterval = secondInterval;
		}
		
		return true;
	}
	
	private boolean validThirdLimit(int curSize)
	{
		if (curSize >= thirdLevelLimit && curSize < secondLevelLimit && !isCrossThirdLevel
				&& (OpUIManager.gw_running || GTTime.isEnable()))
		{
			isCrossThirdLevel = true;
			
			// 发消息提示用户，通知
			Message msg = Message.obtain(handler, DaemonHandler.MEM_THIRD_WARNING_FLAG);
			handler.sendMessage(msg);
			
			// 缩短监视间隔
			curInterval = secondInterval;
			return false;
		}
		else if (curSize < thirdLevelLimit
				&& (OpUIManager.gw_running || GTTime.isEnable()))
		{
			// 恢复为较长的监视间隔
			isCrossThirdLevel = false;
			curInterval = thirdInterval;
		}
		return true;
	}

	private void validSingleLimit(int curSize, TagTimeEntry tte)
	{
		if (curSize >= singleLimit && OpUIManager.gw_running)
		{
			// 停止单项的采集
			Client client = ClientManager.getInstance().getClient(tte.getExkey());
			OutPara op = client.getOutPara(tte.getName());
			if (null != op && op.isMonitor()) // 如果已停止采集，则不需要告警
			{
				op.setMonitor(false);
				
				// 发消息提示用户，通知
				Message msg = Message.obtain(handler, DaemonHandler.MEM_SINGLE_WARNING_FLAG);
				msg.obj = op;
				handler.sendMessage(msg);
			}
		}
	}
}
