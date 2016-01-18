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
package com.tencent.wstt.gt.plugin.netswitch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;
import com.tencent.wstt.gt.plugin.PluginManager;

public class GTNetSwitchActivity extends GTBaseActivity {
	private static boolean switch_state = false;
	private TextView tv_switch;
	public static Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pi_netswitch);
		
		tv_switch = (TextView)findViewById(R.id.net_switch);
		tv_switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(switch_state){

					PluginManager.getInstance().getPluginControler().stopService(GTNetSwitchFloatview.getInstance());

					switch_state = false;
					tv_switch.setBackgroundResource(R.drawable.switch_on_border);
					tv_switch.setText(getString(R.string.pi_netswitch_switch_on));
				}else{
					switch_state = true;
					tv_switch.setBackgroundResource(R.drawable.switch_off_border);
					tv_switch.setText(getString(R.string.pi_netswitch_switch_off));
				
					PluginManager.getInstance().getPluginControler().startService(GTNetSwitchFloatview.getInstance(), intent);
				}
			}
		});
	
		TextView tv_back = (TextView)findViewById(R.id.back_gt);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(switch_state){
			tv_switch.setBackgroundResource(R.drawable.switch_off_border);
			tv_switch.setText(getString(R.string.pi_netswitch_switch_off));
		}else{
			tv_switch.setBackgroundResource(R.drawable.switch_on_border);
			tv_switch.setText(getString(R.string.pi_netswitch_switch_on));
		}
	}
}
