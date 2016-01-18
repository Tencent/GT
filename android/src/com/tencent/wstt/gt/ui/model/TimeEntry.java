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

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.utils.DoubleUtils;
import com.tencent.wstt.gt.utils.GTUtils;

public class TimeEntry {
	public long time; // 本条时间差值记录生成的时间，毫妙级，是由自1970年以来的毫秒数转换成20131030190132123格式
	public long reduce; // 本条时间差值，微秒级
	int funcId = Functions.PERF_DIGITAL_NORMAL; // 该条数据的类型，默认是普通数字型

	public TimeEntry(long time, long reduce, int funcId)
	{
		this.time = time;
		this.reduce = reduce;
		this.funcId = funcId;
	}
	
	@Override
	public String toString()
	{
		String dateString = GTUtils.getSaveTime(time);

		StringBuffer sb = new StringBuffer();
		sb.append(dateString);
		sb.append(",");
		
		if (funcId == Functions.PERF_DIGITAL_CPU)
		{
			sb.append(DoubleUtils.div(reduce, 100, 2));
			sb.append("%");
		}
		else
		{
			sb.append(reduce);
		}
		return sb.toString();
	}
}
