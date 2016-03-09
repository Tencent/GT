/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.ui.views;




import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import com.tencent.wstt.apt.data.APTState;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;
import com.tencent.wstt.apt.data.TestSence;
import com.tencent.wstt.apt.data.APTState.APTEventEnum;
import com.tencent.wstt.apt.ui.custom.SWTNumberVerifyListener;
import com.tencent.wstt.apt.util.GetCurCheckedStateUtil;




/**
* @Description 显示设置的viewpart 
* @date 2013年11月10日 下午6:14:13 
*
 */
public class SettingView extends ViewPart {
	public static final String ID = "com.tencent.wstt.apt.ui.views.SettingView";
	
	//测试周期；其中jiffies和CPU共用一个测试周期
	public Text itemTestPeriod[] = new Text[Constant.TEST_ITEM_COUNT];
	//测试开关
	public Button itemTestSwitch[] = new Button[Constant.TEST_ITEM_COUNT];
	public Button itemTestSwitchClose[] = new Button[Constant.TEST_ITEM_COUNT];
	
	//测试方式
	public Combo cpuTestMethod;
	public Combo jiffiesSwitch;
	
	//hprof dump
	public Combo hprofDumpSwitch;
	public Text hprofDumpThreshold;

	//控制内存视图中的曲线显示
	public Button memStatCheckBtns[];
	
	private String[] memStatCheckBtnTitles = {"Native", "Dalvik", "Total", "Native", "Dalvik", "Total", "Native", "Dalvik", "Total", "Native", "Dalvik", "Total"};
	
	private boolean isSupportCheckChangeOper = true;
	
	public void setCheckChangeEnable(boolean isEnable)
	{
		isSupportCheckChangeOper = isEnable;
	}
	
