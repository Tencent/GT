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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;


import com.tencent.wstt.apt.chart.CPURealTimeChart;
import com.tencent.wstt.apt.data.AbstractStatisticsDataInfo;
import com.tencent.wstt.apt.data.Constant;

/**
* @Description 显示CPU曲线和统计数据表格的viewpart 
* @date 2013年11月10日 下午6:14:13 
*
 */
public class CPUView extends ViewPart {

	public static final String ID = "com.tencent.wstt.apt.ui.views.CPUView";
	
	public CPURealTimeChart cpuRealTimeChart;
	 
	public TableViewer cpuViewer;
	public TableViewer jiffiesViewer;
	
	public ViewerFilter cpuTableFilter;
	public ViewerFilter jiffiesTableFilter;
	
	private Composite chartComposite;
	
	public static final String[] CPU_COLUMN_NAME = {
		"CPU%","当前","增量","平均","最大", "最小"};
	
	public static final String[] JIFFIES_COLUMN_NAME = {"Jiffies", "初始值", "当前值", "增量"};
	
	public static final int[]COLUMN_WIDTH_CPU = {100,50,50,50,50,50};
	public static final int[]COLUMN_WIDTH_JIFFIES = {150, 66, 67, 67};
	
	public static final int COLUMN_WIDTH_SUM = 330;
	
	public CPUView() {
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
	 * 设置统计表格中的filter
	* @Title: updateViewFilter  
	* @Description:   
	* @param filter 
	* void 
	* @throws
	 */
	public void setCpuTableViewerFilter(ViewerFilter filter)
	{
		if(cpuTableFilter != null)
		{
			cpuViewer.removeFilter(cpuTableFilter);
		}
		if(filter == null)
		{
			cpuTableFilter = null;
			return;
		}
		cpuTableFilter = filter;
		cpuViewer.addFilter(filter); 
	}
	
	public void setJiffiesTableViewerFilter(ViewerFilter filter)
	{
		if(jiffiesTableFilter != null)
		{
			jiffiesViewer.removeFilter(jiffiesTableFilter);
		}
		if(filter == null)
		{
			jiffiesTableFilter = null;
			return;
		}
		jiffiesTableFilter = filter;
		jiffiesViewer.addFilter(filter);
	}
	
	/**
	 * 清除CPU视图中统计表格中的数据
	* @Title: clearStatisticsTableDataForNotUIThred  
	* @Description:    
	* void 
	* @throws
	 */
	public void clearStatisticsTableDataForUIThread()
	{
		cpuViewer.getTable().removeAll();
		jiffiesViewer.getTable().removeAll();	
	}
	
	public void clearStatisticsTableDataForNotUIThread()
	{
		cpuViewer.getTable().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				cpuViewer.setInput(null);
				jiffiesViewer.setInput(null);
				
			}
		});
	}
	
	private void createStatisticsTable(Composite rootFrame)
	{
		// TODO 发现第一列总是左对齐，不能改变其对其方式；只能通过增加一列，但是这一列的宽度为0的方式来实现“第一列”右对齐
		cpuViewer = new TableViewer(rootFrame, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		
		//设置表头内容
		for ( int i =0; i<CPU_COLUMN_NAME.length;i++){
		    new TableColumn(cpuViewer.getTable(), SWT.RIGHT).setText(CPU_COLUMN_NAME[i]);
		    cpuViewer.getTable().getColumn(i).setWidth(COLUMN_WIDTH_CPU[i]);
		}

		//设置表头和表格线可见
		cpuViewer.getTable().setHeaderVisible(true);
		cpuViewer.getTable().setLinesVisible( true );

		//分别为表头的每一列注册事件
		for (int i = 0; i < CPU_COLUMN_NAME.length; i++)
		{
			final int j = i;
			TableColumn column = cpuViewer.getTable().getColumn(j);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
				}
			});
		}

		cpuViewer.setContentProvider(new ViewContentProvider());
		cpuViewer.setLabelProvider(new ViewLabelProvider());
		
		
		FormData tableViewFormData = new FormData();
		tableViewFormData.right = new FormAttachment(100, -Constant.VIEW_MARGIN_WIDTH);
		tableViewFormData.width = COLUMN_WIDTH_SUM;
		tableViewFormData.top = new FormAttachment(0, Constant.VIEW_MARGIN_WIDTH);
		tableViewFormData.bottom = new FormAttachment(50, -Constant.VIEW_MARGIN_WIDTH);
		Table table = cpuViewer.getTable();
		table.setLayoutData(tableViewFormData);
		
		jiffiesViewer = new TableViewer(rootFrame, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		//设置表头内容
		for ( int i =0; i<JIFFIES_COLUMN_NAME.length;i++){
		    new TableColumn(jiffiesViewer.getTable(), SWT.RIGHT).setText(JIFFIES_COLUMN_NAME[i]);
		    jiffiesViewer.getTable().getColumn(i).setWidth(COLUMN_WIDTH_JIFFIES[i]);
		}
		//设置表头和表格线可见
		jiffiesViewer.getTable().setHeaderVisible(true);
		jiffiesViewer.getTable().setLinesVisible( true );
		
		
		//分别为表头的每一列注册事件
		for (int i = 0; i < JIFFIES_COLUMN_NAME.length; i++)
		{
			final int j = i;
			TableColumn column = jiffiesViewer.getTable().getColumn(j);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
				}
			});
		}

		jiffiesViewer.setContentProvider(new ViewContentProvider());
		jiffiesViewer.setLabelProvider(new ViewLabelProvider());

		FormData jiffiesTableViewFormData = new FormData();
		jiffiesTableViewFormData.right = new FormAttachment(100, -Constant.VIEW_MARGIN_WIDTH);
		jiffiesTableViewFormData.width = COLUMN_WIDTH_SUM;
		jiffiesTableViewFormData.top = new FormAttachment(cpuViewer.getTable(), Constant.VIEW_MARGIN_WIDTH);
		jiffiesTableViewFormData.bottom = new FormAttachment(100, -Constant.VIEW_MARGIN_WIDTH);
		jiffiesViewer.getTable().setLayoutData(jiffiesTableViewFormData);

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
	/**
	 * 初始化jfreechart
	 * @param rootFrame
	 */
	private void createChartPanel(Composite parent)
	{
		// 放置图表的容器对象
		chartComposite = new Composite(parent, SWT.NO_BACKGROUND
				| SWT.EMBEDDED);
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, Constant.VIEW_MARGIN_WIDTH);
		formData.right = new FormAttachment(cpuViewer.getTable(), -Constant.VIEW_MARGIN_WIDTH);
		formData.top = new FormAttachment(0, Constant.VIEW_MARGIN_WIDTH);
		formData.bottom = new FormAttachment(100, -Constant.VIEW_MARGIN_WIDTH);
	
		chartComposite.setLayoutData(formData);
		// AWT的根容器
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
		
		
		cpuRealTimeChart = new CPURealTimeChart();
		contentPane.add(cpuRealTimeChart);
	}

}
