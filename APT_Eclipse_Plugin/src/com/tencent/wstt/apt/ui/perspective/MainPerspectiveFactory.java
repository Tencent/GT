/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.ui.perspective;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.eclipse.ui.console.IConsoleConstants;


import com.tencent.wstt.apt.ui.views.CPUView;
import com.tencent.wstt.apt.ui.views.DevicesView;
import com.tencent.wstt.apt.ui.views.MemoryView;
import com.tencent.wstt.apt.ui.views.SettingView;

public class MainPerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "com.tencent.wstt.apt.ui.perspective.MainPerspectiveFactory";
	
	//并不是每次启动透视图的时候都会调用的
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		
		IFolderLayout aptLeft = layout.createFolder("aptLeft", IPageLayout.LEFT, 0.16f, editorArea);
		aptLeft.addView(SettingView.ID);
		

		layout.addView(DevicesView.ID, IPageLayout.BOTTOM, 0.3f, "aptLeft");
		layout.addView(CPUView.ID, IPageLayout.BOTTOM, 0.05f, editorArea);
		layout.addView(MemoryView.ID, IPageLayout.BOTTOM, 0.4f, CPUView.ID);

		
		IFolderLayout aptBottom = layout.createFolder("aptBottom", IPageLayout.BOTTOM,0.66f, MemoryView.ID);

		aptBottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		//禁止设置和设备视图关闭
		IViewLayout lay = layout.getViewLayout(SettingView.ID);
		lay.setCloseable(false);
		lay = layout.getViewLayout(DevicesView.ID);
		lay.setCloseable(false);
		lay = layout.getViewLayout(CPUView.ID);
		lay.setCloseable(false);
		lay = layout.getViewLayout(MemoryView.ID);
		lay.setCloseable(false);
			
	}

	
	
}
