/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.smap;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;


import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.ui.views.SMAPSView;
import com.tencent.wstt.apt.util.CTabFolderItemUtil;

public class APTSMAPSCTabItem extends CTabItem {

	private Object sourceData = null;
	
	private TreeViewer viewer = null;
	private Action expandAction = null;
	private Action collapseAction = null;
	
	private static final String[] COLUMN_NAME = {"Item", "Pss(kB)", "SharedDirty(kB)", "PrivateDirty(kB)"};
	private static final int[]COLUMN_WIDTH = {600, 150, 150, 150};
	
	public APTSMAPSCTabItem(CTabFolder parent, int style, String title) {
		super(parent, style);
		this.setText(title);
		
		Group rootGroup = new Group(parent, SWT.NONE);
		rootGroup.setLayout(new FormLayout());
				
		createAPTSMAPSCTablItemUI(rootGroup);
		createMenuAndToolBar();
		
		this.setControl(rootGroup);
	}
	
	private void createMenuAndToolBar() {
		makeActions();
		createContextMenu();
		
	}

	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				
				APTSMAPSCTabItem.this.fillContextMenu(manager);
			}
		});
		
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	protected void fillContextMenu(IMenuManager manager) {
		manager.add(expandAction);
		manager.add(collapseAction);
		
	}

	private void makeActions() {
		expandAction = new Action() {

			@Override
			public void run() {
				super.run();
				viewer.expandAll();;
			}
			
		};
		expandAction.setText("展开");
		expandAction.setToolTipText("展开");

		
		collapseAction = new Action(){
			@Override
			public void run() {
				super.run();
				viewer.collapseAll();
			}
		};
		collapseAction.setText("折叠");
		collapseAction.setToolTipText("折叠");
		
	}

	/**
	 * 更新viewer数据源
	 * @param sourceData
	 */
	public void updateData(Object sourceData)
	{
		if(viewer != null && sourceData != null)
		{
			this.sourceData = sourceData;
			viewer.setInput(sourceData);
			viewer.refresh();
		}
	}
	
	
	/**
	 * 获取当前viewer的数据源
	 * @return
	 */
	public Object getSourceData()
	{
		return this.sourceData;
	}
	
	/**
	 * 初始化UI
	 * @param parent
	 */
	private void createAPTSMAPSCTablItemUI(Composite parent)
	{
		int margin = 5;

//		FormData expandBtnFD = new FormData();
//		expandBtnFD.left = new FormAttachment(0, margin);
//		expandBtnFD.top = new FormAttachment(0, margin);
//		expandBtnFD.height = 16;
//		expandBtnFD.width = 16;
//		
//		Button expandBtn = new Button(parent, SWT.NONE);
//		expandBtn.setToolTipText("展开/折叠");
//		expandBtn.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/add.png").createImage());
//		expandBtn.setLayoutData(expandBtnFD);
//		/**
//		 * TODO 折叠按钮单击响应
//		 */
		
		FormData compareBtnFD = new FormData();
		compareBtnFD.left = new FormAttachment(0, margin);
		compareBtnFD.top = new FormAttachment(0, margin);
		compareBtnFD.height = 16;
		compareBtnFD.width = 16;
		
		Button compareBtn = new Button(parent, SWT.NONE);
		compareBtn.setToolTipText("对比其他smaps");
		compareBtn.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/stat.png").createImage());
		compareBtn.setLayoutData(compareBtnFD);
		compareBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				//首先获取当前已经打开的smaps文件
				CTabItem[] openedTabItems = APTSMAPSCTabItem.this.getParent().getItems();
				if(openedTabItems != null && openedTabItems.length > 1)
				{
					ArrayList<String> files = new ArrayList<String>();
					for(int i = 0; i < openedTabItems.length; i++)
					{
						if(!APTSMAPSCTabItem.this.getText().equals(openedTabItems[i].getText()))
						{
							files.add(openedTabItems[i].getText());
						}					
					}
					
					ListDialog dialog = new ListDialog(Display.getCurrent().getActiveShell());
					dialog.setContentProvider(new ArrayContentProvider());
					dialog.setLabelProvider(new LabelProvider());
					dialog.setInput(files);
					dialog.setHelpAvailable(false);
					dialog.setTitle("选择要对比的smaps文件");
					
					dialog.open();// 返回值为按钮
					
					Object[] selectedFiles = dialog.getResult();
					
					if (selectedFiles != null && selectedFiles.length > 0)
					{
						String fileName = (String) selectedFiles[0];
						Object sourceData = ((APTSMAPSCTabItem)CTabFolderItemUtil.getTabItem(openedTabItems, fileName)).getSourceData();
						
						Object diffSouceData = SmapsUtil.getDiff((SMAPSSourceDataItem)(APTSMAPSCTabItem.this.getSourceData()), (SMAPSSourceDataItem)sourceData);
						SMAPSView smapsViewPart  = (SMAPSView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SMAPSView.ID);
						smapsViewPart.openSmapsDataInSMAPSView(diffSouceData, "diff");
					}
					
				}
				else
				{
			        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "提示", "No other smaps file opened");
			 
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		
		FormData treeViewerFD = new FormData();
		treeViewerFD.left = new FormAttachment(0, margin);
		treeViewerFD.top = new FormAttachment(compareBtn, margin);
		treeViewerFD.right = new FormAttachment(100, -margin);
		treeViewerFD.bottom = new FormAttachment(100, -margin);
		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		for ( int i =0; i<COLUMN_NAME.length;i++){
			new TreeColumn(viewer.getTree(), SWT.LEFT).setText(COLUMN_NAME[i]);
			viewer.getTree().getColumn(i).setWidth(COLUMN_WIDTH[i]);

		}
		viewer.getControl().setLayoutData(treeViewerFD);
		//设置表头和表格线可见
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
			
	}
	
	
	class ViewContentProvider implements ITreeContentProvider {
		//Methods inherited from interface org.eclipse.jface.viewers.IContentProvider
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		// Returns the elements to display in the viewer when its input is set to the given element.
		// These elements can be presented as rows in table, items in a list,etc. The result is not
		// modified by the viewer
		// NOTE: The returned array must not contain the given inputElement, since this leads to recursion issues
		public Object[] getElements(Object parent) {
			SMAPSSourceDataItem root = (SMAPSSourceDataItem) parent;
			//这里完全不需要区分是否是多线程的问题；
			// TODO 区分当前的显示项是线程信息（TID）还是方法信息（TAG）
			if(root.hasChildren())
			{
				return root.getChildren().toArray();
			}
			 //下面的两种方式应该都可以
			 //return new Object[0];
			return null;
		}

		@Override
		// The difference between this method and IStructuredContentProvider.getElements 
		// is that getElements is called to obtain the tree viewer's root elements, 
		// whereas getChildren is used to obtain the children of a given parent element in the tree (including a root
		public Object[] getChildren(Object parentElement) {

			if (parentElement instanceof SMAPSSourceDataItem) {
				return ((SMAPSSourceDataItem) parentElement).getChildren().toArray();
			}

			return null;
		}

		@Override
		// Returns the parent for the given element, or null indicating that the parent can't
		// be computed. In this case the tree-structured viewer can't expand a given node correctly
		// if requested
		public Object getParent(Object element) {

			if (element instanceof SMAPSSourceDataItem) {
				return ((SMAPSSourceDataItem) element).getParent();
			}

			return null;
		}

		@Override
		// Intended as an optimization for when the viewer does not need the actual children.
		// Clients may be able to implement this more efficiently than getChildren.
		public boolean hasChildren(Object element) {
			// TODO 这里有没有进行判断
			if (element instanceof SMAPSSourceDataItem) {
				return ((SMAPSSourceDataItem) element).hasChildren();
			}

			return false;
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return ((SMAPSSourceDataItem)obj).contents[index];

		}
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

}
