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
package com.tencent.wstt.gt.utils;

import com.tencent.wstt.gt.GTApp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationHelper {
	// 1.实例化Notification类
	// 2.设置Notification对象的icon，通知文字，声音
	// 3.实例化PendingIntent类，作为控制点击通知后显示内容的对象
	// 4.加载PendingIntent对象到Notification对象（设置 打开通知抽屉后的 标题/内容）
	// 5.获得 NotificationManager对象
	// 6.使用NotificationManager对象显示通知

	/**
	 * 发布通知
	 * 
	 * @param c
	 *            上下文
	 * @param notifyId
	 *            通知标识id
	 * @param n
	 *            通知对象
	 */
	static public void notify(Context c, int notifyId, Notification n) {
		final NotificationManager nm = (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// 显示通知
		nm.notify(notifyId, n);
	}

	/**
	 * 生成Notification对象
	 * 
	 * @param c
	 *            上下文
	 * @param notifyId
	 *            通知标识id
	 * @param iconResId
	 *            显示的icon的id
	 * @param notifyShowText
	 *            显示的文字
	 * @param soundResId
	 *            声音 - 没有使用（可以自己加）
	 * @param titleText
	 *            打开通知抽屉后的标题
	 * @param contentText
	 *            打开通知抽屉后的内容
	 * @param cls
	 *            点击后打开的类
	 * @param flag
	 *            通知标签
	 * @return 返回Notification对象
	 */
	static public Notification genNotification(Context c, int notifyId,
			int iconResId, String notifyShowText, int soundResId,
			String titleText, String contentText, Class<?> cls, boolean ongoing, boolean autoCancel,
			int notify_way) {

		Intent intent = null;
		if (cls != null) intent = new Intent(c, cls);
		
		// 控制点击通知后显示内容的类
		final PendingIntent pi = PendingIntent.getActivity(c, 0, // requestCode
																 // 现在是没有使用的，所以任意值都可以
				intent, 0 // PendingIntent的flag，在update这个通知的时候可以加特别的flag
				);
		
		Notification.Builder builder = new Notification.Builder(c)
				.setContentTitle(titleText)
				.setContentText(contentText)
				.setContentIntent(pi)
				.setSmallIcon(iconResId)
				.setWhen(System.currentTimeMillis())
				.setOngoing(ongoing)
				.setAutoCancel(autoCancel)
				.setDefaults(notify_way);
		if (soundResId == 0)
		{
			builder.setSound(Uri.parse(GTApp.getContext().getFilesDir().getPath()
					+ FileUtil.separator + "greattit.mp3"));
		}
		else if (soundResId == 1)
		{
			
		}
		else
		{
			builder.setDefaults(DEFAULT);
		}

		Notification notification = builder.getNotification();

		return notification;
	}

	/**
	 * 取消消息
	 * 
	 * @param c
	 * @param notifyId
	 * @return void
	 */
	public static void cancel(Context c, int notifyId) {
		((NotificationManager) ((Activity) c)
				.getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(notifyId);
	}

	// flags
	final static public int FLAG_ONGOING_EVENT = Notification.FLAG_ONGOING_EVENT;
	final static public int FLAG_AUTO_CANCEL = Notification.FLAG_AUTO_CANCEL;
	final static public int DEFAULT = Notification.DEFAULT_ALL;
	final static public int DEFAULT_VB = Notification.DEFAULT_VIBRATE;
	// DEFAULT_ALL 使用所有默认值，比如声音，震动，闪屏等等
	//
	// DEFAULT_LIGHTS 使用默认闪光提示
	//
	// DEFAULT_SOUNDS 使用默认提示声音
	//
	// DEFAULT_VIBRATE 使用默认手机震动

}