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
package com.tencent.wstt.gt.manager;

import android.content.Context;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.plugin.battery.GTBatteryEngine;
import com.tencent.wstt.gt.ui.model.ChangedListener;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.ui.model.ThresholdEntry;
import com.tencent.wstt.gt.utils.StringUtil;
import com.tencent.wstt.gt.utils.ToastUtil;

/**
 * 输出参数与性能模块对接的桥，便于后续重构
 * TODO 后续重构：性能模块需要能区分出出参所属的Client，而不是只由参数名作为key
 */
public class OpPerfBridge {
	private static OpPerfManager opPerfManager = OpPerfManager.getInstance();
	private static OpWarningManager opWarningManager = OpWarningManager.getInstance();

	/**
	 * 要保证每个出参生成的时候key都是有区分的应该就行，在客户端就需要定下来的hash法
	 * @param key
	 * @return
	 */
	public static TagTimeEntry getProfilerData(String key)
	{
		return opPerfManager.get(key);
	}
	
	public static TagTimeEntry[] getAllProfilerData()
	{
		return opPerfManager.getAll();
	}
	
	/**
	 * 获取所有非disable态出参对应的性能对象数组
	 * @return
	 */
	public static TagTimeEntry[] getAllEnableProfilerData()
	{
		return opPerfManager.getAllEnable();
	}
	
	public static void removeProfilerData(String key)
	{
		opPerfManager.remove(key);
	}

	public static void startProfier(OutPara outPara, int funcId, String desc, String unit)
	{
		if (outPara == null)
		{
			return;
		}
		
		outPara.hasMonitorOnce = true;

		TagTimeEntry profilerEntry = opPerfManager.get(outPara.getKey());
		if (null == profilerEntry)
		{
			profilerEntry = new TagTimeEntry(null);
			profilerEntry.setName(outPara.getKey());
			profilerEntry.setAlias(outPara.getAlias());
			profilerEntry.setFunctionId(funcId);
			profilerEntry.setDesc(desc);
			profilerEntry.setUnit(unit);
			profilerEntry.setExkey(ClientManager.getInstance().getClientKey(outPara.getClient()));
			opPerfManager.add(profilerEntry);
		}
	}
	
	/**
	 * 多维的启动性能统计，在UI上也需要展示多条曲线
	 * @param key
	 * @param funcId
	 */
	public static void startProfier(
			OutPara outPara, String[] subKeys, int[] funcIds, String desc, String unit)
	{
		if (outPara == null)
		{
			return;
		}
		
		outPara.hasMonitorOnce = true;

		TagTimeEntry profilerEntry = opPerfManager.get(outPara.getKey());
		if (null == profilerEntry)
		{
			profilerEntry = new TagTimeEntry(null);
			profilerEntry.setName(outPara.getKey());
			profilerEntry.setAlias(outPara.getAlias());
			profilerEntry.setFunctionId(funcIds[0]);
			profilerEntry.setDesc(desc);
			profilerEntry.setUnit(unit);
			profilerEntry.setExkey(ClientManager.getInstance().getClientKey(outPara.getClient()));
			
			profilerEntry.initChildren(subKeys.length);
			int i = 0;
			for (TagTimeEntry subEntry : profilerEntry.getSubTagEntrys())
			{
				subEntry.setName(subKeys[i]);
				subEntry.setFunctionId(funcIds[i]);
				i++;
			}
			
			opPerfManager.add(profilerEntry);
		}
	}

	/**
	 * 目前do nothing
	 * @param op
	 */
	public static void endProfier(OutPara op)
	{

	}

	public static void setThreshold(String key, ThresholdEntry threshold)
	{
		TagTimeEntry profilerEntry = opPerfManager.get(key);
		if (null == profilerEntry)
		{
			return;
		}
		profilerEntry.setThresholdEntry(threshold);
	}

	public static void addHistory(OutPara op, String nowValue, long data)
	{
		// 要在总控开关打开的前提下才要进行历史记录
		if (! OpUIManager.gw_running)
		{
			return;
		}
		
		TagTimeEntry profilerEntry = opPerfManager.get(op.getKey());
		if (null == profilerEntry)
		{
			return;
		}
		
		if (op.isMonitor()) // 有历史出参的告警
		{
			int seq = profilerEntry.add(data);
			
			// 阈值对象对本次输入值进行是否预备告警的记录
			profilerEntry.getThresholdEntry().add(data, seq);
			profilerEntry.setLastValue(nowValue);
		}
		else
		{
			// 不记录历史数据的情况下统计告警，暂不使用
//			profilerEntry.getThresholdEntry().add(data);
		}
	}
	
	public static void addHistory(OutPara op, String nowValue, long time, long data)
	{
		// 要在总控开关打开的前提下才要进行历史记录
		if (! OpUIManager.gw_running)
		{
			return;
		}
		
		TagTimeEntry profilerEntry = opPerfManager.get(op.getKey());
		if (null == profilerEntry)
		{
			return;
		}
		
		if (op.isMonitor()) // 有历史出参的告警
		{
			int seq = profilerEntry.add(time, data);
			
			// 阈值对象对本次输入值进行是否预备告警的记录
			profilerEntry.getThresholdEntry().add(data, seq);
			profilerEntry.setLastValue(nowValue);
		}
		else
		{
			// 不记录历史数据的情况下统计告警，暂不使用
//			profilerEntry.getThresholdEntry().add(data);
		}
	}

