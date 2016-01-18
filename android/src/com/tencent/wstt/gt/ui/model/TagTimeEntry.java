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

import java.util.ArrayList;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.utils.DoubleUtils;

public class TagTimeEntry extends NamedEntry {
	public static final int TYPE_GLOBAL = 0;
	public static final int TYPE_THREAD = 1;
	
	private String nameT;
	private NamedEntry parent;
	private long tid;
	private int exkey;
	private int functionId;

	private String unit = ""; // 单位，性能出参需要标明单位
	private String alias = ""; // 别名，出参需要
	private String lastValue = ""; // 曲线图的数字对应的原始出参字符串
	private String desc = ""; // 描述，出参的解释
	private ThresholdEntry thresholdEntry; // 阈值对象

	// 里面记录的数据单位是微秒
	private DataRecorder<TimeEntry> dataRecorder;

	private TagTimeEntry[] subEntrys = {};
	
	private long lastStart = 0L; // 单位是纳秒，只用于运算

	private long max = Long.MIN_VALUE; // 单位是微秒
	private long min = Long.MAX_VALUE; // 单位是微秒
	private long ave = 0; // 单位是微秒
	private double total = 0; // 单位是微秒

	// 扩展字段
	public int exInt_1 = 0; // SM计算中，用作卡顿区间时长
	public int exInt_2 = 0; // SM计算中，用作流畅区间时长
	public int exInt_3 = 0; // SM计算中，用作总体分数


	
	/*
	 * 这组数据的默认值是给耗时统计的UI展示用的
	 * 
	 * 内部记录用微秒，与纳秒换算是1000，以免太大误差
	 * 注意View上显示用毫秒，与微秒换算还有1000
	 * since 20130404 最后展示时候用double的秒来展示，精度为3
	 */
	private long carry = 1000L; // 保存的数据源微秒精度到显示的数据源毫秒精度
	private long carry_save = 1000L; // 保存的数据源微秒精度
	private long carry_s = 1000000L; // 用于从微秒到秒的转换
	private long carry_l2d = 1000L; // 显示的毫秒精度与UI展示的Double型转换的中间精度
	private int scale = 3; // 转换到秒后的精度

	private ChangedListener changedListener;

	public void setChangedListener(ChangedListener changedListener)
	{
		this.changedListener = changedListener;
	}
	
	public TagTimeEntry(NamedEntry parent)
	{
		this.parent = parent;
		this.thresholdEntry = new ThresholdEntry(this);
		this.dataRecorder = new DataRecorder<TimeEntry>();
	}
	
	public void initChildren(int size)
	{
		if (size < 1)
		{
			return;
		}
		subEntrys = new TagTimeEntry[size];
		for (int i = 0; i < size; i++)
		{
			TagTimeEntry tte = new TagTimeEntry(this);
			tte.setFunctionId(this.functionId);
			tte.setUnit(this.unit);
			subEntrys[i] = tte;
		}
	}

	public TagTimeEntry[] getChildren()
	{
		return subEntrys;
	}
	
	public TagTimeEntry[] getSubTagEntrys()
	{
		return subEntrys;
	}
	
	public boolean hasChild()
	{
		return subEntrys.length > 0;
	}
	
	public String getNameT() {
		return nameT;
	}
	
	public void setName(String name)
	{
		this.name = name;
		this.nameT = name + "(T)";
	}
	
	public long getTid() {
		return tid;
	}

	public void setTid(long type) {
		this.tid = type;
	}
	
	public int getExkey() {
		return exkey;
	}

	public void setExkey(int exkey) {
		this.exkey = exkey;
	}
	
	public void setFunctionId(int functionId)
	{
		// functionId需要保留，UI上可能根据functionId具体展示或不展示某些值
		this.functionId = functionId;
		
		// 更新UI展示用参数
		switch(this.functionId)
		{
		case Functions.PERF_DIGITAL_CPU:
			this.carry = 1;
			this.carry_s = 100; // 最终展示精度再从保存数据源基础上调节
			this.carry_save = 1; // 保存精度不变
			this.carry_l2d = 100; // CPU按xx.xx%展示
			this.scale = 2;
			break;
//		case Functions.PERF_DIGITAL_VOLT:
//			this.carry = 1;
//			this.carry_s = 1000; // 最终展示精度再从保存数据源基础上调节
//			this.carry_save = 1000; // 保存精度到V
//			this.carry_l2d = 1000; // y轴坐标受此控制
//			this.scale = 3;
//			break;
		case Functions.PERF_DIGITAL_MULT_MEM:
			this.carry = 1;
			this.carry_s = 1; // 从KB转成MB
			this.carry_save = 1; // 保存精度不变
			this.carry_l2d = 1000; // y轴坐标受此控制
			this.scale = 3;
			break;
		case Functions.PERF_REDUCE_TIME:
		case Functions.PERF_START_TIME_GLOBAL:
		case Functions.PERF_END_TIME_GLOBAL:
			this.carry = 1000L;
			this.carry_s = 1000000L;
			this.carry_save = 1000L;
			this.carry_l2d = 1000L;
			this.scale = 3;
			break;
		case Functions.PERF_DIGITAL_NORMAL:
		case Functions.PERF_DIGITAL_MULT:
		case Functions.PERF_START_DIGITAL_GLOBAL:
		case Functions.PERF_END_DIGITAL_GLOBAL:
		default:
			this.carry = 1;
			this.carry_s = 1; // 最终展示精度不需要再从保存数据源基础上调节
			this.carry_save = 1; // 保存精度不变
			this.carry_l2d = 1;
			this.scale = 0;
			break;
		}
	}
	
