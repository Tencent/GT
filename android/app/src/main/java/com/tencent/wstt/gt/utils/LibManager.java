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

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.api.utils.Env;

public class LibManager {
//	private final String TAG = LibManager.class.getSimpleName();
	static Object sGlobalLock = new Object();
	static LibManager sInstance;
	private Context mApplicationContext;
	private String libPath = "";

	static public LibManager getInstance(Context context) {
		synchronized (sGlobalLock) {
			if (sInstance == null) {
				sInstance = new LibManager(context);
			}
			return sInstance;
		}
	}

	private LibManager(Context context) {
		mApplicationContext = context.getApplicationContext();
		libPath = Env.INSIDE_SO_FOLDER;
	}

	public boolean loadLibrary(String libName, boolean load) {
		String libFullName = "lib" + libName + ".so";
//		String fromPath = "armeabi/" + libFullName;
		// 如果是arm64的CPU，文件在assets目录下的arm64-v8a目录中
		String fromPath = libFullName;
		if (DeviceUtils.getABI().contains("arm64-v8a"))
		{
			fromPath = "/arm64-v8a/" + fromPath;
		}

		String toPath = libPath + libFullName;

		if (!FileUtil.isFileExists(Env.CMD_ROOT_PATH)) {
			FileUtil.createDir(Env.CMD_ROOT_PATH);
		}

		if (!FileUtil.isFileExists(libPath)) {
			FileUtil.createDir(libPath);
		}

		if (!FileUtil.isFileExists(toPath)) {
			InputStream fs = null;
			try {
				AssetManager am = mApplicationContext.getAssets();
				fs = am.open(fromPath);
				FileUtil.copyInputToFile(fs, toPath);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				FileUtil.closeInputStream(fs);
			}
		}

		if (load)
		{
			try {
//				 System.loadLibrary(libName); // 因为默认不在原始的lib目录下，所以不用该方法
				System.load(toPath);
			} catch (UnsatisfiedLinkError e) {
				return false;
			}
		}
		return true;
	}
}
