/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import com.tencent.wstt.apt.cmdparse.DumpsysMemInfoPkgParseUtil;
import com.tencent.wstt.apt.data.Constant;

/**
* @Description 被测应用内存数据的提供者 
* @date 2013年11月10日 下午5:24:53 
*
 */
public class MemProvider extends DataProvider {

	private String[] pkgNames;
	private int pkgNumber;
	private int androidVersion;
	private Integer[] result;

	public MemProvider(String []pkgNames, int androidVersion)
	{
		this.pkgNumber = pkgNames.length;
		this.androidVersion = androidVersion;
		this.pkgNames = new String[pkgNumber];
		System.arraycopy(pkgNames, 0, this.pkgNames, 0, pkgNumber);
		result = new Integer[pkgNumber*Constant.ALL_MEM_KIND_COUNT];
	}
	@Override
	protected Number[] getData() {
		for(int i = 0; i < pkgNumber; i++)
		{
			Integer[] temp = DumpsysMemInfoPkgParseUtil.run(pkgNames[i], androidVersion);
			if (null == temp)
			{
				return null;
			}
			System.arraycopy(temp, 0, result, i*Constant.ALL_MEM_KIND_COUNT, Constant.ALL_MEM_KIND_COUNT);
		}
		return result;
	}

}
