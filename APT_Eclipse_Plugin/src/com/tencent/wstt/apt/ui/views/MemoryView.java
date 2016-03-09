/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.ui.views;

import java.awt.BorderLayout;

import java.awt.Frame;

import java.awt.Panel;


import javax.swing.JRootPane;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.tencent.wstt.apt.chart.MemoryRealTimeChart;
import com.tencent.wstt.apt.data.AbstractStatisticsDataInfo;
import com.tencent.wstt.apt.data.Constant;


/**
* @Description 显示内存曲线和统计数据表格的viewpart 
* @date 2013年11月10日 下午6:14:13 
*
 */
public class MemoryView extends ViewPart {

	public static final String ID = "com.tencent.wstt.apt.ui.views.MemoryView";
	
	public MemoryRealTimeChart memRealTimeChart;
	
	public TableViewer viewer;
	public ViewerFilter tableFilter = null;
	
	private Composite chartComposite;
	public static final String[] COLUMN_NAME = {
		/*"",*/ "内存（kB）","当前","增量","平均","最大", "最小"};
	public static final int[]COLUMN_WIDTH = {/*0,*/ 100,50,50,50,50,50};
	public static final int COLUMN_WIDTH_SUM = 320;


	public MemoryView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		createStatisticsTable(parent);
		createChartPanel(parent);
	
	}

	@Override
	public void setFocus() {

	}
	
	/**
	 * 更新统计表格中的filter
	* @Title: updateViewFilter  
	* @Description:   
	* @param filter 
	* void 
	* @throws
	 */
	public void setTableViewerFilter(ViewerFilter filter)
	{
		if(tableFilter != null)
		{
			viewer.removeFilter(tableFilter);
		}
		if(filter == null)
		{
			tableFilter = null;
			return;
		}
		tableFilter = filter;
		viewer.addFilter(filter);
	}
	
	public void clearStatisticsTableDataForUIThread()
	{
		viewer.getTable().removeAll();
	}
	
	public void clearStatisticsTableDataForNotUIThread()
	{
		viewer.getTable().getDisplay().asyncExec(new Runnable() {		
			@Override
			public void run() {
				viewer.setInput(null);
			}
		});
		
	}
	/**
	 * 初始化jfreechart
	 * @param rootFrame
	 */
	private void createChartPanel(Composite parent)
	{
		chartComposite = new Composite(parent, SWT.NO_BACKGROUND
				| SWT.EMBEDDED);
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, Constant.VIEW_MARGIN_WIDTH);
		formData.right = new FormAttachment(viewer.getTable(), -Constant.VIEW_MARGIN_WIDTH);
		formData.top = new FormAttachment(0, Constant.VIEW_MARGIN_WIDTH);
		formData.bottom = new FormAttachment(100, -Constant.VIEW_MARGIN_WIDTH);
	
		chartComposite.setLayoutData(formData);
		Frame frame = SWT_AWT.new_Frame(chartComposite);
		// 据说加入这个Panel可以无闪烁
		Panel panel = new Panel(new BorderLayout()) {

			private static final long serialVersionUID = 1L;

			public void update(java.awt.Graphics g) {
				/* Do not erase the background */
				paint(g);
			}
		};
		frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		java.awt.Container contentPane = root.getContentPane();
		
		
		memRealTimeChart = new MemoryRealTimeChart();
		contentPane.add(memRealTimeChart);
	}
	
	
	private void createStatisticsTable(Composite rootFrame)
	{
		viewer = new TableViewer(rootFrame, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		//设置表头内容
		for ( int i =0; i<COLUMN_NAME.length;i++){
		    new TableColumn(viewer.getTable(), SWT.RIGHT).setText(COLUMN_NAME[i]);
		    viewer.getTable().getColumn(i).setWidth(COLUMN_WIDTH[i]);
		}
		//设置表头和表格线可见
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible( true );

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		
		FormData tableViewFormData = new FormData();
		tableViewFormData.right = new FormAttachment(100, -Constant.VIEW_MARGIN_WIDTH);
		tableViewFormData.width = COLUMN_WIDTH_SUM;
		tableViewFormData.top = new FormAttachment(0, Constant.VIEW_MARGIN_WIDTH);
		tableViewFormData.bottom = new FormAttachment(100, -Constant.VIEW_MARGIN_WIDTH);
		Table table = viewer.getTable();
		table.setLayoutData(tableViewFormData);

	}
	/**
	 * 把数据转换为表格需要的数据模型
	 *
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return (Object[])parent;
		}
 
	}
	
	/**
	 * 设置每个单元格显示的文本和图片
	 *
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if(index == 0)
			{
				return ((AbstractStatisticsDataInfo)obj).itemName;
			}
			else
			{
				return ((AbstractStatisticsDataInfo)obj).contents[index-1] + "";
			}
		}
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
		public Image getImage(Object obj) {
			return null;
		}
	}
}
