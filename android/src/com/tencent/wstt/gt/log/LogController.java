/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 * 
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 * 
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tencent.wstt.gt.log;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.StringUtil;

public class LogController extends AbsLogController {

	private File curFile;
	private String curLogFolderName = Env.S_ROOT_LOG_FOLDER;
	private File curLogFolder = new File(curLogFolderName);
	
	// 用于保存过滤下拉列表的状态
	private int curSelectedLevel;
	private String sCurSelectedTag = "";
	private String sCurSelectedMsg = "";
	
	private LinkedList<String> msgHistory = new LinkedList<String>();
	private LinkedList<String> curShowDownMsgList = new LinkedList<String>();
	
	private Map<File, FileWriter> tempLogFileWriterMap;
	private Set<String> tempLogStartingSet;
	private boolean allowAutoSave;  // 是否自动保存日志
	private boolean saveDefaultSeg; // 保存日志的时候是否保存GT日志前缀
	
	public LogController()
	{
		super();
		// TODO 用并发的数据结果更保险一些，待测试观察
//		tempLogFileWriterMap = new ConcurrentHashMap<File, FileWriter>();
		tempLogFileWriterMap = Collections.synchronizedMap(new HashMap<File, FileWriter>());
		tempLogStartingSet = Collections.synchronizedSet(new HashSet<String>());

		/*
		 * 初始化curFile
		 */
		setLastestLogFileAsCurFile();

		allowAutoSave = GTPref.getGTPref().getBoolean(GTPref.LOG_AUTOSAVE_SWITCH, false);
		try {
			if (allowAutoSave)
			{
				// 要将自动保存目录加到目录容器中,参考setAutoSave的逻辑
				addTempLog(curFile.getAbsolutePath(), true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * 从2.2.1版本开始，强制要求日志符合logcat格式
		 */
//		saveDefaultSeg = GTPref.getGTPref().getBoolean(GTPref.LOG_SAVE_DEFAULT_SEG, true);
		saveDefaultSeg = true;
	}
	
	/*
	 * ===================过滤下拉状态保存相关getter和setter=====================
	 */
	public int getCurSelectedLevel() {
		return curSelectedLevel;
	}

	public void setCurSelectedLevel(int curSelectedLevel) {
		this.curSelectedLevel = curSelectedLevel;
	}

	public String getsCurSelectedTag() {
		return sCurSelectedTag;
	}

	public void setsCurSelectedTag(String sCurSelectedTag) {
		this.sCurSelectedTag = sCurSelectedTag;
	}

	public String getsCurSelectedMsg() {
		return sCurSelectedMsg;
	}

	public void setsCurSelectedMsg(String sCurSelectedMsg) {
		this.sCurSelectedMsg = sCurSelectedMsg;
	}

	public LinkedList<String> getMsgHistory() {
		return msgHistory;
	}

	public void setMsgHistory(LinkedList<String> msgHistory) {
		this.msgHistory = msgHistory;
	}

	public LinkedList<String> getCurShowDownMsgList() {
		return curShowDownMsgList;
	}

	public void setCurShowDownMsgList(LinkedList<String> curShowDownMsgList) {
		this.curShowDownMsgList = curShowDownMsgList;
	}

	/*
	 * =============================保存日志相关方法=============================
	 */
	public void changeCurrentLogFolder(String appName)
	{
		curLogFolderName = Env.S_ROOT_LOG_FOLDER + appName + "/";
		curLogFolder = new File(curLogFolderName);
		if (! curLogFolder.exists())
		{
			curLogFolder.mkdirs();
		}
		
		/*
		 * 初始化curFile
		 */
		removeTempLog(curFile.getAbsolutePath());
		setLastestLogFileAsCurFile();

		if(allowAutoSave)
		{
			try {
				addTempLog(curFile.getAbsolutePath(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void beforeAutoSave(StringBuffer bf)
	{
		if (! allowAutoSave)
		{
			return;
		}

		// 如果当前文件被用户从文件系统删掉了等情况，重建当前文件
		if(!curFile.exists())
		{
			removeTempLog(curFile.getAbsolutePath());
			try {
				addTempLog(curFile.getAbsolutePath(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 如果当前文件写满了，就按文件名去取下一个文件为当前文件
		else if (curFile.length() >= LogUtils.CAPACITY)
		{
			removeTempLog(curFile.getAbsolutePath());
			switch2NextLogFile();
			try {
				addTempLog(curFile.getAbsolutePath(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setLastestLogFileAsCurFile()
	{
		// 遍历日志文件夹中的日志文件，取最新的为当前写目标
		if (!curLogFolder.exists())
		{
			curLogFolder.mkdirs();
		}

		// 过滤出合法的日志文件
		File[] files = curLogFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.matches(LogUtils.LOG_FILE_MATCHE);
			}
		});

		// 按修改时间排序
		if (null != files && files.length > 0)
		{
			Arrays.sort(files, 0, files.length, new Comparator<File>() {
				@Override
				public int compare(File lhs, File rhs) {
					return lhs.lastModified() <= rhs.lastModified() ? 1 : -1;
				}

			});
			
			// 取最新的文件为当前文件
			curFile = files[0];
		}
		else
		{
			curFile = new File(curLogFolder, 00 + LogUtils.LOG_POSFIX);
		}
	}
	
	private void switch2NextLogFile()
	{
		File newFile = null;
		int newFileSeq = 0;
		int curFileNameSeq = 0;

		String curFileName = curFile.getName();
		
		if (curFileName.length() > 2
				&& StringUtil.isNumeric(curFileName.substring(0, 2)))
		{
			curFileNameSeq = Integer.parseInt(curFile.getName().substring(0, 2));
		}
		else
		{
			curFileNameSeq = Integer.parseInt(curFile.getName().substring(0, 1));
		}

		if (curFileNameSeq < 99)
		{
			newFileSeq = curFileNameSeq + 1;
		}
		if (newFileSeq < 10)
		{
			newFile = new File(curLogFolder, "0" + newFileSeq + LogUtils.LOG_POSFIX);
		}
		else
		{
			newFile = new File(curLogFolder, newFileSeq + LogUtils.LOG_POSFIX);
		}

		if (newFile.exists())
		{
			newFile.delete();
		}
		try {
			newFile.createNewFile();
			curFile = newFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 只由TempLogConsumer调用，考虑移过去
	 * @param path
	 * @param append 此日志是否追加
	 */
	public void addTempLog(String path, boolean append) throws IOException
	{
		if (null == path)
		{
			return;
		}
		
		File f = null;
		if (FileUtil.isPathStringValid(path))
		{
			String validPath = FileUtil.convertValidFilePath(path, LogUtils.LOG_POSFIX);
			if (FileUtil.isPath(validPath))
			{
				f = new File(validPath);
			}
			else
			{
				f = new File(Env.ROOT_LOG_FOLDER, validPath);
			}
			
			try
			{
				if (! f.exists())
				{
					f.createNewFile();
				}

				FileWriter fw = new FileWriter(f, append);
				tempLogFileWriterMap.put(f, fw);
			}
			catch(IOException e)
			{
				throw new IOException();
			}
		}
	}
	
	/**
	 * 只由TempLogConsumer调用，考虑移过去
	 * @param path
	 */
	public void removeTempLog(String path)
	{
		if (path == null)
		{
			return;
		}
		
		File f = null;
		if (FileUtil.isPathStringValid(path))
		{
			String validPath = FileUtil.convertValidFilePath(path, LogUtils.LOG_POSFIX);
			if (FileUtil.isPath(validPath))
			{
				f = new File(validPath);
			}
			else
			{
				f = new File(Env.ROOT_LOG_FOLDER, validPath);
			}
			
			FileWriter fr = tempLogFileWriterMap.remove(f);
			FileUtil.closeWriter(fr);
		}
	}
	
	/**
	 * 对path代表的文件执行该方法前，需要保证已执行过removeTempLog(path)操作关闭输出流，
	 * 否则可能文件删除失败。
	 * @param path
	 * @throws CleanTempLogException 
	 */
	public void cleanTempLog(String path) throws CleanTempLogException
	{
		File f = null;
		if (FileUtil.isPathStringValid(path))
		{
			String validPath = FileUtil.convertValidFilePath(path, LogUtils.LOG_POSFIX);
			
			if (FileUtil.isPath(validPath))
			{
				f = new File(validPath);
			}
			else
			{
				f = new File(Env.ROOT_LOG_FOLDER, validPath);
			}
			
			if (f.exists())
			{
				// 抛出自定义异常才合理
				boolean result = f.delete();
				if (! result)
				{
					throw new CleanTempLogException();
				}
			}
		}
	}
	
	public void setAutoSave(boolean flag)
	{
		allowAutoSave = flag;
		GTPref.getGTPref().edit().putBoolean(GTPref.LOG_AUTOSAVE_SWITCH, flag).commit();
		
		if (flag)
		{
			try {
				addTempLog(curFile.getAbsolutePath(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			removeTempLog(curFile.getAbsolutePath());
		}
	}
	
	public boolean getAutoSave()
	{
		return allowAutoSave;
	}
	
	public void setSaveDefaultSeg(boolean flag)
	{
		saveDefaultSeg = flag;
		GTPref.getGTPref().edit().putBoolean(GTPref.LOG_SAVE_DEFAULT_SEG, flag).commit();
	}
	
	public boolean getSaveDefaultSeg()
	{
		return saveDefaultSeg;
		
	}
	
	public boolean hasTempLog()
	{
		return tempLogStartingSet.size() > 0;
	}
	
	public void setTempLogStarting(String fileName)
	{
		tempLogStartingSet.add(fileName);
	}
	
	public void reudceTempLogStarting(String fileName)
	{
		tempLogStartingSet.remove(fileName);
	}
	
	/**
	 * 日志的消费者就是单个线程，所以目前本方法可以不针对LogBuff加锁
	 * @param sb
	 */
	public void add2Save(StringBuffer sb)
	{
		beforeAutoSave(sb);
		
		synchronized (tempLogFileWriterMap) {
			for (Entry<File, FileWriter> entry : tempLogFileWriterMap.entrySet())
			{
				LogUtils.writeBuff(sb, entry.getKey(), entry.getValue());
			}
		}
	}
	
	/**
	 * 当出现文件被用户手动删除的情况，需要更新
	 */
	
	public void endAllLog()
	{
		synchronized (tempLogFileWriterMap) {
			for (FileWriter writer : tempLogFileWriterMap.values())
			{
				FileUtil.closeWriter(writer);
			}
		}
	}
}
