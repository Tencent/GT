/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.data;

import org.eclipse.ui.PlatformUI;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.console.StatusBar;
import com.tencent.wstt.apt.ui.views.CPUView;
import com.tencent.wstt.apt.ui.views.DevicesView;
import com.tencent.wstt.apt.ui.views.MemoryView;
import com.tencent.wstt.apt.ui.views.SettingView;

/**
 * APT状态机，用来标明APT三个状态间的转换
* @ClassName: APTState  
* @Description: TODO  
* @date 2013-5-23 下午12:01:20  
*
 */
public class APTState {
	private static APTState instance = null;
	//保存当前APT的状态
	public APTStateEnum curAPTState;
	

	
	private APTState()
	{
		//（1）建议不要在这里获取各view的对象；因为调用位置不太可控，经常会出现view还没有加载成功的情况
		//（2）另一个问题，就是最好加上view对象是否为空的判断
	}
	
	public static APTState getInstance()
	{
		if(instance == null)
		{
			instance = new APTState();
		}
		return instance;
	}
	
	/**
	 * APT启动后的状态是DISPLAYING
	* @Title: setInitState  
	* @Description:    
	* void 
	* @throws
	 */
	public void setInitState()
	{
		curAPTState = APTStateEnum.DISPLAYING;
		StatusBar.getInstance().showInfo(STATE_TITLES[DISPLAY_INDEX]);
	}
	
	/**
	 * APT的三种状态
	* @ClassName: APTStateEnum  
	* @Description: TODO  
	* @date 2013-5-13 下午7:07:38  
	*
	 */
	public enum APTStateEnum
	{
		CONFIGURING,
		TESTING,
		DISPLAYING
	}
	
	private static final String []STATE_TITLES = new String[]{"【APT:配置状态】", "【APT:测试状态】", "【APT:展示状态】"};
	private static final int CONFIG_INDEX = 0;
	private static final int TEST_INDEX = 1;
	private static final int DISPLAY_INDEX = 2;
	
	/**
	 * 所有的APT操作，抽象为下面4中操作
	* @ClassName: APTEventEnum  
	* @Description: TODO  
	* @date 2013-5-14 上午9:59:39  
	*
	 */
	public enum APTEventEnum
	{
		CONFIGRURE_OPER,//setting页面中除checkbox外的UI操作；device页面中pkg增删操作
		START_OPER,//点击开始按钮
		STOP_OPER,//点击停止按钮
		OPENLOG_OPER//打开log按钮
		//除上面操作外，还有进程列表刷新和打开测试结果保存目录两个操作；
		//这两种操作不会更改状态
	}
	

	/**
	 * 收到某种事件后；首先进行清理工作；然后进行初始化操作，如果成功，调用后面状态更改操作
	 * 在收到某种事件后，需要首先进行的清理工作；
	* @Title: DealWithEventBefore  
	* @Description:   
	* @param event 
	* void 
	* @throws
	 */
	public void DealWithEventBefore(APTEventEnum event)
	{
		switch(curAPTState)
		{
		case CONFIGURING:
			//APTConsoleFactory.getInstance().APTPrint("CONFIGURING");
			DealWithEventBefore_Configuration(event);
			break;
			
		case TESTING:
			//APTConsoleFactory.getInstance().APTPrint("TESTING");
			DealWithEventBefore_Test(event);
			break;
			
		case DISPLAYING:
			//APTConsoleFactory.getInstance().APTPrint("DISPLAYING");
			DealWithEventBefore_Display(event);
			break;
		}
		//APTConsoleFactory.getInstance().APTPrint("ends");
	}
	
	/**
	 * 接受事件后，初始化成功后进行的操作
	* @Title: DealWithEventAfter  
	* @Description:   
	* @param event 
	* void 
	* @throws
	 */
	public void DealWithEventAfter(APTEventEnum event)
	{
		switch(curAPTState)
		{
		case CONFIGURING:
			DealWithEventAfter_Configuration(event);
			break;
			
		case TESTING:
			DealWithEventAfter_Test(event);
			break;
			
		case DISPLAYING:
			DealWithEventAfter_Display(event);
			break;
		}
	}
	
	private void DealWithEventBefore_Display(APTEventEnum event) {
		switch(event)
		{
		case CONFIGRURE_OPER:			
			//清除曲线、统计表格
			//APTConsoleFactory.getInstance().APTPrint("CONFIGRURE_OPER");
			APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData");
			clearChartAndTableData();
			APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData ends");
			break;
			
		case START_OPER:
			//清除曲线、统计表格列表清空
			clearChartAndTableData();
			break;
			
		case STOP_OPER:
			APTConsoleFactory.getInstance().APTPrint("展示状态下不可能接收STOP事件");
			StatusBar.getInstance().showInfo("展示状态下不可能接收STOP事件");
			break;
			
		case OPENLOG_OPER:
			//APTConsoleFactory.getInstance().APTPrint("OPENLOG_OPER");
			clearChartAndTableData();
			DevicesView deviceViewPart  = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);			
			if(deviceViewPart == null)
			{
				APTConsoleFactory.getInstance().APTPrint("setUIEnable:Setting或者Device视图对象为空");
				return;
			}
			//TODO:尽量使用UIThread,因为非UI的那种方式，会存在延时，导致删除
			//延时这段时间发生的许多数据
			deviceViewPart.clearTargetPkgTableViewerForUIThread();
			break;
			
		default:
			APTConsoleFactory.getInstance().APTPrint("展示状态default");
			StatusBar.getInstance().showInfo("展示状态default");
			break;

		}
		
