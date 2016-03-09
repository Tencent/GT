/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* @Description PMAP数据对比工具类，获取差值 
* @date 2013年11月10日 下午6:21:13 
*
 */
public class MapSubUtil {
	
	public static Map<String, Integer> sub(Map<String, Integer> map1, Map<String, Integer> map2)
	{
		Map<String, Integer> result = new HashMap<String ,Integer>();
		
		/**
		 * 两个set去重合并
		 */
		Set<String> set1 = map1.keySet();
		Set<String> set2 = map2.keySet();
		Set<String> set = new HashSet<String>();
		set.addAll(set1);
		for(String key : set2)
		{
			if(!set.contains(key))
			{
				set.add(key);
			}
		}
		
		for(String key : set)
		{
			int val1 = 0;
			int val2 = 0;
			if(map1.containsKey(key))
			{
				val1 = map1.get(key);
			}
			if(map2.containsKey(key))
			{
				val2 = map2.get(key);
			}
			
			int val = val1 -val2;
			result.put(key, val);
		}
		
		return result;
	}

}
