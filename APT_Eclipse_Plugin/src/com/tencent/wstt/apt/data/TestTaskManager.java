/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.eclipse.ui.PlatformUI;

import com.tencent.wstt.apt.chart.CPUProvider;
import com.tencent.wstt.apt.chart.ChartObserver;
import com.tencent.wstt.apt.chart.DataProvider;
import com.tencent.wstt.apt.chart.FileObserver;
import com.tencent.wstt.apt.chart.HprofDumpObserver;
import com.tencent.wstt.apt.chart.JiffiesProviderNew;
import com.tencent.wstt.apt.chart.JiffiesTableObserver;
import com.tencent.wstt.apt.chart.MemProvider;
import com.tencent.wstt.apt.chart.PidUpdateObserver;
import com.tencent.wstt.apt.chart.TableAllDataObserver;
import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.file.APTLogFileParse;
import com.tencent.wstt.apt.file.WriteFileUtil;
import com.tencent.wstt.apt.ui.views.CPUView;
import com.tencent.wstt.apt.ui.views.DevicesView;
import com.tencent.wstt.apt.ui.views.MemoryView;
import com.tencent.wstt.apt.util.GetCurCheckedStateUtil;
import com.tencent.wstt.apt.util.TableViewFilter;

/**
* @Description 测试任务管理类，初始化测试、添加测试任务等 
* @date 2013年11月10日 下午5:59:12 
*
 */
public class TestTaskManager {
	private static TestTaskManager instance = null;
	
	public List<TestTask> tasks = new ArrayList<TestTask>();
	public List<Timer> timerPool = new ArrayList<Timer>();
	
	private TestTaskManager()
	{
		
	}
	
	public static TestTaskManager getInstance()
	{
		if(instance == null)
		{
			instance = new TestTaskManager();
		}
		return instance;
	}
	
	/**
	* @Description 添加测试任务 
	* @param @param tt   
	* @return void 
	* @throws
	 */
	private void addTestTask(TestTask tt)
	{
		if(tt == null)
		{
			return;
		}
		tasks.add(tt);
		timerPool.add(new Timer(true));
	}
	
	private void clear()
	{
		tasks.clear();
		timerPool.clear();
	}
	
