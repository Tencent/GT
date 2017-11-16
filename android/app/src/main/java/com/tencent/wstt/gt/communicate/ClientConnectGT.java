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
package com.tencent.wstt.gt.communicate;

import java.util.Properties;

import android.content.Context;

import com.tencent.stat.StatService;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.activity.GTEntrance;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientFactory;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.ConnectedClientFactory;

public class ClientConnectGT
{
	/**
	 * 
	 * @param cur_pkgName  当前请求连接的进程
	 * @return true可以连接；false不可以连接 
	 */
	public static int checkIsCanConnect(String cur_pkgName, int versionId){
		if (versionId != GTConfig.INTERVAL_VID)
		{
			return GTConfig.CONNECT_RES_CODE_VERSION_INVALID;
		}

		// 如果判断client已存在，就不要重复发统计事件了
		if (null != ClientManager.getInstance().getClient(cur_pkgName))
		{
			Properties prop = new Properties();
			prop.setProperty("pkgName", cur_pkgName);
			StatService.trackCustomKVEvent(GTApp.getContext(), "Connected AUT", prop);
		}

		return GTConfig.CONNECT_RES_CODE_OK;
	}

	/**
	 * 这个接口暴露到service中，由客户端连接时调用
	 * @param pkgName 连接来的客户端的包名
	 * @param uid 连接来的客户端的UID，以UID作为连接来客户端的数字key
	 * @param pid 连接来的客户端的进程ID，支持多应用后本参数不再需要
	 */
	public static void initConnectGT(String pkgName, int intKey, int pid) {
		if(!GTApp.getGTRunStatus())
		{
			Context gtContext = GTApp.getContext();
			openGTService(gtContext);
		}
		
		Client client = ClientManager.getInstance().getClient(pkgName);
		if (null == client)
		{
			ClientFactory cf = new ConnectedClientFactory();
			/*
			 * TODO 批量注册的事情是否挪到这里呢，需要考虑老版本兼容性
			 * 如果挪到这里来，在GTBinder里的register方法就不需要考虑初始化的特殊情况了
			 * 结论：对客户端修改量太大，先按原方式处理
			 */
			client = cf.orderClient(pkgName, intKey, null, null);
		}	
	}

	public static boolean disconnectGT(String pkgName){
		boolean result = true;
		try {
			Client client = ClientManager.getInstance().getClient(pkgName);
			if (null != client)
			{
				client.clear();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	private static void openGTService(Context context){
		GTEntrance.GTopen(context);
	}
}
