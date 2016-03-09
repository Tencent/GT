/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.action;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.tencent.wstt.apt.cmdparse.CMDExecute;
import com.tencent.wstt.apt.cmdparse.GetPkgInfosByPsUtil;
import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PCInfo;
import com.tencent.wstt.apt.data.PkgInfo;


/**
 * @Description: 获取smap文件
 * @author: Administrator  
 * @date: 2013年12月31日 下午9:43:06
 */
public class GetSMAPInfoAction extends Action {

	public TableViewer tableViewer;
	public GetSMAPInfoAction(TableViewer viewer)
	{
		this.tableViewer = viewer;
	}
	
	@Override
	public void run() {
		
		/**
		 * TODO 检查当前是否root
		 */
		
		/**
		 * 获取进程名称和PID，并获取当前时间
		 */
		TableItem[] selectData = tableViewer.getTable().getSelection();
		if(selectData == null || selectData.length == 0)
		{
			return;
		}
		
		PkgInfo itemData = (PkgInfo)selectData[0].getData();
		final String pkgName = itemData.contents[PkgInfo.NAME_INDEX];
		//这样就确保了pid是最新的，不用刷新进程列表
		final String pid = GetPkgInfosByPsUtil.getPid(pkgName);
		if(pid == null)
		{
			APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "对应进程不存在");
			return;
		}
		
		/**
		 * 执行cat /proc/pid/smap命令，并将输出写文件
		 */
		Thread thread = new Thread(new Runnable() {		
			@Override
			public synchronized void run() {
				Date curDate = new Date(System.currentTimeMillis());
				String curDateStr = Constant.SIMPLE_DATE_FORMAT_SECOND.format(curDate);
				String fileName = pkgName.replace(":", "-") + "_" + pid + "_smap_" +  curDateStr + ".txt";
				String filePath = Constant.SMAPS_LOG_PATH_ON_PC + File.separator + fileName;
				FileWriter fw = null;
				try {
					//首先检查目录是否存在
					File dirFile = new File(Constant.SMAPS_LOG_PATH_ON_PC);
					if(!dirFile.exists())
					{
						//mkdirs 自动创建不存在的父目录
						//mkdir 不会自动创建不存在的父目录
						if(!dirFile.mkdirs())
						{
							APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "创建目录失败" + Constant.SMAPS_LOG_PATH_ON_PC);
							return;
						}
					}
					File file = new File(filePath);
					if(!file.exists())
					{
						if(!file.createNewFile())
						{
							APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "创建文件失败" + filePath);
							return;
						}
					}
					
					fw = new FileWriter(file);
				} catch (IOException e) {
					e.printStackTrace();
					APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_CRASH] + "文件创建异常:" + filePath);
					return;
				}
				
				String cmdOutputStr = CMDExecute.runCMD(PCInfo.adbShell + " su -c cat /proc/" + pid + "/smaps");	
				try {
					fw.write(cmdOutputStr);
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
					MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay()
							.getActiveShell(), "提示", "写文件失败_" + filePath);
					APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_CRASH] + "获取smap文件异常");
				}
				APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_INFO] + "获取smap文件完成:" + filePath);
			}
		});
		
		thread.start();	
	}
	
}
