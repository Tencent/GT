/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.stubanalysis;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.stubanalysis.data.TopViewDataItem;

/**
* @Description 展示APT插桩数据的统计页面
* @date 2013年11月10日 下午6:04:46 
*
 */
public class TopCTabItem extends CTabItem {
	private static final String TAB_NAME = StubAnalysisUtil.TOP_TAB_NAME;
	private TableViewer viewer = null;
	
	private static final String[] COLUMN_NAME = {"被测项","计数", "最大", "平均", "总量"};
	private static final int[]COLUMN_WIDTH = {600,50,75,75,75};

	public TopCTabItem(CTabFolder parent, int style) {
		super(parent, style);
		this.setText(TAB_NAME);
		this.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/stat.png").createImage());

		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		for ( int i =0; i<COLUMN_NAME.length;i++){
		    new TableColumn(viewer.getTable(), SWT.LEFT).setText(COLUMN_NAME[i]);
		    viewer.getTable().getColumn(i).setWidth(COLUMN_WIDTH[i]);
		}
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible( true );
		
		//TODO 这里是否存在优化空间
		viewer.setSorter( new TableSorter());

		//分别为表头的每一列注册事件
		for (int i = 0; i < COLUMN_NAME.length; i++)
		{
			final int j = i;
			TableColumn column = viewer.getTable().getColumn(j);
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
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return (Object[])parent;
		}
 
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return ((TopViewDataItem)obj).contents[index];
		}
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
		public Image getImage(Object obj) {
			return null;
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

		   TopViewDataItem p1 = (TopViewDataItem) e1;
		   TopViewDataItem p2 = (TopViewDataItem) e2;
		   if(p1 == null || p2 == null)
		   {
			   // TODO: 这种情况最好显示信息
			   return 0;
		   }
		   /*
		    * 默认是升序
		    * 对于字符串列，按字符串排序，数字列按数字排序
		    */	
		   if(isNumeric(p1.contents[column]) && isNumeric(p2.contents[column]))
		   {
			   result = (int)(Long.parseLong(p2.contents[column]) - Long.parseLong(p1.contents[column]));
		   }
		   else
		   {
			   result = p1.contents[column].compareTo(p2.contents[column]);
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