	public SettingView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		createTabs(parent);
	}

	@Override
	public void setFocus() {

	}
	
	
	private void createTabs(Composite parent)
	{
		TabFolder tabFolder = new TabFolder(parent, SWT.None);
		
		createSettingTabItem(tabFolder);
		
		createCPUTabItem(tabFolder);
		
		createMemTabItem(tabFolder);
		
	
	}
	
	/**
	 * 更新TestSence值
	* @Title: getTestArgs  
	* @Description:    
	* void 
	* @throws
	 */
	public void getTestArgs()
	{
		//获取当前APT插件运行的系统
		PCInfo.OSName = System.getProperty("os.name");
		
		//获取测试开关和测试周期
		for(int i = 0; i < Constant.TEST_ITEM_COUNT; i++)
		{
			TestSence.getInstance().itemTestSwitch[i] = itemTestSwitch[i].getSelection();
			TestSence.getInstance().itemTestPeriod[i] = Integer.parseInt(itemTestPeriod[i].getText());
		}
		
		//获取CPU测试方式
		// TODO 当获取进程列表的时候顺便检查当前系统是否支持dumpsys cpuinfo获取CPU使用率
		TestSence.getInstance().cpuTestMethod = cpuTestMethod.getSelectionIndex();
		TestSence.getInstance().isTestJiffies = jiffiesSwitch.getSelectionIndex() == 0?false:true;
		
		TestSence.getInstance().isDumpHprof = hprofDumpSwitch.getSelectionIndex() == 0?false:true;
		TestSence.getInstance().dumpHprofThreshold = Integer.parseInt(hprofDumpThreshold.getText());
	}
	
	public boolean[] getMemChecked()
	{
		boolean[] result = new boolean[Constant.ALL_MEM_KIND_COUNT];
		for(int i = 0; i < Constant.ALL_MEM_KIND_COUNT; i++)
		{
			result[i] = memStatCheckBtns[i].getSelection();
		}
		return result;
	}
	
	private void createSettingTabItem(TabFolder parent)
	{
		// 创建CPU标签页
		TabItem settingTabItem = new TabItem(parent, SWT.NONE);
		settingTabItem.setText("首选项");
		
		//CPU标签根容器
		Composite settingTabRoot = new Composite(parent, SWT.NONE);
		settingTabRoot.setLayout(new FormLayout());

	
		FormData monitorItemGroupFormData = new FormData();
		monitorItemGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		monitorItemGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		monitorItemGroupFormData.top = new FormAttachment(settingTabRoot, Constant.MARGIN_WIDTH_NARROW);

		Group monitorGroup = new Group(settingTabRoot, SWT.NONE);
		monitorGroup.setText("监测项");
		monitorGroup.setLayoutData(monitorItemGroupFormData);
		monitorGroup.setLayout(new FormLayout());


		FormData cpuGroupFormData = new FormData();
		cpuGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		cpuGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		cpuGroupFormData.top = new FormAttachment(monitorGroup, Constant.MARGIN_WIDTH_NARROW);

		Group cpuGroup = new Group(monitorGroup, SWT.NONE);
		cpuGroup.setLayoutData(cpuGroupFormData);
		cpuGroup.setLayout(new GridLayout(3, true));
		Label cpuLabel = new Label(cpuGroup, SWT.NONE);
		cpuLabel.setText("CPU");
		
		itemTestSwitch[Constant.CPU_INDEX] = new Button(cpuGroup, SWT.RADIO);
		itemTestSwitch[Constant.CPU_INDEX].setText("开");
		itemTestSwitch[Constant.CPU_INDEX].setSelection(false);
		itemTestSwitch[Constant.CPU_INDEX].addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		itemTestSwitchClose[Constant.CPU_INDEX] = new Button(cpuGroup, SWT.RADIO);
		itemTestSwitchClose[Constant.CPU_INDEX].setText("关");
		itemTestSwitchClose[Constant.CPU_INDEX].setSelection(true);
		
		FormData memGroupFormData = new FormData();
		memGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		memGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		memGroupFormData.top = new FormAttachment(cpuGroup, Constant.MARGIN_WIDTH_NARROW);
		memGroupFormData.bottom = new FormAttachment(100, -Constant.MARGIN_WIDTH);

		Group memGroup = new Group(monitorGroup, SWT.NONE);
		memGroup.setLayoutData(memGroupFormData);
		memGroup.setLayout(new GridLayout(3, true));


		Label memLabel = new Label(memGroup, SWT.NONE);
		memLabel.setText("内存");
		
		itemTestSwitch[Constant.MEM_INDEX] = new Button(memGroup, SWT.RADIO);
		itemTestSwitch[Constant.MEM_INDEX].setText("开");
		itemTestSwitch[Constant.MEM_INDEX].setSelection(false);
		itemTestSwitch[Constant.MEM_INDEX].addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		itemTestSwitchClose[Constant.MEM_INDEX] = new Button(memGroup, SWT.RADIO);
		itemTestSwitchClose[Constant.MEM_INDEX].setText("关");
		itemTestSwitchClose[Constant.MEM_INDEX].setSelection(true);
		
		settingTabItem.setControl(settingTabRoot);
	}
	
	
	private void createCPUTabItem(TabFolder parent)
	{
		TabItem cpuTabItem = new TabItem(parent, SWT.NONE);
		cpuTabItem.setText("CPU");

		Composite cpuTabRoot = new Composite(parent, SWT.NONE);
		cpuTabRoot.setLayout(new FormLayout());
		
		//采样间隔
		FormData testPeriodGroupFormData = new FormData();
		testPeriodGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		testPeriodGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		testPeriodGroupFormData.top = new FormAttachment(cpuTabRoot, Constant.MARGIN_WIDTH_NARROW);
		Group periodGroup = new Group(cpuTabRoot, SWT.NONE);
		periodGroup.setLayoutData(testPeriodGroupFormData);
		periodGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label cpuLabel = new Label(periodGroup, SWT.NONE);
		cpuLabel.setText("采样间隔(ms)");
		
		itemTestPeriod[Constant.CPU_INDEX] = new Text(periodGroup, SWT.BORDER);
		itemTestPeriod[Constant.CPU_INDEX].setText("3000");
		itemTestPeriod[Constant.CPU_INDEX].addVerifyListener(new SWTNumberVerifyListener());
		itemTestPeriod[Constant.CPU_INDEX].addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
		});
		
		//CPU使用率测试方法设置
		FormData cpuSwitchGroupFormData = new FormData();
		cpuSwitchGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		cpuSwitchGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		cpuSwitchGroupFormData.top = new FormAttachment(periodGroup, Constant.MARGIN_WIDTH_NARROW);
		

		Group testMethodGroup = new Group(cpuTabRoot, SWT.NONE);
		testMethodGroup.setLayoutData(cpuSwitchGroupFormData);
		testMethodGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label cpuTestMethodLabel = new Label(testMethodGroup, SWT.NONE);
		cpuTestMethodLabel.setText("数据源");
		
		cpuTestMethod = new Combo(testMethodGroup, SWT.DROP_DOWN|SWT.READ_ONLY);
		cpuTestMethod.setItems(Constant.CPU_TESTMETHOD_TITLES);
		cpuTestMethod.select(0);
		cpuTestMethod.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
		});
			

		FormData jiffiesGroupFormData = new FormData();
		jiffiesGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		jiffiesGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		jiffiesGroupFormData.top = new FormAttachment(testMethodGroup, Constant.MARGIN_WIDTH_NARROW);
		

		Group jiffiesGroup = new Group(cpuTabRoot, SWT.NONE);
		jiffiesGroup.setLayoutData(jiffiesGroupFormData);
		jiffiesGroup.setLayout(new FillLayout(SWT.HORIZONTAL));

		Label jiffiesLabel = new Label(jiffiesGroup, SWT.NONE);
		jiffiesLabel.setText("获取jiffies值");
		
		jiffiesSwitch = new Combo(jiffiesGroup, SWT.DROP_DOWN|SWT.READ_ONLY);
		jiffiesSwitch.setItems(new String[]{"否", "是"});
		jiffiesSwitch.select(0);
		jiffiesSwitch.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
		});
		
		cpuTabItem.setControl(cpuTabRoot);
	}
	
	/**
	 * 为了迎合兰姐的界面，要求改的好丑啊
	 * @param parent
	 */
	private void createMemTabItem(TabFolder parent)
	{
		TabItem memTabItem = new TabItem(parent, SWT.NONE);
		memTabItem.setText("内存");

		Composite memTabRoot = new Composite(parent, SWT.NONE);
		memTabRoot.setLayout(new FormLayout());
		
		
		FormData testPeriodGroupFormData = new FormData();
		testPeriodGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		testPeriodGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		testPeriodGroupFormData.top = new FormAttachment(memTabRoot, Constant.MARGIN_WIDTH_NARROW);
		Group periodGroup = new Group(memTabRoot, SWT.NONE);
		periodGroup.setLayoutData(testPeriodGroupFormData);
		periodGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label memLabel = new Label(periodGroup, SWT.NONE);
		memLabel.setText("采样间隔(ms)");
		itemTestPeriod[Constant.MEM_INDEX] = new Text(periodGroup, SWT.BORDER);
		itemTestPeriod[Constant.MEM_INDEX].setText("3000");
		itemTestPeriod[Constant.MEM_INDEX].addVerifyListener(new SWTNumberVerifyListener());
		itemTestPeriod[Constant.MEM_INDEX].addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
		});
		
		
		FormData hprofDumpSwitchGroupFormData = new FormData();
		hprofDumpSwitchGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		hprofDumpSwitchGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		hprofDumpSwitchGroupFormData.top = new FormAttachment(periodGroup, Constant.MARGIN_WIDTH_NARROW);
		

		Group hprofDumpSwitchGroup = new Group(memTabRoot, SWT.NONE);
		hprofDumpSwitchGroup.setLayoutData(hprofDumpSwitchGroupFormData);
		hprofDumpSwitchGroup.setLayout(new FillLayout(SWT.HORIZONTAL));

		Label hprofDumpLabel = new Label(hprofDumpSwitchGroup, SWT.NONE);
		hprofDumpLabel.setText("Dump Hprof");
		
		hprofDumpSwitch = new Combo(hprofDumpSwitchGroup, SWT.DROP_DOWN|SWT.READ_ONLY);
		hprofDumpSwitch.setItems(new String[]{"否", "是"});
		hprofDumpSwitch.select(0);
		hprofDumpSwitch.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
