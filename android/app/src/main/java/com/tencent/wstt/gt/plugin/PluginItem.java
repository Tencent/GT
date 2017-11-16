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
package com.tencent.wstt.gt.plugin;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.os.Bundle;

public abstract class PluginItem {
	public String name; // 作为接收从SDK发送来的命令的标识
	public String alias;
	public String description;
	public int logo_id;
	public Class<? extends Activity> activityClass;
	
	protected LinkedBlockingQueue<Bundle> taskQueue;
	private Thread consumerThread;
	private PluginTaskExecutor mExecutor;
	
	public PluginItem(String name,
			String alias,
			String descriotion,
			int logo_id,
			Class<? extends Activity> activityClass)
	{
		this.name = name;
		this.alias = alias;
		this.description = descriotion;
		this.logo_id = logo_id;
		this.activityClass = activityClass;
		
		// 任务限量100
		taskQueue = new LinkedBlockingQueue<Bundle>(100);
	}

	/**
	 * 初始化连接 GT 时，该插件需要做的事情
	 */
	public void onInitConnectGT() {
		
	}
	
	/**
	 * 可以接收被测应用传递过来的bundle，至于具体怎么处理，插件拿着Queue看着办吧
	 * @param bundle
	 */
	public void addTask(Bundle bundle)
	{
		taskQueue.offer(bundle);
	}

	public void setTaskExecutor(final PluginTaskExecutor executor)
	{
		if (null != consumerThread)
		{
			consumerThread.interrupt();
			consumerThread = null;
		}
		mExecutor = executor;
		consumerThread = new Thread(mExecutor.getClass().getName())
		{
			@Override
			public void run()
			{
				try {
					while (true)
					{
						Bundle task = taskQueue.take();
						mExecutor.execute(task);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		consumerThread.start();
	}
	
	public void removeTaskExecutor()
	{
		if (null != consumerThread)
		{
			consumerThread.interrupt();
			consumerThread = null;
		}
	}
	
	/**
	 * 同步执行命令方法
	 * @param bundle
	 */
	public void doTask(Bundle bundle)
	{
		mExecutor.execute(bundle);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		if (! (other instanceof PluginItem))
		{
			return false;
		}
		PluginItem otherItem = (PluginItem)other;
		if (name.equals(otherItem.name))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		if (name == null)
		{
			return -1;
		}
		return name.hashCode();
	}
}
