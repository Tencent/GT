/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;


import com.tencent.wstt.apt.cmdparse.DumpsysCpuInfoParseUtil;
import com.tencent.wstt.apt.cmdparse.GetCpuByTopCmdUtil;
import com.tencent.wstt.apt.data.Constant;

/**
* @Description CPU数据提供者，定时更新被测进程的CPU数据 
* @date 2013年11月10日 下午5:22:08 
*
 */
public class CPUProvider extends DataProvider {
	
	private int pkgNumber;
	private String[] pkgNames;
	private int androidVersion;
	private int testMethod;

	/**
	 * @param pkgNames
	 * @param anroidVersion 标明当前系统是4.0以下还是4.0以上（top命令格式有多不同）
	 * @param method 标明是用top方式还是dumpsys cpuinfo来获取cpu数据
	 */
	public CPUProvider(String[] pkgNames, int anroidVersion, int method)
	{
		pkgNumber = pkgNames.length;
		this.androidVersion = anroidVersion;
		this.pkgNames = new String[pkgNumber];
		System.arraycopy(pkgNames, 0, this.pkgNames, 0, pkgNumber);
		testMethod = method;
	}

	@Override
	protected Number[] getData() {
		if(testMethod == Constant.TOP_INDEX)
		{
			return GetCpuByTopCmdUtil.getPkgCpuByTopCmd(pkgNames, androidVersion);
		}
		else
		{
			return DumpsysCpuInfoParseUtil.getCpuValues(pkgNames);
		}
	}

}
