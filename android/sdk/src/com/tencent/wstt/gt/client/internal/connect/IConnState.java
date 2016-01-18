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
package com.tencent.wstt.gt.client.internal.connect;

import android.os.Bundle;

import com.tencent.wstt.gt.IService;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;

public interface IConnState {

	void init(IConnState lastState);
	
	// 需要AIDL远程调用的状态的初始化接口
	void init(IConnState lastState, IService gtService);

	void finish();
	
	// 注册出参,只在连接态有效，连接后按目前的接口客户端没有注册的机会了
	void registerOutParas(OutPara[] outParas);
	
	// TODO 是否异步可选或待定
	void setOutPara(String paraName, Object value, boolean isGlobal);

	// 注册入参
	void registerInParas(InPara[] inParas);
	
	// TODO 是否异步可选或待定
	void setInPara(String paraName, Object newValue, boolean isGlobal);
	
	// 同步的
	String getInPara(String paraName, String origVal, boolean isGlobal);
	boolean getInPara(String paraName, boolean origVal, boolean isGlobal);
	int getInPara(String paraName, int origVal, boolean isGlobal);
	double getInPara(String paraName, double origVal, boolean isGlobal);
	float getInPara(String paraName, float origVal, boolean isGlobal);
	long getInPara(String paraName, long origVal, boolean isGlobal);
	short getInPara(String paraName, short origVal, boolean isGlobal);
	byte getInPara(String paraName, byte origVal, boolean isGlobal);
	char getInPara(String paraName, char origVal, boolean isGlobal);
	
	void logI(String tag, String msg);
	
	void logD(String tag, String msg);
	
	void logW(String tag, String msg);
	
	void logE(String tag, String msg);
	
	void startTime(String group, String tag, int...exKey);
	
	long endTime(String group, String tag, int...exKey);
	
	void startTimeInThread(String group, String tag, int...exKey);
	
	long endTimeInThread(String group, String tag, int...exKey);
	
	void startTimeAcrossProcess(String group, String tag, int...exKey);
	// 注意跨进程的end是异步的，无返回值
	void endTimeAcrossProcess(String group, String tag, int...exKey);
	
	void setProfilerEnable(boolean flag);
	
	void setFloatViewFront(boolean flag);
	
	void setCommand(String receiver, Bundle bundle);
}
