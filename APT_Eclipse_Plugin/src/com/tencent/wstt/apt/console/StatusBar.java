/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.console;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;


/**
* @Description 控制APT状态栏输出 
* @date 2013年11月10日 下午6:01:24 
*
 */
@SuppressWarnings("restriction")
public class StatusBar {
	private static StatusBar instance = null;
	IStatusLineManager lineManager;
	StatusLineContributionItem statusItem;
	private StatusBar()
	{
		
	}
	
	public static StatusBar getInstance()
	{
		if(instance == null)
		{
			instance = new StatusBar();
		}
		return instance;
	}
	
	@SuppressWarnings("restriction")	
	public void init()
	{
		WorkbenchWindow curworkbenchWindow = (WorkbenchWindow)PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		curworkbenchWindow.setStatusLineVisible(true);
		
		lineManager = curworkbenchWindow.getStatusLineManager();
		statusItem = new StatusLineContributionItem("APT", 100);
		lineManager.add(statusItem);
	}
	public void showInfo(final String info)
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if(statusItem!=null)
				{
					statusItem.setText(info);
				}		
			}
		});

	}
}
