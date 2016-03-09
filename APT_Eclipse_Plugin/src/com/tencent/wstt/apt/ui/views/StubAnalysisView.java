/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.ui.views;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.stubanalysis.APTStubAnalysisCTabItem;
import com.tencent.wstt.apt.stubanalysis.StubLogParseUtil;
import com.tencent.wstt.apt.util.CTabFolderItemUtil;
import com.tencent.wstt.apt.util.FileOperatorUtil;


/**
* @Description 显示APT插桩数据的视图 
* @date 2013年11月10日 下午6:16:17 
*
 */
public class StubAnalysisView extends ViewPart {
	public static final String ID = "com.tencent.wstt.apt.ui.views.StubAnalysisView";
	
	private Action openLogOnPCAction;
	private Action openLogOnPhoneAction;
	private CTabFolder rootTabFolder = null;
	
	
	public StubAnalysisView() {		
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
		//TODO 是否需要进行修改
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
		manager.add(openLogOnPCAction);	
		manager.add(openLogOnPhoneAction);
	}

	/**
	 * 初始化各种Action
	 */
	private void makeActions() {	

		openLogOnPCAction = new Action() {
			public void run() {
				FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				dialog.setFilterPath(Constant.LOG_FOLDER_ON_PC);//设置初始路径
				String fileName = dialog.open();//返回的全路径(路径+文件名)
				if(fileName == null)
				{
					return;
				}
				
				openFileInAPT(fileName);
			}
		};
		
		openLogOnPCAction.setText("打开APT插桩文件");
		openLogOnPCAction.setToolTipText("打开APT插桩文件");
		openLogOnPCAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/open_pc.png"));	
		
		openLogOnPhoneAction = new Action() {
			public void run() {
				List<String> fileOnPhoneList = FileOperatorUtil.getLogFilesOnPhone();
				//APTConsoleFactory.getInstance().APTPrint("size=" + fileOnPhoneList.size());
				ListDialog dialog = new ListDialog(Display.getCurrent().getActiveShell());
				dialog.setContentProvider(new ArrayContentProvider());
				dialog.setLabelProvider(new LabelProvider());
				dialog.setInput(fileOnPhoneList);
				dialog.setHelpAvailable(false);
				dialog.setTitle("请选择一个手机上的文件");
				
				dialog.open();// 返回值为按钮
				
				Object[] selectedFiles = dialog.getResult();
				
				if (selectedFiles != null && selectedFiles.length > 0)
				{
					String fileName = (String) selectedFiles[0];
					FileOperatorUtil.pullLogFileWithNameFromSDCard(
							fileName, Constant.LOG_FOLDER_ON_PC);
					// 解析刚刚拉取的文件作为数据源
					// TODO 获取耗时数据
					String fileNameWithPath = Constant.LOG_FOLDER_ON_PC + File.separator + fileName;
					
					openFileInAPT(fileNameWithPath);
				}
			}
		};
		
		openLogOnPhoneAction.setText("Open Log On Phone..");
		openLogOnPhoneAction.setToolTipText("打开手机端指定的内存日志文件");
		openLogOnPhoneAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/open_phone.png"));
		
		
	}
	
	/**
	 * 新建一个APTlog文件对应的CTablItem
	 * 每一个APTlog文件CTablItem包括一个工具栏和一个CTabFolder组成
	 * @param fileName
	 */
	private APTStubAnalysisCTabItem createAPTStubAnalysisCTabItem(String fileName) {
		APTStubAnalysisCTabItem item = new APTStubAnalysisCTabItem(rootTabFolder, SWT.NONE, fileName);
		rootTabFolder.setSelection(item);
		return item;
	}
	
	private void openFileInAPT(String fileName)
	{
		Object[][] res = StubLogParseUtil.getData(fileName);
		if(res == null)
		{
			APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "解析插桩文件失败" + fileName);
			return;
		}
		int len1 = res[0].length;
		int len2 = res[1].length;
		Object[] detailData = new Object[len1 + len2];//detail数据
		Object topData = StubLogParseUtil.statisticsData(res[0]);
		Object treeData = StubLogParseUtil.parseSourceDataAsTree(res[0]);
		
		System.arraycopy(res[0], 0, detailData, 0, len1);
		System.arraycopy(res[1], 0, detailData, len1, len2);
		
		
		CTabItem[] openedTabItems = rootTabFolder.getItems();
		CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, fileName);
		if (openedTabItems == null || openedTabItems.length == 0 || targetTabItem == null) 
		{
			APTStubAnalysisCTabItem item = createAPTStubAnalysisCTabItem(fileName);
			item.updateData(detailData, topData, treeData);
		} 
		else 
		{
			rootTabFolder.setSelection(targetTabItem);
			((APTStubAnalysisCTabItem)targetTabItem).updateData(detailData, topData, treeData);
		}
	}
}
