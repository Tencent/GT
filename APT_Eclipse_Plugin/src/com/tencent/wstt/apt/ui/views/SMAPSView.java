/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.ui.views;



import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.smap.APTSMAPSCTabItem;
import com.tencent.wstt.apt.smap.SmapsUtil;
import com.tencent.wstt.apt.util.CTabFolderItemUtil;

public class SMAPSView extends ViewPart {
	public static final String ID = "com.tencent.wstt.apt.ui.views.SMAPSView";
	
	
	private Action openLogAction;	
	private CTabFolder rootTabFolder = null;

	public SMAPSView() {

	}

	@Override
	public void createPartControl(Composite parent) {
		createRootTabFolder(parent);
		createMenuAndToolBar();
	}

	@Override
	public void setFocus() {

	}

	/**
	 * 初始化各种Action
	 * 上下文菜单，工具栏按钮，下拉菜单初始化
	 */
	private void createMenuAndToolBar() {
		makeActions();
		createLocalToolBar();	
	}

	/**
	 * 初始化根CTablFolder
	 * @param parent
	 */
	private void createRootTabFolder(Composite parent) {
		rootTabFolder = new CTabFolder(parent, SWT.TOP|SWT.CLOSE|SWT.BORDER);
		rootTabFolder.setTabHeight(20);
		rootTabFolder.setLayout(new FillLayout());
		rootTabFolder.marginHeight = 10;
		rootTabFolder.marginWidth = 10;
		
		rootTabFolder.setSelectionBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		rootTabFolder.setSelectionForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		rootTabFolder.setUnselectedCloseVisible(true);
		rootTabFolder.pack();	
		
	}


	private void createLocalToolBar() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(openLogAction);	
	}

	/**
	 * 初始化各种Action
	 */
	private void makeActions() {	

		openLogAction = new Action() {
			public void run() {
				FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				dialog.setFilterPath(Constant.SMAPS_LOG_PATH_ON_PC);//设置初始路径
				String fileName = dialog.open();//返回的全路径(路径+文件名)
				if(fileName == null)
				{
					return;
				}

				Object smapsdata = SmapsUtil.getSmapsShowDataFromFile(fileName);
				if(smapsdata == null)
				{
					APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "解析smaps文件失败");
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "提示", "解析smaps文件失败");				
					return;
				}
				openSmapsDataInSMAPSView(smapsdata, fileName);
			}
		};
		
		openLogAction.setText("打开smaps文件");
		openLogAction.setToolTipText("打开smaps文件");
		openLogAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/open_pc.png"));	
		
	}
	
	
	/**
	 * 展示smapsData
	 * @param smapsData
	 * @param fileName
	 */
	public void openSmapsDataInSMAPSView(Object smapsData, String fileName)
	{		
		CTabItem[] openedTabItems = rootTabFolder.getItems();
		CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, fileName);
		if (openedTabItems == null || openedTabItems.length == 0 || targetTabItem == null) 
		{
			APTSMAPSCTabItem item = createAPTSMAPCTabItem(fileName);
			item.updateData(smapsData);
		} 
		else 
		{
			rootTabFolder.setSelection(targetTabItem);
			((APTSMAPSCTabItem)targetTabItem).updateData(smapsData);
		}
	}
	
	/**
	 * 新建一个展示smaps数据的CTabItem
	 * @param fileName
	 * @return
	 */
	private APTSMAPSCTabItem createAPTSMAPCTabItem(String fileName) {
		APTSMAPSCTabItem item = new APTSMAPSCTabItem(rootTabFolder, SWT.NONE, fileName);
		rootTabFolder.setSelection(item);
		return item;
	}
}
