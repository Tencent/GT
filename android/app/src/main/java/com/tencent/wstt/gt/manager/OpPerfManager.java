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
package com.tencent.wstt.gt.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;

/**
 * 统一处理出参历史值的管理器，这样出参对象与性能对象通过出参名字弱关联即可
 *
 */
public class OpPerfManager {
	
	private Map<String, TagTimeEntry> dataMap;

	private static final TagTimeEntry[] EMPTY = {};
	
	private static OpPerfManager INSTANCE = new OpPerfManager();

	private OpPerfManager()
	{
		dataMap = Collections.synchronizedMap(new LinkedHashMap<String, TagTimeEntry>());
	}
	
	public static OpPerfManager getInstance()
	{
		return INSTANCE;
	}

	/**
	 * 通过出参的名字获取出参对应的性能对象
	 * @param key
	 * @return
	 */
	public synchronized TagTimeEntry get(String key)
	{
		return dataMap.get(key);
	}

	/**
	 * 获取所有出参对应的性能对象
	 * @param key
	 * @return 如果当前并没有出参对应的性能对象，会返回空数组而不是返回null
	 */
	public synchronized TagTimeEntry[] getAll()
	{
		if (dataMap == null || dataMap.isEmpty())
		{
			return EMPTY;
		}
		
		List<TagTimeEntry> entryList = new ArrayList<TagTimeEntry>();
		for (Entry<String, TagTimeEntry> entry : dataMap.entrySet())
		{
			entryList.add(entry.getValue());
		}
		
		return entryList.toArray(EMPTY);
	}
	
	/**
	 * 获取所有非disable态出参对应的性能对象数组
	 * @return
	 */
	public synchronized TagTimeEntry[] getAllEnable()
	{
		List<TagTimeEntry> entryList = new ArrayList<TagTimeEntry>();
		for (TagTimeEntry tte : getAll())
		{
			Client client = ClientManager.getInstance().getClient(tte.getExkey());
			OutPara op = client.getOutPara(tte.getName());
			if (null != op && op.getDisplayProperty() < OutPara.DISPLAY_DISABLE
					&& op.isMonitor())
			{
				entryList.add(tte);
			}
		}
		return entryList.toArray(EMPTY);
	}

	/**
	 * 添加一个出参对其进行性能统计关联
	 * @param key
	 * @return
	 */
	public synchronized void add(TagTimeEntry entry)
	{
		dataMap.put(entry.getName(), entry);
	}
	/**
	 * 清理一个出参对应的性能统计对象
	 * @param key
	 * @return
	 */
	public synchronized void remove(String key)
	{
		dataMap.remove(key);
	}
	
	/**
	 * 清理所有出参对应的性能统计对象
	 * @param key
	 * @return
	 */
	public synchronized void removeAll()
	{
		dataMap.clear();
	}
	
//	public void recordHistory(String key, long data)
//	{
//		TagTimeEntry entry = dataMap.get(key);
//		entry.add(data);
//	}
}
