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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.ui.model.LogEntry;
import com.tencent.wstt.gt.ui.model.MatchedEntry;
import com.tencent.wstt.gt.utils.GTUtils;

public class GTLogInternal {
	/*
	 * 注意在array.xml中有对应，需要同时改；客户端中AbsConnState类里也要对应
	 */
	public static final int LOG_VERBOSE = 0; // 全部
	public static final int LOG_DEBUG = 1; // 调试
	public static final int LOG_INFO = 2; // 信息
	public static final int LOG_WARNING = 3; // 警告
	public static final int LOG_ERROR = 4; // 错误
	public static int Log_Level = LOG_VERBOSE; // 显示的log级别，起到开关的作用
	
	// 因为Activity的生命周期不太靠谱，所以在这里保存上次的文件名
	private static String lastSaveLog = "GTLog";

	// 日志的日期部分长度是19
	private static final int TIMESTAMP_LENGTH = 19;
	// 去掉日期，logcat的匹配模式
	private static Pattern logPattern = Pattern.compile(
			// level
			"(\\w)" +
			"/" +
			// tag
			"([^(]+)" +
			"\\(\\s*" +
			// pid
			"(\\d+)" +
			// optional weird number that only occurs on ZTE blade
			"(?:\\*\\s*\\d+)?" +
			"\\): ");

	// 常规日志的控制
	private static LogController logController = new LogController();
	private static LogTaskConsumer logTaskConsumer;
	private static TempLogConsumer tempLogConsumer = new TempLogConsumer(logController);
	private static NormalLogAdapter curLogAdapter;
	static {
		tempLogConsumer.start();
	}
	
	// 日志搜索控制
	private static LogSearchController logSearchController = new LogSearchController();
	
	private static boolean log_enable_flag =
			GTPref.getGTPref().getBoolean(GTPref.LOG_MASTER_SWITCH, true);
	
	public static void setCurLogAdapter(NormalLogAdapter curLogAdapter)
	{
		GTLogInternal.curLogAdapter = curLogAdapter;
	}
	
	public NormalLogAdapter getCurLogAdapter()
	{
		return curLogAdapter;
	}
	
	public static List<LogEntry> getNormalLogList()
	{
		return logController.getShowLogList();
	}
	public static LogEntry[] getCurNormalLogs()
	{
		LogEntry[] result = {};
		GTLogInternal.getLogListReadLock().lock();
		List<LogEntry> list = logController.getShowLogList();
		result = list.toArray(result);
		GTLogInternal.getLogListReadLock().unlock();
		
		return result;
	}
	public static LogEntry[] getCurFilteredLogs()
	{
		LogEntry[] result = {};
		GTLogInternal.getLogListReadLock().lock();
		List<LogEntry> list = logController.getFilterdLogList();
		result = list.toArray(result);
		GTLogInternal.getLogListReadLock().unlock();
		
		return result;
	}
	public static void setFilterdLogList(List<LogEntry> list)
	{
		logController.setFilterdLogList(list);
	}
	public static int getNormalLogLastFilterEndLocation()
	{
		return logController.getLastFilterEndLocation();
	}
	public static void resetNormalLogLastFilterEndLocation()
	{
		logController.resetLastFilterEndLocation();
	}
	public static List<String> getTags()
	{
		return logController.getShowTags();
	}
	/*
	 * 将ShowLogList的读锁返回，交给调用端维护
	 */
	public static Lock getLogListReadLock()
	{
		return logController.lock.readLock();
	}
	
	// 日志过滤下拉菜单相关接口
	public static int getCurFilterLevel() {
		return logController.getCurSelectedLevel();
	}

	public static void setCurFilterLevel(int curSelectedLevel) {
		logController.setCurSelectedLevel(curSelectedLevel);
	}

	public static String getCurFilterTag() {
		return logController.getsCurSelectedTag();
	}

	public static void setCurFilterTag(String sCurSelectedTag) {
		logController.setsCurSelectedTag(sCurSelectedTag);
	}

	public static String getCurFilterMsg() {
		return logController.getsCurSelectedMsg();
	}

	public static void setCurFilterMsg(String sCurSelectedMsg) {
		logController.setsCurSelectedMsg(sCurSelectedMsg);
	}

