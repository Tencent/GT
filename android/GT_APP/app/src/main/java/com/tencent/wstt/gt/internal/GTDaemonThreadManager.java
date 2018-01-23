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

import java.util.HashMap;
import java.util.Map;

public class GTDaemonThreadManager {
	
	private Map<String, GTDaemonThread> threadMap;
	
	private static GTDaemonThreadManager INSTANCE = new GTDaemonThreadManager();
	
	public static GTDaemonThreadManager getInstance()
	{
		return INSTANCE;
	}
	
	private GTDaemonThreadManager()
	{
		threadMap = new HashMap<String, GTDaemonThread>();
	}

	public void start(String key)
	{
		Thread t = threadMap.get(key);
		if (t != null)
		{
			t.start();
		}
	}
	
	public void stop(String key)
	{
		Thread t = threadMap.get(key);
		if (t != null)
		{
			t.stop();
		}
	}
	
	public boolean contains(String key)
	{
		if (threadMap.containsKey(key))
		{
			return true;
		}
		return false;
	}
	
	public void put(String key, GTDaemonThread t)
	{
		if (! threadMap.containsKey(key))
		{
			threadMap.put(key, t);
		}
	}
	
	public void quickFnish(String key)
	{
		GTDaemonThread t = threadMap.get(key);
		if (t != null)
		{
			t.quickFnish();
		}
	}
}
