/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;

import org.eclipse.ui.PlatformUI;

import com.tencent.wstt.apt.data.TestSence;
import com.tencent.wstt.apt.ui.views.CPUView;
import com.tencent.wstt.apt.ui.views.DevicesView;
import com.tencent.wstt.apt.ui.views.MemoryView;
import com.tencent.wstt.apt.ui.views.SettingView;

public class GetCurCheckedStateUtil {

	/**
	* @Description 按照设置和设备视图中的更改更新CPU和内存视图中的曲线 
	* @param    
	* @return void 
	* @throws
	 */
	public static void update()
	{
		SettingView settingViewPart  = (SettingView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SettingView.ID);
		DevicesView deviceViewPart  = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);
		CPUView cpuViewPart  = (CPUView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CPUView.ID);
		MemoryView memViewPart  = (MemoryView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MemoryView.ID);
	
		
		if(deviceViewPart==null || settingViewPart==null)
		{
			return;
		}
		boolean[] pkgCheckeds = deviceViewPart.getPkgChecked();

		if(cpuViewPart != null)
		{
			for(int i = 0; i < pkgCheckeds.length; i++)
			{
				cpuViewPart.cpuRealTimeChart.setTimeSeriesEnable(i, 0, pkgCheckeds[i]);
			}

			//当启动APT后，直接打开log，此时tableviewfilter为空
			if(cpuViewPart.cpuTableFilter != null)
			{
				((TableViewFilter)cpuViewPart.cpuTableFilter).update(pkgCheckeds, new boolean[]{true});
				cpuViewPart.cpuViewer.refresh();
			}
			
			if(TestSence.getInstance().isTestJiffies)
			{
				if(cpuViewPart.jiffiesTableFilter != null)
				{
					((TableViewFilter)cpuViewPart.jiffiesTableFilter).update(pkgCheckeds, new boolean[]{true});
					cpuViewPart.jiffiesViewer.refresh();
				}

			}
		}
		

		if(memViewPart != null)
		{
			boolean[] memStateCheckeds = settingViewPart.getMemChecked();
			//更新曲线
			for(int i = 0; i < pkgCheckeds.length; i++)
			{
				for(int j = 0; j < memStateCheckeds.length; j++)
				{	
					memViewPart.memRealTimeChart.setTimeSeriesEnable(i, j, pkgCheckeds[i]&&memStateCheckeds[j]);
				}
			}
			//更新统计表格
			if(memViewPart.tableFilter != null)
			{
				((TableViewFilter)memViewPart.tableFilter).update(pkgCheckeds, memStateCheckeds);
				memViewPart.viewer.refresh();
			}
		}
	}
	
		
}
