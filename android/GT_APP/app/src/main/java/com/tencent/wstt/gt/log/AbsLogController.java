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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.ui.model.LogEntry;
import com.tencent.wstt.gt.utils.GTUtils;

public abstract class AbsLogController implements ICacheable {
	
	// 过滤锚点，上次过滤对于数据源的位置，用于后面过滤条件不变的情况提高效率
	protected int lastFilterEndLocation = 0; 
	protected RemoveRangeArrayList<LogEntry> showLogList = new RemoveRangeArrayList<LogEntry>();
	protected List<LogEntry> filterdLogList;
	protected LinkedHashSet<String> showTagSet = new LinkedHashSet<String>();
	protected LogTaskConsumer logConsumer;
	protected ArrayList<LogListener> listenerList = new ArrayList<LogListener>();
	protected TempLogConsumer tempLogConsumer;
	
	protected boolean allowSave = true; // 保留作为日志总开关的字段
	
	// 用于写日志的读写锁
	protected ReadWriteLock lock = new ReentrantReadWriteLock(false);
	
	public AbsLogController()
	{
		if (GTUtils.isSDCardExist() && !Env.ROOT_LOG_FOLDER.exists())
		{
			Env.ROOT_LOG_FOLDER.mkdirs();
		}
	}
	
	void setLogTaskConsumer(LogTaskConsumer logConsumer)
	{
		this.logConsumer = logConsumer;
	}
	
	void setTempLogConsumer(TempLogConsumer tempLogConsumer)
	{
		this.tempLogConsumer = tempLogConsumer;
	}
	
	List<LogEntry> getShowLogList()
	{
		return showLogList; // TODO 外面需要加读锁
	}
	
	List<LogEntry> getFilterdLogList()
	{
		return filterdLogList;
	}
	
	void setFilterdLogList(List<LogEntry> list)
	{
		filterdLogList = list;
	}
	
	public List<String> getShowTags()
	{	
		List<String> result = new ArrayList<String>();
		String[] ss = showTagSet.toArray(new String[]{});
		Collections.addAll(result, ss);
		return result;
	}
	
	synchronized int getLastFilterEndLocation()
	{
		return lastFilterEndLocation;
	}

	synchronized void resetLastFilterEndLocation()
	{
		lock.readLock().lock();
		lastFilterEndLocation = showLogList.size();
		lock.readLock().unlock();
	}

	@Override
	synchronized public void clearCache()
	{
		lock.writeLock().lock();
		if (null != showLogList)
		{
			lastFilterEndLocation = 0;
			showLogList.clear();
//			showTagSet.clear();
		}
		lock.writeLock().unlock();
		
		// 此时需要刷新UI
		onDataChanged();
	}
	
	public void setAllowSave(boolean flag)
	{
		allowSave = flag;
	}
	
	public boolean getAllowSave()
	{
		return allowSave;
	}
	
	@Override
	/*
	 * 这个方法是被生产者线程调用的，所以可以阻塞
	 * 目前实现是读写锁，这里应该是优先级最低的写锁
	 */
	public void addEntrys(Collection logList)
	{
		lock.writeLock().lock();
		try
		{
			// 需要显示的log只控制显示1000行，多于1000行，则把list队头的删除
			showLogList.addAll(logList);

			if (showLogList.size() > LogUtils.CACHE)
			{
				int length = showLogList.size();
				lastFilterEndLocation = lastFilterEndLocation - length + LogUtils.CACHE;
				// 如果lastFilterEndLocation小于0，对调用程序来说已经没有用处了，取0即可
				lastFilterEndLocation = Math.max(lastFilterEndLocation, 0);
				showLogList.remove(0, length - LogUtils.CACHE);
			}
			
			for (Object o : logList)
			{
				LogEntry entry = (LogEntry)o;
				showTagSet.add(entry.tag);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}

		// 此时最适合刷新UI，走过滤逻辑刷新
		onDataChanged();
	}
	
	@Override
	synchronized public void onDataChanged()
	{
		// 此时最适合刷新UI，走过滤逻辑刷新
		for (LogListener listener : listenerList)
		{
			listener.onLogChanged();
		}
	}

	@Override
	synchronized public void saveCache(String fileName)
	{
		lock.readLock().lock();
		try
		{
			// 路径的情况，直接存在指定路径
			if (fileName.contains("/") || fileName.contains("\\"))
			{
				int la = fileName.lastIndexOf(".");
				if (la < 0)
				{
					fileName = fileName + LogUtils.LOG_POSFIX;
				}
				else
				{
					String temp = fileName.substring(la);
					if (temp.contains("/") || temp.contains("\\"))
					{
						// "."是目录名的一部分而不是后缀名的情况
						fileName = fileName + LogUtils.LOG_POSFIX;
					}
					// else fileName = fileName
				}
				
				
				LogUtils.writeLog(showLogList, fileName, false);
				return;
			}
			else
			{
				// 文件名的情况，保存在默认日志路径
				String filePath = null;
				if (fileName.contains(".")) // 自带后缀了
				{
					filePath = fileName;
				}
				else
				{
					filePath = fileName + LogUtils.LOG_POSFIX;
				}
				File f = new File(Env.S_ROOT_LOG_FOLDER, filePath);
				if (f.exists())
				{
					f.delete();
				}

				LogUtils.writeLog(showLogList, f, false);
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void addListener(LogListener listener) {
		if (! listenerList.contains(listener))
		{
			listenerList.add(listener);
		}
	}

	@Override
	public void removeListener(LogListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public void removeAllListener() {
		listenerList.clear();
	}
	
	@Override
	public void addEntry(Object entry) {
		// do nothing
	}
	
	@Override
	public void removeEntry(Object entry){
		// do nothing
	}
}
