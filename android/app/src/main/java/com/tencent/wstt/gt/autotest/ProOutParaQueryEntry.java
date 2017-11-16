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
package com.tencent.wstt.gt.autotest;


public class ProOutParaQueryEntry {

	public String pkgName;
	public int pid;
	public String key;
	
	public ProOutParaQueryEntry(String pkgName, int pid, String key)
	{
		this.pkgName = pkgName;
		this.pid = pid;
		this.key = key;
	}

	@Override
	public boolean equals(Object another)
	{
		if (this == another)
		{
			return true;
		}
		if (another instanceof ProOutParaQueryEntry)
		{
			ProOutParaQueryEntry anotherOne = (ProOutParaQueryEntry)another;
			// 非进程指标如FPS的情况
			// 非进程指标如FPS的情况,pid == -1, pkgName==null
			if ((pid == anotherOne.pid)
					&& (pkgName == null && anotherOne.pkgName == null || pkgName.equals(anotherOne.pkgName))
					&& key != null && anotherOne.key != null && key.equals(anotherOne.key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int result = 17;
		result = 37 * result + (pkgName == null ? 0 : pkgName.hashCode());
		result = 37 * result + (key == null ? 0 : key.hashCode());
		result = 37 * result + (int) (pid ^ (pid >>> 32));

		return result;
	}
}
