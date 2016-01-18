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
package com.tencent.wstt.gt.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GroupTimeEntry extends NamedEntry {
	
	private LinkedHashMap<TagQueryEntry, TagTimeEntry> tagMap =
			new LinkedHashMap<TagQueryEntry, TagTimeEntry>();
	
	// UI对应的进程内跨线程差值变量缓存
	private LinkedHashMap<String, TagTimeEntry> globalTagEntry =
			new LinkedHashMap<String, TagTimeEntry>();
	
	// UI对应的线程差值变量缓存
	private LinkedHashMap<String, TagTimeEntry> staticsThreadTagEntry =
			new LinkedHashMap<String, TagTimeEntry>();
	
	/*
	 * addEntry和entrys方法之间应加锁，保证不出容器并发问题，在万条记录级别上误差还是在1ms以内
	 */
	private Lock lock;

	public GroupTimeEntry(String name)
	{
		this.name = name;
		this.lock = new ReentrantLock();
	}

	public void addEntry(TagTimeEntry tagPerLogEntry)
	{
		TagQueryEntry queryEntry = new TagQueryEntry(
				tagPerLogEntry.getName(), tagPerLogEntry.getTid(), tagPerLogEntry.getExkey());
		tagMap.put(queryEntry, tagPerLogEntry);
		
		if (tagPerLogEntry.getTid() == 0)
		{
			if (null == globalTagEntry.get(tagPerLogEntry))
			{
				try{
					lock.lock();
					globalTagEntry.put(tagPerLogEntry.getName(), tagPerLogEntry);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
		else
		{
			if (null == staticsThreadTagEntry.get(queryEntry))
			{
				try
				{
					lock.lock();
					staticsThreadTagEntry.put(tagPerLogEntry.getName(), tagPerLogEntry);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}
	
	public void addStaticsEntry(TagTimeEntry tagPerLogEntry)
	{
		String key = tagPerLogEntry.getName();
		long tid = tagPerLogEntry.getTid();

		if (tid == 0)
		{
			if (null == globalTagEntry.get(key))
			{
				try{
					lock.lock();
					globalTagEntry.put(key, tagPerLogEntry);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
		else
		{
			if (null == staticsThreadTagEntry.get(key))
			{
				try
				{
					lock.lock();
					staticsThreadTagEntry.put(key, tagPerLogEntry);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}
	
	/**
	 * 获取统计用的线程耗时对象
	 * @param tag
	 * @param tid
	 * @return
	 */
	private TagTimeEntry getGlobalEntry(String tag)
	{
		return globalTagEntry.get(tag);
	}
	
	/**
	 * 获取统计用的线程耗时对象
	 * @param tag
	 * @param tid
	 * @return
	 */
	private TagTimeEntry getStaticsThreadEntry(String tag)
	{
		return staticsThreadTagEntry.get(tag);
	}
	
	public TagTimeEntry getStaticsEntry(String tag, long tid)
	{
		if (tid == 0)
		{
			return getGlobalEntry(tag);
		}
		else
		{
			return getStaticsThreadEntry(tag);
		}
	}
	
	/**
	 * 获取线程计算用的线程耗时对象
	 * @param tag
	 * @param tid
	 * @return
	 */
	public TagTimeEntry getThreadEntry(String tag, long tid, int exkey)
	{
		TagQueryEntry queryEntry = new TagQueryEntry(tag, tid, exkey);
		return tagMap.get(queryEntry);
	}
	
	/**
	 * 返回给UI显示用的TagTimeEntry
	 * @return
	 */
	public List<TagTimeEntry> entrys()
	{
		List<TagTimeEntry> result = new ArrayList<TagTimeEntry>();
		try
		{
			lock.lock();
			result.addAll(globalTagEntry.values());
			result.addAll(staticsThreadTagEntry.values());
			for (Iterator<TagTimeEntry> iter = result.iterator(); iter.hasNext();)
			{
				TagTimeEntry tte = iter.next();
				if (null != tte && tte.getRecordSize() <= 0)
				{
					iter.remove();
				}
			}
		}
		finally
		{
			lock.unlock();
		}

		return result;
	}
	
	public void clear()
	{
		tagMap.clear();
		globalTagEntry.clear();
		staticsThreadTagEntry.clear();
	}
}
