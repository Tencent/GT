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
package com.tencent.wstt.gt.plugin.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.plugin.BaseService;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class PluginService extends Service {

	private static final HashMap<Class<?>, BaseService> mServiceMap = new HashMap<Class<?>, BaseService>();
	private static final HashMap<Class<?>, ArrayList<BaseServiceConnection>> mServiceConnections =
			new HashMap<Class<?>, ArrayList<BaseServiceConnection>>();
	private static Handler handler;

	public static Handler getRootHandler()
	{
		return handler;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mServiceMap.clear();
		mServiceConnections.clear();
		handler = new Handler();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		String action = intent == null ? null : intent.getAction();

		if (action != null) {
			// TODO
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return 1;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		synchronized (BaseService.class) {
			for (BaseService service : mServiceMap.values()) {
				service.onDestroy();
			}
			mServiceMap.clear();
		}
		mServiceConnections.clear();
		super.onDestroy();
	}
	
	public static BaseService startService(BaseService service, Intent intent) {
		synchronized (BaseService.class) {
			if (mServiceMap.containsKey(service.getClass())) {
				((BaseService) mServiceMap.get(service.getClass()))
						.onStart(intent);
			} else {
				service.onCreate(GTApp.getContext());
				service.onStart(intent);
				mServiceMap.put(service.getClass(), service);
			}
			return service;
		}
	}
	
	public static BaseService startService(BaseService service) {
		return startService(service, null);
	}
	
	public static boolean stopService(Class<? extends BaseService> claxx) {
		synchronized (BaseService.class) {
			if (mServiceMap.containsKey(claxx)) {
				List<BaseServiceConnection> serviceCons = mServiceConnections.get(claxx);
				if ((null == serviceCons) || (serviceCons.size() == 0)) {
					BaseService theService = (BaseService) mServiceMap.get(claxx);
					theService.onDestroy();
					mServiceMap.remove(claxx);
					mServiceConnections.remove(claxx);
					return true;
				}
				return false;
			}

			return true;
		}
	}
	
	public static synchronized boolean stopService(BaseService service) {
		return stopService(service.getClass());
	}
	
	public static IBinder bindService(Class<? extends BaseService> claxx,
			BaseServiceConnection connection) {
		synchronized (BaseService.class) {
			IBinder binder = null;
			BaseService service = (BaseService) mServiceMap.get(claxx);
			if (service != null) {
				binder = service.getBinder();
				ArrayList<BaseServiceConnection> serviceCons = mServiceConnections
						.get(claxx);
				if (null == serviceCons) {
					serviceCons = new ArrayList<BaseServiceConnection>(1);
					mServiceConnections.put(claxx, serviceCons);
				}
				serviceCons.add(connection);
			}

			return binder;
		}
	}
	
	public static void unBindService(Class<? extends BaseService> claxx,
			BaseServiceConnection connection) {
		synchronized (BaseService.class) {
			List<BaseServiceConnection> serviceCons = mServiceConnections.get(claxx);
			if (null != serviceCons)
				serviceCons.remove(connection);
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
