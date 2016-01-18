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
package com.tencent.wstt.gt.data;

import com.tencent.wstt.gt.AidlTask;
import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.PerfDigitalEntry;
import com.tencent.wstt.gt.data.local.LocalEndTimePerfEntry;
import com.tencent.wstt.gt.data.local.LocalNumberDataPerfEntry;
import com.tencent.wstt.gt.data.local.LocalStartTimePerfEntry;

/**
 * 本地耗时统计数据的缓存
 */
public class LocalTimePerfCache extends PerfDataCache {

	@Override
	public AidlTask profiler(LocalNumberDataPerfEntry... endDataEntry) {
		if (null == endDataEntry || endDataEntry.length == 0) {
			return null;
		}
		LocalNumberDataPerfEntry end = endDataEntry[0];
		if (!(end instanceof LocalEndTimePerfEntry))
		{
			return null;
		}
		LocalNumberDataPerfEntry start = get(end.getQueryEntry());
		if (!(start instanceof LocalStartTimePerfEntry))
		{
			return null;
		}
		
		PerfDigitalEntry result = new PerfDigitalEntry();
		result.setFunctionId(Functions.PERF_REDUCE_TIME);
		result.setQueryEntry(end.getQueryEntry());
		result.setLogTime(end.getLogTime());
		result.setData(end.getData() - start.getData());

		return result;
	}

}
