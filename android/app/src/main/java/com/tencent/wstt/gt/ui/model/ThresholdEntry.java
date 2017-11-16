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
package com.tencent.wstt.gt.ui.model;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.activity.GTIntervalSettingActivity;
import com.tencent.wstt.gt.api.base.GTLog;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.utils.ToastUtil;


/**
 * 阈值对象，随着TagTimeEntry对象的初始化而初始化
 * @author yoyoqin
 *
 */
public class ThresholdEntry {
	private TagTimeEntry src;
	private boolean enable = true; // 本告警自身的开关，单独控制告警是否可用

	private int duration = Integer.MAX_VALUE; // 连续超出上限阈值的次数，作为告警
	private double upperValue = Integer.MAX_VALUE; // 上限阈值，不能用Double的
	private double lowerValue = Integer.MIN_VALUE; // 下限阈值，不能用Double的
	
	private int lastUpperBeginSeq; // 当前记录的下限告警起始序列
	private boolean isUpperRecordStarted; // 是否已开始上限告警记录
	
	private int lastLowerBeginSeq; // 当前记录的下限告警起始序列
	private boolean isLowerRecordStarted; // 是否已开始下限告警记录
	
	private int durUpperTimes = 0;// 连续超上限的次数
	private int durLowerTimes = 0;// 连续超下限的次数
	
	private boolean newWarning; // 是否有新告警，也许应该放在OP类中
	private WarningEntry lastestWarning; // 记录了已触发告警的范围

	private int upperWariningCount; // 上限告警计数
	private int lowerWariningCount; // 下限告警计数
	
	private boolean isToasted; // 对于下限大于上限的告警，只弹出一次提示
	
	private static int UPPER = 0;
	private static int LOWER = 1;
	
	protected ThresholdEntry(TagTimeEntry src)
	{
		this.src = src;
	}
	
	public void clear()
	{
		upperWariningCount = 0;
		lowerWariningCount = 0;
		lastUpperBeginSeq = 0;
		isLowerRecordStarted = false;
		lastLowerBeginSeq = 0;
		isLowerRecordStarted = false;
		newWarning = false;
		lastestWarning = null;
	}
	
	private void reset()
	{
		duration = Integer.MAX_VALUE;
		upperValue = Integer.MAX_VALUE;
		lowerValue = Integer.MIN_VALUE;
	}
	
	public int getUpperWariningCount() {
		return upperWariningCount;
	}

	public int getLowerWariningCount() {
		return lowerWariningCount;
	}
	
	public int getduration() {
		if (duration == Integer.MAX_VALUE)
		{
			return Integer.MAX_VALUE;
		}

		/*
		 * 持续时间需要根据采样率进行转换
		 * 如果采样率可选的是100,500,1000,5000,10000,可以用简单逻辑
		 * 不过get这个方法不需要加1
		 */
		int temp = duration * GTIntervalSettingActivity.msecond / 1000;
		
		// 如果temp小于0，说明计算中超出MAX_VALUE上限了
		return temp > 0 ? temp : Integer.MAX_VALUE;
	}

	public double getUpperValue() {
		return upperValue;
	}

	public double getLowerValue() {
		return lowerValue;
	}

	/**
	 * 
	 * @param duration
	 * @param upperValue
	 * @param lowerValue
	 * @return
	 */
	public boolean setThreshold(
			int duration, double upperValue, double lowerValue)
	{
		if (duration == Integer.MAX_VALUE) // 说明没输入持续时间，直接重置
		{
			clear();
			reset();
			return false;
		}
		
		// 用户输入的持续时间，需要比数据采样率大，采样率单位是毫秒，最好是整数倍
		if (duration * 1000 < GTIntervalSettingActivity.msecond)
		{
			return false;
		}
		
		// 持续时间需要根据采样率进行转换
		int sampleRate = GTIntervalSettingActivity.msecond;
		
		/*
		 * 如果采样率可选的是100,500,1000,5000,10000,可以用简单逻辑
		 * 否则结果应该加1
		 */
		if (duration * 1000 % sampleRate > 0)
		{
			this.duration = duration * 1000 / sampleRate + 1;
		}
		else
		{
			this.duration = duration * 1000 / sampleRate;
		}
		
		
		this.upperValue = upperValue;
		this.lowerValue = lowerValue;
		
		/*
		 * 需要重新统计告警,增加告警后通知UI
		 */
		lastestWarning = null;
		upperWariningCount = 0;
		lowerWariningCount = 0;
		
		return true;
	}
	
