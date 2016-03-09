/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;


/**
* @Description 保存APT测试数据工具类 
* @date 2013年11月10日 下午6:00:28 
*
 */
public class WriteFileUtil {

	private static WriteFileUtil instance = null;
	
	private Map<String, WriteFileStruct> wfStructMap;

	private WriteFileUtil()
	{
		wfStructMap = new HashMap<String, WriteFileStruct>();
	}
	
	public static WriteFileUtil getInstance()
	{
		if(instance == null)
		{
			instance = new WriteFileUtil();
		}
		return instance;
	}
	
	//初始化
	public boolean openWriteFile(String dirName, String tag, int bufThreshold)
	{
		//说明已经存在相同tag的WF，不能重新创建
		if(wfStructMap.get(tag) != null)
		{
			APTConsoleFactory.getInstance().APTPrint("已存在同名文件，不能重复打开");
			return false;
		}
		
		String dirPath = Constant.LOG_FOLDER_ON_PC + File.separator + dirName;
		File writeFile = new File(dirPath);
		//if(!writeFile.exists())
		if(!writeFile.isDirectory())
		{
			if(!writeFile.mkdirs())
			{
				APTConsoleFactory.getInstance().APTPrint("创建目录" + dirPath + "失败");
				APTConsoleFactory.getInstance().APTPrint("请检查创建失败的路径中是否已存在相同名字的文件，删除后重新测试");
				return false;
			}
			else
			{
				APTConsoleFactory.getInstance().APTPrint("创建目录" + dirPath + "成功");
			}	
		}
		
		
		
		String fileName = dirPath + File.separator + Constant.APTLOG_FILENAME_PREFIX + Constant.APTLOG_FILENAME_SPLIT + tag + Constant.APTLOG_FILENAME_SUFFIX;
		try {
			FileWriter fw = new FileWriter(fileName);
			StringBuilder sb = new StringBuilder();
			WriteFileStruct wfs = new WriteFileStruct(fw, sb, bufThreshold, 0, fileName);
			wfStructMap.put(tag, wfs);
			
		} catch (IOException e) {
			APTConsoleFactory.getInstance().APTPrint("创建文件失败" + fileName);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//具体写文件
	public boolean append(String tag, String content)
	{
		WriteFileStruct curWFStruct = wfStructMap.get(tag);
		
		if(curWFStruct == null)
		{
			return false;
		}
		curWFStruct.sb.append(content);
		curWFStruct.bufSize++;
		
		if(curWFStruct.bufSize >= curWFStruct.bufThreshold)
		{
			try {
				curWFStruct.fw.write(curWFStruct.sb.toString());
				curWFStruct.sb.setLength(0);
				curWFStruct.bufSize = 0;
			} catch (IOException e) {
				APTConsoleFactory.getInstance().APTPrint("写文件失败" + tag);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	
	public boolean closeWriteFile(String tag) 
	{
		WriteFileStruct curWFStruct = wfStructMap.get(tag);
		if (curWFStruct == null) {
			return false;
		}
		try {
			curWFStruct.fw.write(curWFStruct.sb.toString());
			curWFStruct.fw.close();
			APTConsoleFactory.getInstance().APTPrint("保存文件" + curWFStruct.fileName);
			wfStructMap.remove(tag);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * 关闭所有文件
	 * @return
	 */
	public boolean closeAll()
	{
		boolean result = true;
		for(Entry<String, WriteFileStruct> entry : wfStructMap.entrySet())
		{
			WriteFileStruct wfs = entry.getValue();
			try {
				wfs.fw.write(wfs.sb.toString());
				wfs.fw.close();
				APTConsoleFactory.getInstance().APTPrint("保存文件" + wfs.fileName);
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
				continue;
			}
		}
		wfStructMap.clear();
		return result;
	}
	
	/**
	 * 写文件的结构体，包含文件句柄，缓冲区，缓冲区大小，当前缓冲区数据数量
	 *
	 */
	private class WriteFileStruct
	{
		public FileWriter fw;
		public StringBuilder sb;
		public int bufThreshold;
		public int bufSize;
		public String fileName;
		
		public WriteFileStruct(FileWriter fw, StringBuilder sb, int bufThreshold, int bufSize, String fileName)
		{
			this.fw = fw;
			this.sb = sb;
			this.bufThreshold = bufThreshold;
			this.bufSize = bufSize;
			this.fileName = fileName;
		}
	}
}
