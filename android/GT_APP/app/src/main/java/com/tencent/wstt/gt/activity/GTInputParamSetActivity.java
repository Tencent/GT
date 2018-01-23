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

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientManager;

public class GTInputParamSetActivity extends GTBaseActivity {
	
	private AutoCompleteTextView autoTV;
	private ArrayList<String> values;
	private String ip_name;
	private String ip_client;
	private Button btn_c;
	public static boolean fromfinish = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_inputparamset);

		TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel_setinput);
		tv_cancel.setOnClickListener(cancel);
		TextView tv_finish = (TextView) findViewById(R.id.tv_finish_setinput);
		tv_finish.setOnClickListener(finish);

		Intent intent = getIntent();
		ip_name = intent.getStringExtra("ip_name");
		ip_client = intent.getStringExtra("ip_client");
		values = intent.getStringArrayListExtra("ip_values");

		// 将list转为linkedlist，队列类型去init autoTV
		ArrayList<String> linked_vals = new ArrayList<String>();
		for (int j = 0; j < values.size(); j++) {
			linked_vals.add(values.get(j));
		}

		TextView tv_ip_name = (TextView) findViewById(R.id.name_inputparam);
		tv_ip_name.setText(ip_name);

		autoTV = (AutoCompleteTextView) findViewById(R.id.autoTV_ipvalues);
		initAutoCompleteTV(linked_vals, autoTV);

		btn_c = (Button) findViewById(R.id.autoTV_cancel);
		btn_c.setOnClickListener(c);

	}

	private OnClickListener c = new OnClickListener() {
		public void onClick(View v) {
			autoTV.setText("");
		} 
	};
    
	private void initAutoCompleteTV(ArrayList<String> vals, final AutoCompleteTextView auto){

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.gt_drop_default_listitem, vals);

		auto.setDropDownBackgroundResource(R.drawable.btn_gray);
		
		auto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				auto.showDropDown();
			}
		});
		auto.setText(vals.get(0));
		auto.setSelection(vals.get(0).length());
		auto.setAdapter(adapter);
		auto.setThreshold(1);
		auto.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(auto.getText().toString().equals("")){
					btn_c.setVisibility(View.GONE);
				}else{
					btn_c.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {		
			}
		});
		auto.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				auto.showDropDown();
			}
		});
		
	}

	
	private OnClickListener cancel = new OnClickListener() {
		public void onClick(View v) {			
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(GTInputParamSetActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finish();	
		}
	};
	
	private OnClickListener finish = new OnClickListener() {
		public void onClick(View v) {
			fromfinish = true;
			//先判断下有没有输入为空
			autoTV = (AutoCompleteTextView)findViewById(R.id.autoTV_ipvalues);
			String newValue = autoTV.getText().toString();
			if(newValue.equals("") || newValue.trim().equals("")){
				openToast("入参不能设置为空");
			}else{
				//判断下，是历史值还是新值
				//是历史值，则不用处理原来的values，只是需要把value的位置进行调换
				int locationAtValues = values.indexOf(newValue);
				ArrayList<String> new_values = new ArrayList<String>();
				if(-1 != locationAtValues ){
					new_values.addAll(values);
					new_values.remove(locationAtValues);
					new_values.add(0, newValue);
					
				}else{
					new_values.add(newValue);
					new_values.addAll(values);
				}

				Client client = ClientManager.getInstance().getClient(ip_client);
				InPara inPara = client.getInPara(ip_name);
				inPara.setValues(new_values);

				initAutoCompleteTV(new_values, autoTV);
				
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(GTInputParamSetActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				finish();
				
			}
		}
	};
	
	private void openToast(String message) {
		Toast toast = Toast.makeText(GTInputParamSetActivity.this, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
