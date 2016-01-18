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
package test.tencent.wstt.gt.receiver;

import com.tencent.wstt.gt.client.GT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSMockReceiver extends BroadcastReceiver {
	public static final String ACTION_GPS_MOCK = "com.tencent.wstt.gt.ACTION_GPS_MOCK";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String type = intent.getStringExtra("type");
		if (! ACTION_GPS_MOCK.equals(action))
		{
			return;
		}
		if (null != type && "start".equals(type))
		{
			GT.logD("gpsmock", "start...");
		}
		else if (null != type && "end".equals(type))
		{
			GT.logD("gpsmock", "end...");
		}
		else if (null != type && "stop".equals(type))
		{
			GT.logD("gpsmock", "stop...");
		}
	}
}
