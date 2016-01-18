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

import java.util.concurrent.LinkedBlockingQueue;

import com.tencent.wstt.gt.ui.model.CleanLogBuff;
import com.tencent.wstt.gt.ui.model.EndLogBuff;
import com.tencent.wstt.gt.ui.model.LogBuff;
import com.tencent.wstt.gt.ui.model.StartLogBuff;
import com.tencent.wstt.gt.ui.model.ValueBuff;

public class TempLogConsumer {
	private static final int CAPACITY = 64;//64;
	private static final int CHCHE = 2048;//2048; // 因为java一个空字符串占40字节，每个字符内容占2字节
	
	private LogController controller;
	private Thread thread;
	private boolean flag;
	private LinkedBlockingQueue<LogBuff> queue = new LinkedBlockingQueue<LogBuff>(CAPACITY);
	private Thread curLogBuffCheckThread;
	private StringBuffer curLogBuff; // 当前log内容缓存
	private Object curLogBuffLock;
	
	// 生产者执行队列，专门处理queue.put等阻塞式操作的任务执行器，以免阻塞式操作卡住消费者线程
//	private ExecutorService producerExecutor;
	
	TempLogConsumer(final LogController controller) {
		this.controller = controller;
		curLogBuffLock = new Object();
//		producerExecutor = Executors.newSingleThreadExecutor();
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (flag) {
						LogBuff lb = queue.take();
						lb.execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "TempLogConsumer");
		
		curLogBuff = new StringBuffer(CHCHE);
		
		controller.setTempLogConsumer(this);
		
		curLogBuffCheckThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (flag) {
						// 1s内没新日志，则把缓存送走
						Thread.sleep(1000);
						synchronized(curLogBuffLock)
						{
							if (curLogBuff.length() > 0)
							{
								LogBuff lb = new ValueBuff(curLogBuff, controller);
								curLogBuff = new StringBuffer();
								queue.offer(lb);
							}
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, TempLogConsumer.this.getClass().getSimpleName() + "$curLogBuffCheckThread");
	}
	
	public void start()
	{
		flag = true;
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		curLogBuffCheckThread.setPriority(Thread.MIN_PRIORITY);
		curLogBuffCheckThread.start();
	}
	
	public void stop()
	{
		flag = false;
	}
	
	/**
	 * LogBuff生产者调用，产生日志，
	 * 需要加锁的地方是curLogBuff的处理，不过StringBuffer本身就是同步的。同样会操作
	 * curLogBuff的就是下面startAutoLog等几个方法
	 * @param content
	 */
	public void putLog(String content)
	{
		if (flag && GTLogInternal.isEnable() && GTLogInternal.hasLogNeedIO())
		{
			synchronized(curLogBuffLock)
			{
				curLogBuff.append(content);
				curLogBuff.append("\r\n");
				if (curLogBuff.length() >= CHCHE)
				{
					LogBuff lb = new ValueBuff(curLogBuff, controller);
					curLogBuff = new StringBuffer();
					queue.offer(lb);
				}
			}
		}
	}
	
	/**
	 * 由生产者线程调用该方法，开始记录一个日志，主要不要被消费者线程调用了
	 * @param fileName
	 */
	public void startALog(String fileName)
	{
		controller.setTempLogStarting(fileName);
		
		synchronized(curLogBuffLock) {
			if (curLogBuff.length() > 0)
			{
				LogBuff lb = new ValueBuff(curLogBuff, controller);
				queue.offer(lb);
				curLogBuff = new StringBuffer();
			}
		}
		
		LogBuff startTag = new StartLogBuff(fileName, controller);
		asynPutLogBuff(startTag);
	}
	
	public void endALog(String fileName)
	{
		controller.reudceTempLogStarting(fileName);
		
		synchronized(curLogBuffLock) {
			if (curLogBuff.length() > 0)
			{
				LogBuff lb = new ValueBuff(curLogBuff, controller);
				queue.offer(lb);
				curLogBuff = new StringBuffer();
			}
		}
		
		LogBuff endTag = new EndLogBuff(fileName, controller);
		asynPutLogBuff(endTag);
	}
	
	public void cleanALog(String fileName)
	{
		synchronized(curLogBuffLock) {
			if (curLogBuff.length() > 0)
			{
				LogBuff lb = new ValueBuff(curLogBuff, controller);
				queue.offer(lb);
				curLogBuff = new StringBuffer();
			}
		}

		CleanLogBuff cleanTag = new CleanLogBuff(fileName, controller);
		asynPutLogBuff(cleanTag);
	}
	
	/*
	 * 为了避免阻塞的put操作死锁，需要单独对put操作异步化并串行处理
	 */
	private void asynPutLogBuff(final LogBuff lb)
	{
		try {
			queue.put(lb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