	/**
	* @Title: initTest  
	* @Description: 根据测试场景配置来初始化测试   
	* void 
	* @throws
	 */
	public boolean initTest()
	{
		clear();
		//获取进程列表
		int pkgNumber = TestSence.getInstance().pkgInfos.size();
		String []pkgNames = new String[pkgNumber];
		String []pkgAbbreviation = new String[pkgNumber];
		
		//取进程名的后缀
		for(int i = 0; i < pkgNumber; i++)
		{
			pkgNames[i] = TestSence.getInstance().pkgInfos.get(i).contents[PkgInfo.NAME_INDEX];
			int index = pkgNames[i].lastIndexOf(".");;
			pkgAbbreviation[i] = pkgNames[i].substring(index+1, pkgNames[i].length());	
		}
		
		CPUView cpuViewPart  = (CPUView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CPUView.ID);
		MemoryView memViewPart  = (MemoryView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MemoryView.ID);
		
		
		//设置当前测试场景的结果保存目录
		Date curDate = new Date(System.currentTimeMillis());
		TestSence.getInstance().curDir = Constant.SIMPLE_DATE_FORMAT_SECOND.format(curDate);
		
		APTConsoleFactory.getInstance().APTPrint("测试开始时间：" + TestSence.getInstance().curDir);
		
		if(TestSence.getInstance().itemTestSwitch[Constant.CPU_INDEX])
		{
			if(cpuViewPart==null)
			{
				return false;
			}
			//打开文件
			if(!(WriteFileUtil.getInstance().openWriteFile(TestSence.getInstance().curDir, Constant.TEXT_ITEM_TITLES[Constant.CPU_INDEX], TestSence.bufThreshold[0])))
			{
				APTConsoleFactory.getInstance().APTPrint("创建CPU结果文件失败");
				return false;
			}
			
			//写文件头
			if(!APTLogFileParse.writeAPTLogFileHeader(Constant.CPU_INDEX))
			{
				APTConsoleFactory.getInstance().APTPrint("写CPU文件头部失败");
				return false;
			}
			
			cpuViewPart.cpuRealTimeChart.initDataset(pkgAbbreviation, new String[]{Constant.CPU_ITEM_TITLES[Constant.CPU_PERSENT_INDEX]});
			DataProvider dataProvider = new CPUProvider(pkgNames, DeviceInfo.getInstance().androidVersion, TestSence.getInstance().cpuTestMethod);
			
			dataProvider.attach(new ChartObserver(cpuViewPart.cpuRealTimeChart));
			dataProvider.attach(new TableAllDataObserver(cpuViewPart.cpuViewer,pkgAbbreviation, new String[]{Constant.CPU_ITEM_TITLES[Constant.CPU_PERSENT_INDEX]}));
			dataProvider.attach(new FileObserver(Constant.TEXT_ITEM_TITLES[Constant.CPU_INDEX]));
			TestTask tt = new TestTask(dataProvider, TestSence.getInstance().itemTestPeriod[Constant.CPU_INDEX]);
			addTestTask(tt);
			
			cpuViewPart.setCpuTableViewerFilter(new TableViewFilter(pkgAbbreviation, new String[]{Constant.CPU_ITEM_TITLES[Constant.CPU_PERSENT_INDEX]}));
			
			if(TestSence.getInstance().isTestJiffies)
			{
				DevicesView dv = (DevicesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DevicesView.ID);
				if(dv==null)
				{
					return false;
				}
				List<PkgInfo> pkgInfos = TestSence.getInstance().pkgInfos;
				String []pids = new String[pkgInfos.size()];
				for(int i = 0; i < pids.length; i++)
				{
					pids[i] = pkgInfos.get(i).contents[PkgInfo.PID_INDEX];
				}
				
				
				DataProvider jiffiesDataProvider = new JiffiesProviderNew(pkgNumber);
				cpuViewPart.setJiffiesTableViewerFilter(new TableViewFilter(pkgAbbreviation, new String[]{Constant.CPU_ITEM_TITLES[Constant.CPU_JIFFIES_INDEX]}));
				//jiffiesDataProvider.attach(new TableAllDataObserver(cpuViewPart.jiffiesViewer, pkgAbbreviation, new String[]{Constant.CPU_ITEM_TITLES[Constant.CPU_JIFFIES_INDEX]}));
				jiffiesDataProvider.attach(new JiffiesTableObserver(cpuViewPart.jiffiesViewer, pkgAbbreviation));
				jiffiesDataProvider.attach(new PidUpdateObserver(dv.targetPkgTableViewer, pids));
				TestTask jiffiesTT = new TestTask(jiffiesDataProvider, TestSence.getInstance().itemTestPeriod[Constant.CPU_INDEX]);
				addTestTask(jiffiesTT);
			}
			APTConsoleFactory.getInstance().APTPrint("CPU测试Ready");
		}
		//内存测试
		if(TestSence.getInstance().itemTestSwitch[Constant.MEM_INDEX])
		{
			if(memViewPart==null)
			{
				return false;
			}
			//打开文件
			if(!(WriteFileUtil.getInstance().openWriteFile(TestSence.getInstance().curDir, Constant.TEXT_ITEM_TITLES[Constant.MEM_INDEX], TestSence.bufThreshold[1])))
			{
				APTConsoleFactory.getInstance().APTPrint("创建内存结果文件失败");
				return false;
			}
			
			//写文件头
			if(!APTLogFileParse.writeAPTLogFileHeader(Constant.MEM_INDEX))
			{
				APTConsoleFactory.getInstance().APTPrint("写内存文件头部失败");
				return false;
			}
			
			//填充数据
			memViewPart.memRealTimeChart.initDataset(pkgAbbreviation, Constant.MEM_ITEM_TITLES);
			memViewPart.setTableViewerFilter(new TableViewFilter(pkgAbbreviation, Constant.MEM_ITEM_TITLES));
			
			//初始化内存视图的数据提供者
			DataProvider dataProvider = new MemProvider(pkgNames, DeviceInfo.getInstance().androidVersion);
			//主持内存数据的观察者
			dataProvider.attach(new ChartObserver(memViewPart.memRealTimeChart));
			dataProvider.attach(new TableAllDataObserver(memViewPart.viewer, pkgAbbreviation, Constant.MEM_ITEM_TITLES));
			dataProvider.attach(new FileObserver(Constant.TEXT_ITEM_TITLES[Constant.MEM_INDEX]));
			
			if(TestSence.getInstance().isDumpHprof)
			{
				dataProvider.attach(new HprofDumpObserver(pkgNames));
			}
			
			//构建测试任务
			TestTask tt = new TestTask(dataProvider, TestSence.getInstance().itemTestPeriod[Constant.MEM_INDEX]);
			addTestTask(tt);
			
			memViewPart.setTableViewerFilter(new TableViewFilter(pkgAbbreviation, Constant.MEM_ITEM_TITLES));
			
			APTConsoleFactory.getInstance().APTPrint("内存测试Ready");
		}
		
		APTConsoleFactory.getInstance().APTPrint("开始测试更新");
		GetCurCheckedStateUtil.update();
		return true;
	}
	
	/**
	 * 启动测试
	* @Title: start  
	* @Description:    
	* void 
	* @throws
	 */
	public void start()
	{
		for(int i = 0; i < timerPool.size(); i++)
		{
			timerPool.get(i).schedule(tasks.get(i).task, 0, tasks.get(i).period);
		}
	}
	
	/**
	 * 停止测试
	* @Title: stop  
	* @Description:    
	* void 
	* @throws
	 */
	public void stop()
	{
		for(int i = 0; i < timerPool.size(); i++)
		{
			timerPool.get(i).cancel();
		}
		clear();
		
		if(!WriteFileUtil.getInstance().closeAll()) 
		{
			APTConsoleFactory.getInstance().APTPrint("保存文件失败");
			//return;
		}
		
		CPUView cpuViewPart  = (CPUView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CPUView.ID);
		MemoryView memViewPart  = (MemoryView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MemoryView.ID);
		
		if(cpuViewPart==null||memViewPart==null)
		{
			return;
		}
		
		if(TestSence.getInstance().itemTestSwitch[Constant.CPU_INDEX])
		{
			String path = Constant.LOG_FOLDER_ON_PC + File.separator + TestSence.getInstance().curDir + File.separator + "APT_CPU.png";
			if(!cpuViewPart.cpuRealTimeChart.saveToPNG(path))
			{
				APTConsoleFactory.getInstance().APTPrint("图片保存失败");
			}
		}
		
		
		if(TestSence.getInstance().itemTestSwitch[Constant.MEM_INDEX])
		{
			String path = Constant.LOG_FOLDER_ON_PC + File.separator + TestSence.getInstance().curDir + File.separator + "APT_Mem.png";
			if(!memViewPart.memRealTimeChart.saveToPNG(path))
			{
				APTConsoleFactory.getInstance().APTPrint("图片保存失败");
			}
		}
	}

}
