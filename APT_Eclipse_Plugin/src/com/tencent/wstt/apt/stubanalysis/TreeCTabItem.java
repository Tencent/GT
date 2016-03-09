/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.stubanalysis;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.stubanalysis.data.SourceDataItem;

/**
* @Description 展示APT插桩数据的树形页面
* @date 2013年11月10日 下午6:04:46 
*
 */
public class TreeCTabItem extends CTabItem {
	private static final String TAB_NAME = StubAnalysisUtil.TREE_TAB_NAME;
	private TreeViewer viewer = null;
	
	// 完整表格的列名称
	public static final String[] COLUMN_NAME = {"被测项", "增量", "其他"};
	public static final int[]COLUMN_WIDTH = {600,75,75};

	public TreeCTabItem(CTabFolder parent, int style) {
		super(parent, style);
		this.setText(TAB_NAME);
		this.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/tree.png").createImage());

		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		for ( int i =0; i<COLUMN_NAME.length;i++){
			new TreeColumn(viewer.getTree(), SWT.LEFT).setText(COLUMN_NAME[i]);
			viewer.getTree().getColumn(i).setWidth(COLUMN_WIDTH[i]);
			//viewer.getTree().getColumn(i).setAlignment(alignment);

		}
		//设置表头和表格线可见
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		

		//分别为表头的每一列注册事件
		for (int i = 0; i < COLUMN_NAME.length; i++)
		{
			final int j = i;
			TreeColumn column = viewer.getTree().getColumn(j);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// 单击时，重新设置排序对象属性
					((TableSorter) viewer.getSorter()).doSort(j);
					// 刷新表格数据
					viewer.refresh();
				}
			});
		}

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
			
		this.setControl(viewer.getControl());
	}
	
	public void updateData(Object data)
	{
		if(viewer != null && data != null)
		{
			viewer.setInput(data);
			viewer.refresh();
		}
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
			SourceDataItem root = (SourceDataItem) parent;

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

			if (parentElement instanceof SourceDataItem) {
				return ((SourceDataItem) parentElement).getChildren().toArray();
			}

			return null;
		}

		@Override
		// Returns the parent for the given element, or null indicating that the parent can't
		// be computed. In this case the tree-structured viewer can't expand a given node correctly
		// if requested
		public Object getParent(Object element) {

			if (element instanceof SourceDataItem) {
				return ((SourceDataItem) element).getParent();
			}

			return null;
		}

		@Override
		// Intended as an optimization for when the viewer does not need the actual children.
		// Clients may be able to implement this more efficiently than getChildren.
		public boolean hasChildren(Object element) {
			// TODO 这里有没有进行判断
			if (element instanceof SourceDataItem) {
				return ((SourceDataItem) element).hasChildren();
			}

			return false;
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			switch(index)
			{
			case 0:
				return ((SourceDataItem)obj).contents[SourceDataItem.TAG];
			case 1:
				return ((SourceDataItem)obj).contents[SourceDataItem.VALUE];
			default:
				return "暂不支持";
			}
		}
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	
	
	public class TableSorter extends ViewerSorter {
		private static final int ASCENDING = 0;
		private static final int DESCENDING = 1;

		private int order;//判断是升序还是降序
		private int column;//判断排序的列

		public void doSort(int column) {
		   // 如果是同一列，改变排列的顺序
		   if (column == this.column) {
		    order = 1 - order;
		   } else {// 如果是另一列，则默认为升序排列
		    this.column = column;
		    order = ASCENDING;
		   }
		}
		//覆盖父类的方法，返回比较结果有-1,0,1三种情况
		public int compare(Viewer viewer, Object e1, Object e2) {
		   int result = 0;
		   
		   TreeItem item1 = (TreeItem)e1;
		   TreeItem item2 = (TreeItem)e1;
		   
		   
		   if(item1 == null || item2 == null)
		   {
			   // TODO: 这种情况最好显示信息
			   return 0;
		   }
		   /*
		    * 默认是升序
		    * 对于字符串列，按字符串排序，数字列按数字排序
		    */	
		   if(isNumeric(item1.getText(column)) && isNumeric(item2.getText(column)))
		   {
			   result = (int)(Long.parseLong(item2.getText(column)) - Long.parseLong(item1.getText(column)));
		   }
		   else
		   {
			   result = (item1.getText(column)).compareTo(item2.getText(column));
		   }

		   //如果此时为降序
			if (order == DESCENDING) {
				result = -result;
			}
		   return result;
		}
		
		/**
		 * 判断字符串是否为数字
		 * @param str
		 * @return
		 */
		private boolean isNumeric(String str)
		{ 
			Pattern pattern = Pattern.compile("[0-9]*"); 
			return pattern.matcher(str).matches(); 
		}
		
	}

}
