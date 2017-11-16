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

public class BooleanEntry extends AidlEntry {
	private boolean data;
	
	public BooleanEntry()
	{

	}
	
	public BooleanEntry(Parcel parcel) {
		setFunctionId(parcel.readInt());
		boolean[] datas = new boolean[1];
		parcel.readBooleanArray(datas);
		data = datas[0];
	}
	
	public void setData(boolean data)
	{
		this.data = data;
	}
	
	public boolean getData()
	{
		return data;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public void readFromParcel(Parcel parcel) {
		super.readFromParcel(parcel);
		boolean[] booleanVals = new boolean[1];
		parcel.readBooleanArray(booleanVals);
		data = booleanVals[0];
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		boolean[] booleanVals = new boolean[1];
		booleanVals[0] = data;
		parcel.writeBooleanArray(booleanVals);
	}
	
	public static final Parcelable.Creator<BooleanEntry> CREATOR = new Creator<BooleanEntry>() {
		public BooleanEntry createFromParcel(Parcel source) {
			return new BooleanEntry(source);
		}

		public BooleanEntry[] newArray(int size) {
			return new BooleanEntry[size];
		}
	};
}
