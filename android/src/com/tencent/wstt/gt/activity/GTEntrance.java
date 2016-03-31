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
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.service.GTFloatView;
import com.tencent.wstt.gt.service.GTLogo;
import com.tencent.wstt.gt.service.GTService;

import android.content.Context;
import android.content.Intent;
/**
 * 开启关闭BH的入口
 */
public class GTEntrance {

	public static void GTopen(Context context) {
		// 启动Aidl服务
		Intent aidlIntent = new Intent(context, GTService.class);
		aidlIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(aidlIntent);

		// 插件加载完成后显示GT入口悬浮图标
		if ( GTPref.getGTPref().getBoolean(GTPref.AC_SWITCH_FLAG, true))
		{
			Intent intent = new Intent(context, GTLogo.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(intent);

			Intent mintent = new Intent(context, GTFloatView.class);
			mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(mintent);
		}

		GTApp.setGTRunStatus(true);
	}

	public static void GTclose(Context context) {
		Intent aidlIntent = new Intent(context, GTService.class);
		context.stopService(aidlIntent);
		
		if ( GTPref.getGTPref().getBoolean(GTPref.AC_SWITCH_FLAG, true))
		{
			Intent intent = new Intent(context, GTLogo.class);
			context.stopService(intent);
			
			Intent FVintent = new Intent(context, GTFloatView.class);
			context.stopService(FVintent);
		}
		try
		{
			ProcessUtils.killprocess(context.getPackageName(), 9);
		}
		catch (Exception e)
		{
			
		}
		System.exit(0);
	}
}