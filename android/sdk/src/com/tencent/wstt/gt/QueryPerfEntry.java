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
import android.os.Parcelable;

public class QueryPerfEntry implements Parcelable {
	public String group;
	public String tag;
	public long tid;
	public int exkey; // 用户自定义的区分标识
	
	public QueryPerfEntry(String group, String tag, long tid, int exKey)
	{
		this.group = group;
		this.tag = tag;
		this.tid = tid;
		this.exkey = exKey;
	}
	
	public QueryPerfEntry(Parcel parcel){
		group = parcel.readString();
		tag = parcel.readString();
		tid = parcel.readLong();
		exkey = parcel.readInt();
	}
	
	@Override
	public boolean equals(Object another)
	{
		if (this == another)
		{
			return true;
		}
		if (another instanceof QueryPerfEntry)
		{
			QueryPerfEntry anotherEntry = (QueryPerfEntry) another;
			boolean tempResult = 
					tid == anotherEntry.tid
					&& tag.equals(anotherEntry.tag)
					&& group.equals(anotherEntry.group)
					&& exkey == anotherEntry.exkey;
			
			return tempResult;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		int result = 17;
		result = 37 * result + group.hashCode();
		result = 37 * result + tag.hashCode();
		result = 37 * result + (int) (tid ^ (tid >>> 32));
		result = 37 * result + exkey;
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(group);
		parcel.writeString(tag);
		parcel.writeLong(tid);
		parcel.writeInt(exkey);
	}
	
	/**
	 * 在AIDL调用中做为参数的类都要有这个方法
	 */
	public void readFromParcel(Parcel parcel) {
		group = parcel.readString();
		tag = parcel.readString();
		tid = parcel.readLong();
		exkey = parcel.readInt();
	}
	
	public static final Parcelable.Creator<QueryPerfEntry> CREATOR = new Creator<QueryPerfEntry>(){
		public QueryPerfEntry createFromParcel(Parcel parcel){
			return new QueryPerfEntry(parcel);
		}
		public QueryPerfEntry[] newArray(int size){
			return new QueryPerfEntry[size];
		}
	};
}
