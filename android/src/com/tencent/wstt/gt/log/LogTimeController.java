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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.ui.model.GroupTimeEntry;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.utils.GTUtils;

public class LogTimeController {
	
	private LinkedHashMap<String, GroupTimeEntry> groupMap =
			new LinkedHashMap<String, GroupTimeEntry>();
	
	private boolean started = false;
	
	// TODO 为提高性能引入的变量，在第二期应该尽量优化掉
	private boolean startedOnceUntilNotClear = false;
	private Lock lock; // 结合界面的实际逻辑，用普通的锁即可，不需要读写锁
	
	public LogTimeController()
	{
		if (GTUtils.isSDCardExist() && !Env.ROOT_TIME_FOLDER.exists())
		{
			Env.ROOT_TIME_FOLDER.mkdirs();
		}
		
//		started = GTPref.getGTPref().getBoolean(GTPref.PERF_MASTER_SWITCH, started);
		
		lock = new ReentrantLock();
	}
	
	public List<GroupTimeEntry> getShowList()
	{
		List<GroupTimeEntry> result = new ArrayList<GroupTimeEntry>();
		
		if (startedOnceUntilNotClear)
		{
			lock.lock();
			result.addAll(groupMap.values());
			lock.unlock();
		}
		
		return result;
	}
	
	public GroupTimeEntry getGroupTimeEntry(String name)
	{
		return groupMap.get(name);
	}
	
	/**
	 * 给详情页返回数据源，要么返回全局的统计，要么返回区分线程的统计
	 * @param tid
	 * @param parentName
	 * @param name
	 * @return
	 */
	public TagTimeEntry getTagTimeEntry(long tid, String parentName, String name)
	{
		GroupTimeEntry groupTimeEntry = groupMap.get(parentName);
		TagTimeEntry tagTimeEntry = null;
		if (null != groupTimeEntry)
		{
			tagTimeEntry = groupTimeEntry.getStaticsEntry(name, tid);
		}
		return tagTimeEntry;
	}
	
	/**
	 * 记录单点的性能数据（start和end这种成对的称为双点性能数据），也按tid区分全局的还是线程的
	 */
	public void recordDigital(long tid, String group, String tag, long[] datas, int funcId) {
		if (!started)
		{
			return;
		}
		
		if (null != group && null != tag)
		{
			GroupTimeEntry groupEntry = groupMap.get(group);
			
			if (null == groupEntry)
			{
				groupEntry = new GroupTimeEntry(group);
				lock.lock();
				groupMap.put(group, groupEntry);
				lock.unlock();
			}

			// 从Group中取出由tag, tid共同标示的统计对象（exKey无用）
			TagTimeEntry staticsTagTimeEntry = groupEntry.getStaticsEntry(tag, tid);

			if (null == staticsTagTimeEntry)
			{
				staticsTagTimeEntry = new TagTimeEntry(groupEntry);
				staticsTagTimeEntry.setName(tag);
				staticsTagTimeEntry.setTid(tid);
				staticsTagTimeEntry.setFunctionId(funcId);
				staticsTagTimeEntry.initChildren(datas.length - 1);
				groupEntry.addStaticsEntry(staticsTagTimeEntry);
			}
			
			if (Functions.PERF_DIGITAL_MULT == funcId
					|| Functions.PERF_DIGITAL_MULT_MEM == funcId)
			{
				TagTimeEntry[] subEntrys = staticsTagTimeEntry.getSubTagEntrys();
				for (int i = 0; i < subEntrys.length; i++)
				{
					TagTimeEntry subEntry = subEntrys[i];
					subEntry.add(datas[i]);
				}
				staticsTagTimeEntry.add(datas[0]); // TODO 得记录一个维度的值，否则外面UI无法展示
			}
			else
			{
				staticsTagTimeEntry.add(datas[0]);
			}
		}
	}
	
