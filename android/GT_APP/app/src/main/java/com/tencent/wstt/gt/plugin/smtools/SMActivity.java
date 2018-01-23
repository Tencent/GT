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
package com.tencent.wstt.gt.plugin.smtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.api.utils.ProcessUtils.ProcessInfo;
import com.tencent.wstt.gt.utils.ToastUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SMActivity extends GTBaseActivity implements SMPluginListener {

	private ListView listview = null;
	private ArrayList<String> data = new ArrayList<String>();
	private static String selectedItem = null;

	private TextView tv_switch;
	
	private Integer pid = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pi_othersm);

		Button buttonCheckStatus = (Button) findViewById(R.id.buttonCheckStatus);
		buttonCheckStatus.setOnClickListener(button_check_status);

		Button buttonWriteProperty = (Button) findViewById(R.id.buttonWriteProperty);
		buttonWriteProperty.setOnClickListener(button_write_property);

		Button buttonRecoverProperty = (Button) findViewById(R.id.buttonRecoverProperty);
		buttonRecoverProperty.setOnClickListener(button_recover_property);

		Button buttonRestart = (Button) findViewById(R.id.buttonRestart);
		buttonRestart.setOnClickListener(button_restart);

		tv_switch = (TextView) findViewById(R.id.sample_switch);
		if (SMServiceHelper.getInstance().isStarted()) {
			tv_switch.setBackgroundResource(R.drawable.switch_off_border);
			tv_switch.setText(getString(R.string.stop));
		} else {
			tv_switch.setBackgroundResource(R.drawable.switch_on_border);
			tv_switch.setText(getString(R.string.start));
		}
		tv_switch.setOnClickListener(onStartClick);

		TextView tv_back = (TextView) findViewById(R.id.back_gt);
		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listview = (ListView) findViewById(R.id.listViewOtherSM);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		ArrayList<String> datas = getData();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,
				datas);
		listview.setAdapter(adapter);
		if (datas != null && selectedItem != null) {
			int position = datas.indexOf(selectedItem);
			if (position >= 0) {
				listview.setItemChecked(position, true);
			}
		}

		listview.setOnItemClickListener(listview_listener);

		SMServiceHelper.getInstance().addListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SMServiceHelper.getInstance().removeListener(this);
	}

	private ArrayList<String> getData() {
		List<ProcessInfo> rp = ProcessUtils.getAllRunningAppProcessInfo();
		for (ProcessInfo i : rp) {
			data.add(i.name);
		}
		return data;
	}

	View.OnClickListener button_check_status = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String cmd = "getprop debug.choreographer.skipwarning";
			ProcessBuilder execBuilder = new ProcessBuilder("sh", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				TextView textview = (TextView) findViewById(R.id.textviewInformation);
				Process p = execBuilder.start();
				InputStream is = p.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				Boolean flag = false;
				String line;
				while ((line = br.readLine()) != null) {
					if (line.compareTo("1") == 0) {
						flag = true;
						break;
					}
				}

				if (flag) {
					textview.setText("OK");
				} else {
					textview.setText("NOT OK");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	View.OnClickListener button_write_property = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String cmd = "setprop debug.choreographer.skipwarning 1";
			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				execBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	View.OnClickListener button_recover_property = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String cmd = "setprop debug.choreographer.skipwarning 30";
			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				execBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	View.OnClickListener button_restart = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String cmd = "setprop ctl.restart surfaceflinger; setprop ctl.restart zygote";
			ProcessBuilder execBuilder = new ProcessBuilder("su", "-c", cmd);
			execBuilder.redirectErrorStream(true);
			try {
				execBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	AdapterView.OnItemClickListener listview_listener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
			selectedItem = (String) adapter.getItemAtPosition(position);
			pid = ProcessUtils.getProcessPID(selectedItem);
		}
	};

	View.OnClickListener onStartClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (SMServiceHelper.getInstance().isStarted()) {
				SMServiceHelper.getInstance().stopBackgroundServiceIfRunning(SMActivity.this);
			} else {
				if (null == selectedItem) {
					ToastUtil.ShowLongToast(SMActivity.this, "select a app first!");
				}
				else
				{
					SMServiceHelper.getInstance().startBackgroundService(SMActivity.this, pid, selectedItem);
				}
			}
		}
	};

	@Override
	public void onSMStart() {
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				tv_switch.setBackgroundResource(R.drawable.switch_off_border);
				tv_switch.setText(getString(R.string.stop));
			}
		});
	}

	@Override
	public void onSMStop() {
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				tv_switch.setBackgroundResource(R.drawable.switch_on_border);
				tv_switch.setText(getString(R.string.start));
			}
		});
	}
}
