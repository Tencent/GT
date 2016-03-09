/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.stubanalysis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description 描述APT插桩文件中的一条数据 
* @date 2013年11月10日 下午6:07:40 
*
 */
public class SourceDataItem {
	public long index;//原始文件中出现的序号
	public String[] contents;
	
	public static final int TIME = 0;
	public static final int PID = 1;
	public static final int TID = 2;
	public static final int SID = 3;
	public static final int SAMPLING_POSITION = 4;
	public static final int TAG = 5;
	public static final int VALUE = 6;
	
	public static final int COLUMUNS_NUM = 7;
	
	public static final String SAMPLING_POSITION_START = "0";
	public static final String SAMPLING_POSITION_END = "-1";
	public static final String SAMPLING_POSITION_DIFFERENCE = "-2";
	public static final String NO_SUPPORT_VALUE = "-";
	public static final String SPLIT = "|";
	public static final String EXPORT_SPLIT = ",";
	
	public static final Map<String, String> samplingPositionMap = new HashMap<String, String>()
	{
		{
			put("0", "开始");
			put("-1", "结束");
			put("-2", "差值");
		}
	};
	
	// 树型结构属性
	private SourceDataItem parent;
	public List<SourceDataItem> children; 
	
	/**
	 * 树形结构方法
	 * @return
	 */
	public SourceDataItem getParent() {
		return parent;
	}
	public void setParent(SourceDataItem parent) {
		this.parent = parent;
	}
	public List<SourceDataItem> getChildren() {
		return children;
	}
	public void addChild(SourceDataItem child) {
		if (null == children)
		{
			children = new ArrayList<SourceDataItem>();
		}
		
		children.add(child);
	}
	
	public boolean hasChildren()
	{
		return children != null && !children.isEmpty();
	}

}
