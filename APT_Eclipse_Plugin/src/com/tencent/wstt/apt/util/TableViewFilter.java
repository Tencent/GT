/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;



import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.AbstractStatisticsDataInfo;

/**
* @Description 过滤统计表格中的数据 
* @date 2013年11月10日 下午6:22:46 
*
 */
public class TableViewFilter extends ViewerFilter {

	private boolean []pkgCheckeds;
	private boolean []dataCheckeds;
	private boolean isSoleData;//标明当前仅仅测试一种数据；如果是item中就仅仅包含pkgName
	
	private Map<String, Integer> pkgAbbreviationMaps = new HashMap<String, Integer>();
	private Map<String, Integer> dataTitleMaps = new HashMap<String, Integer>();
	
	public TableViewFilter(String[] pkgAbbreviations, String[] dataTitles)
	{
		for(int i = 0; i < pkgAbbreviations.length; i++)
		{
			pkgAbbreviationMaps.put(pkgAbbreviations[i], i);
		}
		
		for(int i = 0; i < dataTitles.length; i++)
		{
			dataTitleMaps.put(dataTitles[i], i);
		}
		this.pkgCheckeds = new boolean[pkgAbbreviations.length];
		this.dataCheckeds = new boolean[dataTitles.length];
		if(dataTitles.length == 1)
		{
			isSoleData = true;
		}
		else
		{
			isSoleData = false;
		}
		
	}
	
	public void update(boolean []pkgCheckeds, boolean []dataCheckeds)
	{
		if(pkgCheckeds == null || dataCheckeds == null)
		{
			APTConsoleFactory.getInstance().APTPrint(" update failed");
			return;
		}
		if(pkgCheckeds == null || pkgCheckeds.length != this.pkgCheckeds.length || dataCheckeds == null ||dataCheckeds.length != this.dataCheckeds.length)
		{
			APTConsoleFactory.getInstance().APTPrint("Checkbox update failed");
			return;
		}
		System.arraycopy(pkgCheckeds, 0, this.pkgCheckeds, 0, this.pkgCheckeds.length);
		System.arraycopy(dataCheckeds, 0, this.dataCheckeds, 0, this.dataCheckeds.length);
	}
	

	/**
	 * 该函数的调用频率并不是很频繁，表的结构不发生变化，就不会更新；
	 * 所以在更新表格数据时，要使用update的方式，可以减少下面函数的调用次数
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		//获取给item在列表中的index
		AbstractStatisticsDataInfo data = (AbstractStatisticsDataInfo)element;
		String curItemName = data.itemName;
		
		//这种情况下itemName中仅仅包含pkgAbbreviation
		if(isSoleData)
		{
			int pkgIndex = pkgAbbreviationMaps.get(curItemName);
			return pkgCheckeds[pkgIndex];
		}
		else
		{
			//TODO 这里会不会有进程的名字中带"_"感觉还是有可能的
			String temp[] = curItemName.split("_");
			if(temp == null || temp.length != 2)
			{
				return true;
			}
			String pkgAbbreviation = temp[0];
			String dataTitle = temp[1];
			
			int pkgIndex = pkgAbbreviationMaps.get(pkgAbbreviation);
			int dataIndex = dataTitleMaps.get(dataTitle);
			
			return pkgCheckeds[pkgIndex]&&dataCheckeds[dataIndex];
		}

	}

}
