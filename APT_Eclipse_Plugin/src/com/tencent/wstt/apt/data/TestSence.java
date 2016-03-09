/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.data;

import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.apt.console.APTConsoleFactory;


/**
* @Description 测试场景结构体，eg测试项、被测进程列表等 
* @date 2013年11月10日 下午5:58:15 
*
 */
public class TestSence
{
	private static TestSence instance = null;
	private TestSence()
	{
		
	}
	
	public static TestSence getInstance()
	{
		if(instance == null)
		{
			instance = new TestSence();
		}
		return instance;
	}
	
	
	public  List<PkgInfo> pkgInfos = new ArrayList<PkgInfo>();
	
	public int[] itemTestPeriod = new int[Constant.TEST_ITEM_COUNT];
	public boolean[] itemTestSwitch = new boolean[Constant.TEST_ITEM_COUNT];
	public boolean isTestJiffies = false;
	
	public int cpuTestMethod;
	
	public boolean isDumpHprof = false;
	public int dumpHprofThreshold = 0;

	//当前保存目录
	public  String curDir = "";
	//当前插件运行系统
	//public  String OSName = "Windows 7";
	
	//数据buf的长度，每N条写一次文件，暂时不对外控制
	public static final int[] bufThreshold = new int[]{60, 60};
	

	public int getIndex(String pkgNames)
	{
		for(int i = 0; i < pkgInfos.size(); i++)
		{
			if(pkgNames.equalsIgnoreCase(pkgInfos.get(i).contents[PkgInfo.NAME_INDEX]))
			{
				return i;
			}
		}
		return -1;
	}
	
	//返回当前测试场景信息的字符串
	public void print()
	{	
		String str = "当前测试场景：\r\n";
		str += "CPU测试方法：" + Constant.CPU_TESTMETHOD_TITLES[cpuTestMethod] + "\r\n";
		
		for(int i = 0; i < pkgInfos.size(); i++)
		{
			str += "测试进程名：" + pkgInfos.get(i).contents[PkgInfo.NAME_INDEX] + " PID：" + pkgInfos.get(i).contents[PkgInfo.PID_INDEX] + "\r\n";
		}
		
		
		for(int i = 0; i < Constant.TEST_ITEM_COUNT; i++)
		{
			if(itemTestSwitch[i])
			{
				str += "测试项：" + Constant.TEXT_ITEM_TITLES[i] + " " + "测试周期：" + itemTestPeriod[i] + "ms" + "\r\n";
			}
		}
		
		APTConsoleFactory.getInstance().APTPrint(str);
	}
}
