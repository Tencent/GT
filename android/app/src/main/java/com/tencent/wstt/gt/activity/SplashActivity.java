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
package com.tencent.wstt.gt.activity;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private static SplashActivity INSTANCE;
	
	public static SplashActivity getInstance() {
		return INSTANCE;
	}

	/**
	 * 是否是首次进入欢迎页
	 */
	public static boolean sIsFirstTimeEnter = true;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		INSTANCE = this;
		// 已启动
		if(GTApp.getGTRunStatus()){
			if(!GTMainActivity.isActived){
				Intent intent = new Intent(SplashActivity.this, GTMainActivity.class);
				startActivity(intent);
			}
			finish();
		}
		// 启动
		else
		{
			GTApp.setGTRunStatus(true);

			// 新启动进闪屏页2s
			if (sIsFirstTimeEnter) {
				
				// 显示闪屏
				setContentView(R.layout.gt_splash);

				// 开启入口service 开启远程服务service
				GTEntrance.GTopen(getApplicationContext());

				new Handler().postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(SplashActivity.this,
								GTMainActivity.class);
						startActivity(intent);
						SplashActivity.this.finish();
					}
				}, 2000);

				sIsFirstTimeEnter = false;
			} else {
				Intent intent = new Intent(this, GTMainActivity.class);
				startActivity(intent);
				this.finish();
			}
		}
	}
}
