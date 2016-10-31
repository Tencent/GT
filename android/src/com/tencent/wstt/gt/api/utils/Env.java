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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;
import com.tencent.wstt.gt.utils.ToastUtil;

import android.os.Environment;
import android.text.TextUtils;

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
			SDCardPathHelper.getAbsoluteSdcardPath() + "/GT/";
//	static
//	{
//		if (API > 18)
//		{
//			// Android4.x开始的个别版本系统，/storage/emulated/0/GT需要转成/storage/sdcard0/
			// 否则出现抓包无法写入等问题，故引入SDCardPathHelper类适配此问题
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
	// @Fixme 抓包直接使用 S_ROOT_GT_FOLDER 在4.4.4手机上无效，所以先写死用sdcard
	public static final String S_ROOT_TCPDUMP_FOLDER = SDCardPathHelper.CT_S_Sdcard_Sign_sdcard + "/GT/" + "Tcpdump/";
	public static final String S_ROOT_CONFIG_FOLDER = S_ROOT_GT_FOLDER + "Config/";
	public static final String S_ROOT_BATTERY_FOLDER = S_ROOT_GT_FOLDER + "Battery/";

	public static File ROOT_GT_FOLDER;
	public static File ROOT_LOG_FOLDER;
	public static File ROOT_GPS_FOLDER;
	public static File CRASH_LOG_FOLDER;
	public static File ROOT_TIME_FOLDER;
	public static File ROOT_GW_FOLDER;
	public static File ROOT_GW_MAN_FOLDER;
	public static File ROOT_TIME_AUTO_FOLDER;
	public static File ROOT_TCPDUMP_FOLDER;
	public static File ROOT_CONFIG_FOLDER;
	public static File ROOT_BATTERY_FOLDER;
	
	public static final File GT_CRASH_LOG = new File(CRASH_LOG_FOLDER, "gt_crashlog.log");
	public static final String GT_APP_NAME = "default";
	public static String CUR_APP_NAME = GT_APP_NAME;
	public static String CUR_APP_VER = "";

	// 网站的url
	public static final String GT_HOMEPAGE = "http://gt.qq.com/";
	public static final String GT_POLICY = "http://gt.qq.com/wp-content/EULA_EN.html";
	public static final String BUGLY_APPPAGE = "http://bugly.qq.com/apps";

	public static void init()
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

		ROOT_GT_FOLDER = new File(S_ROOT_GT_FOLDER);
		ROOT_GT_FOLDER = new File(S_ROOT_GT_FOLDER);
		ROOT_LOG_FOLDER = new File(S_ROOT_LOG_FOLDER);
		ROOT_GPS_FOLDER = new File(S_ROOT_GPS_FOLDER);
		CRASH_LOG_FOLDER = new File(S_CRASH_LOG_FOLDER);
		ROOT_TIME_FOLDER = new File(S_ROOT_TIME_FOLDER);
		ROOT_GW_FOLDER = new File(S_ROOT_GW_FOLDER);
		ROOT_GW_MAN_FOLDER = new File(S_ROOT_GW_MAN_FOLDER);
		ROOT_TIME_AUTO_FOLDER = new File(S_ROOT_TIME_AUTO_FOLDER);
		ROOT_TCPDUMP_FOLDER = new File(S_ROOT_TCPDUMP_FOLDER);
		ROOT_CONFIG_FOLDER = new File(S_ROOT_CONFIG_FOLDER);
		ROOT_BATTERY_FOLDER = new File(S_ROOT_BATTERY_FOLDER);
	}

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
	
	public static class SDCardPathHelper {

		public static final String CT_S_Sdcard_Sign_Storage_emulated = "storage/emulated/";
		public static final String CT_S_Sdcard_Sign_Storage_sdcard = "storage/sdcard";
		// 根据Nexus5 Android6.01适配
		public static final String CT_S_Sdcard_Sign_Storage_emulated_0 = "storage/emulated/0";
		public static final String CT_S_Sdcard_Sign_sdcard = "sdcard";

		private static String CD_S_SdcardPath = "";
		private static String CD_S_SdcardPathAbsolute = "";

		public static String getSdcardPath() {
			if (TextUtils.isEmpty(CD_S_SdcardPath))
				CD_S_SdcardPath = Environment.getExternalStorageDirectory().getPath();

			CD_S_SdcardPath = checkAndReplaceEmulatedPath(CD_S_SdcardPath);

			return CD_S_SdcardPath;
		}

		public static String getAbsoluteSdcardPath() {
			if (TextUtils.isEmpty(CD_S_SdcardPathAbsolute))
			{
				CD_S_SdcardPathAbsolute = Environment.getExternalStorageDirectory().getAbsolutePath();
			}
			// 先试试默认的目录，如果创建目录失败再试其他方案
			String testFileName = GTUtils.getSaveDateMs();
			File testF = new File(CD_S_SdcardPathAbsolute + "/GT/" + testFileName + "/");
			if (testF.mkdirs())
			{
				FileUtil.deleteFile(testF);
				return CD_S_SdcardPathAbsolute;
			}

			// 默认路径不可用，尝试其他方案
			CD_S_SdcardPathAbsolute = checkAndReplaceEmulatedPath(CD_S_SdcardPathAbsolute);

			return CD_S_SdcardPathAbsolute;
		}

		public static File getSdcardPathFile() {
			return new File(getSdcardPath());
		}

		public static String checkAndReplaceEmulatedPath(String strSrc) {
			String result = strSrc;
			Pattern p = Pattern.compile("/?storage/emulated/\\d{1,2}");
			Matcher m = p.matcher(strSrc);
			if (m.find()) {
				result= strSrc.replace(CT_S_Sdcard_Sign_Storage_emulated, CT_S_Sdcard_Sign_Storage_sdcard);
				// 如果目录建立失败，最后尝试Nexus5 Android6.01适配
				String testFileName = GTUtils.getSaveDateMs();
				File testFile = new File(CD_S_SdcardPathAbsolute + "/GT/" + testFileName + "/");
				if (testFile.mkdirs())
				{
					FileUtil.deleteFile(testFile);
				}
				else
				{
					result = strSrc.replace(CT_S_Sdcard_Sign_Storage_emulated_0, CT_S_Sdcard_Sign_sdcard);
					
					// test
					File testF = new File(result + "/GT/" + testFileName + "/");
					if (testF.mkdirs())
					{
						FileUtil.deleteFile(testF);
					}
				}
				
			}

			return result;
		}
	}
}