	/**
	 * 多维的历史数据，在UI上也需要展示多条曲线
	 * @param key 对应出参的key
	 * @param opValue 原始出参值
	 * @param data 对应多维数据的值数组
	 */
	public static void addHistory(OutPara parentOp, String nowValue, long[] data)
	{
		// 要在总控开关打开的前提下才要进行历史记录
		if (! OpUIManager.gw_running)
		{
			return;
		}
				
		TagTimeEntry profilerEntry = opPerfManager.get(parentOp.getKey());
		if (null == profilerEntry)
		{
			return;
		}
		
		if (parentOp.isMonitor()) // 有历史出参的告警
		{
			profilerEntry.setLastValue(nowValue);
			
			int i = 0;
			for (TagTimeEntry subEntry : profilerEntry.getChildren())
			{
				
				
				int seq = subEntry.add(data[i]);
				
				// 阈值对象对本次输入值进行是否预备告警的记录
				subEntry.getThresholdEntry().add(data[i], seq);
				i++;
			}
		}
		else // 没有记录历史出参的告警
		{
			int i = 0;
			for (TagTimeEntry subEntry : profilerEntry.getChildren())
			{
				// 阈值对象对本次输入值进行是否预备告警的记录,暂不使用
				subEntry.getThresholdEntry().add(data[i]);
				i++;
			}
		}
		
	}

	// 全局的
	public static OpWarningManager getOpWarningManager()
	{
		return opWarningManager;
	}

	/**
	 * @since 2.2
	 * 这个方法的实际作用是开始监控，并对要监控的出参的不同特点进行不同设置，
	 * 放在这里统一处理也是弥补两个方面问题：
	 * 1.客户端和服务端出参对象为了尽量一致，不带有性能统计对象属性，而本期不会对性能统计UI进行变更
	 * 2.职责是面向单位和是否多曲线，没有性能压力，就聚合在一个方法里处理了
	 * @param ov
	 */
	public static void registMonitor(OutPara ov) {
		String key = ov.getKey();
		if (ov.hasMonitorOnce) {
			return;
		}
		Context context = GTApp.getContext();
		if (key.equals("MEM")) {
			OpPerfBridge.startProfier(ov,
					Functions.PERF_DIGITAL_NORMAL, "ALL Used Memory(MB)", "MB");
		} else if (key.equals("CPU")) {
			OpPerfBridge.startProfier(ov, Functions.PERF_DIGITAL_CPU,
					"ALL CPU occupy", "%");
		} else if (key.equals("NET")) {
			int[] funIds = { Functions.PERF_DIGITAL_MULT,
					Functions.PERF_DIGITAL_MULT};
			String[] subKeys = { "transmitted", "received"};
			OpPerfBridge.startProfier(ov,
					subKeys,funIds, "", "KB");
		} else if (key.equals("Signal")) {
			String[] sigSubKeys = { "net", "wifi" };
			int[] sigFuncIds = { Functions.PERF_DIGITAL_MULT,
					Functions.PERF_DIGITAL_MULT };
			OpPerfBridge.startProfier(ov, sigSubKeys, sigFuncIds, "", "dbm");
		}else if (key.equals("FPS")) {
			OpPerfBridge.startProfier(ov,
					Functions.PERF_DIGITAL_NORMAL, "FPS", "");
		/*
		 * 插件的，需要转换成mV
		 */
		} else if (key.equals(GTBatteryEngine.OPU)) // 电压值，在GTBatteryActivity中定义的
		{
			OpPerfBridge.startProfier(ov,
					Functions.PERF_DIGITAL_VOLT, "Volt value(mV)", "mV");
		}
		/*
		 * 插件的Pow，也要支持记录历史记录
		 */
		 else if (key.equals(GTBatteryEngine.OPPow)) // 电压值，在GTBatteryActivity中定义的
		{
			OpPerfBridge.startProfier(ov,
					Functions.PERF_DIGITAL_NORMAL, "Power value(%)", "%");
		}
		else {
			String value = ov.getValue();
			// value是空字符串时，经常是还没开始统计，这时不应该判定为不可监控
			if (StringUtil.isNumeric(value) || "".equals(value)) {
				OpPerfBridge.startProfier(ov,
						Functions.PERF_DIGITAL_NORMAL, "", "");
			} else {
				// 非数字的不可监控
				ov.setMonitor(false);
				ov.hasMonitorOnce = false;
				ToastUtil.ShowShortToast(context,
						"observable OutPara value must be digit.");
				return;
			}
		}
		ov.hasMonitorOnce = true;
	}

	public static void setOutparaChangedListener(String smName,
			ChangedListener changedListener) {
		TagTimeEntry tte = getProfilerData(smName);
		tte.setChangedListener(changedListener);
	}
}
