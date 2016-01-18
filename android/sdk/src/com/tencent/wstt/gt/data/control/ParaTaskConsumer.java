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
package com.tencent.wstt.gt.data.control;

import com.tencent.wstt.gt.AidlTask;
import com.tencent.wstt.gt.BooleanEntry;
import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.IService;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.PerfDigitalEntry;
import com.tencent.wstt.gt.QueryPerfEntry;

public class ParaTaskConsumer {
	private Thread thread;
	boolean flag;
	
	public ParaTaskConsumer(final IService gtService,
			final DataCacheController dataCacheController)
	{
		thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					while (flag) {
						AidlTask task = dataCacheController.takeParaTask();
						if (null != task) {
							if (task instanceof InPara)
							{
								InPara para = (InPara)task;
								// 注册的入参，可能包括set入参的怨念
								if (para.isRegistering())
								{
									if (para.isGlobal())
									{
										gtService.registerGlobalInPara(para);
									}
									else
									{
										gtService.registerInPara(para);
									}
								}
								// 单独的设置入参值的任务
								else
								{
									if (null != para.getValues() && para.getValues().size() > 0)
									{
										if (para.isGlobal())
										{
											gtService.setGlobalInPara(para.getKey(), para.getValues().get(0));
										}
										else
										{
											gtService.setInPara(para.getKey(), para.getValues().get(0));
										}
										
									}
								}
							}
							else if (task instanceof OutPara)
							{
								OutPara para = (OutPara)task;
								// 注册的出参，可能包括本地set出参的历史记录
								if (para.isRegistering())
								{
									if (para.isGlobal())
									{
										gtService.registerGlobalOutPara(para);
									}
									else
									{
										gtService.registerOutPara(para);
									}
								}
								else
								{
									if (null != para.getValue())
									{
										if (para.isGlobal())
										{
											gtService.setGlobalOutPara(para.getKey(), para.getValue());
										}
										else
										{
											gtService.setOutPara(para.getKey(), para.getValue());
										}
										
									}
								}
							}
							else if (task instanceof BooleanEntry)
							{
								gtService.setBooleanEntry((BooleanEntry) task);
							}
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, ParaTaskConsumer.this.getClass().getSimpleName());
	}
	
	public void start()
	{
		flag = true;
		thread.setPriority(Thread.MIN_PRIORITY + 2); // 比性能优先级高点
		thread.start();
	}
	
	public void stop(final DataCacheController dataCacheController)
	{
		flag = false;
		/*
		 * stop时，如果日志队列为空，那会始终使线程卡在takeTask上使flag没机会生效，
		 * 所以这里取巧放一个task，使线程运转起来
		 */
		QueryPerfEntry queryPerfEntry = new QueryPerfEntry("GTsys", "startStopTaskConsumer", 0, 0);

		PerfDigitalEntry entry = new PerfDigitalEntry();
		entry.setFunctionId(Functions.PERF_END_TIME_GLOBAL); // 用endTime,这样不会在控制台侧积压
		entry.setQueryEntry(queryPerfEntry);
		entry.setLogTime(System.currentTimeMillis());
		entry.setData(System.nanoTime());

		dataCacheController.putParaTask(entry);
		thread = null;
	}
}