	public boolean isNewWarning() {
		return newWarning;
	}

	/**
	 * 记录最新一次告警
	 * @param begin
	 * @param end
	 */
	private void recordLastestWaring(int begin, int end, int type, double value)
	{
		lastestWarning = new WarningEntry(src, begin, end);
		OpPerfBridge.getOpWarningManager().add(lastestWarning);
		String name = src.getParent() == null ? src.name : src.getParent().name + "_" + src.name;
		if (type == UPPER)
		{
			GTLog.logW(name, "Exceeds threshold value:" + value + " upper than " + upperValue);
		}
		else if (type == LOWER)
		{
			GTLog.logW(name, "Exceeds threshold value:" + value + " lower than " + lowerValue);
		}
	}
	
	private void judgeToast()
	{
		if (upperValue <= lowerValue && !isToasted)
		{
			isToasted = true;
			ToastUtil.ShowLongToast(
					GTApp.getContext(), "lower value must less than upper value!");
			return;
		}
		else if (upperValue <= lowerValue && isToasted)
		{
			return;
		}
		
		isToasted = false;
	}
	
	/**
	 * 不记录历史数据的情况下统计告警，暂不使用
	 * @param data
	 */
	public void add(long data)
	{
		if (!enable)
		{
			return;
		}

		double realUpperV = upperValue;
		double realLowerV = lowerValue;

		judgeToast(); // 上限小于下限会弹一次toast告警
		// 需要做数据转换
		double d = data / src.getCarry_l2d();
		
		if (d > realUpperV) // 本次数据大于上限阈值
		{
			durUpperTimes++;
			
			if (isUpperRecordStarted) // 已开始记录过或持续次数为1
			{
				if (durUpperTimes == duration) // 一次新的告警生成
				{
					// 新告警，如在性能页面，需要在页面刷新后重置告警为false
					newWarning = true;
					upperWariningCount++;
					recordLastestWaring(-1, -1, UPPER , d);
				}
			}
			else if (!isUpperRecordStarted && duration == 1) // 直接记录为一次告警
			{
				isUpperRecordStarted = true;

				// 新告警，如在性能页面，需要在页面刷新后重置告警为false
				newWarning = true;
				upperWariningCount++;
				recordLastestWaring(-1, -1, UPPER , d);
			}
			else // 第一次超上限，需要持续观察
			{
				isUpperRecordStarted = true;
			}
		}
		else if (d < realLowerV) // 本次数据小于下限阈值
		{
			durLowerTimes++;
			
			if (isLowerRecordStarted) // 已开始记录过或持续次数为1
			{
				if (durLowerTimes == duration) // 一次新的告警生成
				{
					// 新告警，如在性能页面，需要在页面刷新后重置告警为false
					newWarning = true;
					lowerWariningCount++;
					recordLastestWaring(-1, -1, LOWER , d);
				}
			}
			else if (!isLowerRecordStarted && duration == 1) // 直接记录为一次告警
			{
				isLowerRecordStarted = true;

				// 新告警，如在性能页面，需要在页面刷新后重置告警为false
				newWarning = true;
				lowerWariningCount++;
				recordLastestWaring(-1, -1, LOWER , d);
			}
			else // 第一次超下限，需要持续观察
			{
				isLowerRecordStarted = true;
			}
		}
		else // 阈值内，重置告警
		{
			isUpperRecordStarted = false;
			lastUpperBeginSeq = -1;
			isLowerRecordStarted = false;
			lastLowerBeginSeq = -1;
			durUpperTimes = 0;
			durLowerTimes = 0;
		}
	}

