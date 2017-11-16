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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.tencent.wstt.gt.ui.model.LogEntry;
import com.tencent.wstt.gt.ui.model.MatchedEntry;

public class LogSearchController {
	
	private String lastSearchMsg;
	private LogEntry[] lastEntrys;
	private List<MatchedEntry> lastMatchedEntryList;
	
	private int lastMatchedSeq;
	private boolean interrupted;
	
	private LinkedList<String> msgHistory = new LinkedList<String>();
	private LinkedList<String> curShowDownMsgList = new LinkedList<String>();
	private String sCurSelectedMsg = "";
	
	public LogSearchController()
	{
		lastMatchedEntryList = new ArrayList<MatchedEntry>();
	}
	
	public String getLastSearchMsg() {
		return lastSearchMsg;
	}
	public void setLastSearchMsg(String lastSearchMsg) {
		this.lastSearchMsg = lastSearchMsg.toLowerCase(Locale.CHINA);
	}
	public LogEntry[] getLastEntrys() {
		return lastEntrys;
	}
	public List<MatchedEntry> getLastMatchedEntryList() {
		return lastMatchedEntryList;
	}

	public void setLastMatchedEntryList(List<MatchedEntry> lastMatchedEntryList) {
		this.lastMatchedEntryList = lastMatchedEntryList;
	}
	
	public void setLastEntrys(LogEntry[] lastEntrys) {
		this.lastEntrys = lastEntrys;
		int buffLength = lastSearchMsg.length();
		// TODO 异步处理，解析搜索数据源，生成LastMatchedEntry数组
		// TODO 此时需要堵住UI
		lastMatchedEntryList.clear();
		for (int i = 0; i < lastEntrys.length; i++)
		{
			if (interrupted)
			{
				break;
			}
			
			LogEntry log = lastEntrys[i];
			String temp = log.msg.toLowerCase(Locale.CHINA);
			int start = -1;
			int end = -1;
			
			do
			{
				start = temp.indexOf(lastSearchMsg, end);
				if (start >= 0)
				{
					end = start + buffLength;
					MatchedEntry matched = new MatchedEntry(i, start, end);
					lastMatchedEntryList.add(matched);
				}
			}
			while(start >= 0);
		}
		
		// 这段影响到初始搜索，是否已选中最后一条匹配记录
		if (lastMatchedEntryList.size() > 0)
		{
			lastMatchedSeq = lastMatchedEntryList.size() - 1;
		}
		else
		{
			lastMatchedSeq = 0;
		}
	}
	
	public void clear()
	{
		lastMatchedEntryList.clear();
		lastMatchedSeq = 0;
	}
	
	public int getLastMatchedSeq()
	{
		return lastMatchedSeq;
	}
	
	public MatchedEntry getLastMatched()
	{
		return lastMatchedEntryList.get(lastMatchedSeq);
	}
	
	public void setLastMatchedSeq(int lastMatchedSeq)
	{
		this.lastMatchedSeq = lastMatchedSeq;
	}
	
	/*
	 * ===========================用于搜索历史的方法===========================
	 */
	public LinkedList<String> getMsgHistory() {
		return msgHistory;
	}

	public void setMsgHistory(LinkedList<String> msgHistory) {
		this.msgHistory = msgHistory;
	}

	public LinkedList<String> getCurShowDownMsgList() {
		return curShowDownMsgList;
	}

	public void setCurShowDownMsgList(LinkedList<String> curShowDownMsgList) {
		this.curShowDownMsgList = curShowDownMsgList;
	}

	public String getsCurSelectedMsg() {
		return sCurSelectedMsg;
	}

	public void setsCurSelectedMsg(String sCurSelectedMsg) {
		this.sCurSelectedMsg = sCurSelectedMsg;
	}
}
