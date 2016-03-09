/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import com.tencent.wstt.apt.cmdparse.GetJittiesByPidUtil;
import com.tencent.wstt.apt.data.Jiffies;
import com.tencent.wstt.apt.data.PkgInfo;
import com.tencent.wstt.apt.data.TestSence;

/**
* @Description 获取jiffies数据 
* @date 2013年11月10日 下午5:21:18 
*
 */
public class JiffiesProviderNew extends DataProvider {

	private int pkgNumber;
	private Long []results = null;
	public JiffiesProviderNew(int pkgNumber)
	{
		this.pkgNumber = pkgNumber;
		results = new Long[pkgNumber];
	}
	
	@Override
	protected Number[] getData() {
		for(int i = 0; i < pkgNumber; i++)
		{
			Jiffies jiffies = GetJittiesByPidUtil.getJitties(TestSence.getInstance().pkgInfos.get(i).contents[PkgInfo.PID_INDEX]);
			long tempResult = (jiffies.sTime + jiffies.uTime);
			results[i] = tempResult;
		}
		return results;
	}
}
