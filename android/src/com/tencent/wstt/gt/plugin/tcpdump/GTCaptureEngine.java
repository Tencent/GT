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
package com.tencent.wstt.gt.plugin.tcpdump;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.plugin.PluginTaskExecutor;
import com.tencent.wstt.gt.utils.RootUtil;

import android.content.Context;
import android.os.Bundle;

/**
 * 因为抓包的UI过于复杂，所以引擎与UI暂走两条路互不干涉
 */
public class GTCaptureEngine implements PluginTaskExecutor {

	private static GTCaptureEngine INSTANCE;

	private List<GTCaptureListener> listeners;

	private static final String DEFAULT_PARAM = "-p -s 0 -vv -w";

	private String line = "";
	private List<String> list_temp = new ArrayList<String>();
	private boolean captureState = false;
	
	public boolean getCaptureState(){
		return captureState;
	}
	
	public void setCaptureState(boolean captureState){
		this.captureState = captureState;
	}

	public static GTCaptureEngine getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new GTCaptureEngine();
		}
		return INSTANCE;
	}

	private GTCaptureEngine()
	{
		listeners = new ArrayList<GTCaptureListener>();
	}

	public synchronized void addListener(GTCaptureListener listener)
	{
		listeners.add(listener);
	}

	public synchronized void removeListener(GTCaptureListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void execute(Bundle bundle) {
		String cmd = bundle.getString("cmd");
		if (cmd != null && cmd.equals("startCapture")) {
			String filePath = bundle.getString("filePath");
			String param = bundle.getString("param");
			doCapture(filePath, param);
		}
		else if (cmd != null && cmd.equals("stopCapture"))
		{
			doStopCapture();
		}
	}
	
	public void doCapture(final String filePath, final String param)
	{
		for (GTCaptureListener listener : listeners)
		{
			listener.onStartCaptureBegin();
		}
		
		// 先check抓包环境
		if (checkTcpDump(filePath, param))
		{
			new Thread(new Runnable(){

				@Override
				public void run() {
					String realParam = param;
					if (param == null || param.trim().equals("")) {
						realParam = DEFAULT_PARAM;
					}
					startTcpDump(filePath, realParam, GTApp.getContext(), true);

					/*
					 * 因为上面startTcpDump方法需要输出输入流（最后一个参数是true），
					 * 所以抓包不停止或发生异常前面startTcpDump方法是不会结束，所以这里处理抓包结束监听
					 */
					for (GTCaptureListener listener : listeners)
					{
						listener.onStopCaptureEnd();
					}
				}}, "GTCaptureThread").start();
		}

		for (GTCaptureListener listener : listeners)
		{
			listener.onStartCaptureEnd();
		}
	}
	
	public void doStopCapture()
	{
		for (GTCaptureListener listener : listeners)
		{
			listener.onStopCaptureBegin();
		}
		endTcpDump();
	}
	
	private boolean checkTcpDump(final String filePath, final String param) {
		// 判断手机是否root
		String errorstr = "";
		if (!RootUtil.isRooted()) {
			errorstr = "root needed!";
			for (GTCaptureListener listener : listeners) {
				listener.onCaptureFail(errorstr);
			}
			return false;
		}
		// 判断是否有手机存储
		if (!Env.isSDCardExist()) {
			errorstr = "phone storage needed!";
			for (GTCaptureListener listener : listeners) {
				listener.onCaptureFail(errorstr);
			}
			return false;
		}
		// 校验filePath
		if (filePath == null || filePath.trim().equals("")) {
			errorstr = "filePath cannot be null!";
			for (GTCaptureListener listener : listeners) {
				listener.onCaptureFail(errorstr);
			}
			return false;
		}
		if (filePath.contains("\\") // 路径可以有'/'
				|| filePath.contains(":") || filePath.contains("*") || filePath.contains("?") || filePath.contains("\"")
				|| filePath.contains("<") || filePath.contains(">") || filePath.contains("|")) {
			errorstr = "filePath can't contain::*?\"<>|";
			for (GTCaptureListener listener : listeners) {
				listener.onCaptureFail(errorstr);
			}
			return false;
		}
		if (param != null && (param.contains("|") || param.contains(">") || param.contains(">>"))) {
			errorstr = "param can't contain: | > >>";
			for (GTCaptureListener listener : listeners) {
				listener.onCaptureFail(errorstr);
			}
			return false;
		}

		return true;
	}

	/**
	 * 开始抓包
	 * @param path
	 * 			抓包文件存储路径
	 * @param command
	 * 			命令参数
	 * @param context
	 * 			应用程序的上下文环境
	 * @param needInputStream
	 * 			是否需要输出抓包命令执行后返回的输入流
	 */
	public void startTcpDump(String path, String command, Context context, boolean needInputStream) {
		if (getCaptureState())
		{
			// 已启动抓包，直接退出
			return;
		}
		
		setCaptureState(true);
		try {
			ProcessUtils.killprocess("tcpdump");
			String cmd = context.getFilesDir().getPath() + "/" + "tcpdump " + command + " " + path;
			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			Process exec = null;
			exec = execBuilder.start();
			if(needInputStream){
				InputStream is = exec.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				while((line = reader.readLine()) != null){
					synchronized (list_temp) {
						if (list_temp.size() > 1000)
						{
							list_temp.clear();
						}
						list_temp.add(line);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 强制启动抓包且将输出重定向到context.getFilesDir().getPath() + "/Capture.txt"
	 * @param context
	 */
	public void startTcpDump(Context context) {
		setCaptureState(true);
		try {
			ProcessUtils.killprocess("tcpdump");
			String cmd = context.getFilesDir().getPath() + "/" + "tcpdump "
					+ "> " + context.getFilesDir().getPath() + "/Capture.txt";

			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			execBuilder.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取抓包命令执行后返回的输入流
	 * @return 抓包命令执行后返回的输入流
	 */
	public List<String> getTcpDumpInputStream(){
		List<String> lst = new ArrayList<String>();
		synchronized (list_temp) {
			lst.addAll(list_temp);
			list_temp.clear();
		}
		return lst;
	}

	/**
	 * 结束抓包
	 * @param context
	 * 			应用程序的上下文环境
	 */
	public void endTcpDump(){
		setCaptureState(false);
		ProcessUtils.killprocess("tcpdump");
	}
}
