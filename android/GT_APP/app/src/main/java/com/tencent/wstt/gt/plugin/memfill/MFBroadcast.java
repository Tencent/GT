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
package com.tencent.wstt.gt.plugin.memfill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MFBroadcast extends BroadcastReceiver {
	public static final String MEM_FILL_ACTION = "com.tencent.wstt.gt.plugin.memfill.fill";
	public static final String MEM_FREE_ACTION = "com.tencent.wstt.gt.plugin.memfill.free";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			if (action != null && action.equals(MEM_FILL_ACTION)) {
				if (intent != null) {
					int size = intent.getIntExtra("size", 200);
					if (size > 0)
					{
						GTMemFillEngine.getInstance().fill(size);
					}
				}
			} else if (action != null && action.equals(MEM_FREE_ACTION)) {
				GTMemFillEngine.getInstance().free();
			}
		} catch (Exception e) {
			Log.e("GT", "error on MFBroadcast.onReceive()...");
		}
	}
}
