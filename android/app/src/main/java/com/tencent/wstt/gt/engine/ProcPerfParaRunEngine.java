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
import java.util.Collections;
import java.util.List;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.activity.GTIntervalSettingActivity;
import com.tencent.wstt.gt.api.utils.CpuUtils;
import com.tencent.wstt.gt.api.utils.MemUtils;
import com.tencent.wstt.gt.api.utils.NetUtils;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.utils.CommonString;

public class ProcPerfParaRunEngine {

	private boolean engineRun;
	private static ProcPerfParaRunEngine instance = new ProcPerfParaRunEngine();
	private List<OutPara> enable_procPerf_ops = new ArrayList<OutPara>();
	private List<OutPara> EMPTY_LIST = Collections.emptyList();

	private ProcPerfParaRunEngine() {

	}

	public static ProcPerfParaRunEngine getInstance() {
		return instance;
	}

	public synchronized void start() {

		if (!engineRun) {
			Thread thread = new Thread(new EngineRunnable());
			thread.start();

			engineRun = true;
		}

	}

	public synchronized boolean isStarted() {
		return engineRun;
	}

	public synchronized void end() {
		engineRun = false;
	}

	public class EngineRunnable implements Runnable {

		@Override
		public void run() {
			while (engineRun) {
				int len = getCurEnableProcPerfParas().size();
				if (0 != len) {
					try {
						for (OutPara op : enable_procPerf_ops) {
							String value = getProcPerfParasValue(op);
							op.setValue(value);
						}
					} catch (Exception e) {
						/*
						 * add on 20131129 enable_procPerf_ops会经常出现同步问题，
						 * 导致引擎线程挂掉，这里紧急用try..catch规避
						 * 造成的影响是偶尔某个点没打，同时跑多个性能指标时会发现最后的计数不同
						 */
					}
				}

				try {
					Thread.sleep(GTIntervalSettingActivity.msecond);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private List<OutPara> getCurEnableProcPerfParas() {
		Client client = ClientManager.getInstance().getAUTClient();
		if (client == null)
		{
			return EMPTY_LIST;
		}
		List<OutPara> ops = ClientManager.getInstance().getAUTClient().getAllOutParas();
		enable_procPerf_ops.clear();
		for (OutPara op : ops) {
			if (OutPara.DISPLAY_DISABLE != op.getDisplayProperty()) {
				enable_procPerf_ops.add(op);
			}
		}
		return enable_procPerf_ops;
	}

	private String getProcPerfParasValue(OutPara op) {
		String value = "";
		try {
			int size = AUTManager.pIds.length;
			for (int i = 0; i < size; i++) {
				if (!ProcessUtils
						.isProcessAlive(AUTManager.pIds[i])) {
					value = "";
					break;
				}
				if (op.getKey().contains(
						CommonString.pcpu_key + i + ":"
								+ AUTManager.pNames[i])) {
					value = CpuUtils.cpuInfoMap
							.get(CommonString.pcpu_key + i + ":"
									+ AUTManager.pNames[i])
							.getProcessCpuUsage(
									Integer.valueOf(AUTManager.pIds[i]));
					long tempValue = Double.valueOf(
							(Double.valueOf(value.substring(0,
									value.length() - 1)) * 100))
							.longValue();

					OpPerfBridge.addHistory(op, value, tempValue);
					return value;

				} else if (op.getKey().contains(
						CommonString.pjif_key + i + ":"
								+ AUTManager.pNames[i])) {
					// 如果需要刷新的列表中也包含对应进程的CPU，那不需要重复刷CPU值了
					String tempCpuKey = CommonString.pcpu_key + i + ":"
							+ AUTManager.pNames[i];
					boolean hasCpuObservered = false;
					for (OutPara opTemp : enable_procPerf_ops)
					{
						if (opTemp.getKey().equals(tempCpuKey))
						{
							hasCpuObservered = true;
							break;
						}
					}

					if (! hasCpuObservered)
					{
						 CpuUtils.cpuInfoMap
							.get(tempCpuKey).getProcessCpuUsage(
									Integer.valueOf(AUTManager.pIds[i]));
					}

					long tempValue = CpuUtils.cpuInfoMap.get(tempCpuKey).getJif();
					value = String.valueOf(tempValue);
					OpPerfBridge.addHistory(op, value, tempValue);
					return value;
				} else if (op.getKey().contains(CommonString.pnet_key)) {
					String pName = AUTManager.pkn.toString();
					NetUtils netUtils = NetUtils.netInfoMap.get(pName);

					// 实际使用时候发现，收发的数据分成两条曲线最合理
					double lastT = netUtils.getP_t_add();
					double lastR = netUtils.getP_r_add();

					value = netUtils.getProcessNetValue(pName);

					double nowT = netUtils.getP_t_add();
					double nowR = netUtils.getP_r_add();

					// modify on 20120616 过滤有的手机进程流量偶尔输出负数的情况
					if ((nowT != lastT || nowR != lastR) && nowT >= 0 && nowR >= 0) {
						OpPerfBridge.addHistory(op, value, new long[]{(long) nowT, (long) nowR});
					}

					return value;
				}

				else if (op.getKey().equals(
						CommonString.pm_pss_key + i + ":"
								+ AUTManager.pNames[i])) {
					long[] long_value = MemUtils.getPSS(GTApp.getContext(),
							Integer.parseInt(AUTManager.pIds[i]));
					long tmp = long_value[0];
					long_value[0] = long_value[2];
					long_value[2] = tmp;
					for (int p = 0; p < long_value.length; p++) {
						if (p == 2) {
							value += " | Native:"
									+ String.valueOf(long_value[p] + "KB");
						} else if (p == 1) {
							value += " | Dalvik:"
									+ String.valueOf(long_value[p] + "KB");

						} else if (p == 0) {
							value += "Total:"
									+ String.valueOf(long_value[p] + "KB");
						}

					}
					OpPerfBridge.addHistory(op, value, long_value);
					return value;
				} else if (op.getKey().equals(
						CommonString.pm_pd_key + i + ":"
								+ AUTManager.pNames[i])) {
					long[] long_value1 = MemUtils.getPrivDirty(GTApp.getContext(),
							Integer.parseInt(AUTManager.pIds[i]));

					long tmp = long_value1[0];
					long_value1[0] = long_value1[2];
					long_value1[2] = tmp;
					for (int pos = 0; pos < long_value1.length; pos++) {

						if (pos == 2) {
							value += " | Native:"
									+ String.valueOf(long_value1[pos]);
						} else if (pos == 1) {
							value += " | Dalvik:"
									+ String.valueOf(long_value1[pos]);

						} else if (pos == 0) {
							value += "Total:"
									+ String.valueOf(long_value1[pos]);
						}
					}
					OpPerfBridge.addHistory(op, value, long_value1);
					return value;
				} else if (op.getKey().equals(
						CommonString.pm_hp_key + i + ":"
								+ AUTManager.pNames[i])) {
					long[] longhp_value = MemUtils.getHeapDalvik();
					long[] longhp_value2 = MemUtils.getHeapNative();
					long[] long_value3 = new long[2];
					// .getHeapDalvik();(GTSettingActivity.Pid[i]);
					long_value3[0] = longhp_value2[1];
					long_value3[1] = longhp_value[1];
					value += "Native:"
							+ String.valueOf(longhp_value2[1] + "/"
									+ String.valueOf(longhp_value2[0]));
					value += " | Dalvik:"
							+ String.valueOf(longhp_value[1] + "/"
									+ String.valueOf(longhp_value[0]));

					OpPerfBridge.addHistory(op, value, long_value3);
					return value;
				}
			}
		} catch (Exception e) {

		}
		return value;
	}
}
