/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;


import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.data.PkgInfo;
import com.tencent.wstt.apt.data.TestSence;

public class APTLogFileParse {
	

	/**
	 * 写文件头
	* @Title: writeAPTLogFileHeader  
	* @Description:   
	* @param testItemIndex
	* @return 是否成功
	* boolean 
	* @throws
	 */
	public static boolean  writeAPTLogFileHeader(int testItemIndex)
	{
		if(testItemIndex != Constant.CPU_INDEX && testItemIndex != Constant.MEM_INDEX)
		{
			return false;
		}
		//构造文件头对象
		APTLogFileHeader header = new APTLogFileHeader();
		
		int pkgNumber = TestSence.getInstance().pkgInfos.size();
		header.pkgNames = new String[pkgNumber];
		
		for(int i = 0; i < pkgNumber; i++)
		{
			header.pkgNames[i] = TestSence.getInstance().pkgInfos.get(i).contents[PkgInfo.NAME_INDEX];
		}
		
		header.monitorItem = Constant.TEXT_ITEM_TITLES[testItemIndex];
		
		if(testItemIndex == Constant.CPU_INDEX)
		{
			header.dataItems = new String[1];
			header.dataItems[0] = Constant.CPU_TESTMETHOD_TITLES[TestSence.getInstance().cpuTestMethod];
		}
		else
		{
			header.dataItems = new String[Constant.ALL_MEM_KIND_COUNT];
			for(int i = 0; i < Constant.ALL_MEM_KIND_COUNT; i++)
			{
				header.dataItems[i] = Constant.MEM_ITEM_TITLES[i];
			}
		}
		
		String headerStr = header.getAPTLogFileHeaderStr();
		if(headerStr.equals("-1"))
		{
			return false;
		}
		else
		{
			WriteFileUtil.getInstance().append(Constant.TEXT_ITEM_TITLES[testItemIndex], headerStr);
			return true;
		}
	}
	
	/**
	 * 读取APTlog文件的头部
	 * @param fileName
	 * @return
	 * null；文件格式错误
	 * suc：APTLogFileHeader结构
	 */
	public static APTLogFileHeader pareseAPTLogFileHeader(String fileName)
	{
		APTLogFileHeader fileHeader = new APTLogFileHeader();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String line = "";
			
			
			if ((line = br.readLine()) == null) {
				return null;
			}
			
			String columns[] = null;
			line = line.trim();
			columns = line.split(Constant.APTLOG_KEYVALUE_SPLIT);
			if(columns == null || columns.length != 2 || !columns[0].equals(Constant.APTLOG_PKGNAME))
			{
				return null;
			}
			
			//获取进程列表
			String pkgNames[] = columns[1].split(Constant.APTLOG_FILECONTENT_SPLIT);
			if(pkgNames == null)
			{
				return null;
			}
			fileHeader.pkgNames = new String[pkgNames.length];
			System.arraycopy(pkgNames, 0, fileHeader.pkgNames, 0, pkgNames.length);
			
			
			//获取监测项名称
			if ((line = br.readLine()) == null) {
				return null;
			}
			line = line.trim();
			columns = line.split(Constant.APTLOG_KEYVALUE_SPLIT);
			if(columns == null || columns.length != 2 || !columns[0].equals(Constant.APTLOG_TESTITEM))
			{
				return null;
			}
			boolean isExist = false;
			for(int i = 0; i < Constant.TEST_ITEM_COUNT; i++)
			{
				if(columns[1].equals(Constant.TEXT_ITEM_TITLES[i]))
				{
					isExist = true;
					break;
				}
				isExist = false;
			}
			if(!isExist)
			{
				return null;
			}
			fileHeader.monitorItem = columns[1];
			
			//获取测试项数组
			if ((line = br.readLine()) == null) {
				return null;
			}
			line = line.trim();
			columns = line.split(Constant.APTLOG_KEYVALUE_SPLIT);
			if(columns == null || columns.length != 2 || !columns[0].equals(Constant.APTLOG_COLUMN_NAME))
			{
				return null;
			}
			
			fileHeader.dataItems = columns[1].split(Constant.APTLOG_FILECONTENT_SPLIT);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return fileHeader;
	}
	
