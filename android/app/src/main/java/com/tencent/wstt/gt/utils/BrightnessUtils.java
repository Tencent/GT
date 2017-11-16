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

import android.net.Uri;
import android.provider.Settings;

import com.tencent.wstt.gt.GTApp;

public class BrightnessUtils {

	/**
	 * 设置为非自动调节亮度的模式
	 */
	public static void setManualMode() {
		try {
			Settings.System.putInt(GTApp.getContext().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}catch(Exception e) { // api23以上需要申请权限android.permission.WRITE_SETTINGS
			e.fillInStackTrace();
		}
	}

	/**
	 * 设置当前屏幕亮度值 0--255
	 */
	public static void setScreenBrightness(int value) {
		try {
			Settings.System.putInt(GTApp.getContext().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, value);
		} catch (Exception localException) { // api23以上需要申请权限android.permission.WRITE_SETTINGS
			localException.printStackTrace();
		}
	}

	/**
	 * 保存亮度设置状态
	 * 
	 * @param resolver
	 * @param brightness
	 */
	public static void saveBrightness(int brightness) {
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(GTApp.getContext()
				.getContentResolver(), "screen_brightness", brightness);
		GTApp.getContext().getContentResolver().notifyChange(uri, null);
	}
}
