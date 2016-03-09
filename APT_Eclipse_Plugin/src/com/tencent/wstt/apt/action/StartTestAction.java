/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.action;



import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.APTState;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.DeviceInfo;
import com.tencent.wstt.apt.data.TestSence;
import com.tencent.wstt.apt.data.TestTaskManager;
import com.tencent.wstt.apt.data.APTState.APTEventEnum;
import com.tencent.wstt.apt.data.Constant.PhoneState;
import com.tencent.wstt.apt.util.TestSenceUtil;




/**
* @Description: 开始/暂停按钮对应的Action 
* @date 2013年11月10日 下午4:53:11 
 */
public class StartTestAction extends Action {
	
	//标识当前是否处于测试中
	boolean bStatus = true;
	public StartTestAction()
	{
		
	}
	
	@Override
	public void run()
	{	
		if(bStatus)
		{	
			if(DeviceInfo.getInstance().state != PhoneState.STATE_OK)
			{
				APTConsoleFactory.getInstance().APTPrint("请检测当前测试环境");
				MessageDialog.openWarning(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "提示", "请检测当前测试环境");
				return;
			}
			
			/**
			 * 输出当前手机信息
			 */
			DeviceInfo.getInstance().print();
			/**
			 * 获取当前测试配置信息
			 */
			if(!TestSenceUtil.update())
			{
				APTConsoleFactory.getInstance().APTPrint("获取测试配置失败，测试前优先打开配置页面，测试开始后，可关闭");
				return;
			}
			if(!TestSenceUtil.verifyTestSence())
			{
				return;
			}
			
			APTConsoleFactory.getInstance().APTPrint("测试参数检查完毕\r\n");
			
			APTState.getInstance().DealWithEventBefore(APTEventEnum.START_OPER);
			if(!TestTaskManager.getInstance().initTest())
			{
				APTConsoleFactory.getInstance().APTPrint("测试初始化失败\r\n");
				return;
			}
			TestTaskManager.getInstance().start();

			APTState.getInstance().DealWithEventAfter(APTEventEnum.START_OPER);
			/**
			 * 更改UI显示
			 */
			this.setText("Stop");
			this.setToolTipText("停止检测");
			this.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Constant.PLUGIN_ID,
							"icons/stop.png"));	
			bStatus = !bStatus;
			
			/**
			 * 输出当前的测试场景到控制台
			 */			
			TestSence.getInstance().print();
			
		}
		else
		{	
			// 停止测试
			APTState.getInstance().DealWithEventBefore(APTEventEnum.STOP_OPER);
			TestTaskManager.getInstance().stop();
			APTConsoleFactory.getInstance().APTPrint("测试完毕！");
			
			
			/**
			 * 恢复UI
			 */
			this.setText("Start");
			this.setToolTipText("开始检测");
			this.setImageDescriptor(AbstractUIPlugin
					.imageDescriptorFromPlugin(Constant.PLUGIN_ID,
							"icons/start.png"));
			bStatus = !bStatus;
			
			APTState.getInstance().DealWithEventAfter(APTEventEnum.STOP_OPER);
		}

	}
}	


