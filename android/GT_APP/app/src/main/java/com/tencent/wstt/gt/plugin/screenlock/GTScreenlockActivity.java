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
package com.tencent.wstt.gt.plugin.screenlock;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;
import com.tencent.wstt.gt.views.GTCheckBox;

public class GTScreenlockActivity extends GTBaseActivity {
	
	private static final String TAG = "GTScreenlockActivity";
	
	private TextView back_gt;

	private GTCheckBox tb_full;
	private GTCheckBox tb_partial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pi_screenlock);
		
		back_gt = (TextView)findViewById(R.id.screenlock_back_gt);
		back_gt.setOnClickListener(back);

		tb_full = (GTCheckBox)findViewById(R.id.screenlock_toggle_wakelock);
		tb_partial = (GTCheckBox)findViewById(R.id.screenlock_toggle_partiallock);

		tb_full.setChecked(ScreenWakeLock.flag);
		tb_partial.setChecked(PartialWakeLock.flag);
		
		tb_full.setOnClickListener(fullWakelockClickListener);
		tb_partial.setOnClickListener(partialWakelockClickListener);
		
	}
	
	private OnClickListener back = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	
	private OnClickListener fullWakelockClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ScreenWakeLock.toggle(getBaseContext());
			tb_full.setChecked(ScreenWakeLock.flag);
		}
	};

	private OnClickListener partialWakelockClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			PartialWakeLock.toggle(getBaseContext());
			tb_partial.setChecked(PartialWakeLock.flag);
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();

	}
}