	public int getFunctionId() {
		return functionId;
	}
	
	public long getCarry() {
		return carry;
	}

	public long getCarry_s() {
		return carry_s;
	}
	
	public long getCarry_save() {
		return carry_save;
	}
	
	public long getCarry_l2d() {
		return carry_l2d;
	}

	public int getScale() {
		return scale;
	}
	
	public NamedEntry getParent()
	{
		return parent;
	}
	
	public void setLastStart(long start)
	{
		this.lastStart = start;
	}
	
	public long getLastStart()
	{
		return lastStart;
	}
	
	public int add(final long record)
	{
		long secondRecord = record / carry_save; // 用微秒单位记录差值，即可保证精度
		dataRecorder.add(new TimeEntry (System.currentTimeMillis(), secondRecord, functionId));
		int result = dataRecorder.size() - 1;
		max = Math.max(secondRecord, max);
		min = Math.min(secondRecord, min);
		total+=secondRecord;
		calcAve();

		try {
			if (null != changedListener)
			{
				changedListener.onLongValueChanged(record);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int add(final long time, final long record)
	{
		long secondRecord = record / carry_save; // 用微秒单位记录差值，即可保证精度
		dataRecorder.add(new TimeEntry (time, secondRecord, functionId));
		int result = dataRecorder.size() - 1;
		max = Math.max(secondRecord, max);
		min = Math.min(secondRecord, min);
		total+=secondRecord;
		calcAve();

		try {
			if (null != changedListener)
			{
				changedListener.onLongValueChanged(time, record);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void calcAve()
	{
		ave = (long) (total/dataRecorder.size());
		// 不需要total的算法：
//		int size = recordList.size();
//		ave = ave + (recordList.get(size - 1).reduce - ave) / size;
	}
	
	/**
	 * UI展示只取小范围的数据记录即可
	 * @param start 起始数据序号
	 * @param end 结束数据序号+1
	 * @return 数据记录列表
	 */
	public ArrayList<TimeEntry> getRecordList(int start, int end)
	{
		ArrayList<TimeEntry> tempRecordList = dataRecorder.getRecordList(start, end);
		return tempRecordList;
	}
	
	/**
	 * 用于保存时返回所有数据记录
	 * @return 数据记录列表
	 */
	public ArrayList<TimeEntry> getRecordList()
	{
		ArrayList<TimeEntry> tempRecordList = dataRecorder.getRecordList();
		return tempRecordList;
	}
	
	/**
	 * 获取序号为seq的数据记录
	 * @return 序号为seq的数据记录
	 */
	public TimeEntry getRecord(int seq) {
		return dataRecorder.getRecord(seq);
	}
	
	public String getRecordSizeText()
	{
		return Integer.toString(dataRecorder.size());
	}
	
	public int getRecordSize()
	{
		return dataRecorder.size();
	}
	
	public void clear()
	{
		for (TagTimeEntry child : subEntrys)
		{
			child.clear();
		}

		dataRecorder.clear();
//		tempRecordList = null;
		
		// 其他需要重置的属性
		max = Long.MIN_VALUE;
		min = Long.MAX_VALUE;
		ave = 0;
		total = 0;
		thresholdEntry.clear();
	}
	
	public String getMax() {
		if (max == Long.MIN_VALUE)
		{
			return "";
		}
		if (this.functionId == Functions.PERF_DIGITAL_CPU)
		{
			return Double.toString(DoubleUtils.div(max, carry_s, scale)) + "%";
		}
		
		return Double.toString(DoubleUtils.div(max, carry_s, scale));
	}

	public String getMin() {
		if (min == Long.MAX_VALUE)
		{
			return "";
		}
		if (this.functionId == Functions.PERF_DIGITAL_CPU)
		{
			return Double.toString(DoubleUtils.div(min, carry_s, scale)) + "%";
		}
		
		return Double.toString(DoubleUtils.div(min, carry_s, scale));
	}

	public String getAve() {
		if (this.functionId == Functions.PERF_DIGITAL_CPU)
		{
			return Double.toString(DoubleUtils.div(ave, carry_s, scale))  + "%";
		}
		return Double.toString(DoubleUtils.div(ave, carry_s, scale));
	}
	
	/**
	 * 直接返回微秒级的平均值
	 * @return
	 */
	public long getAveLong() {
		return ave;
	}

	public String getTotal() {
		if (total < 999999999L) // 1000s下，显示小数点后的，否则显示s级别即可
		{
			return Double.toString(DoubleUtils.div(total, carry_s, scale));
		}
		else
		{
			long result = (long)(total/carry_s);
			return Long.toString(result);
		}
	}
	
	public String getLastValue() {
		return lastValue;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		if (null == unit)
		{
			this.unit = "";
		}
		if (! unit.equals(""))
		{
			this.unit = "(" + unit + ")";
		}
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ThresholdEntry getThresholdEntry() {
		return thresholdEntry;
	}

	public void setThresholdEntry(ThresholdEntry thresholdEntry) {
		this.thresholdEntry = thresholdEntry;
	}
}
