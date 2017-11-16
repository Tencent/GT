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
package com.tencent.wstt.gt.ui.model;

public class TagQueryEntry {
	public String tag;
	public long tid;
	public int[] exkeys; // 用户自定义的区分标识
	
	public TagQueryEntry(String tag, long tid, int...exKey)
	{
		this.tag = tag;
		this.tid = tid;
		this.exkeys = exKey;
	}
	
	@Override
	public boolean equals(Object another)
	{
		if (this == another)
		{
			return true;
		}
		if (another instanceof TagQueryEntry)
		{
			TagQueryEntry anotherEntry = (TagQueryEntry) another;
			boolean tempResult = tid == anotherEntry.tid && tag.equals(anotherEntry.tag);
			if (! tempResult)
			{
				return false;
			}
			if (null == exkeys && null == anotherEntry.exkeys)
			{
				return tempResult;
			}
			if (null == exkeys && null != anotherEntry.exkeys
					|| null != exkeys && null == anotherEntry.exkeys)
			{
				return false;
			}
			// 剩下的情况是比较的双方都有exkeys
			if (exkeys.length != anotherEntry.exkeys.length)
			{
				return false;
			}
			for (int i = 0; i < exkeys.length; i++)
			{
				if (exkeys[i] != anotherEntry.exkeys[i])
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		int result = 17;
		result = 37 * result + tag.hashCode();
		result = 37 * result + (int) (tid ^ (tid >>> 32));
		if (exkeys != null && exkeys.length > 0)
		{
			for (int k : exkeys)
			{
				result = 37 * result + k;
			}
		}
		return result;
	}
}
