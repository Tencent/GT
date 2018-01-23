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
package com.tencent.wstt.gt.plugin.memfill;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.tencent.wstt.gt.api.utils.MemFillTool;
import com.tencent.wstt.gt.plugin.PluginTaskExecutor;

public class GTMemFillEngine implements PluginTaskExecutor {

	private static GTMemFillEngine INSTANCE;

	private List<GTMemFillListener> listeners;
	private boolean isFilled;

	public static GTMemFillEngine getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new GTMemFillEngine();
		}
		return INSTANCE;
	}

	private GTMemFillEngine()
	{
		listeners = new ArrayList<GTMemFillListener>();
	}

	public synchronized void addListener(GTMemFillListener listener)
	{
		listeners.add(listener);
	}

	public synchronized void removeListener(GTMemFillListener listener)
	{
		listeners.remove(listener);
	}

	public boolean isFilled() {
		return isFilled;
	}

	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}

	public void fill(final int size)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				if (isFilled())
				{
					return;
				}
				
				try {
					for (GTMemFillListener listener : listeners)
					{
						listener.onFillStart();
					}
					MemFillTool.getInstance().fillMem(size);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					for (GTMemFillListener listener : listeners)
					{
						listener.onFillFail("");
					}
					return;
				}
				for (GTMemFillListener listener : listeners)
				{
					listener.onFillEnd();
				}
				setFilled(true);
			}}, "GTMemFillThread").start();
		
	}

	public void free()
	{
		if (isFilled())
		{
			MemFillTool.getInstance().freeMem();
			for (GTMemFillListener listener : listeners)
			{
				listener.onFree();
			}
			setFilled(false);
		}
	}

	@Override
	public void execute(Bundle bundle) {
		String cmd = bundle.getString("cmd");
		if (cmd != null && cmd.equals("fill")) {
			int num = bundle.getInt("fillnum", -1);
			if (num > 0)
			{
				fill(num);
			}
		}
		else if (cmd != null && cmd.equals("free")) {
			free();
		}
	}
}
