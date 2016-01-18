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

import android.os.Process;

import com.tencent.wstt.gt.IService;

public class GTLogTaskConsumer {
	private Thread thread;
	boolean flag;
	
	public GTLogTaskConsumer(final IService gtService,
			final DataCacheController dataCacheController) {

		thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					while (flag) {
						String[] task = dataCacheController.takeLog();
						if (null != task && task.length == 4) {
							
							gtService.log(
									Long.parseLong(task[0]),
									Integer.parseInt(task[1]),
									task[2],
									task[3]);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, GTLogTaskConsumer.this.getClass().getSimpleName());
	}

	public void start()
	{
		flag = true;
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	public void stop(final DataCacheController dataCacheController)
	{
		flag = false;
		/*
		 * stop时，如果日志队列为空，那会始终使线程卡在takeLog上使flag没机会生效，
		 * 所以这里取巧放一个日志，使线程运转起来
		 */
		long tid = Process.myTid();
		String[] content = new String[1]; // 故意让这条日志长度不够，这样就不会打到控制台上
		content[0] = String.valueOf(tid);
//		content[1] = String.valueOf(AbsConnState.LOG_INFO);
//		content[2] = "GTsys";
//		content[3] = "Log Consumer Stoped.";
		dataCacheController.putLog(content);
		thread = null;
	}
}
