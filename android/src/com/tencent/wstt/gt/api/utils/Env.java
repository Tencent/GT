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
package com.tencent.wstt.gt.api.utils;

import java.io.DataOutputStream;
import java.io.File;

import android.os.Environment;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.ToastUtil;

/**
 * 环境参数及方法工具类。
 */
public class Env {
	
	public static final String CMD_ROOT_PATH =
			GTApp.getContext().getFilesDir().getPath() + "/";
	
	// /data/data/com.tencent.wstt.gt/lib
	public static final String LIB_FILE =
			GTApp.getContext().getFilesDir().getParent() + "/lib";

	// /data/data/com.tencent.wstt.gt/lib/
	public static final String LIB_FOLDER =
			GTApp.getContext().getFilesDir().getParent() + "/lib/";

	// /data/data/com.tencent.wstt.gt/files/ 放其他目录权限控制太麻烦，就放files了
	public static final String INSIDE_SO_FOLDER = 
//			GTApp.getContext().getFilesDir().getPath()+ "/lib/";
			GTApp.getContext().getFilesDir().getPath()+ "/";
	
	public static final int API = android.os.Build.VERSION.SDK_INT;
	
	public static String S_ROOT_GT_FOLDER =
			Environment.getExternalStorageDirectory().getAbsolutePath() + "/GT/";
//	static
//	{
//		if (API > 18)
//		{
//			// Android4.4开始，/storage/emulated/0/GT需要转成/storage/sdcard0/
//			S_ROOT_GT_FOLDER = "/storage/sdcard0/GT/";
//		}
//	};
	
	public static final String S_ROOT_LOG_FOLDER = S_ROOT_GT_FOLDER + "Log/";
	public static final String S_ROOT_GPS_FOLDER = S_ROOT_LOG_FOLDER + "GPS/";
	public static final String S_CRASH_LOG_FOLDER = S_ROOT_LOG_FOLDER + "Crash/";
	public static final String S_ROOT_TIME_FOLDER = S_ROOT_GT_FOLDER + "Profiler/";
	public static final String S_ROOT_GW_FOLDER = S_ROOT_GT_FOLDER + "GW/";
	public static final String S_ROOT_GW_MAN_FOLDER = S_ROOT_GT_FOLDER + "Man/"; // 手动点击记录的内存值跟目录
	public static final String S_ROOT_TIME_AUTO_FOLDER = S_ROOT_TIME_FOLDER + "Auto/";
	public static final String S_ROOT_DUMP_FOLDER = S_ROOT_GT_FOLDER + "Dump/";
	public static final String S_ROOT_TCPDUMP_FOLDER = S_ROOT_GT_FOLDER + "Tcpdump/";
	public static final String S_ROOT_CONFIG_FOLDER = S_ROOT_GT_FOLDER + "Config/";
	public static final String S_ROOT_BATTERY_FOLDER = S_ROOT_GT_FOLDER + "Battery/";
	static
	{
		FileUtil.createDir(S_ROOT_GT_FOLDER);
		FileUtil.createDir(S_ROOT_LOG_FOLDER);
		FileUtil.createDir(S_ROOT_GPS_FOLDER);
		FileUtil.createDir(S_CRASH_LOG_FOLDER);
		FileUtil.createDir(S_ROOT_TIME_FOLDER);
		FileUtil.createDir(S_ROOT_GW_FOLDER);
		FileUtil.createDir(S_ROOT_GW_MAN_FOLDER);
		FileUtil.createDir(S_ROOT_TIME_AUTO_FOLDER);
		FileUtil.createDir(S_ROOT_TCPDUMP_FOLDER);
		FileUtil.createDir(S_ROOT_CONFIG_FOLDER);
		FileUtil.createDir(S_ROOT_BATTERY_FOLDER);
	}

	public static final File ROOT_GT_FOLDER = new File(S_ROOT_GT_FOLDER);
	public static final File ROOT_LOG_FOLDER = new File(S_ROOT_LOG_FOLDER);
	public static final File ROOT_GPS_FOLDER = new File(S_ROOT_GPS_FOLDER);
	public static final File CRASH_LOG_FOLDER = new File(S_CRASH_LOG_FOLDER);
	public static final File ROOT_TIME_FOLDER = new File(S_ROOT_TIME_FOLDER);
	public static final File ROOT_GW_FOLDER = new File(S_ROOT_GW_FOLDER);
	public static final File ROOT_GW_MAN_FOLDER = new File(S_ROOT_GW_MAN_FOLDER);
	public static final File ROOT_TIME_AUTO_FOLDER = new File(S_ROOT_TIME_AUTO_FOLDER);
	public static final File ROOT_TCPDUMP_FOLDER = new File(S_ROOT_TCPDUMP_FOLDER);
	public static final File ROOT_CONFIG_FOLDER = new File(S_ROOT_CONFIG_FOLDER);
	public static final File ROOT_BATTERY_FOLDER = new File(S_ROOT_BATTERY_FOLDER);
	
	public static final File GT_CRASH_LOG = new File(CRASH_LOG_FOLDER, "gt_crashlog.log");
	public static final String GT_APP_NAME = "default";
	public static String CUR_APP_NAME = GT_APP_NAME;

	// 网站的url
	public static final String GT_HOMEPAGE = "http://gt.qq.com/";
	public static final String GT_POLICY = "http://gt.qq.com/wp-content/EULA_EN.html";
	
	/**
	 * 是否存在SD卡
	 */
	public static boolean isSDCardExist(){
		if(!android.os.Environment.getExternalStorageState(
				).equals(android.os.Environment.MEDIA_MOUNTED)){
			// 对用户只提示一次，以免干扰
			if (!Env.hasSDCardNotExistWarned)
			{
				ToastUtil.ShowLongToast(GTApp.getContext(), "sdcard required!");
				Env.hasSDCardNotExistWarned = true;
			}	
			return false;
		}
		return true;
	}
	private static boolean hasSDCardNotExistWarned = false;

	public static boolean upgradeRootPermission(String pkgCodePath) {
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}
}