	public static LinkedList<String> getCurFilterMsgHistory() {
		return logController.getMsgHistory();
	}

	public static LinkedList<String> getCurFilterShowDownMsgList() {
		return logController.getCurShowDownMsgList();
	}
	
	/*
	 * 搜索相关接口
	 */
	public static String getLastSearchMsg() {
		return logSearchController.getLastSearchMsg();
	}
	public static void setLastSearchMsg(String lastSearchMsg) {
		logSearchController.setLastSearchMsg(lastSearchMsg);
	}
	public static LogEntry[] getLastSearchDataSet() {
		return logSearchController.getLastEntrys();
	}
	public static void setLastSearchDataSet(LogEntry[] lastEntrys) {
		logSearchController.setLastEntrys(lastEntrys);
	}
	public static List<MatchedEntry> getLastMatchedEntryList() {
		return logSearchController.getLastMatchedEntryList();
	}
	public static void clearLastSearchMarks()
	{
		logSearchController.clear();
	}
	public static int getLastMatchedSeq()
	{
		return logSearchController.getLastMatchedSeq();
	}
	public static void setLastMatchedSeq(int lastMatchedSeq)
	{
		logSearchController.setLastMatchedSeq(lastMatchedSeq);
	}
	
	// 搜索下拉菜单相关接口
	public static LinkedList<String> getCurSearchMsgHistory() {
		return logSearchController.getMsgHistory();
	}
	
	public static LinkedList<String> getCurShowDownMsgList() {
		return logSearchController.getCurShowDownMsgList();
	}

	public static String getCurSearchMsg() {
		return logSearchController.getsCurSelectedMsg();
	}

	public static void setCurSearchMsg(String sCurSelectedMsg) {
		logSearchController.setsCurSelectedMsg(sCurSelectedMsg);
	}

	// 从界面上设置，传进来用户定义的显示级别
	public static void setLogLevel(int level) {
		Log_Level = level;
	}

	public static void logCat(String originalLine)
	{
		// 先解析日志
		String sTime = null;
		long pid = -1;
		char level = 'I';
		String tag = null;
		String msg = null;
		
		int startIdx = 0;
		if (!TextUtils.isEmpty(originalLine) 
				&& Character.isDigit(originalLine.charAt(0))
				&& originalLine.length() >= TIMESTAMP_LENGTH) {
			String timestamp = originalLine.substring(0, TIMESTAMP_LENGTH - 1);
			sTime = timestamp;
			startIdx = TIMESTAMP_LENGTH; // cut off timestamp
		}

		Matcher matcher = logPattern.matcher(originalLine);
		if (matcher.find(startIdx)) {
			level = matcher.group(1).charAt(0);
			int gtLevel = LOG_VERBOSE;
			switch (level)
			{
			case 'V':
				gtLevel = LOG_VERBOSE;
				break;
			case 'D':
				gtLevel = LOG_DEBUG;
				break;
			case 'I':
				gtLevel = LOG_INFO;
				break;
			case 'W':
				gtLevel = LOG_WARNING;
				break;
			case 'E':
				gtLevel = LOG_ERROR;
				break;
			}

			tag = matcher.group(2);
			pid = Integer.parseInt(matcher.group(3));
			msg = originalLine.substring(matcher.end());

			log(pid, gtLevel, tag, msg, sTime);
		}
	}

