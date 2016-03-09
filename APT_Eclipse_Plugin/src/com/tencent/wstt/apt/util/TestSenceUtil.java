/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.TestSence;
import com.tencent.wstt.apt.ui.views.DevicesView;
import com.tencent.wstt.apt.ui.views.SettingView;

/**
 * @Description * 测试配置的工具类 （1）更新当前的测试参数 （2）检查当前的测试参数是否合法
 * @date 2013年11月10日 下午6:23:11
 * 
 */
public class TestSenceUtil {
	/**
	* @Description 获取当前的测试设置 
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean update()
	{
		DevicesView dvPart = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);
		SettingView svPart = (SettingView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SettingView.ID);
		
		if(dvPart==null||svPart==null)
		{
			return false;
		}
		/**
		 * 用这种方式获取测试参数的方式有很大的问题
		 * 比如用户设置完后，关闭了settings页面，然后点击开始测试
		 * 
		 * 理想的方式应该是用编辑器实现，页面背后对应一个xml文件进行存储
		 */
		dvPart.getTargetPkgInfoList();
		svPart.getTestArgs();
		return true;
	}
	
	/**
	* @Description 检查当前测试设置是否准确 
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean verifyTestSence()
	{
		//检查被测进程
		if(TestSence.getInstance().pkgInfos.size() == 0)
		{
			String info = "请选择被测进程";
			APTConsoleFactory.getInstance().APTPrint(info);
			MessageDialog.openWarning(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "提示", info);
			return false;
		}
		
		//检查开关状态
		int testItemCount = 0;
		for(int i = 0; i < Constant.TEST_ITEM_COUNT; i++)
		{
			if(TestSence.getInstance().itemTestSwitch[i])
			{
				testItemCount++;
			}
		}
		
		if(testItemCount == 0)
		{
			String info = "至少选择一项进行测试";
			APTConsoleFactory.getInstance().APTPrint(info);
			MessageDialog.openWarning(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "提示", info);
			return false;
		}
		
		if(TestSence.getInstance().itemTestSwitch[Constant.MEM_INDEX] &&  testItemCount > 1)
		{
			String info = "同时测试内存和CPU，会影响到CPU的测试结果";
			APTConsoleFactory.getInstance().APTPrint(info);
			if(!MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "提示", info))
			{
				return false;
			}
		}
		
		
		if(TestSence.getInstance().itemTestPeriod[Constant.CPU_INDEX] < Constant.TOP_UPDATE_PERIOD*1000)
		{
			String info = "CPU检测周期建议设置为3秒";
			APTConsoleFactory.getInstance().APTPrint(info);
			MessageDialog.openWarning(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "提示", info);
			return false;
		}
		
		return true;
	}

}
