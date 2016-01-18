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

import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.utils.StringUtil;

public class SplashActivity extends Activity {

	private static SplashActivity INSTANCE;
	
	public static SplashActivity getInstance() {
		return INSTANCE;
	}

	/**
	 * 是否是首次进入欢迎页
	 */
	public static boolean sIsFirstTimeEnter = true;

	/**
	 * dialogs
	 */
	private static final int DIALOG_EULA = 2;
	public String eluaStr = null; // 用完要及时置null
	private EULATask eulaTask = null;
	
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

			// 协议对话框，同意一次之后不再弹出
			if (sIsFirstTimeEnter) {
				
				// 显示闪屏
				setContentView(R.layout.gt_splash);

				// 开启入口service 开启远程服务service
				GTEntrance.GTopen(getApplicationContext());

				// 已同意过法律条款
				if (Eula.isAccepted()) {
					// 加载完跳到MainActivity
					new Handler().postDelayed(new Runnable() {
						public void run() {
							Intent intent = new Intent(SplashActivity.this,
									GTMainActivity.class);
							startActivity(intent);
							SplashActivity.this.finish();
						}
					}, 2000);
				}
				else
				{
					// 异步从网络上准备协议条款
					eulaTask = new EULATask();
					eulaTask.execute();
				}
				sIsFirstTimeEnter = false;
			} else {
				Intent intent = new Intent(this, GTMainActivity.class);
				startActivity(intent);
				this.finish();
			}
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EULA:
			return new Eula(this).buildEulaDialog(eluaStr);
		}
		return super.onCreateDialog(id);
	}
	
	static class EULATask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// 如果当前是wifi网络，从网上下载法律条文，其他情况读本地文件
			String sUrl = "http://gt.qq.com/wp-content/EULA_EN.html";
			URL url = null;
			String eula = "";
			/*
			 * since 2.1.6
			 *  有时候wifi也比较慢，所以去除从网络读条款的逻辑
			 */
//			try {
//				if (NetUtils.isWifiActive())
//				{
//					url = new URL(sUrl);
//					eula = Eula.readEula(url).toString(); // 如果没网络等IO异常，eula会置为""
//				}
//			} catch (Exception e) {
//				Log.i("SplashActivity", "EULA download or format fail.");
//			}
			
			if (StringUtil.isEmptyOrWhitespaceOnly(eula))
			{
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.obj = eula;
			handler.sendMessage(msg);
			return null;
		}
	}
	
	static Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:
				INSTANCE.eluaStr = (String)msg.obj;
				INSTANCE.showDialog(DIALOG_EULA);
				break;
			}
		}
	};
}
