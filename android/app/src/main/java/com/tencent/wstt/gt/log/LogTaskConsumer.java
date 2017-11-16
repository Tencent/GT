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
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.tencent.wstt.gt.ui.model.LogEntry;

public class LogTaskConsumer {
	private Thread thread;
	private boolean flag;
	protected boolean allowAdd2Visable = true;
	private LinkedBlockingQueue<LogEntry> queue = new LinkedBlockingQueue<LogEntry>(1000);
	
	LogTaskConsumer(final AbsLogController controller) {
		
		controller.setLogTaskConsumer(this);
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				
				LogEntry temp = null;
				try {
					while (flag) {
						Thread.sleep(500);
						if (allowAdd2Visable) // 不允许加入可视图，就让queue爆吧
						{
							List<LogEntry> tempList = new ArrayList<LogEntry>();
							if (null != temp)
							{
								tempList.add(temp);
							}
							queue.drainTo(tempList);
							if (tempList.size() > 0)
							{
								controller.addEntrys(tempList);
							}
							temp = queue.take();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, LogTaskConsumer.this.getClass().getSimpleName());
	}

	public void start()
	{
		flag = true;
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	public void stop()
	{
		flag = false;
	}
	
	public void setAllowAdd2Visable(boolean allow)
	{
		this.allowAdd2Visable = allow;
	}

	public void putLog(LogEntry log) {
		queue.offer(log);
	}
}
