/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.stubanalysis;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.util.CTabFolderItemUtil;


/**
* @Description 对应打开APT插桩数据的一个展示页面 
* @date 2013年11月10日 下午6:04:17 
*
 */
public class APTStubAnalysisCTabItem extends CTabItem {
	

	private Group rootGroup = null;
	private CTabFolder showViewTF = null;
	private Button detailBtn = null;
	private Button topBtn = null;
	private Button treeBtn = null;
	
	private DetailCTabItem detailTabItem = null;
	private TopCTabItem topTabItem = null;
	private TreeCTabItem treeTabItem = null;
	
	private Object detailData = null;
	private Object topData = null;
	private Object treeData = null;
	

	public APTStubAnalysisCTabItem(CTabFolder parent, int style, String title) {
		super(parent, style);
		this.setText(title);
		
		rootGroup = new Group(parent, SWT.NONE);
		rootGroup.setLayout(new FormLayout());
		createAPTStubAnalysisCTablItemUI(rootGroup);	
		this.setControl(rootGroup);
	}
	
	
	public void updateData(Object detailData, Object topData, Object treeData)
	{
		this.detailData = detailData;
		this.topData = topData;
		this.treeData = treeData;
		detailTabItem.updateData(detailData);
		topTabItem.updateData(topData);
		treeTabItem.updateData(treeData);
	}
	/**
	 * 初始化页面中的所有UI
	 * @param parent
	 */
	private void createAPTStubAnalysisCTablItemUI(Composite parent)
	{
		int margin = 5;
		/**
		 * 三种展示视图的CTabFolder
		 */
		FormData showViewTabFolderFD = new FormData();
		showViewTabFolderFD.left = new FormAttachment(0, margin);
		showViewTabFolderFD.top = new FormAttachment(0, 25);
		showViewTabFolderFD.right = new FormAttachment(100, -margin);
		showViewTabFolderFD.bottom = new FormAttachment(100, -margin);
		
		showViewTF = new CTabFolder(parent, SWT.TOP|SWT.CLOSE|SWT.BORDER);
		showViewTF.setTabHeight(20);
		showViewTF.setLayoutData(showViewTabFolderFD);
		showViewTF.marginHeight = 10;
		showViewTF.marginWidth = 10;
		
		showViewTF.setSelectionBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		showViewTF.setSelectionForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		showViewTF.setUnselectedCloseVisible(true);
		showViewTF.pack();
		
		/**
		 * 详细视图按钮初始化
		 */
		FormData detailBtnFD = new FormData();
		detailBtnFD.left = new FormAttachment(0, margin);
		detailBtnFD.top = new FormAttachment(0, margin);
		detailBtnFD.height = 16;
		detailBtnFD.width = 16;
		detailBtn = new Button(parent, SWT.NONE);
		detailBtn.setToolTipText(StubAnalysisUtil.DETAIL_TAB_NAME);
		detailBtn.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/detail.png").createImage());
		detailBtn.setLayoutData(detailBtnFD);
		detailBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem []openedTabItems = showViewTF.getItems();
				CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, StubAnalysisUtil.DETAIL_TAB_NAME);
				if(openedTabItems == null || openedTabItems.length == 0 || targetTabItem == null)
				{
					detailTabItem = createDetailCTabItem(showViewTF);
					detailTabItem.updateData(detailData);
				}
				else
				{
					if(!showViewTF.getSelection().getText().equals(targetTabItem.getText()));
					{
						showViewTF.setSelection(targetTabItem);
					}	
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				CTabItem []openedTabItems = showViewTF.getItems();
				CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, StubAnalysisUtil.DETAIL_TAB_NAME);
				if(openedTabItems == null || openedTabItems.length == 0 || targetTabItem == null)
				{
					detailTabItem = createDetailCTabItem(showViewTF);
					detailTabItem.updateData(detailData);
				}
				else
				{
					if(!showViewTF.getSelection().getText().equals(targetTabItem.getText()));
					{
						showViewTF.setSelection(targetTabItem);
					}
				}
			}
		});

		
		/**
		 * 统计视图按钮初始化
		 */
		FormData topBtnFD = new FormData();
		topBtnFD.left = new FormAttachment(detailBtn, margin);
		topBtnFD.top = new FormAttachment(0, margin);
		topBtnFD.height = 16;
		topBtnFD.width = 16;
		topBtn = new Button(parent, SWT.NONE);
		topBtn.setToolTipText(StubAnalysisUtil.TOP_TAB_NAME);
		topBtn.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/stat.png").createImage());
		topBtn.setLayoutData(topBtnFD);
		topBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem[] openedTabItems = showViewTF.getItems();
				CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, StubAnalysisUtil.TOP_TAB_NAME);
				if (openedTabItems == null || openedTabItems.length == 0
						|| targetTabItem == null) {
					topTabItem = createTopCTabItem(showViewTF);
					topTabItem.updateData(topData);
				} else {
					if(!showViewTF.getSelection().getText().equals(targetTabItem.getText()));
					{
						showViewTF.setSelection(targetTabItem);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				CTabItem[] openedTabItems = showViewTF.getItems();
				CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, StubAnalysisUtil.TOP_TAB_NAME);
				if (openedTabItems == null || openedTabItems.length == 0
						|| targetTabItem == null) {
					topTabItem = createTopCTabItem(showViewTF);
					topTabItem.updateData(topData);
				} else {
					if(!showViewTF.getSelection().getText().equals(targetTabItem.getText()));
					{
						showViewTF.setSelection(targetTabItem);
					}
				}
			}
		});
		
		/**
		 * 树形视图按钮初始化
		 */
		FormData treeBtnFD = new FormData();
		treeBtnFD.left = new FormAttachment(topBtn, margin);
		treeBtnFD.top = new FormAttachment(0, margin);
		treeBtnFD.height = 16;
		treeBtnFD.width = 16;
		treeBtn = new Button(parent, SWT.NONE);
		treeBtn.setToolTipText(StubAnalysisUtil.TREE_TAB_NAME);
		treeBtn.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/tree.png").createImage());
		treeBtn.setLayoutData(treeBtnFD);
		treeBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem[] openedTabItems = showViewTF.getItems();
				CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, StubAnalysisUtil.TREE_TAB_NAME);
				if (openedTabItems == null || openedTabItems.length == 0
						|| targetTabItem == null) {
					treeTabItem = createTreeCTabItem(showViewTF);
					treeTabItem.updateData(treeData);
				} else {
					if(!showViewTF.getSelection().getText().equals(targetTabItem.getText()));
					{
						showViewTF.setSelection(targetTabItem);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				CTabItem[] openedTabItems = showViewTF.getItems();
				CTabItem targetTabItem = CTabFolderItemUtil.getTabItem(openedTabItems, StubAnalysisUtil.TREE_TAB_NAME);
				if (openedTabItems == null || openedTabItems.length == 0
						|| targetTabItem == null) {
					treeTabItem = createTreeCTabItem(showViewTF);
				} else {
					if(!showViewTF.getSelection().getText().equals(targetTabItem.getText()));
					{
						showViewTF.setSelection(targetTabItem);
					}
				}
			}
		});
		

		
		/**
		 * 打开三个视图
		 */
		detailTabItem = createDetailCTabItem(showViewTF);
		topTabItem = createTopCTabItem(showViewTF);
		treeTabItem = createTreeCTabItem(showViewTF);
	}
	

	/**
	 * 初始化详细视图
	 * @param tf
	 */
	private DetailCTabItem createDetailCTabItem(CTabFolder tf) {
		DetailCTabItem item = new DetailCTabItem(tf, SWT.NONE);
		tf.setSelection(item);
		return item;
	}
	
	
	/**
	 * 初始化统计视图
	 * @param tf
	 */
	private TopCTabItem createTopCTabItem(CTabFolder tf) {
		TopCTabItem item = new TopCTabItem(tf, SWT.NONE);
		tf.setSelection(item);
		return item;
	}
	
	
	/**
	 * 初始化树形视图
	 * @param tf
	 */
	private TreeCTabItem createTreeCTabItem(CTabFolder tf) {
		TreeCTabItem item = new TreeCTabItem(tf, SWT.NONE);
		tf.setSelection(item);
		return item;
	}
}