	public static void log(long pid, int level, String tag, String msg, String sTime) {
		if (level < Log_Level) {
			return;
		}

		if (! log_enable_flag) {
			return;
		}
		
		if (level > LOG_ERROR || null == tag || null == msg)
		{
			return;
		}
		
		// 第一次打日志，初始化日志消费者对象。TODO 按道理说应该加锁。
		if (null == logTaskConsumer)
		{
			logTaskConsumer = new LogTaskConsumer(logController);
			logTaskConsumer.setAllowAdd2Visable(log_enable_flag);
			logTaskConsumer.start();
			
		}

		char sLevel = 'V';
		switch (level) {
		case LOG_VERBOSE:
			sLevel = 'V';
			break;
		case LOG_DEBUG:
			sLevel = 'D';
			break;
		case LOG_INFO:
			sLevel = 'I';
			break;
		case LOG_WARNING:
			sLevel = 'W';
			break;
		case LOG_ERROR:
			sLevel = 'E';
			break;
		}

		String time = sTime;
		if (sTime == null)
		{
			time = GTUtils.getSystemDateTime();
		}

		LogEntry entry = new LogEntry();
		entry.tid = pid;
		entry.tag = tag;
		entry.level = level;
		
		if (isSaveDefaultSeg())
		{
			StringBuffer sb = new StringBuffer();
			sb.append(time);
			sb.append(": ");
			sb.append(sLevel);
			sb.append("/"); // sb.append(" | ");
			sb.append(tag);
			sb.append("("); // sb.append(" | ");
			sb.append(pid);
			sb.append("): "); // sb.append(" : ");
			sb.append(msg);
//			sb.append("\r\n"); // 因为UI与自动保存分开了，所以不能统一加换行了
			entry.msg = sb.toString();
		}
		else
		{
			entry.msg = msg;
		}

		entry.sTime = time;
		logTaskConsumer.putLog(entry);
		tempLogConsumer.putLog(entry.msg);
	}
	
	public static void enable()
	{
		log_enable_flag = true;
		GTPref.getGTPref().edit().putBoolean(GTPref.LOG_MASTER_SWITCH, true).commit();
		
		if (null != logTaskConsumer)
		{
			logTaskConsumer.setAllowAdd2Visable(true);
		}
	}
	
	public static void disable()
	{
		log_enable_flag = false;
		GTPref.getGTPref().edit().putBoolean(GTPref.LOG_MASTER_SWITCH, false).commit();
		
		if (null != logTaskConsumer)
		{
			logTaskConsumer.setAllowAdd2Visable(false);
		}
	}
	
	public static boolean isEnable()
	{
		return log_enable_flag;
	}

	public static void setSaveDefaultSeg(boolean flag)
	{
		if (null != logController)
		{
			logController.setSaveDefaultSeg(flag);
		}
	}

	public static boolean isSaveDefaultSeg()
	{
		if (null != logController)
		{
			return logController.getSaveDefaultSeg();
		}
		return false;
	}

	public static void setAutoSave(boolean flag)
	{
		if (null != logController)
		{
			logController.setAutoSave(flag);
		}
	}

	public static boolean isAutoSave()
	{
		if (null != logController)
		{
			return logController.getAutoSave();
		}
		return false;
	}
	
	public static boolean hasLogNeedIO()
	{
		if (null != logController)
		{
//			return logController.hasTempLog(); //TODO 自动写日志合入前用这个
			return logController.getAutoSave() || logController.hasTempLog();
		}
		return false;
	}
	
	public static void saveLog(String logFileName)
	{
		setLastSaveLog(logFileName);
		logController.saveCache(logFileName);
	}
	
	public static String getLastSaveLog() {
		return lastSaveLog;
	}

	public static void setLastSaveLog(String lastSaveLogName) {
		GTLogInternal.lastSaveLog = lastSaveLogName;
	}

	/**
	 * 开始记录临时日志，文件名要合法，默认保存在mnt/sdcard/GT/Log/目录下
	 * @param logFileName 合法的文件名，不需要加后缀
	 * @throws IOException 如文件名不合法或没有创建文件的权限，会抛出该异常
	 */
	public static void startLog(String logFileName) throws IOException {
		tempLogConsumer.startALog(logFileName);
	}
	
	public static void endLog(String logFileName) {
		tempLogConsumer.endALog(logFileName);
	}
	
	public static void endAllLog() {
		logController.endAllLog();
	}

	public static void cleanLog(String logFileName) {
		tempLogConsumer.cleanALog(logFileName);
	}
	
	/**
	 * 清除屏幕上的日志
	 */
	public static void clearLog()
	{
		logController.clearCache();
	}
	
	/*
	 * 被测App变化，也有改变保存日志的根目录
	 * TODO 适配到支持多应用后，该方法暂无使用了
	 */
	public static void changeCurAppName(String name)
	{
		logController.changeCurrentLogFolder(name);
	}
	
	public static void addLogListener(LogListener listener)
	{
		logController.addListener(listener);
	}
	
	public static void removeLogListener(LogListener listener)
	{
		logController.removeListener(listener);
	}
}
