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
package com.tencent.wstt.gt.plugin.smtools;

import java.util.concurrent.atomic.AtomicInteger;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.ui.model.TimeEntry;

import android.app.IntentService;
import android.content.Intent;

public class SMDataService extends IntentService {

	private AtomicInteger count = new AtomicInteger(0);
	private boolean pause = false;

	private Thread dataCountThread = new Thread("SMDataCountThread") {
		@Override
		public void run() {
			while (!pause)
			{
				try {
					int value = SMServiceHelper.getInstance().dataQueue.take();
					count.addAndGet(value);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	};

	public SMDataService() {
		super("SMDataService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String pkgName = intent.getStringExtra("pkgName");
		String key = "SM:" + pkgName;
//		String SFKey = "SF:" + pkgName;

		Client globalClient = ClientManager.getInstance().getGlobalClient();
		globalClient.registerOutPara(key, "SM");
		globalClient.setOutparaMonitor(key, true);
		OpPerfBridge.startProfier(globalClient.getOutPara(key),
				Functions.PERF_DIGITAL_NORMAL, "", "");

		/*
		 * SM设置默认的告警阈值为40，
		 * TODO 这里设置是简单支持SM的告警需求，SDK命令支持比较麻烦，就先加在这里了
		 */
		OpPerfBridge.getProfilerData(key).getThresholdEntry(
				).setThreshold(1, Integer.MAX_VALUE, 40);

		// 主动刷新出参页面的列表
		GTApp.getOpHandler().sendEmptyMessage(5);
		GTApp.getOpEditHandler().sendEmptyMessage(0);

//		globalClient.registerOutPara(SFKey, "SF");
//		globalClient.setOutparaMonitor(SFKey, true);
//		OpPerfBridge.startProfier(globalClient.getOutPara(SFKey),
//				Functions.PERF_DIGITAL_NORMAL, "", "");
//		
		while (true) {
			if (pause) {
				break;
			}
			int x = count.getAndSet(0);
			// 卡顿大于60时，要将之前几次SM计数做修正
			if (x > 60) {
				int n = x / 60;
				int v = x % 60;
				TagTimeEntry tte = OpPerfBridge.getProfilerData(key);
				int len = tte.getRecordSize();
				// 补偿参数
				int p = n;//Math.min(len, n);
				/*
				 * n > len是刚启动测试的情况，日志中的亡灵作祟，这种情况不做补偿;
				 * 并且本次也记为60。本逻辑在两次测试间会清理数据的情况生效。
				 */
				if (n > len) 
				{
					globalClient.setOutPara(key, 60);
//					globalClient.setOutPara(SFKey, 0);
				}
				else
				{
					for (int i = 0; i < p; i++) {
						TimeEntry te = tte.getRecord(len - 1 - i);
						te.reduce = 0;
					}
					globalClient.setOutPara(key, 60 - v);
//					globalClient.setOutPara(SFKey, v);
				}
			} else {
				int sm = 60 - x;
				globalClient.setOutPara(key, sm);
//				globalClient.setOutPara(SFKey, x);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SMServiceHelper.getInstance().dataQueue.clear();
		dataCountThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		pause = true;
		// 加个0占位，免得dataCountThread线程吊死
		SMServiceHelper.getInstance().dataQueue.offer(0);
	}
}