//				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
//				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
		});
		
		
		FormData hprofDumpThresholdGroupFormData = new FormData();
		hprofDumpThresholdGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		hprofDumpThresholdGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		hprofDumpThresholdGroupFormData.top = new FormAttachment(hprofDumpSwitchGroup, Constant.MARGIN_WIDTH_NARROW);
		Group hrpofDumpThresholdGroup = new Group(memTabRoot, SWT.NONE);
		hrpofDumpThresholdGroup.setLayoutData(hprofDumpThresholdGroupFormData);
		hrpofDumpThresholdGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label hprofDumpThresholdLabel = new Label(hrpofDumpThresholdGroup, SWT.NONE);
		hprofDumpThresholdLabel.setText("Dump阈值(kB)");
		hprofDumpThreshold = new Text(hrpofDumpThresholdGroup, SWT.BORDER);
		hprofDumpThreshold.setText("30000");
		hprofDumpThreshold.addVerifyListener(new SWTNumberVerifyListener());
		hprofDumpThreshold.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				APTState.getInstance().DealWithEventBefore(APTEventEnum.CONFIGRURE_OPER);
				APTState.getInstance().DealWithEventAfter(APTEventEnum.CONFIGRURE_OPER);
			}
		});
		
		//Priv Dirty Group
		FormData privGroupFormData = new FormData();
		privGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		privGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		privGroupFormData.top = new FormAttachment(hrpofDumpThresholdGroup, Constant.MARGIN_WIDTH_NARROW);
		
		Group privGroup = new Group(memTabRoot, SWT.NONE);
		privGroup.setText("Priv Dirty");
		privGroup.setLayoutData(privGroupFormData);
		privGroup.setLayout(new GridLayout(3, true));
				
		
		//PSS Group
		FormData pssGroupFormData = new FormData();
		pssGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		pssGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		pssGroupFormData.top = new FormAttachment(privGroup, Constant.MARGIN_WIDTH_NARROW);

		Group pssGroup = new Group(memTabRoot, SWT.NONE);
		pssGroup.setText("PSS");
		pssGroup.setLayoutData(pssGroupFormData);
		pssGroup.setLayout(new GridLayout(3, true));
		
		//Heap Allocated Group
		FormData heapGroupFormData = new FormData();
		heapGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		heapGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		heapGroupFormData.top = new FormAttachment(pssGroup, Constant.MARGIN_WIDTH_NARROW);
		
		Group heapGroup = new Group(memTabRoot, SWT.NONE);
		heapGroup.setText("Heap Allocated");
		heapGroup.setLayoutData(heapGroupFormData);
		heapGroup.setLayout(new GridLayout(3, true));
		
		//Heap Size Group
		FormData heapsizeGroupFormData = new FormData();
		heapsizeGroupFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		heapsizeGroupFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		heapsizeGroupFormData.top = new FormAttachment(heapGroup, Constant.MARGIN_WIDTH_NARROW);
		
		Group heapsizeGroup = new Group(memTabRoot, SWT.NONE);
		heapsizeGroup.setText("Heap Size");
		heapsizeGroup.setLayoutData(heapsizeGroupFormData);
		heapsizeGroup.setLayout(new GridLayout(3, true));
		
		
		memStatCheckBtns = new Button[Constant.ALL_MEM_KIND_COUNT];
		for(int i = 0; i < Constant.ALL_MEM_KIND_COUNT; i++)
		{
			Group group = null;
			if(i<3)
			{
				group = privGroup;
			}
			else if(i < 6)
			{
				group = pssGroup;
			}
			else if(i < 9)
			{
				group = heapGroup;
			}
			else
			{
				group = heapsizeGroup;
			}
				
			//final int index = i;
			memStatCheckBtns[i] = new Button(group, SWT.CHECK);
			memStatCheckBtns[i].setText(memStatCheckBtnTitles[i]);
			memStatCheckBtns[i].addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(isSupportCheckChangeOper)
					{
						//APTConsoleFactory.getInstance().APTPrint("内存check更新");
						GetCurCheckedStateUtil.update();
					}	
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		
		//memStatCheckBtns[Constant.PRIV_TOTAL_INDEX].setSelection(true);
		memStatCheckBtns[Constant.PSS_TOTAL_INDEX].setSelection(true);
		//memStatCheckBtns[Constant.HEAPALLOC_TOTAL_INDEX].setSelection(true);

		
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, Constant.MARGIN_WIDTH);
		labelFormData.right = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		labelFormData.bottom = new FormAttachment(100, -Constant.MARGIN_WIDTH);
		
		
		Label showLabel = new Label(memTabRoot, SWT.NONE);
		showLabel.setLayoutData(labelFormData);
		showLabel.setText("提示：测试过程中可动态调整曲线显示");
		showLabel.setForeground(new Color(parent.getDisplay(), 0,64,64));
		showLabel.setFont(new Font(parent.getDisplay(), "宋体", 10, SWT.BOLD));
		
		
		memTabItem.setControl(memTabRoot);
	}
}
