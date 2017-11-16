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
package com.tencent.wstt.gt.plugin.battery;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;
import com.tencent.wstt.gt.utils.ToastUtil;

public class GTBatteryActivity extends GTBaseActivity
	implements BatteryPluginListener, OnCheckedChangeListener {

	private TextView battery_back_gt;

	private TextView tv_switch;
	private EditText et_refresh_rate;
	private EditText et_brightness;

	private CheckBox cb_I;
	private CheckBox cb_U;
	private CheckBox cb_Power;
	private CheckBox cb_Temp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pi_battery);
		
		battery_back_gt = (TextView)findViewById(R.id.battery_back_gt);
		battery_back_gt.setOnClickListener(back);
		
		tv_switch = (TextView)findViewById(R.id.sample_switch);
		et_refresh_rate = (EditText)findViewById(R.id.et_refresh_rate);
		et_refresh_rate.setText(Integer.toString(GTBatteryEngine.getInstance().getRefreshRate()));
		et_refresh_rate.setInputType(EditorInfo.TYPE_CLASS_PHONE);

		et_brightness = (EditText)findViewById(R.id.et_brightness);
		if (GTBatteryEngine.getInstance().getBrightness() > 0)
		{
			et_brightness.setText(Integer.toString(GTBatteryEngine.getInstance().getBrightness()));
		}
		et_brightness.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		if (GTBatteryEngine.getInstance().isStarted())
		{
			tv_switch.setBackgroundResource(R.drawable.switch_off_border);
			tv_switch.setText(getString(R.string.stop));
		}
		else
		{
			tv_switch.setBackgroundResource(R.drawable.switch_on_border);
			tv_switch.setText(getString(R.string.start));
		}
		
		tv_switch.setOnClickListener(switchOnClickListener);
		
		cb_I = (CheckBox)findViewById(R.id.cb_I);
		cb_U = (CheckBox)findViewById(R.id.cb_U);
		cb_Power = (CheckBox)findViewById(R.id.cb_Power);
		cb_Temp = (CheckBox)findViewById(R.id.cb_Temp);

		cb_I.setChecked(GTBatteryEngine.getInstance().isState_cb_I());
		cb_U.setChecked(GTBatteryEngine.getInstance().isState_cb_U());
		cb_Power.setChecked(GTBatteryEngine.getInstance().isState_cb_P());
		cb_Temp.setChecked(GTBatteryEngine.getInstance().isState_cb_T());

		cb_I.setOnCheckedChangeListener(this);
		cb_U.setOnCheckedChangeListener(this);
		cb_Power.setOnCheckedChangeListener(this);
		cb_Temp.setOnCheckedChangeListener(this);

		GTBatteryEngine.getInstance().addListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		GTBatteryEngine.getInstance().removeListener(this);
	}
	
	private OnClickListener back = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	private OnClickListener switchOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if (! GTBatteryEngine.getInstance().isStarted())
			{
				int refreshRate = 250;
				int brightness = -1;
				try {
					refreshRate = Integer.parseInt(et_refresh_rate.getText().toString().trim());
				}
				catch(Exception e)
				{
//					ToastUtil.ShowShortToast(this, "输入的刷新率或亮度不是数字！");
					ToastUtil.ShowShortToast(GTBatteryActivity.this, getString(R.string.pi_battery_sample_tip));
					return;
				}
				
				
				if (! et_brightness.getText().toString().trim().equals("")) 
				{
					try {
						brightness = Integer.parseInt(et_brightness.getText().toString().trim());
					}
					catch(Exception e)
					{
//						ToastUtil.ShowShortToast(this, "输入的刷新率或亮度不是数字！");
						ToastUtil.ShowShortToast(GTBatteryActivity.this, getString(R.string.pi_battery_sample_tip));
						return;
					}
				}

				GTBatteryEngine.getInstance().doStart(refreshRate, brightness);
			}
			else
			{
				GTBatteryEngine.getInstance().doStop();
			}
		}
	};

	@Override
	public void onBatteryStart()
	{
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				tv_switch.setBackgroundResource(R.drawable.switch_off_border);
				tv_switch.setText(getString(R.string.stop));
			}
		});
	}

	@Override
	public void onBatteryStop()
	{
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				tv_switch.setBackgroundResource(R.drawable.switch_on_border);
				tv_switch.setText(getString(R.string.start));
//				ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_battery_sample_tip4);
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (GTBatteryEngine.getInstance().isStarted())
		{
			buttonView.setOnCheckedChangeListener(null);
			buttonView.setChecked(!isChecked);
			buttonView.setOnCheckedChangeListener(this);
			ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_battery_sample_tip3);
			return;
		}
		
		switch (buttonView.getId())
		{
		case R.id.cb_I:
			GTBatteryEngine.getInstance().updateI(isChecked);
			break;
		
		case R.id.cb_U:
			GTBatteryEngine.getInstance().updateU(isChecked);
			break;
		
		case R.id.cb_Power:
			GTBatteryEngine.getInstance().updateP(isChecked);
			break;
		
		case R.id.cb_Temp:
			GTBatteryEngine.getInstance().updateT(isChecked);
			break;
		}
	}

	@Override
	public void onBatteryException(final String e) {
		ToastUtil.ShowShortToast(GTBatteryActivity.this, e);
	}

	@Override
	public void onUpdateI(boolean isSelected) {
		cb_I.setOnCheckedChangeListener(null);
		cb_I.setChecked(isSelected);
		cb_I.setOnCheckedChangeListener(this);
	}

	@Override
	public void onUpdateU(boolean isSelected) {
		cb_U.setOnCheckedChangeListener(null);
		cb_U.setChecked(isSelected);
		cb_U.setOnCheckedChangeListener(this);
	}

	@Override
	public void onUpdateT(boolean isSelected) {
		cb_Temp.setOnCheckedChangeListener(null);
		cb_Temp.setChecked(isSelected);
		cb_Temp.setOnCheckedChangeListener(this);
	}

	@Override
	public void onUpdateP(boolean isSelected) {
		cb_Power.setOnCheckedChangeListener(null);
		cb_Power.setChecked(isSelected);
		cb_Power.setOnCheckedChangeListener(this);
	}
}
