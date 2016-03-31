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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;

public class GTMemFillActivity extends GTBaseActivity implements GTMemFillListener {

//	private static boolean switch_state = false;
	private EditText et_Num;
	private TextView tv_switch;
	private static int fillNum = 200;
	private ProgressDialog proDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pi_memfill);
		
		et_Num = (EditText)findViewById(R.id.et_num);
		et_Num.setText(Integer.toString(fillNum));
		et_Num.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		
		tv_switch = (TextView)findViewById(R.id.net_switch);
		tv_switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(GTMemFillEngine.getInstance().isFilled()){
					GTMemFillEngine.getInstance().free();
				}else{
					try {
						fillNum  = Integer.parseInt(et_Num.getText().toString());
						GTMemFillEngine.getInstance().fill(fillNum);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
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
		
		GTMemFillEngine.getInstance().addListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		et_Num.setText(Integer.toString(fillNum));
		
		if(GTMemFillEngine.getInstance().isFilled()){
			tv_switch.setBackgroundResource(R.drawable.switch_off_border);
			tv_switch.setText(getString(R.string.pi_memfill_off));
		}else{
			tv_switch.setBackgroundResource(R.drawable.switch_on_border);
			tv_switch.setText(getString(R.string.pi_memfill_on));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		GTMemFillEngine.getInstance().removeListener(this);
	}

	@Override
	public void onFillStart() {
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				tv_switch.setBackgroundResource(R.drawable.switch_off_border);
				tv_switch.setText(getString(R.string.pi_memfill_off));
				
				proDialog = ProgressDialog.show(GTMemFillActivity.this,
						"Filling..", "filling..wait....", true, true);
			}});
	}

	@Override
	public void onFillEnd() {
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				proDialog.dismiss();
			}});
	}

	@Override
	public void onFillFail(String errorstr) {
		
	}

	@Override
	public void onFree() {
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				tv_switch.setBackgroundResource(R.drawable.switch_on_border);
				tv_switch.setText(getString(R.string.pi_memfill_on));
			}});
	}
}
