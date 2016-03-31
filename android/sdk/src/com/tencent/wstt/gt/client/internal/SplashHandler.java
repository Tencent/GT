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

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

import com.tencent.wstt.gt.GTConfig;

/**
 * GT启动相关逻辑的Handler处理类
 */
public class SplashHandler extends Handler {

	WeakReference<Context> mContext;

	public static final int MSG_START_CONNECT_GT = 0x0;
	public static final int MSG_START_DISCONNECT_GT = 0x1;
	public static final int MSG_GT_SERVICE_CONNECTED = 0x2;
	public static final int MSG_GT_SERVICE_DISCONNECTED = 0x3;

	public SplashHandler(Context context) {
//		super(Looper.myLooper());
		super(Looper.getMainLooper()); // 这样写在自动化脚本里才能调起GT
		mContext = new WeakReference<Context>(context);
	}

	@Override
	public void handleMessage(Message msg) {
		if (mContext == null) {
			return;
		}
		Context context = mContext.get();
		if (mContext == null) {
			return;
		}

		switch (msg.what) {
		case MSG_START_CONNECT_GT: {
			// 连接GT Service,拉起GT
			Intent bhIntent = (Intent) msg.obj;
			context.bindService(bhIntent, GTInternal.INSTANCE.gtServiceConnection,
					Context.BIND_AUTO_CREATE);
		}
			break;
		case MSG_START_DISCONNECT_GT: {
			// 用户主动断连GT Service
			try{
				// 主动断连时，不会有GtServiceConnection.onServiceDisconnected方法被回调
				context.unbindService(GTInternal.INSTANCE.gtServiceConnection);
				GTInternal.INSTANCE.setConnState(GTInternal.INSTANCE.CONNECT_STATE_NOT_CONNECTED);
				GTInternal.INSTANCE.gtServiceConnection = null;
				GTInternal.INSTANCE.setGTService(null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
			break;
		case MSG_GT_SERVICE_CONNECTED: {
			// AIDL服务已连接，连接的AIDL接口先可用。后面判断是否控制台已连接其他应用
			GTInternal.INSTANCE.initComImpl();
			int connRes = GTInternal.INSTANCE.checkIsCanConnect(context.getPackageName());
			if(connRes == GTConfig.CONNECT_RES_CODE_OK){
				// 先把GT启动起来
				GTInternal.INSTANCE.initConnectGT(context.getPackageName(), android.os.Process.myPid());
				// 控制台认定连接有效后，正式设置状态为已连接
				GTInternal.INSTANCE.setConnState(GTInternal.INSTANCE.CONNECT_STATE_CONNECTED);
			}else if (connRes == GTConfig.CONNECT_RES_CODE_REFUSE) {
				openToast(context, "GT has connected conflict APP, this connection failed!");
				this.sendEmptyMessage(MSG_GT_SERVICE_DISCONNECTED);
			}else if (connRes == GTConfig.CONNECT_RES_CODE_VERSION_INVALID) {
				// 版本不配套，提示用户到网站上更新最新版本
				openToast(context, "the current version of GT SDK does not match the GT Console," +
						" please download the lastest version of them");
				this.sendEmptyMessage(MSG_GT_SERVICE_DISCONNECTED);
			}
		}
			break;
		case MSG_GT_SERVICE_DISCONNECTED: {
			// 服务已断接，重置GT各操作为本地空实现
			//之前存在一个问题，就是客户端主动断连后，手动退出GT，如果不关闭客户端，GT会自动被重新启动，而且会默默的连上客户端
			//尝试在客户端主动断连后，将客户端的用于bind服务端的service置成null，就解决了这个问题
			//猜测的原因是，如果不置为null，这个service会不停的要连服务端。。。连服务端。。。。就无情的把服务端勾引起来了
			//置为null，没有东西再去主动的连服务端的service，也就不会把服务端整起来了
			GTInternal.INSTANCE.setConnState(GTInternal.INSTANCE.CONNECT_STATE_NOT_CONNECTED);
			/*
			 * add by yoyoqin on 20130730
			 * 杀死GT控制台时，客户端会走这个逻辑，此时需要主动unbind，
			 *  否则客户端会自动重连服务
			 */
			try{
				context.unbindService(GTInternal.INSTANCE.gtServiceConnection);
			}catch(Exception e){
				e.printStackTrace();
			}
			GTInternal.INSTANCE.gtServiceConnection = null;
			GTInternal.INSTANCE.setGTService(null);
		}
			break;
		}
	}
	
	private void openToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
