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
package com.tencent.wstt.gt.client.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.os.Handler;
import android.os.Process;

public class GTUncaughtExceptionHandler implements UncaughtExceptionHandler {
	
	private final String recordFolder =
			Environment.getExternalStorageDirectory() + "/GT/CrashRecord/";
	private final String recordSuffix = ".log";
	private final long maxSize = 1024*1024*4;

	public GTUncaughtExceptionHandler() {

	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// 收集异常信息 并且发送到服务器
		sendCrashReport(ex);

		// 等500ms再处理异常
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		handleException(ex);
	}

	/*
	 * 输出意外信息到日志
	 */
	private void sendCrashReport(Throwable ex) {
		Date now=new Date();
		SimpleDateFormat fmt = (SimpleDateFormat)SimpleDateFormat.getTimeInstance();
		fmt.applyPattern("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Time:");
		exceptionStr.append(fmt.format(now));
		exceptionStr.append("\r\n");
		exceptionStr.append(ex.getClass());
		exceptionStr.append(" Cause:");
		exceptionStr.append(ex.getMessage());
		exceptionStr.append("\r\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString());
			exceptionStr.append("\r\n");
		}
		exceptionStr.append("\r\n");
		
		File folder = new File(recordFolder);
		FileWriter writer = null;
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		try {
			File f = new File(
					folder, Thread.currentThread().getName() + recordSuffix);
			
			if (f.exists() && f.length() >= maxSize)
			{
				f.delete();
			}
			else
			{
				f.createNewFile();
			}

			writer = new FileWriter(f, true);
			writer.write(exceptionStr.toString());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	private void handleException(Throwable ex) {
		// 先抛出异常到logcat
		ex.printStackTrace();
		
		// 保存环境
		
		// 延迟半秒杀进程
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Process.killProcess(Process.myPid());
			}
		}, 500);
		
//		// 先直接退出虚拟机
//		System.exit(0);
	}
}
