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
package com.tencent.wstt.gt;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class OutPara extends AidlEntry {

	// AIDL属性
	private String key;
	private String alias;
	private String value;
	private String freezValue; // 保存disable前一刻的出参值
	private int displayProperty; // 0:展示在悬浮窗 1：展示在normal区 2：展示在disable区

	private List<String> cacheHistory;

	private long time = -1; // 最后一次值变更的时间，用于手动输入时间的情况

	// 本地属性
	private boolean isRegistering; // 本地用于区分是否是注册的对象
	private boolean isGlobal; // 是否注册为全局参数，全局参数即可以跨APP共享，非全局参数只在单个APP内有效

	private boolean monitor; // 标记是否监控
	public boolean hasMonitorOnce; // 标记是否曾监控过
	public boolean alert = false;// 标记是否告警过

	// GT服务端属性，client标识，因为UI和性能结构里没有Client的概念，所以参数需要记住其所属的client
	private String client;

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public OutPara() {
		setFunctionId(Functions.REGISTER_OUT_PARA);
		cacheHistory = new ArrayList<String>();
	}

	public OutPara(Parcel parcel) {
		setFunctionId(parcel.readInt());
		key = parcel.readString();
		alias = parcel.readString();
		value = parcel.readString();
		freezValue = "";
		displayProperty = parcel.readInt();
		cacheHistory = new ArrayList<String>();
		parcel.readStringList(cacheHistory);
		this.time = parcel.readLong();
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setValue(String value) {
		if (null == value) {
			this.value = "";
		} else {
			this.value = value;
		}
	}

	public void setValue(long time, String value) {
		if (null == value) {
			this.value = "";
		} else {
			this.value = value;
		}
		this.time = time;
	}

	public void setFreezValue(String freezValue) {
		this.freezValue = freezValue;
	}

	public void setDisplayProperty(int displayProperty) {
		this.displayProperty = displayProperty;
	}

	public void setRegistering(boolean isRegistering) {
		this.isRegistering = isRegistering;
	}

	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	public void addHistory(String h) {
		cacheHistory.add(h);
	}

	public String getKey() {
		return key;
	}

	public String getAlias() {
		return alias;
	}

	public String getValue() {
		return value;
	}

	public String getFreezValue() {
		return freezValue;
	}

	public int getDisplayProperty() {
		return displayProperty;
	}

	public boolean isRegistering() {
		return isRegistering;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		parcel.writeString(key);
		parcel.writeString(alias);
		parcel.writeString(value);
		parcel.writeInt(displayProperty);
		parcel.writeStringList(cacheHistory);
		parcel.writeLong(time);
	}

	public static final Parcelable.Creator<OutPara> CREATOR = new Creator<OutPara>() {
		public OutPara createFromParcel(Parcel source) {
			return new OutPara(source);
		}

		public OutPara[] newArray(int size) {
			return new OutPara[size];
		}
	};
}
