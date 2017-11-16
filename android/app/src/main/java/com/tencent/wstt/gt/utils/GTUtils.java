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
package com.tencent.wstt.gt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.Env;

public class GTUtils {

	
	//=========================================================关于系统、进程信息==============================================
	/**
	 * 获得系统时间
	 * @return
	 */
	private static SimpleDateFormat simpleTimeFormat =
			new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
	
	public static String getSystemTime() {
		Date date = new Date();
		return simpleTimeFormat.format(date);
	}
	
	public static String getSystemTime(long date) {
		return simpleTimeFormat.format(new Date(date));
	}
	
	// 获取系统短日期时间
	private static SimpleDateFormat simpleDateTimeFormat =
			new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);

	public static String getSystemDateTime() {
		Date date = new Date();
		return simpleDateTimeFormat.format(date);
	}

	public static String getSystemDateTime(long date) {
		return simpleDateTimeFormat.format(new Date(date));
	}

	// GPS使用的日期格式
	private static SimpleDateFormat gpsDataFormatter =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	public static String getGpsSaveTime() {
		Date date = new Date();
		return gpsDataFormatter.format(date);
	}
	
	public static String getGpsSaveTime(long data) {
		return gpsDataFormatter.format(new Date(data));
	}

	public static String getGpsSaveTime(Date date) {
		return gpsDataFormatter.format(date);
	}

	// 供外部模块做保存操作时引用的日期格式转换器
	private static SimpleDateFormat saveFormatter =
			new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

	public static String getSaveTime() {
		Date date = new Date();
		return saveFormatter.format(date);
	}

	public static String getSaveTime(long data) {
		return saveFormatter.format(new Date(data));
	}
	
	// 日期，到ms
	private static SimpleDateFormat saveDateMsFormatter =
			new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
	
	public static String getSaveDateMs() {
		Date date = new Date();
		return saveDateMsFormatter.format(date);
	}

	public static String getSaveDateMs(long data) {
		return saveDateMsFormatter.format(new Date(data));
	}
	
	// 日期，到s
	private static SimpleDateFormat saveDateFormatter =
			new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	
	public static String getSaveDate() {
		Date date = new Date();
		return saveDateFormatter.format(date);
	}
	
	public static String getSaveDate(long data) {
		return saveDateFormatter.format(new Date(data));
	}
	
	// 日期，到日
	private static SimpleDateFormat dateFormatter =
			new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	public static String getDate() {
		Date date = new Date();
		return dateFormatter.format(date);
	}
	
	public static String getDate(long data) {
		return dateFormatter.format(new Date(data));
	}

	/**
	 * 设置未捕获异常记录
	 */
	public static void setGTUncaughtExceptionHandler()
	{
		// 设置未捕获异常记录
		Thread.setDefaultUncaughtExceptionHandler(gTUncaughtExceptionHandler);
	}
	
	/**
	 * 未捕获异常处理者
	 */
	public static UncaughtExceptionHandler
		gTUncaughtExceptionHandler = new UncaughtExceptionHandler()
	{
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			File crashFolder = Env.CRASH_LOG_FOLDER;
			if (! crashFolder.exists() || ! crashFolder.isDirectory())
			{
				crashFolder.mkdirs();
			}
			
			File crashLog = Env.GT_CRASH_LOG;
			long limit = 1024*1024*10;
			PrintStream fw = null;
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,
					DateFormat.LONG);
			String time = df.format(d);
			
			if (crashLog.length()  >= limit)
			{
				crashLog.delete();
			}
			if (!crashLog.exists())
			{
				try {
					crashLog.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				OutputStream out =new FileOutputStream(crashLog, true);
				fw = new PrintStream(out);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (null != fw)
			{
				fw.println(time);
				ex.printStackTrace(fw);
				fw.println("\r\n");
				fw.flush();
				FileUtil.closeOutputStream(fw);
			}
			ex.printStackTrace();
			GTApp.exitGT();
		}};

	/**
	 * 是否存在SD卡
	 */
	public static boolean isSDCardExist(){
		if(!android.os.Environment.getExternalStorageState(
				).equals(android.os.Environment.MEDIA_MOUNTED)){
			// 对用户只提示一次，以免干扰
			if (!hasSDCardNotExistWarned)
			{
				openToast("保存内容请先插入sdcard!!!");
				hasSDCardNotExistWarned = true;
			}	
			return false;
		}
		return true;
	}
	private static boolean hasSDCardNotExistWarned = false;
	
	/**
	 * toast提示
	 * 该方法在GT尚未完成初始化时调用会有异常：Caused by: java.lang.RuntimeException:
	 *  Can't create handler inside thread that has not called Looper.prepare()，所以
	 *  需要try..catch保护
	 * @param message
	 */
	private static void openToast(String message) {
		try
		{
			Toast toast = Toast.makeText(GTApp.getContext(), message, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		catch(Exception e)
		{
			Log.e("GTUtils.openToast", "Toast when GT App not inited.");
		}
	}

	//================================关于android资源文件的拷贝===========================
	public static void copySoToDest(Context context)
	{	
		// 内置应用需要自己管理so文件，在第一次启动时，将so拷贝到内置管理路径
//		String insideSoPath = Env.INSIDE_SO_FOLDER;
//		File insideSoFolder = new File(insideSoPath);
//		if (!insideSoFolder.exists())
//		{
//			// 第一次要创建路径
//			insideSoFolder.mkdirs();
//		}

//		LibManager.getInstance(context).loadLibrary("mem_fill_tool", true);
	}

	public static void copyTcpdump(Context context){
		String filePath = context.getFilesDir().getPath() + FileUtil.separator;
		String fileName = "tcpdump";
		String TCPDUMPFN = filePath + fileName;
		
		try{
			File dir = new File(filePath);
			if(dir.exists()){
				System.out.print("dir exists!");
			}else{
				dir.mkdir();
			}
			// 因为之前的版本出错过需要覆盖，所以的文件检测屏蔽掉
//			if(!(new File (TCPDUMPFN).exists())){
				int resId = Env.API > 22 ? R.raw.tcpdump6 : R.raw.tcpdump;
				InputStream is = context.getResources().openRawResource(resId);
				FileOutputStream fos;
				fos = new FileOutputStream(TCPDUMPFN);
				
				byte[] buffer = new byte[8192];
				int count = 0;
				while((count = is.read(buffer)) > 0){
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			String command = "chmod 777 " + TCPDUMPFN;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyalarm(Context context){
		String filePath = context.getFilesDir().getPath() + FileUtil.separator;
		String fileName = "greattit.mp3";
		String BUSYBOXFN = filePath + fileName;
		
		try{
			File dir = new File(filePath);
			if(dir.exists()){
				System.out.print("dir exists!");
			}else{
				dir.mkdir();
			}
			if(!(new File (BUSYBOXFN).exists())){
				InputStream is = context.getResources().openRawResource(R.raw.greattit);
				FileOutputStream fos;
				fos = new FileOutputStream(BUSYBOXFN);
				
				byte[] buffer = new byte[8192];
				int count = 0;
				while((count = is.read(buffer)) > 0){
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			String command = "chmod 777 " + BUSYBOXFN;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}