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
package com.tencent.wstt.gt.api.base;

import java.io.IOException;

import com.tencent.wstt.gt.log.GTLogInternal;
import android.os.Process;

/**
 * 控制台本身及工具使用的日志服务入口类。
 */
public class GTLog {
	private static int pid = Process.myPid();
	
	/**
	 * 打印日志，INFO级别.
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志文本
	 */
	public static void logI(String tag, String msg) {
		log(GTLogInternal.LOG_INFO, tag, msg);
	}

	/**
	 * 打印logcat格式日志，直接按logcat格式解析.
	 * 
	 * @param log
	 *            logcat格式日志
	 */
	public static void logCat(String log) {
		GTLogInternal.logCat(log);
	}

	/**
	 * 打印日志，DEBUG级别.
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志文本
	 */
	public static void logD(String tag, String msg) {
		log(GTLogInternal.LOG_DEBUG, tag, msg);
	}

	/**
	 * 打印日志，WARNING级别.
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志文本
	 */
	public static void logW(String tag, String msg) {
		log(GTLogInternal.LOG_WARNING, tag, msg);
	}

	/**
	 * 打印日志，ERROR级别.
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志文本
	 */
	public static void logE(String tag, String msg) {
		log(GTLogInternal.LOG_ERROR, tag, msg);
	}

	/*
	 * 打印日志，注意这里是控制台打印，所以线程号取负数与被测端区别
	 * 
	 * @param level 日志级别
	 * 
	 * @param tag 日志标签
	 * 
	 * @param msg 日志文本
	 */
	private static void log(int level, String tag, String msg) {
		GTLogInternal.log(pid, level, tag, msg, null);
	}

	/**
	 * 开始记录临时日志，追加模式。
	 * 
	 * @param logFileName
	 *            合法的文件名或路径。
	 *            如果传入的字符串不加后缀， 会自动加上".log"后缀名。 
	 *            如果传入的是文件名，默认保存在mnt/sdcard/GT/Log/目录下。
	 *            如果传入的是路径，则需要是文件完整路径，如mnt/sdcard/GT/temp/test.log
	 * @throws IOException
	 *             如文件或路径名不合法或没有创建文件的权限，会抛出该异常
	 */
	public static void startLog(String logFileName) throws IOException {
		GTLogInternal.startLog(logFileName);
	}

	/**
	 * 结束记录临时日志，与startLog方法成对使用.
	 * 
	 * @param logFileName
	 *            之前在startLog方法中传入的文件名， 如文件名非法或未在startLog启动过，则直接返回
	 */
	public static void endLog(String logFileName) {
		GTLogInternal.endLog(logFileName);
	}

	/**
	 * 清理对应文件名的临时日志文件.
	 * 
	 * @param logFileName
	 *            之前在startLog方法中传入的文件名， 如文件名非法或未在startLog启动过，则直接返回
	 */
	public static void cleanLog(String logFileName) {
		GTLogInternal.cleanLog(logFileName);
	}
}
