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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.stat.StatService;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.manager.ClientFactory;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.SingleInstanceClientFactory;
import com.tencent.wstt.gt.utils.AppInfo;

@SuppressLint("HandlerLeak")
public class GTShowPackageActivity extends GTBaseActivity {
	private ArrayList<AppInfo> dataList = new ArrayList<AppInfo>();
	ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
	private ProgressDialog proDialog;

	Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			ListView app_listView = (ListView) findViewById(R.id.listview);
			app_listView.setDividerHeight(10);  
			AppAdapter appAdapter = new AppAdapter(GTShowPackageActivity.this,
					appList);
			app_listView.setDividerHeight(5);
			if (app_listView != null) {
				app_listView.setAdapter(appAdapter);
				app_listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						backToSetting(arg2);
					}
				});
			}

			if (proDialog != null) {
				proDialog.dismiss();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gw_showapp);
		proDialog = ProgressDialog.show(GTShowPackageActivity.this, "Searching..",
				"searching..wait....", true, true);
		Thread loginThread = new Thread(new ShowappHandler());
		loginThread.start();
	}

	class ShowappHandler implements Runnable {
		@Override
		public void run() {
			getInstalledApp();
			ProcessUtils.initUidPkgCache();
			Message message = new Message();
			message.what = 1;
			updateHandler.sendMessage(message);
		}

	}

	public void getInstalledApp() {
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		ArrayList<AppInfo> tempList = new ArrayList<AppInfo>();
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
					getPackageManager()).toString();
			tmpInfo.packageName = packageInfo.packageName;
			tmpInfo.versionName = packageInfo.versionName;
			tmpInfo.versionCode = packageInfo.versionCode;
			tmpInfo.appIcon = packageInfo.applicationInfo
					.loadIcon(getPackageManager());
			// 非系统应用先加列表
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appList.add(tmpInfo); 
			} 
			else
			{
				tempList.add(tmpInfo);
			}
		}
		// 系统应用加在列表后面
		appList.addAll(tempList);
	}

	protected void onResume() {
		super.onResume();

	}

	public class AppAdapter extends BaseAdapter {

		Context context;

		public AppAdapter(Context context, ArrayList<AppInfo> inputDataList) {
			this.context = context;
			dataList.clear();
			for (int i = 0; i < inputDataList.size(); i++) {
				dataList.add(inputDataList.get(i));
			}
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			final AppInfo appUnit = dataList.get(position);
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.gw_showapp_row, null);
			}
			TextView appName = (TextView) v.findViewById(R.id.appName);
			ImageView appIcon = (ImageView) v.findViewById(R.id.icon);
			if (appName != null)
				appName.setText(appUnit.appName);
			if (appIcon != null)
				appIcon.setImageDrawable(appUnit.appIcon);
			return v;
		}
	}

	private void backToSetting(int pos) {
		AppInfo appSelected = dataList.get(pos);
		AUTManager.pkn= appSelected.packageName;
		AUTManager.apn = appSelected.appName;
		AUTManager.appic = appSelected.appIcon;
		Env.CUR_APP_NAME = appSelected.packageName;
		Env.CUR_APP_VER = appSelected.versionName;

		// 清除旧的AUT_CLIENT
		ClientManager.getInstance().removeClient(ClientManager.AUT_CLIENT);

		// 创建新的AUT_CLIENT
		ClientFactory cf = new SingleInstanceClientFactory();
		cf.orderClient(
				ClientManager.AUT_CLIENT, ClientManager.AUT_CLIENT.hashCode(), null, null);

		// MTA记录选中的AUT
		Properties prop = new Properties();
		prop.setProperty("pkgName", AUTManager.pkn);
		StatService.trackCustomKVEvent(this, "Selected AUT", prop);
		this.finish();
	}
}