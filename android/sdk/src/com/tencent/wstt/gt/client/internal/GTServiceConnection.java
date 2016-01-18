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
package com.tencent.wstt.gt.client.internal;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.tencent.wstt.gt.IService;
import com.tencent.wstt.gt.client.GTConnectListener;

/**
 * 与GTService勾兑后的回调类
 */
class GTServiceConnection implements ServiceConnection {
	
	private SplashHandler hanlder;
	
	public GTServiceConnection(SplashHandler hanlder)
	{
		this.hanlder = hanlder;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		IService iService = IService.Stub.asInterface(service);
		GTInternal.INSTANCE.setGTService(iService);
		
		// 通知前台与GT服务已连接
		hanlder.sendEmptyMessage(SplashHandler.MSG_GT_SERVICE_CONNECTED);
		
		/*
		 * 执行用户注入的服务连接上的逻辑
		 */
		GTConnectListener gTConnectedListener =
				GTInternal.getInstance().getGTConnectListener();
		if (gTConnectedListener != null)
		{
			gTConnectedListener.onGTServiceBinded();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// 通知前台与GT服务已断连
		hanlder.sendEmptyMessage(SplashHandler.MSG_GT_SERVICE_DISCONNECTED);
//		this.hanlder = null;
	}

}
