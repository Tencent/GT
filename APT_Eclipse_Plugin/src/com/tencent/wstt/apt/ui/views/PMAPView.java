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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JRootPane;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;






import com.tencent.wstt.apt.chart.PieChart;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PMAPDataItem;
import com.tencent.wstt.apt.data.PieChartDataItem;
import com.tencent.wstt.apt.util.MapUtil;
import com.tencent.wstt.apt.util.PMAPFileParse;


/**
* @Description 显示PMAP饼图和统计数据表格的viewpart 
* @date 2013年11月10日 下午6:14:13 
*
 */
public class PMAPView extends ViewPart {
	public static final String ID = "com.tencent.wstt.apt.ui.views.PMAPView";
	
	
	private Action openLogAction;
	
	private Group toolGroup = null;
	private Text showDataItemCountText = null;
	private CTabFolder rootTabFolder = null;

	public static final String[] COLUMN_NAME = {"Mapping", "PSS(kB)"};
	public static final int[] COLUMN_WIDTH = {400, 100};


	public PMAPView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		//初始化工具条
		createToolGroup(parent);
		//初始化rootTab
		createRootTabFolder(parent);
		//添加上下文菜单和工具栏按钮
		createMenuAndToolBar();
	}

	@Override
	public void setFocus() {

	}

	
	
	/**
	 * 初始化工具条
	 * @param parent
	 */
	private void createToolGroup(Composite parent)
	{
		FormData toolBarGroupFromData = new FormData();
		toolBarGroupFromData.top = new FormAttachment(0, 5);
		toolBarGroupFromData.left = new FormAttachment(0, 5);
		toolBarGroupFromData.right = new FormAttachment(100, -5);
		toolBarGroupFromData.bottom = new FormAttachment(parent, 50);//绝对位置
		toolGroup = new Group(parent, SWT.NONE);
		toolGroup.setLayoutData(toolBarGroupFromData);
		toolGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label label = new Label(toolGroup, SWT.NONE|SWT.BOLD);
		label.setText("饼图中显示数据项个数");
		label.setFont(new Font(parent.getDisplay(), "宋体", 10, SWT.BOLD));
		
		showDataItemCountText = new Text(toolGroup, SWT.BORDER);
		showDataItemCountText.setText("10");	
	}
	
	/**
	 * 初始化action、上下文菜单、工具栏按钮
	 */
	private void createMenuAndToolBar()
	{
		makeActions();
		createLocalToolBar();
	}


	/**
	 * 添加工具条
	 */
	private void createLocalToolBar()
	{
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(openLogAction);
	}
	
	
	/**
	 * 初始化Action
	 */
	private void makeActions()
	{
		
		openLogAction = new Action() {
			public void run() {
				FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterPath(Constant.LOG_FOLDER_ON_PC);//设置初始路径
				String fileName = dialog.open();//返回的全路径(路径+文件名)
				if(fileName == null)
				{
					return;
				}
				String path = dialog.getFilterPath();
				String []fileNames = dialog.getFileNames();
				int fileNum = fileNames.length;
				if(fileNum > 2)
				{
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "提示", "不支持同时打开超过2个pmap文件");
					return;
				}
				PMAPFileParse parse = new PMAPFileParse();
				if(fileNum == 1)
				{
					Map<String, Integer> pmapResult = null;
					if((pmapResult = parse.parse(fileName)) == null)
					{
						return;
					}
					createCTabItem(fileNames[0], pmapResult);
				}
				else
				{
					String file1 = path + File.separator + fileNames[0];
					String file2 = path + File.separator + fileNames[1];
					Map<String, Integer> file1Map = parse.parse(file1);
					Map<String, Integer> file2Map = parse.parse(file2);
					if(file1Map == null || file2Map == null)
					{
						return;
					}
					Map<String, Integer> pmapResult = MapUtil.sub(file2Map, file1Map);
					createCTabItem(fileNames[0], file1Map);
					createCTabItem(fileNames[1], file2Map);
					createCTabItem(getMergeFileName(fileNames[0], fileNames[1]), pmapResult);	
				}				
				
			}
		};
		
		openLogAction.setText("打开pmap文件");
		openLogAction.setToolTipText("打开pmap文件");
		openLogAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Constant.PLUGIN_ID, "icons/open_pc.png"));	
		
	}
	
	/**
	 * 初始化tab页的根
	 * @param parent
	 */
	private void createRootTabFolder(Composite parent) {
		rootTabFolder = new CTabFolder(parent, SWT.TOP | SWT.CLOSE | SWT.BORDER);
		rootTabFolder.setTabHeight(20);
		rootTabFolder.setLayout(new FillLayout());
		rootTabFolder.marginHeight = 10;
		rootTabFolder.marginWidth = 10;
		// 显示“最大化、最小化”按钮
		rootTabFolder.setMaximizeVisible(true);
		rootTabFolder.setMinimizeVisible(true);
		
		rootTabFolder.setSelectionBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		rootTabFolder.setSelectionForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		rootTabFolder.setUnselectedCloseVisible(true);
		
		rootTabFolder.pack();
		
		FormData rootTabFormData  = new FormData();
		rootTabFormData.left = new FormAttachment(0, 5);
		rootTabFormData.right = new FormAttachment(100, -5);
		rootTabFormData.top = new FormAttachment(toolGroup, 5);
		rootTabFormData.bottom = new FormAttachment(100, -5);
		rootTabFolder.setLayoutData(rootTabFormData);
	}
	
	
	
	
	/**
	 * 生成一个新的tab页,并生成数据
	 */
	private void createCTabItem(String name, Map<String, Integer>pmap)
	{
		CTabItem item = new CTabItem(rootTabFolder, SWT.NONE);
		item.setText(name);
		Composite rootComposite = new Composite(rootTabFolder, SWT.NO_BACKGROUND
				| SWT.EMBEDDED);
		rootComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		//生成饼图
		createChartPanel(rootComposite, pmap);
		//生成表格
		createStatisticsTable(rootComposite, getTableDataFromMap(pmap));
		item.setControl(rootComposite);
		rootTabFolder.setSelection(item);

	}
	
	/**
	 * 生成饼图
	 * @param parent
	 * @param data
	 */
	private void createChartPanel(Composite parent, Map<String, Integer>data)
	{
		Composite composite = new Composite(parent, SWT.NO_BACKGROUND
				| SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(composite);
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
		
		PieChart chart = new PieChart();
		int showItemNum = 0;
		try {
			showItemNum = Integer.parseInt(showDataItemCountText.getText());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			showItemNum = 10;
			
		}
		chart.setDataset(getPieChartDataFromMap(data, showItemNum));
		contentPane.add(chart);
	}
	
	/**
	 * 生成详细数据的table
	 * @param rootFrame
	 * @param data
	 */
	private void createStatisticsTable(Composite rootFrame, Object[] data)
	{
		TableViewer viewer = new TableViewer(rootFrame, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
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
		viewer.setInput(data);

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
			return ((PMAPDataItem) obj).contents[index];

		}
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
		public Image getImage(Object obj) {
			return null;
		}
	}
	
	/**
	 * 合并两个文件名字
	 * @param fileName1
	 * @param fileName2
	 * @return
	 */
	private String getMergeFileName(String fileName1, String fileName2)
	{
		String result = null;
		int index = fileName2.lastIndexOf("_");
		String dateStr = ".txt";
		if(index != -1)
		{
			dateStr = fileName2.substring(index, fileName2.length());
		}
		else
		{
			int dotIndex = fileName2.lastIndexOf(".");
			if(dotIndex == -1)
			{
				dotIndex = fileName2.length();
			}
			dateStr = fileName2.substring(0, dotIndex);
			//APTConsoleFactory.getInstance().APTPrint(dateStr);
		}
		result = fileName1.replaceAll(".txt", dateStr);
		//APTConsoleFactory.getInstance().APTPrint(result);
		return result;
	}
	
	/**
	 * 获取统计表格的原始数据
	 * @param sour
	 * @return
	 */
	private Object[] getTableDataFromMap(Map<String, Integer> sour)
	{
		List<PMAPDataItem> list = new ArrayList<PMAPDataItem>();
		/**
		 * 过滤掉等于0的数据
		 */
		for(Entry<String, Integer> entry: sour.entrySet())
		{
			if(entry.getValue() != 0)
			{
				PMAPDataItem item = new PMAPDataItem();
				item.contents[PMAPDataItem.NAME_INDEX] = entry.getKey();
				item.contents[PMAPDataItem.VALUE_INDEX] = entry.getValue() + "";
				list.add(item);
			}
		}
		

		Collections.sort(list, new Comparator<PMAPDataItem>() {

			@Override
			public int compare(PMAPDataItem arg0, PMAPDataItem arg1) {
				int val1 = Integer.parseInt(((PMAPDataItem)arg0).contents[PMAPDataItem.VALUE_INDEX]);
				int val2 = Integer.parseInt(((PMAPDataItem)arg1).contents[PMAPDataItem.VALUE_INDEX]);
				return (val2-val1);		
			}
		});
		
		return list.toArray();		
	}
	
	/**
	 * 获取饼图数据
	 * @param sour
	 * @param showItemNum
	 * @return
	 */
	private List<PieChartDataItem> getPieChartDataFromMap(Map<String, Integer> sour, int showItemNum)
	{
		List<PieChartDataItem> tempList = new ArrayList<PieChartDataItem>();
		/**
		 * 仅仅统计大于0的数据
		 */
		long sum = 0;
		for(Entry<String, Integer> entry: sour.entrySet())
		{
			if(entry.getValue() > 0 && !entry.getKey().equalsIgnoreCase("total"))
			{
				PieChartDataItem item = new PieChartDataItem();
				item.mapping = entry.getKey();
				item.value = entry.getValue();
				sum += (Integer)item.value;
				tempList.add(item);
			}
		}
			
		Collections.sort(tempList, new Comparator<PieChartDataItem>() {

			@Override
			public int compare(PieChartDataItem arg0, PieChartDataItem arg1) {
				int val0 = (Integer)arg0.value;
				int val1 = (Integer)arg1.value;
				return (val1 - val0);
			}
		});
		
		int len = Math.min(showItemNum, tempList.size());
		double subSum = 0;
		List<PieChartDataItem> result = new ArrayList<PieChartDataItem>();
		for(int i = 0; i < len-1; i++)
		{
			int curVal = (Integer)tempList.get(i).value;
			PieChartDataItem item = new PieChartDataItem();
			item.mapping = tempList.get(i).mapping;
			item.value = (double)curVal/sum;
			subSum += (Double)item.value;
			result.add(item);
		}
		PieChartDataItem item = new PieChartDataItem();
		item.mapping = "other";
		item.value = 1-subSum;
		result.add(item);
		
		return result;
	}
}
