/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.util.Arrays;
import java.util.Date;

import org.eclipse.jface.viewers.TableViewer;

import com.tencent.wstt.apt.data.JiffiesStatisticsDataInfo;

/**
* @Description jiffies统计表格观察者，用来更新jiffies统计表格数据
* @date 2013年11月10日 下午5:32:48 
*
 */
public class JiffiesTableObserver extends Observer {

	private TableViewer viewer;
	private JiffiesStatisticsDataInfo [] curData = null;
	private boolean []isFirsts;
	
	public JiffiesTableObserver(TableViewer viewer, String []pkgNames)
	{
		this.viewer = viewer;
		
		int pkgNumber = pkgNames.length;
		curData = new JiffiesStatisticsDataInfo[pkgNumber];
		for(int i = 0; i < pkgNumber; i++)
		{
			curData[i] = new JiffiesStatisticsDataInfo();
			curData[i].itemName = pkgNames[i];
			Arrays.fill(curData[i].contents, 0);
		}
		
		isFirsts = new boolean[pkgNumber];
		Arrays.fill(isFirsts, true);
		
		if(viewer!=null)
		{
			this.viewer.setInput(null);
		}	
	}
	
	@Override
	public void update(Date time, Number[] datas) {		
		for(int i = 0; i < datas.length; i++)
		{
			long curValue = datas[i].longValue();
			if(isFirsts[i] && curValue != 0)
			{
				curData[i].contents[JiffiesStatisticsDataInfo.INITVALUE_INDEX] = curValue;
				isFirsts[i] = false;
			}
			
			curData[i].contents[JiffiesStatisticsDataInfo.CURVALUE_INDEX] = curValue;
			curData[i].contents[JiffiesStatisticsDataInfo.DVALUE_INDEX] = curValue-curData[i].contents[JiffiesStatisticsDataInfo.INITVALUE_INDEX];
		}
		if(!viewer.getTable().isDisposed())
		{
			viewer.getTable().getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if(!viewer.getTable().isDisposed())
					{
						viewer.setInput(curData);
						viewer.refresh();
					}
				}
			});	
		}
	}

}
