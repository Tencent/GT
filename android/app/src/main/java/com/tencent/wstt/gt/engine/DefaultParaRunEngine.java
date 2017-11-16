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
package com.tencent.wstt.gt.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.activity.GTIntervalSettingActivity;
import com.tencent.wstt.gt.api.utils.CpuUtils;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.FpsTimerTask;
import com.tencent.wstt.gt.api.utils.MemUtils;
import com.tencent.wstt.gt.api.utils.NetUtils;
import com.tencent.wstt.gt.api.utils.SignalUtils;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientFactory;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.manager.SingleInstanceClientFactory;
import com.tencent.wstt.gt.utils.GTFrameUtils;

public class DefaultParaRunEngine {

	private String[] gt_default_op_keys = { "MEM", "NET", "CPU", "Signal","FPS" };
	private String[] gt_default_op_alias = { "MEM", "NET", "CPU", "SIG", "FPS" };
	private List<OutPara> enable_default_ops;
	private Client client;
	private Thread thread;

	Timer fpsTimer40;

	private boolean fps_gather = false;
	private boolean hasCheckSu = false;

	public DefaultParaRunEngine() {
		enable_default_ops = new ArrayList<OutPara>();
		ClientFactory cf = new SingleInstanceClientFactory();

		int size = gt_default_op_keys.length;
		OutPara[] ops = new OutPara[size];
		for (int i = 0; i < size; i++) {
			OutPara para = new OutPara();
			para.setKey(gt_default_op_keys[i]);
			para.setAlias(gt_default_op_alias[i]);
			ops[i] = para;
		}

		client = cf.orderClient(ClientManager.DEFAULT_CLIENT, ClientManager.DEFAULT_CLIENT.hashCode(), ops, null);
		// 全局执行一次即可
		OpUIManager.initDefaultOutputParamList();
	}

	public void start() {
		Thread thread = new Thread(new EngineRunnable());
		thread.start();
	}

	public class EngineRunnable implements Runnable {

		@Override
		public void run() {
			while (true) {
				int len = getCurEnableDefaultOutParas().size();
				if (0 != len) {
					for (OutPara op : enable_default_ops) {
						String value = refreshDefaultOutParasValue(op);
						op.setValue(value);
					}
				} else {

				}
				try {
					Thread.sleep(GTIntervalSettingActivity.msecond);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private List<OutPara> getCurEnableDefaultOutParas() {
		List<OutPara> ops = client.getAllOutParas();
		enable_default_ops.clear();
		for (OutPara op : ops) {
			if (OutPara.DISPLAY_DISABLE != op.getDisplayProperty()) {
				enable_default_ops.add(op);
			}
		}
		return enable_default_ops;
	}

	private String refreshDefaultOutParasValue(OutPara para) {
		if (null == para) {
			return "";
		}

		String key = para.getKey();
		String value = "";
		if (OutPara.DISPLAY_DISABLE != para.getDisplayProperty()
				&& key.equals(gt_default_op_keys[0])) {
			long[] memInfo = MemUtils.getMemInfo();
			value = MemUtils.trans2FreeAndTotalMem(memInfo);

			long[] memInfo4UI = new long[2];
			memInfo4UI[0] = (memInfo[1] + memInfo[2] + memInfo[3]);
			memInfo4UI[1] = memInfo[0];
			OpPerfBridge.addHistory(para, value, memInfo4UI[0]);
		} else if (OutPara.DISPLAY_DISABLE != para.getDisplayProperty()
				&& key.equals(gt_default_op_keys[1])) {	
			// 实际使用时候发现，收发的数据分成两条曲线最合理
			double lastT = NetUtils.getT_add_wifi()
					+ NetUtils.getT_add_3G()
					+ NetUtils.getT_add_2G();
			double lastR = NetUtils.getR_add_wifi()
					+ NetUtils.getR_add_3G()
					+ NetUtils.getR_add_2G();

			value = NetUtils.getNetValue();

			double nowT = NetUtils.getT_add_wifi()
					+ NetUtils.getT_add_3G()
					+ NetUtils.getT_add_2G();
			double nowR = NetUtils.getR_add_wifi()
					+ NetUtils.getR_add_3G()
					+ NetUtils.getR_add_2G();

			value = NetUtils.getNetValue();

			if (nowT != lastT || nowR != lastR) {
				OpPerfBridge.addHistory(para, value, new long[]{(long) nowT, (long) nowR});
			}
		} else if (OutPara.DISPLAY_DISABLE != para.getDisplayProperty()
				&& key.equals(gt_default_op_keys[2])) {
			value = CpuUtils.getCpuUsage() + "%";

			long tempValue = Double
					.valueOf(
							(Double.valueOf(value.substring(0,
									value.length() - 1)) * 100)).longValue();
			OpPerfBridge.addHistory(para, value, tempValue);
		} else if (OutPara.DISPLAY_DISABLE != para.getDisplayProperty()
				&& key.equals(gt_default_op_keys[3])) {
			int wifiData = SignalUtils.getWifiStrength();
			if (wifiData < -200) {
				wifiData = -200;
			}
			int netData = SignalUtils.getDBM();
			value = netData + "dbm[" + SignalUtils.getNetType() + "]|"
					+ wifiData + "dbm[WIFI]";

			long[] datas = { netData, wifiData };
			OpPerfBridge.addHistory(para, value, datas);
		} else if (key.equals(gt_default_op_keys[4])) {
			if (OutPara.DISPLAY_DISABLE != para.getDisplayProperty()) {
				if (! fps_gather) {
					runFps();
				}
				value = para.getValue();
			}
			else
			{
				// 这里想执行stop逻辑根本没机会，因为在这个方法之前监听的出参就洗牌了
//				stopFps();
			}
		}
		return value;
	}

	private synchronized void runFps() {
		if (Env.API >= 14)
		{
			fps_gather = true;

			if (! hasCheckSu) {
				thread = new Thread(new CheckSuRunnable(), "CheckSu");
				thread.setDaemon(true);
				thread.start();
			}

			// 因为这个Timer是延时执行，所以基本能赶上su判断线程出结果
			fpsTimer40 = new Timer();
			fpsTimer40.schedule(new FpsTimerTask(), 0, GTIntervalSettingActivity.msecond_FPS);
		}
	}

	class CheckSuRunnable implements Runnable { // 无su，线程挂住
		@Override
		public void run() {
			hasCheckSu = true;
			GTFrameUtils.setPid();
		}

	}

//	private synchronized void stopFps() {
//		if (Env.API >= 14)
//		{
//			fpsTimer40.cancel();
//			FpsTimerTask.stopCurrentTask();
//		}
//		// 老版本无法真正stop
//	}
}