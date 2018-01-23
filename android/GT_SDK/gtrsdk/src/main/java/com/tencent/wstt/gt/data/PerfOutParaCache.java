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

import com.tencent.wstt.gt.OutPara;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 出参缓存，只用于在客户端连接控制台这时间段中，连上后即调用端直接把出参数据放到异步队列里
 */
public class PerfOutParaCache {

	private Map<String, OutPara> outParaMap;

	public PerfOutParaCache() {
		outParaMap = new LinkedHashMap<String, OutPara>();
	}

	/**
	 * 注意，目前缓存的做法，注册和setOutPara将会在一起，注册将会成为第一个有默认值历史
	 * @param para
	 */
	public void register(OutPara para) {
		if (null != para && null != para.getKey()) {
			outParaMap.put(para.getKey(), para);
		}
	}
	
	public void put(String key, String value, Object ...extras) {
		OutPara outPara = outParaMap.get(key);
		if (null == outPara) {
			outPara = new OutPara();
		}
		outPara.setKey(key);
		if (outPara.getValue() != null)  {
			// 如果有上一次的数据就移到历史
			outPara.addHistory(outPara.getValue());
		}
		outPara.setValue(value);

		if (extras != null) {
		    int idx = 0;

		    if (idx < extras.length && extras[idx] instanceof Boolean) {
		        outPara.setGlobal((Boolean)extras[idx++]);
            }

            if (idx < extras.length && extras[idx] instanceof String) {
		        outPara.setUnit((String)extras[idx]);
            }
        }
	}

	public Collection<OutPara> getAll() {
		return outParaMap.values();
	}
	
	public OutPara get(String key) {
		return outParaMap.get(key);
	}

	public void clear() {
		outParaMap.clear();
	}
}
