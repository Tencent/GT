/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.tencent.wstt.apt.adb.DDMSUtil;
import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.PkgInfo;
import com.tencent.wstt.apt.ui.views.DevicesView;

/**
* @Description 触发GC，并获取一次内存数据 
* @date 2013-11-14 下午11:57:15 
*
 */
public class GCAction extends Action {
	public GCAction()
	{
	}
	
	@Override
	public void run() {

		DevicesView deviceViewPart  = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);
		TableItem[] selectData = deviceViewPart.sourcePkgTableViewer.getTable().getSelection();
		if(selectData == null || selectData.length == 0)
		{
			APTConsoleFactory.getInstance().APTPrint("进程列表为空");
			return;
		}
		
		PkgInfo itemData = (PkgInfo)selectData[0].getData();
		final String pkgName = itemData.contents[PkgInfo.NAME_INDEX];	
		DDMSUtil.gc(pkgName);
	}

}
