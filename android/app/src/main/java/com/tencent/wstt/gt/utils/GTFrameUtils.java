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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.api.utils.ProcessUtils;

public class GTFrameUtils {

	private static int pid = 0;
	public static boolean hasSu = false;
	
	public static boolean isHasSu() {
		return hasSu;
	}

	public static void setHasSu(boolean hasSu) {
		GTFrameUtils.hasSu = hasSu;
	}

	public static void setPid() {
		setHasSu(false);
		try {
			ProcessBuilder execBuilder = null;
			if (pid == 0) {
				execBuilder = new ProcessBuilder("su", "-c", "ps");

				execBuilder.redirectErrorStream(true);

				Process exec = null;
				exec = execBuilder.start();
				InputStream is = exec.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				String line = "";
				while ((line = reader.readLine()) != null) {
					if (line.contains("surfaceflinger")) {
						String regEx = "\\s[0-9][0-9]*\\s";
						Pattern pat = Pattern.compile(regEx);
						Matcher mat = pat.matcher(line);
						if (mat.find()) {
							String temp = mat.group();
							temp = temp.replaceAll("\\s", "");
							pid = Integer.parseInt(temp);
						}
						break;
					}
				}
			}

			if (pid == 0) {
				if (ProcessUtils.getProcessPID("system_server") != -1) {
					pid = ProcessUtils.getProcessPID("system_server");
				} else {
					pid = ProcessUtils.getProcessPID("system");
				}

			}
			setHasSu(true);
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.ShowLongToast(GTApp.getContext(), "Root is demanded");
			setHasSu(false);
		}

		Log.d("pid: ", String.valueOf(pid));
	}
}
