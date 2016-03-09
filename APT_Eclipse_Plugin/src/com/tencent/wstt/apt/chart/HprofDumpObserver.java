/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.util.Arrays;
import java.util.Date;

import com.tencent.wstt.apt.adb.DDMSUtil;
import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.TestSence;

/**
* @Description 自动dump内存快照观察者，当前进程PSSTotal大于设定的阈值时自动dump 
* @date 2013年11月10日 下午5:29:42 
*
 */
public class HprofDumpObserver extends Observer {

	private String []pkgNames;
	private boolean []lastStates;
	private int pkgNum;
	public HprofDumpObserver(String []pkgs)
	{
		pkgNum = pkgs.length;
		pkgNames = new String[pkgNum];
		System.arraycopy(pkgs, 0, pkgNames, 0, pkgNum);
		lastStates = new boolean[pkgNum];
		Arrays.fill(lastStates, false);
	}
	@Override
	public void update(Date time, Number[] datas) {

		for(int i = 0; i < pkgNum; i++)
		{
			int pssTotal = datas[i*Constant.ALL_MEM_KIND_COUNT + Constant.PSS_TOTAL_INDEX].intValue();
			if(pssTotal > TestSence.getInstance().dumpHprofThreshold)
			{	
				if(!lastStates[i])
				{
					DDMSUtil.dump(pkgNames[i]);
					APTConsoleFactory.getInstance().APTPrint("dump hprof " + pkgNames[i]);
				}
				
				lastStates[i] = true;
			}
			else
			{
				lastStates[i] = false;
			}
		}
	}

}
