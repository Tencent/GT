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
package com.tencent.wstt.gt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.GTBinder;
import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTMainActivity;
import com.tencent.wstt.gt.utils.NotificationHelper;

/**
 * GT对外的服务,同时声明为前台服务
 */
public class GTService extends Service {

	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/*
	 * 远程调用使用
	 */
	private final IBinder binder = new GTBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) return START_STICKY_COMPATIBILITY;
		String version_type = "Release";
		if (1 == GTConfig.VERSION_TYPE) {
			version_type = "Develop";
		}
		GTMainActivity.notification = NotificationHelper.genNotification(
				GTApp.getContext(), 0, R.drawable.gt_entrlogo, "GT", 1,
				"Version: " + version_type + " " + GTConfig.VERSION,
				"GT is running", GTMainActivity.class,
				true, false, 0);
		startForeground(10, GTMainActivity.notification);
		return super.onStartCommand(intent, flags, startId);
	}
}