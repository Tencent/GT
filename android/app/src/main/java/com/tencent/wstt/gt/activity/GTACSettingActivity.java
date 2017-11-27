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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.service.GTFloatView;
import com.tencent.wstt.gt.service.GTLogo;
import com.tencent.wstt.gt.views.GTCheckBox;

public class GTACSettingActivity extends GTBaseActivity implements CompoundButton.OnCheckedChangeListener {
	
	private RadioGroup show_quickswitch = null;
	private RadioButton show_profiler = null;
	private RadioButton show_gw = null;
	private static int switch_type = 1; // 0表示显示profiler 1 表示显示gw

	public static final int PROFILER = 0;
	public static final int GW = 1;
	
	// 是否显示悬浮框
	private boolean isShow;
	private GTCheckBox cb_show_Switch;
	
	public static int getSwitchType()
	{
		return switch_type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_floatview_setting);
		TextView tv_back = (TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(back);

		show_quickswitch = (RadioGroup) findViewById(R.id.show_quickswitch);
		show_profiler = (RadioButton) findViewById(R.id.show_profiler);
		show_gw = (RadioButton) findViewById(R.id.show_gw);
		
		cb_show_Switch = (GTCheckBox)findViewById(R.id.cb_switch);
		isShow = GTPref.getGTPref().getBoolean(GTPref.AC_SWITCH_FLAG, true);
	}

	private class OnCheckedChangeListenerImp implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			if (GTACSettingActivity.this.show_profiler.getId() == checkedId) {
				GTACSettingActivity.switch_type = PROFILER;
				GTPref.getGTPref().edit().putInt(GTPref.AC_SWITCH, PROFILER).commit();
			}
			else if (GTACSettingActivity.this.show_gw.getId() == checkedId) {
				GTACSettingActivity.switch_type = GW;
				GTPref.getGTPref().edit().putInt(GTPref.AC_SWITCH, GW).commit();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		cb_show_Switch.setOnCheckedChangeListener(null);
		this.show_quickswitch.setOnCheckedChangeListener(null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		cb_show_Switch.setChecked(isShow);
		cb_show_Switch.setOnCheckedChangeListener(this);

		switch_type = GTPref.getGTPref().getInt(GTPref.AC_SWITCH, GW);

		switch(switch_type)
		{
			case PROFILER:{
				show_profiler.setChecked(true);
				break;
			}
			case GW:
			default:{
				show_gw.setChecked(true);
				break;
			}
		}

		// 监听放在上面初始setChecked后面，避免提前触发监听造成逻辑混乱
		this.show_quickswitch
				.setOnCheckedChangeListener(new OnCheckedChangeListenerImp());
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		isShow = isChecked;
		GTPref.getGTPref().edit().putBoolean(GTPref.AC_SWITCH_FLAG, isShow).commit();
		
		if (isShow)
		{
			Intent intent = new Intent(this, GTLogo.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(intent);

			Intent mintent = new Intent(this, GTFloatView.class);
			mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(mintent);
		}
		else
		{
			Intent intent = new Intent(this, GTLogo.class);
			stopService(intent);
			
			Intent FVintent = new Intent(this, GTFloatView.class);
			stopService(FVintent);
		}
	}

	private OnClickListener back = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};
}
