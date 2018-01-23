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

import com.tencent.wstt.gt.InPara;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 入参缓存，只用于在客户端连接控制台这时间段中，连上后即调用端直接把出参数据放到异步队列里
 */
public class PerfInParaCache {

	private Map<String, InPara> inParaMap;

	public PerfInParaCache() {
		inParaMap = new LinkedHashMap<String, InPara>();
	}

	/**
	 * 注册时候的缓存
	 * @param para
	 */
	public void register(InPara para) {
		if (null != para && null != para.getKey()) {
			inParaMap.put(para.getKey(), para);
		}
	}
	
	public void put(String key, String newValue) {
		InPara outPara = inParaMap.get(key);
		if (null == outPara) {
			return;
		}
		outPara.setKey(key);
		List<String> values = outPara.getValues();
		if (values != null) {
			values.add(0, newValue);
		}
	}

	public Collection<InPara> getAll() {
		Collection<InPara> result = inParaMap.values();
		return result;
	}
	
	public InPara get(String key) {
		return inParaMap.get(key);
	}

	public void clear() {
		inParaMap.clear();
	}
}
