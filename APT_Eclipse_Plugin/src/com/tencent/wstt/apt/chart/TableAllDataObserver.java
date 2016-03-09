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

import com.tencent.wstt.apt.data.GeneralStatisticsDataInfo;

/**
* @Description CPU内存统计表格观察者 
* @date 2013年11月10日 下午5:33:23 
*
 */
public class TableAllDataObserver extends Observer {

	private TableViewer viewer;
	private long testCount = 0;
	private GeneralStatisticsDataInfo[] statisticsDatas = null;
	
	private int pkgNumber = 0;
	private int dataNumber = 0;
	
	public TableAllDataObserver(TableViewer viewer, String[] pkgNames, String[] datas)
	{
		this.viewer = viewer;
		this.pkgNumber = pkgNames.length;
		this.dataNumber = datas.length;
		
		int allDataNumber = pkgNumber*dataNumber;//总数据条数
		
		statisticsDatas = new GeneralStatisticsDataInfo[allDataNumber];
		
		if(datas.length == 1)
		{
			for(int i = 0; i < pkgNumber; i++)
			{
				statisticsDatas[i] = new GeneralStatisticsDataInfo();
				statisticsDatas[i].itemName = pkgNames[i];
				Arrays.fill(statisticsDatas[i].contents, 0);
			}	
		}
		else
		{
			for(int i = 0; i < pkgNumber; i++)
			{
				for(int j = 0; j < dataNumber; j++)
				{
					statisticsDatas[i*dataNumber+j] = new GeneralStatisticsDataInfo();
					statisticsDatas[i*dataNumber+j].itemName = pkgNames[i] + "_" + datas[j];
				}
			}	
		}

		//清空之前的数据;不能用下面的这种方式，主要是因为用到了过滤功能，过滤以为这数据结构发生了变化
		//this.viewer.setInput(null);
		//this.viewer.add(curDatas);
		this.viewer.setInput(null);
		
	}
	
	@Override
	public void update(Date time, Number[] datas) {
		if(testCount == 0)
		{
			for(int i = 0; i < datas.length; i++)
			{
				//对于第一个值，除了DValue，其他值都等于第一个值
				for(int j = 0; j < GeneralStatisticsDataInfo.VALUE_NUMBER; j++)
				{
					statisticsDatas[i].contents[j] = datas[i].longValue();
				}
				statisticsDatas[i].contents[GeneralStatisticsDataInfo.DVALUE_INDEX] = 0;
			}
		}
		else
		{
			for(int i = 0; i < datas.length; i++)
			{			
				long curValue = datas[i].longValue();
				statisticsDatas[i].contents[GeneralStatisticsDataInfo.CURVALUE_INDEX] = curValue;
				statisticsDatas[i].contents[GeneralStatisticsDataInfo.DVALUE_INDEX] = curValue - statisticsDatas[i].contents[GeneralStatisticsDataInfo.FIRST_INDEX];
				statisticsDatas[i].contents[GeneralStatisticsDataInfo.SUM_INDEX] = statisticsDatas[i].contents[GeneralStatisticsDataInfo.SUM_INDEX] + curValue;
				statisticsDatas[i].contents[GeneralStatisticsDataInfo.AVGVALUE_INDEX] = statisticsDatas[i].contents[GeneralStatisticsDataInfo.SUM_INDEX]/(testCount+1);
				statisticsDatas[i].contents[GeneralStatisticsDataInfo.MAXVALUE_INDEX] = Math.max(statisticsDatas[i].contents[GeneralStatisticsDataInfo.MAXVALUE_INDEX], curValue);
				statisticsDatas[i].contents[GeneralStatisticsDataInfo.MINVALUE_INDEX] = Math.min(statisticsDatas[i].contents[GeneralStatisticsDataInfo.MINVALUE_INDEX], curValue);
			}
			
		}
		
		testCount++;
		
		if(!viewer.getTable().isDisposed())
		{
			viewer.getTable().getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if(!viewer.getTable().isDisposed())
					{
//						viewer.update(curDatas, null);
						viewer.setInput(statisticsDatas);
						viewer.refresh();
					}
					
				}
			});	
		}
		
	}
}