		APTConsoleFactory.getInstance().APTPrint("DealWithEventBefore_Display ends");
		
	}

	private void DealWithEventAfter_Display(APTEventEnum event) {

		switch(event)
		{
		case CONFIGRURE_OPER:			
			setCheckBoxsEnable(false);
			
			curAPTState = APTStateEnum.CONFIGURING;
			StatusBar.getInstance().showInfo(STATE_TITLES[CONFIG_INDEX]);
			break;
			
		case START_OPER:
			setUIEnable(false);
			
			StatusBar.getInstance().showInfo(STATE_TITLES[TEST_INDEX]);
			curAPTState = APTStateEnum.TESTING;
			break;
			
		case STOP_OPER:
			APTConsoleFactory.getInstance().APTPrint("展示状态下不可能接收STOP事件");
			StatusBar.getInstance().showInfo("展示状态下不可能接收STOP事件");
			break;
			
		case OPENLOG_OPER:
			break;
			
		default:
			APTConsoleFactory.getInstance().APTPrint("展示状态default");
			StatusBar.getInstance().showInfo("展示状态default");
			break;

		}
	}
	
	
	private void DealWithEventBefore_Test(APTEventEnum event) {
		switch(event)
		{
		case CONFIGRURE_OPER:
			APTConsoleFactory.getInstance().APTPrint("测试状态下不可能接收配置事件");
			StatusBar.getInstance().showInfo("测试状态下不可能接收配置事件");
			break;
			
		case START_OPER:
			APTConsoleFactory.getInstance().APTPrint("测试状态下不可能接收START事件");
			StatusBar.getInstance().showInfo("测试状态下不可能接收START事件");
			break;
			
		case STOP_OPER:
			
			break;
			
		case OPENLOG_OPER:
			APTConsoleFactory.getInstance().APTPrint("测试状态下不可能接收OPENLOG事件");
			StatusBar.getInstance().showInfo("测试状态下不可能接收OPENLOG事件");
			break;
			
		default:
			APTConsoleFactory.getInstance().APTPrint("测试状态default事件");
			StatusBar.getInstance().showInfo("测试状态default事件");
			break;

		}
		
	}
	private void DealWithEventAfter_Test(APTEventEnum event) {
		switch(event)
		{
		case CONFIGRURE_OPER:
			APTConsoleFactory.getInstance().APTPrint("测试状态下不可能接收配置事件");
			StatusBar.getInstance().showInfo("测试状态下不可能接收配置事件");
			break;
			
		case START_OPER:
			APTConsoleFactory.getInstance().APTPrint("测试状态下不可能接收START事件");
			StatusBar.getInstance().showInfo("测试状态下不可能接收START事件");
			break;
			
		case STOP_OPER:
			setUIEnable(true);
			//setCheckBoxsEnable(true);
			
			curAPTState = APTStateEnum.DISPLAYING;
			StatusBar.getInstance().showInfo(STATE_TITLES[DISPLAY_INDEX]);
			
			break;
			
		case OPENLOG_OPER:
			APTConsoleFactory.getInstance().APTPrint("测试状态下不可能接收OPENLOG事件");
			StatusBar.getInstance().showInfo("测试状态下不可能接收OPENLOG事件");
			break;
			
		default:
			APTConsoleFactory.getInstance().APTPrint("测试状态default事件");
			StatusBar.getInstance().showInfo("测试状态default事件");
			break;

		}
	}
	


	private void DealWithEventBefore_Configuration(APTEventEnum event) {
		switch(event)
		{
		case CONFIGRURE_OPER:
			break;
			
		case START_OPER:
			break;
			
		case STOP_OPER:
			APTConsoleFactory.getInstance().APTPrint("配置状态下不可能接收停止事件");
			StatusBar.getInstance().showInfo("配置状态下不可能接收停止事件");
			break;
			
		case OPENLOG_OPER:
			//TODO:这里有没有必要把所有的配置进行归位;至少要进行的操作应该是把被测进程列表中的进程清除
			DevicesView deviceViewPart  = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);			
			if(deviceViewPart == null)
			{
				APTConsoleFactory.getInstance().APTPrint("setUIEnable:Setting或者Device视图对象为空");
				return;
			}
			deviceViewPart.clearTargetPkgTableViewerForUIThread();
			break;
			
		default:
			APTConsoleFactory.getInstance().APTPrint("配置状态default");
			StatusBar.getInstance().showInfo("配置状态default");
			break;
		}
	}

	private void DealWithEventAfter_Configuration(APTEventEnum event) {
		switch(event)
		{
		case CONFIGRURE_OPER:
			break;
			
		case START_OPER:
			setUIEnable(false);
			setCheckBoxsEnable(true);
			
			curAPTState = APTStateEnum.TESTING;
			StatusBar.getInstance().showInfo(STATE_TITLES[TEST_INDEX]);
			break;
			
		case STOP_OPER:
			APTConsoleFactory.getInstance().APTPrint("配置状态下不可能接收停止事件");
			StatusBar.getInstance().showInfo("配置状态下不可能接收停止事件");
			break;
			
		case OPENLOG_OPER:
			setCheckBoxsEnable(true);
			
			curAPTState = APTStateEnum.DISPLAYING;
			StatusBar.getInstance().showInfo(STATE_TITLES[DISPLAY_INDEX]);
			break;
			
		default:
			APTConsoleFactory.getInstance().APTPrint("配置状态default");
			StatusBar.getInstance().showInfo("配置状态default");
			break;
		}
	}

	
	/**
	 * 设置UI控件是否可用；主要包括三类控件
	* @Title: setUIEnable  
	* @Description:   
	* @param isEnable 
	* void 
	* @throws
	 */
	private boolean setUIEnable(boolean isEnable)
	{
		SettingView settingViewPart  = (SettingView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SettingView.ID);
		DevicesView deviceViewPart  = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);
		
		if(settingViewPart==null || deviceViewPart == null)
		{
			APTConsoleFactory.getInstance().APTPrint("setUIEnable:Setting或者Device视图对象为空");
			return false;
		}
		
		/**
		 * 测试开关和测试周期
		 */
		for(int i = 0; i < Constant.TEST_ITEM_COUNT; i++)
		{
			settingViewPart.itemTestSwitch[i].setEnabled(isEnable);
			settingViewPart.itemTestSwitchClose[i].setEnabled(isEnable);
			settingViewPart.itemTestPeriod[i].setEnabled(isEnable);
		}
		
		/**
		 * CPU测试方式和是否测试jiffies
		 */
		settingViewPart.cpuTestMethod.setEnabled(isEnable);
		settingViewPart.jiffiesSwitch.setEnabled(isEnable);
		
		settingViewPart.hprofDumpSwitch.setEnabled(isEnable);
		settingViewPart.hprofDumpThreshold.setEnabled(isEnable);
		
		/**
		 * 禁止进程列表增删操作，包括手动添加
		 */
		deviceViewPart.setAddAndDelOperEnable(isEnable);
		
		/**
		 * 其他三个按钮
		 */
		deviceViewPart.refreshAction.setEnabled(isEnable);
		deviceViewPart.openLogWithChartAction.setEnabled(isEnable);
		deviceViewPart.openResultDirAction.setEnabled(isEnable);
		
		return true;
	}
	
	/**
	 * 控制内存曲线、进程显示按钮是否可用
	* @Title: setCheckBoxsEnable  
	* @Description:   
	* @param isEnable 
	* void 
	* @throws
	 */
	private boolean setCheckBoxsEnable(boolean isEnable)
	{
		SettingView settingViewPart  = (SettingView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SettingView.ID);
		DevicesView deviceViewPart  = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);
		if(settingViewPart==null || deviceViewPart == null)
		{
			APTConsoleFactory.getInstance().APTPrint("setCheckBoxsEnable:Setting或者Device视图对象为空");
			return false;
		}
		deviceViewPart.setCheckChangeEnable(isEnable);
		settingViewPart.setCheckChangeEnable(isEnable);
		return true;
	}
	
	/**
	 * 清除曲线和统计表格数据
	* @Title: clearChartAndTableData  
	* @Description:    
	* void 
	* @throws
	 */
	private boolean clearChartAndTableData()
	{
		//APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData:1");
		CPUView cpuViewPart  = (CPUView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CPUView.ID);
		//APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData:2");
		MemoryView memViewPart  = (MemoryView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MemoryView.ID);
		//APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData:3");

		if(cpuViewPart != null)
		{
			if(cpuViewPart.cpuRealTimeChart != null)
			{
				//APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData:4");
				cpuViewPart.cpuRealTimeChart.clearAllData();
				//APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData:5");
				cpuViewPart.clearStatisticsTableDataForNotUIThread();
			}
		}
		
		if(memViewPart != null)
		{
			if(memViewPart.memRealTimeChart != null)
			{
				memViewPart.memRealTimeChart.clearAllData();
				//APTConsoleFactory.getInstance().APTPrint("clearChartAndTableData:6");
				memViewPart.clearStatisticsTableDataForNotUIThread();
			}
		}
		
		return true;
	}	
}