	public static JfreeChartDatas getData(String fileName, APTLogFileHeader afh)
	{
		if(afh == null)
		{
			return null;
		}
		
		JfreeChartDatas result = new JfreeChartDatas();
		
		int pkgNumber = afh.pkgNames.length;
		int dataItemCount = afh.dataItems.length;
		
		result.pkgNames = new String[pkgNumber];
		//取进程名的后缀
		for(int i = 0; i < pkgNumber; i++)
		{
			int index = afh.pkgNames[i].lastIndexOf(".");;
			if(index == -1)
			{
				index = 0;
			}
			result.pkgNames[i] = afh.pkgNames[i].substring(index+1, afh.pkgNames[i].length());	
		}
		result.monitorItem = afh.monitorItem;
		result.dataItems = new String[dataItemCount];
		System.arraycopy(afh.dataItems, 0, result.dataItems, 0, dataItemCount);
		
		
		result.tsDataList = new ArrayList<TSData>();
	
		for(int i = 0; i < pkgNumber*dataItemCount; i++)
		{
			TSData tsData = new TSData();
			result.tsDataList.add(tsData);
		}
		
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String line = "";
			for(int i = 0; i < 3; i++)
			{
				br.readLine();
			}
			Date lastDate = null;
			while((line = br.readLine()) != null)
			{
				String []datas = line.split(Constant.APTLOG_FILECONTENT_SPLIT);
				if(datas == null || datas.length != pkgNumber*dataItemCount+1)
				{
					continue;
				}
				
				Date date = Constant.SIMPLE_DATE_FORMAT_MILLISECOND.parse(datas[0]);
				/**
				 * 过滤相同时间点的数据
				 */
				if(date.equals(lastDate))
				{
					continue;
				}
				lastDate = date;
				RegularTimePeriod time = new Millisecond(date);
				for(int i = 0; i < pkgNumber*dataItemCount; i++)
				{
					float value = Float.parseFloat(datas[i+1]);
					DataUnit du = new DataUnit(time, value);
					result.tsDataList.get(i).dataUnitList.add(du);
				}	
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return result;
	}
	
	/**
	 * APTLog 文件格式
	 *
	 */
	public static class APTLogFileHeader
	{
		public String pkgNames[];
		public String monitorItem;
		public String dataItems[];
		
		public APTLogFileHeader()
		{	
		}
		
		public String getAPTLogFileHeaderStr()
		{
			if(pkgNames == null || dataItems == null)
			{
				return "-1";
			}
			String strAPTLogHeader = Constant.APTLOG_PKGNAME + Constant.APTLOG_KEYVALUE_SPLIT;
			//进程名称
			for(int i = 0; i < pkgNames.length; i++)
			{
				if(i == pkgNames.length-1)
				{
					strAPTLogHeader += pkgNames[i] + Constant.APTLOG_FILECONTENT_NEWLINE;
				}
				else
				{
					strAPTLogHeader += pkgNames[i] + Constant.APTLOG_FILECONTENT_SPLIT;
				}
			}
			//测试项名称
			strAPTLogHeader += (Constant.APTLOG_TESTITEM + Constant.APTLOG_KEYVALUE_SPLIT + monitorItem + Constant.APTLOG_FILECONTENT_NEWLINE);
			
			//数据项名称
			strAPTLogHeader += Constant.APTLOG_COLUMN_NAME + Constant.APTLOG_KEYVALUE_SPLIT;
			for(int i = 0; i < dataItems.length; i++)
			{
				if(i == dataItems.length-1)
				{
					strAPTLogHeader += dataItems[i] + Constant.APTLOG_FILECONTENT_NEWLINE;
				}
				else
				{
					strAPTLogHeader += dataItems[i] + Constant.APTLOG_FILECONTENT_SPLIT;
				}
			}
			return strAPTLogHeader;
		}
	}
	
	/**
	 * 一次测试的所有数据，对应一个测试log文件
	* @ClassName: JfreeChartDatas  
	* @Description: TODO  
	* @date 2013-5-6 下午8:57:42  
	*
	 */
	public static class JfreeChartDatas
	{
		public String pkgNames[];
		public String monitorItem;
		public String dataItems[];
		public List<TSData> tsDataList;
	}
	
	/**
	 * 一条曲线的所有数据
	* @ClassName: TSData  
	* @Description: TODO  
	* @date 2013-5-6 下午8:57:31  
	*
	 */
	public static class TSData
	{
		public List<DataUnit> dataUnitList = new ArrayList<DataUnit>();
	}
	
	/**
	 * 一个数据点
	* @ClassName: DataUnit  
	* @Description: TODO  
	* @date 2013-5-6 下午8:57:24  
	*
	 */
	public static class DataUnit
	{
		public RegularTimePeriod time;
		public Number value;	
		public DataUnit(RegularTimePeriod time, Number value)
		{
			this.time = time;
			this.value = value;
		}
	}

}


