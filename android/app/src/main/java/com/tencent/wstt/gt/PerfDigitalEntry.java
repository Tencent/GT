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

import android.os.Parcel;


/**
 * 该类对象是需要通过aidl调用送到控制台，所以需要实现Parcelable
 * @author yoyoqin
 *
 */
public class PerfDigitalEntry extends AidlEntry {

	long logTime;
	int validLen; // TLV的L
	long[] datas;
	QueryPerfEntry queryEntry;
	
	/**
	 * 注意，该类对应了多个功能码，所以应该在外面给功能码赋值
	 * 即AIDL先生成该类型的无属性对象，
	 * 实际使用或传输前需要完成该对象的赋值，如functionId
	 */
	public PerfDigitalEntry() {
		validLen = 1;
		datas = new long[1];
	}
	
	public PerfDigitalEntry(Parcel parcel){
		setFunctionId(parcel.readInt());
		logTime = parcel.readLong();
		validLen = parcel.readInt();
		datas = new long[validLen];
		parcel.readLongArray(datas);
		queryEntry = parcel.readParcelable(getClass().getClassLoader());
	}
	
	public long getLogTime() {
		return logTime;
	}
	public void setLogTime(long logTime) {
		this.logTime = logTime;
	}
	public int getValidLen() {
		return validLen;
	}
//	public void setValidLen(int validLen) {
//		this.validLen = validLen;
//	}
	public long getData() {
		return datas[0];
	}
	public void setData(long data) {
		this.datas[0] = data;
	}
	public long[] getDatas() {
		return datas;
	}
	public void setDatas(long[] datas) {
		this.datas = datas;
		this.validLen = datas.length;
	}
	public QueryPerfEntry getQueryEntry() {
		return queryEntry;
	}
	public void setQueryEntry(QueryPerfEntry queryEntry) {
		this.queryEntry = queryEntry;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		parcel.writeLong(logTime);
		parcel.writeInt(validLen);
		parcel.writeLongArray(datas);
		parcel.writeParcelable(queryEntry, flags);
	}
	
	/**
	 * 在AIDL调用中有超类的似乎都要有这个方法
	 */
	public void readFromParcel(Parcel parcel) {
		super.readFromParcel(parcel);
		logTime = parcel.readLong();
		validLen = parcel.readInt();
		datas = new long[validLen];
		parcel.readLongArray(datas);
		queryEntry = parcel.readParcelable(getClass().getClassLoader());
	}
	
	public static final Creator<PerfDigitalEntry> CREATOR = new Creator<PerfDigitalEntry>(){
		public PerfDigitalEntry createFromParcel(Parcel parcel){
			return new PerfDigitalEntry(parcel);
		}
		public PerfDigitalEntry[] newArray(int size){
			return new PerfDigitalEntry[size];
		}
	};
}