	/**
	 * 规定不区分线程的tid为0，被测程序的线程号为正数，GT控制台的线程号为负数
	 */
	public void startTime(long tid, String group, String tag,
			int exKey, long start, int funcId) {
		if (null != group && null != tag)
		{
			GroupTimeEntry groupEntry = groupMap.get(group);
			
			if (null == groupEntry)
			{
				groupEntry = new GroupTimeEntry(group);
				lock.lock();
				groupMap.put(group, groupEntry);
				lock.unlock();
			}

			// 从Group中取出由tag, tid, exKey共同标示的TagTimeEntry对象
			TagTimeEntry tagEntry = groupEntry.getThreadEntry(tag, tid, exKey);

			if (null == tagEntry)
			{
				tagEntry = new TagTimeEntry(groupEntry);
				tagEntry.setName(tag);
				tagEntry.setTid(tid);
				tagEntry.setExkey(exKey);
				/*
				 * funcId用的是PERF_START_TIME_GLOBAL或PERF_START_DIGITAL_GLOBAL
				 * 对于后面的使用只是保证UI精度，不会做其他依赖funcId的逻辑计算
				 */
				tagEntry.setFunctionId(funcId);
				groupEntry.addEntry(tagEntry);
			}
			
			tagEntry.setLastStart(start);
		}
	}
	
	public long endTime(long tid, String group, String tag,
			int exKey, long end, int funcId) {
		if (null != group && null != tag)
		{
			GroupTimeEntry groupEntry = null;
			TagTimeEntry tagEntry = null;
			
			if (null == groupMap.get(group))
			{
				return -1;
			}
			
			groupEntry = groupMap.get(group);
			if (null == groupEntry)
			{
				return -1;
			}
			
			tagEntry = groupEntry.getThreadEntry(tag, tid, exKey);
			if (null == tagEntry)
			{
				return -1;
			}
			
			if (tagEntry.getLastStart() <= 0)
			{
				return -1;
			}
			
			long reduce = end - tagEntry.getLastStart();
			tagEntry.setLastStart(0);

			if (started)
			{
				// 在对应的统计对象中加入差值，注意统计值是不关注exKey的
				TagTimeEntry staticsTagTimeEntry = groupEntry.getStaticsEntry(tag, tid);
				staticsTagTimeEntry.add(reduce);
			}

			return reduce;
		}
		
		return -1;
	}
	
	public void setState(boolean flag)
	{
		started = flag;
		if (flag)
		{
			startedOnceUntilNotClear = true;
		}
		
//		GTPref.getGTPref().edit().putBoolean(GTPref.PERF_MASTER_SWITCH, started).commit();
		if (!started)
		{
			for (GroupTimeEntry gte : groupMap.values())
			{
				for (TagTimeEntry tte : gte.entrys())
				{
					/*
					 * 后面处理注意，这个操作之后如果TagTimeEntry对象收到对应的
					 * 一条endTime消息，则是上次消费的，需要丢弃，
					 * 即在lastStart==0时，只有收到startTime消息才是开始有效的。
					 * 同一个线程内，不需要担心乱序、多条的问题
					 */
					tte.setLastStart(0);
				}
			}
		}
	}
	
	public boolean getState()
	{
		return started;
	}
	
	public void clearAllCache()
	{
		if (started)
		{
			return;
		}
		
		startedOnceUntilNotClear = false;
		
		if (null != groupMap)
		{
			for (GroupTimeEntry gte : groupMap.values())
			{
				for (TagTimeEntry tte : gte.entrys())
				{
					tte.clear();
				}
				gte.clear();
			}
			groupMap.clear();
		}
	}
	
	public void saveAllCache(String fileName)
	{
		LogUtils.writeTimeLog(getShowList(), fileName);
	}
	
	public void saveCache(String fileName, TagTimeEntry tagTimeEntry)
	{
		// 路径的情况，直接存在指定路径
		LogUtils.writeTimeDetail(tagTimeEntry, fileName);
	}
}