	public void add(long data, int seq)
	{
		if (!enable)
		{
			return;
		}

		/*
		 * 这两个值是兼容用户输入的上限值小于下限值设定的
		 * 但这时只写一个数时不好处理，不知道它是上限还是下限，所以恢复原来逻辑
		 */
		double realUpperV = upperValue;
		double realLowerV = lowerValue;
		
		judgeToast(); // 上限小于下限会弹一次toast告警

		// 需要做数据转换
		double d = data / src.getCarry_l2d();
		if (d > realUpperV) // 本次数据大于上限阈值
		{
			if (isUpperRecordStarted) // 已开始记录过或持续次数为1
			{
				int seqReduce = seq - lastUpperBeginSeq;
				
				if (seqReduce > duration && null != lastestWarning) // 持续超出阈值
				{
					// 更新最后一次告警对象（延长告警结束seq为当前seq）
					lastestWarning.end = seq;
				}
				else if (seqReduce == duration) // 一次新的告警生成
				{
					// TODO 新告警，如在性能页面，需要在页面刷新后重置告警为false
					newWarning = true;
					upperWariningCount++;
					recordLastestWaring(lastUpperBeginSeq, seq, UPPER, d);
				}
			}
			else if (!isUpperRecordStarted && duration == 1) // 直接记录为一次告警
			{
				isUpperRecordStarted = true;
				lastUpperBeginSeq = seq;
				
				// TODO 新告警，如在性能页面，需要在页面刷新后重置告警为false
				newWarning = true;
				upperWariningCount++;
				recordLastestWaring(lastUpperBeginSeq, seq, UPPER, d);
			}	
			else // 第一次超上限，需要持续观察
			{
				isUpperRecordStarted = true;
				lastUpperBeginSeq = seq;
			}

			// 清理下限记录（因为一开始限定了下限不能大于上限）
			isLowerRecordStarted = false;
			lastLowerBeginSeq = -1;
		}
		else if (d < realLowerV) // 本次数据小于下限阈值
		{
			if (isLowerRecordStarted)
			{
				int seqReduce = seq - lastLowerBeginSeq;
				
				if (seqReduce > duration && null != lastestWarning) // 持续超出阈值
				{
					// 更新最后一次告警对象（延长告警结束seq为当前seq）
					lastestWarning.end = seq;
				}
				else if (seqReduce == duration) // 一次新的告警生成
				{
					// TODO 新告警，如在性能页面，需要在页面刷新后重置告警为false
					newWarning = true;
					lowerWariningCount++;
					recordLastestWaring(lastLowerBeginSeq, seq, LOWER, d);
				}
			}
			else if (!isLowerRecordStarted && duration == 1) // 直接记录为一次告警
			{
				isLowerRecordStarted = true;
				lastLowerBeginSeq = seq;
				
				// TODO 新告警，如在性能页面，需要在页面刷新后重置告警为false
				newWarning = true;
				lowerWariningCount++;
				recordLastestWaring(lastLowerBeginSeq, seq, LOWER, d);
			}
			else // 第一次超下限，需要持续观察
			{
				isLowerRecordStarted = true;
				lastLowerBeginSeq = seq;
			}

			// 清理上限记录（因为一开始限定了下限不能大于上限）
			isUpperRecordStarted = false;
			lastUpperBeginSeq = -1;
		}
		else // 阈值内，重置告警
		{
			isUpperRecordStarted = false;
			lastUpperBeginSeq = -1;
			isLowerRecordStarted = false;
			lastLowerBeginSeq = -1;
		}
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
		if (!enable)
		{
			clear();
		}
	}
}