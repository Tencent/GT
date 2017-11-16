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
package com.tencent.wstt.gt.internal;

import android.os.Handler;
import android.os.Message;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTMainActivity;
import com.tencent.wstt.gt.utils.NotificationHelper;

public class DaemonHandler extends Handler {
	
	public static final int MEM_TOP_WARNING_FLAG = 0;
	public static final int MEM_SECOND_WARNING_FLAG = 1;
	public static final int MEM_THIRD_WARNING_FLAG = 3;
	public static final int MEM_SINGLE_WARNING_FLAG = 4;
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MEM_TOP_WARNING_FLAG: // 发消息提示用户，通知
			GTMainActivity.notification = NotificationHelper
			.genNotification(GTApp.getContext(), 0,
					R.drawable.gt_entrlogo, "GT", -1,
					"GT:Memory Waring",
					"More than " + GTMemoryDaemonThread.topLevelLimit + " GW and Prof records.",
					GTMainActivity.class,
					false, true,
					NotificationHelper.DEFAULT_VB);
	
			NotificationHelper.notify(GTApp.getContext(), 31,
					GTMainActivity.notification);
			break;
			
		case MEM_SECOND_WARNING_FLAG: // 发消息提示用户，通知
			GTMainActivity.notification = NotificationHelper
			.genNotification(GTApp.getContext(), 0,
					R.drawable.gt_entrlogo, "GT", -1,
					"GT:Memory Waring",
					"More than " + GTMemoryDaemonThread.secondLevelLimit + " GW and Prof records.",
					GTMainActivity.class,
					false, true,
					NotificationHelper.DEFAULT_VB);
	
			NotificationHelper.notify(GTApp.getContext(), 32,
					GTMainActivity.notification);
			break;
		
		case MEM_THIRD_WARNING_FLAG:
			GTMainActivity.notification = NotificationHelper
			.genNotification(GTApp.getContext(), 0,
					R.drawable.gt_entrlogo, "GT", -1,
					"GT:Memory Waring",
					"More than " + GTMemoryDaemonThread.thirdLevelLimit + " GW and Prof records.",
					GTMainActivity.class,
					false, true,
					NotificationHelper.DEFAULT_VB);
	
			NotificationHelper.notify(GTApp.getContext(), 32,
					GTMainActivity.notification);
			break;
		case MEM_SINGLE_WARNING_FLAG: // 发消息提示用户，通知
			OutPara op = (OutPara)(msg.obj);
			GTMainActivity.notification = NotificationHelper
					.genNotification(GTApp.getContext(), 0,
							R.drawable.gt_entrlogo, "GT", -1,
							"GT:Memory Waring",
							"OutPara " + op.getAlias() +" has more than " + GTMemoryDaemonThread.singleLimit + " records." ,
							GTMainActivity.class,
							false, true,
							NotificationHelper.DEFAULT_VB);
			
			NotificationHelper.notify(GTApp.getContext(), 33,
					GTMainActivity.notification);
			break;
		}
	}
}
