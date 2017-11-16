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
package com.tencent.wstt.gt.plugin;

import android.content.Intent;
import android.os.IBinder;

import com.tencent.wstt.gt.plugin.internal.BaseServiceConnection;
import com.tencent.wstt.gt.plugin.internal.PluginService;

public class PluginControler {
	protected BaseServiceConnection mServiceConn = new BaseServiceConnection(PluginControler.class);
	
	/**
	 * 启动插件服务
	 * @param service 插件服务
	 * @return 插件服务对象(即传进去的参数)
	 */
	public final BaseService startService(BaseService service) {
		return PluginService.startService(service);
	}
	
	/**
	 * 启动插件服务
	 * @param service 插件服务
	 * @param intent 传入的intent
	 * @return 插件服务对象(即传进去的参数)
	 */
	public final BaseService startService(BaseService service, Intent intent) {
		return PluginService.startService(service, intent);
	}
	
	/**
	 * 停止插件服务,之前要先调用unBindService方法断开服务
	 * @param service 插件服务
	 */
	public final void stopService(BaseService service){
		PluginService.stopService(service.getClass());
	}
	
	/**
	 * 断开插件服务
	 * @param claxx 插件服务的类
	 */
	public final void unBindService(Class<? extends BaseService> claxx) {
		PluginService.unBindService(claxx, mServiceConn);
		mServiceConn = null;
	}
	
	/**
	 * 绑定插件服务
	 * @param claxx 插件服务的类
	 * @return 绑定接口
	 */
	public final IBinder bindService(Class<? extends BaseService> claxx) {
		return PluginService.bindService(claxx, mServiceConn);
	}
}
