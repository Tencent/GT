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

public class InPara extends AidlEntry {

	// AIDL属性
	private String key;
	private String alias;
	private List<String> values = new ArrayList<String>();
	private int displayProperty;

	// 本地属性
	private boolean isRegistering; // 本地用于区分是否是注册的对象，客户端用属性
	private boolean isGlobal; // 是否注册为全局属性

	// GT服务端属性，client标识，因为UI和性能结构里没有Client的概念，所以参数需要记住其所属的client
	private String client;

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public InPara() {
		setFunctionId(Functions.REGISTER_IN_PARA);
	}

	public InPara(Parcel parcel){
		setFunctionId(parcel.readInt());
		key = parcel.readString();
		alias = parcel.readString();
		parcel.readStringList(values);
		displayProperty = parcel.readInt();
	}

	public void setKey(String key){
		this.key = key;
	}

	public void setAlias(String alias){
		this.alias = alias;
	}

	public void setValues(List<String> values){
		this.values = values;
	}

	public void setDisplayProperty(int displayProperty){
		this.displayProperty = displayProperty;
	}

	public void setRegistering(boolean isRegistering) {
		this.isRegistering = isRegistering;
	}

	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	public String getKey(){
		return key;
	}

	public String getAlias(){
		return alias;
	}

	public List<String> getValues(){
		return values;
	}

	public int getDisplayProperty(){
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
		parcel.writeStringList(values);
		parcel.writeInt(displayProperty);
	}

	public static final Parcelable.Creator<InPara> CREATOR = new Creator<InPara>(){
		public InPara createFromParcel(Parcel parcel){
			return new InPara(parcel);
		}
		public InPara[] newArray(int size){
			return new InPara[size];
		}
	};
}
