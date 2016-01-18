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

public class AbsConnState implements IConnState {
	
	public static final int LOG_ALL = 0; // 全部，注意数字要和GT服务端对应
	public static final int LOG_DEBUG = 1; // 调试
	public static final int LOG_INFO = 2; // 信息
	public static final int LOG_WARNING = 3; // 警告
	public static final int LOG_ERROR = 4; // 错误

	@Override
	public void init(IConnState lastState) {

	}

	@Override
	public void init(IConnState lastState, IService gtService) {

	}

	@Override
	public void finish() {

	}
	
	@Override
	public void registerOutParas(OutPara[] outParas)
	{
		
	}

	@Override
	public void setOutPara(String paraName, Object value, boolean isGlobal) {

	}

	@Override
	public void registerInParas(InPara[] inParas) {

	}

	@Override
	public void setInPara(String paraName, Object newValue, boolean isGlobal) {

	}

	@Override
	public String getInPara(String paraName, String origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public boolean getInPara(String paraName, boolean origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public int getInPara(String paraName, int origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public double getInPara(String paraName, double origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public float getInPara(String paraName, float origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public long getInPara(String paraName, long origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public short getInPara(String paraName, short origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public byte getInPara(String paraName, byte origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public char getInPara(String paraName, char origVal, boolean isGlobal) {
		return origVal;
	}

	@Override
	public void logI(String tag, String msg) {

	}

	@Override
	public void logD(String tag, String msg) {

	}

	@Override
	public void logW(String tag, String msg) {

	}

	@Override
	public void logE(String tag, String msg) {

	}

	@Override
	public void startTime(String group, String tag, int... exKey) {

	}

	@Override
	public long endTime(String group, String tag, int... exKey) {
		return -1;
	}

	@Override
	public void startTimeInThread(String group, String tag, int... exKey) {

	}

	@Override
	public long endTimeInThread(String group, String tag, int... exKey) {
		return -1;
	}

	@Override
	public void startTimeAcrossProcess(String group, String tag, int... exKey) {

	}

	@Override
	public void endTimeAcrossProcess(String group, String tag, int... exKey) {

	}

	@Override
	public void setProfilerEnable(boolean flag) {
		
	}

	@Override
	public void setFloatViewFront(boolean flag) {
		
	}

	@Override
	public void setCommand(String receiver, Bundle bundle) {
		
	}
}
