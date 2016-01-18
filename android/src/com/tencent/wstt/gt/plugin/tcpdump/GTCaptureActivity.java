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
package com.tencent.wstt.gt.plugin.tcpdump;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.NetUtils;
import com.tencent.wstt.gt.api.utils.WidgetUtils;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;
import com.tencent.wstt.gt.utils.RootUtil;
import com.tencent.wstt.gt.views.GTCheckBox;

public class GTCaptureActivity extends GTBaseActivity {

	private EditText et_filename;
	private TextView tv_param_title;
	private FrameLayout fl_param;
	private TextView tv_tcpdump_back;
	private GTCheckBox cb_param_switch;
	private TextView tv_param_switch;
	private Button btn_param_clear;
	private EditText et_param;
	private TextView tv_switch;

	private ListView lv_tcpdump_progress;
	private static ArrayAdapter<String> adapter;
	private static List<String> list_input_stream = new ArrayList<String>();
	private List<String> list_temp = new ArrayList<String>();
	private Handler tcpdumpSwitchHandler;
	private ProgressDialog proDialog;

	public static String network_status = "";
	public static int count = 1;
	public static String filename_path;
	private static String foldername;
	private static String filename = "Capture";
	private static final String default_param = "-p -s 0 -vv -w";
	private static String param = default_param;

	private static boolean initOnCreate = true;
	private static boolean flag_listRefresh = true;
	private static boolean switch_param = true;
	public static boolean switch_tcpdump = false;
	private static boolean tcpdumpRunning = false;
	private static boolean cur_param_switch_status = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pi_capture);
		initLayout();

		tv_tcpdump_back = (TextView) findViewById(R.id.tcpdump_back_gt);
		tv_tcpdump_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		checkNetworkInfo();
		tv_param_switch = (TextView) findViewById(R.id.tcpdump_param_switch);
		tv_param_switch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!switch_param) {
					switch_param = true;
					tv_param_title.setVisibility(View.VISIBLE);
					fl_param.setVisibility(View.VISIBLE);
				} else {
					switch_param = false;
					et_param.setText(param);
					tv_param_title.setVisibility(View.GONE);
					fl_param.setVisibility(View.GONE);
				}
			}
		});

		btn_param_clear = (Button) findViewById(R.id.tcpdump_param_cancel);
		btn_param_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				param = "";
				et_param.setText(param);
			}
		});

		et_param = (EditText) findViewById(R.id.tcpdump_param);
		et_param.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
			}
		});

		tv_switch = (TextView) findViewById(R.id.tcpdump_switch);
		tv_switch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取权限过程比较耗时，交给菊花去处理
				proDialog = ProgressDialog.show(GTCaptureActivity.this,
						"get root..", "geting root..wait...", true, true);
				Thread t = new Thread(new ProgressRunnable());
				t.start();
			}
		});

		cb_param_switch = (GTCheckBox) findViewById(R.id.cb_param_switch);
		cb_param_switch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cur_param_switch_status = isChecked;
							tv_param_title.setVisibility(View.VISIBLE);
							fl_param.setVisibility(View.VISIBLE);

							String cur_param = et_param.getText().toString();
							if (cur_param.equals("")
									|| cur_param.trim().equals("")) {
								et_param.setText(default_param);
							}
						} else {
							cur_param_switch_status = isChecked;
							tv_param_title.setVisibility(View.GONE);
							fl_param.setVisibility(View.GONE);
						}
					}
				});

		tcpdumpSwitchHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0: // 抓包开始，控件状态置为红色，显示stop
					switch_tcpdump = true;
					tv_switch.setBackgroundResource(R.drawable.switch_off_border);
					tv_switch.setText(getString(R.string.stop));
					break;
				case 1: // 抓包结束，控件状态置为绿色，显示start
					switch_tcpdump = false;
					tv_switch.setBackgroundResource(R.drawable.switch_on_border);
					tv_switch.setText(getString(R.string.start));
					break;
				case 2:// 吐槽提示 TODO
					String message = msg.obj == null ? "" : msg.obj.toString();
					WidgetUtils.openToast(message);
					break;
				case 3:
					
					break;
				case 4:
	
					break;
				}
			}
		};
	}

	private void initLayout() {
		et_filename = (EditText) findViewById(R.id.et_tcpdump_filename);
		et_filename.setText(filename);
		tv_param_title = (TextView) findViewById(R.id.tcpdump_param_title);
		fl_param = (FrameLayout) findViewById(R.id.tcpdump_param_layout);

		if (initOnCreate) {
			initOnCreate = false;
			adapter = new ArrayAdapter<String>(this,
					R.layout.gt_tcpdump_listrow, list_input_stream);
		}
		lv_tcpdump_progress = (ListView) findViewById(R.id.tcpdump_progress);
		lv_tcpdump_progress.setAdapter(adapter);
		lv_tcpdump_progress.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (visibleItemCount + firstVisibleItem == totalItemCount) {
					flag_listRefresh = true;
				} else {
					flag_listRefresh = false;
				}
			}
		});
	}

	private boolean checkTcpDump() {
		Message message = tcpdumpSwitchHandler.obtainMessage();
		message.what = 2;
		
		// 判断手机是否root
		if (! RootUtil.isRooted()) {
			message.obj = "root needed!";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		}
		// 先判断是不是有sdcard,没sdcard就弹toast什么都不做
		if (!Env.isSDCardExist()) {
			message.obj = "sdcard needed!";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		}
		// 获取filename,filename为空弹toast什么都不做
		foldername = et_filename.getText().toString();
		if (foldername.equals("") || foldername.trim().equals("")) {
			message.obj = "foldername cannot be null!";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		}
		if (foldername.contains("\\") || foldername.contains("/")
				|| foldername.contains(":") || foldername.contains("*")
				|| foldername.contains("?") || foldername.contains("\"")
				|| foldername.contains("<") || foldername.contains(">")
				|| foldername.contains("|")) {
			message.obj = "foldername can't contain:\\/:*?\"<>|";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		}

		// 获取param,如果param为空则采用默认给的参数,并把默认给的参数放到UI中显示
		param = et_param.getText().toString();
		if (param.contains("|") || param.contains(">") || param.contains(">>")) {
			message.obj = "param can't contain: | > >>";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		}
		if (param.equals("") || param.trim().equals("")) {
			param = default_param;
			et_param.setText(default_param);
		}
		// 先把存储抓包文件的文件夹创建出来
		String dir = Env.S_ROOT_GT_FOLDER;
		if (!FileUtil.createDir(dir)) {
			message.obj = "folder creates failed!";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		}
		dir = dir + "tcpdump/";

		if (!FileUtil.createDir(dir)) {
			message.obj = "file creates failed!";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		}

		dir = dir + foldername + "/";
		if (GTCaptureEngine.getInstance().getCaptureState()) {
			message.obj = "capture has start!";
			tcpdumpSwitchHandler.sendMessage(message);
			return false;
		} else {
			// 检查文件夹是否存在
			if (!FileUtil.createDir(dir)) {
				message.obj = "file creates failed!";
				tcpdumpSwitchHandler.sendMessage(message);
				return false;
			}

			// modify on 20150108
			filename = "Capture" + GTUtils.getSaveDate();
			filename_path = dir + filename;
			startTcpDump();

			return true;
		}

	}

	private void startTcpDump() {
		tcpdumpRunning = true;
		handler.postDelayed(task, 1000);
		Thread thread_tcpdump = new Thread(new TcpDumpRunnable());
		thread_tcpdump.start();
	}

	private void endTcpDump() {
		GTCaptureEngine.getInstance().endTcpDump();
		tcpdumpRunning = false;
		count = 1;
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {

			handler.postDelayed(this, 1000);

			if (flag_listRefresh) {
				// 获取命令行输出内容
				list_temp = GTCaptureEngine.getInstance().getTcpDumpInputStream();
				list_input_stream.addAll(list_temp);

				if (list_temp.contains("tcpdump: syntax error")
						|| list_temp.contains("tcpdump: illegal token:")) {
					endTcpDump();
					tcpdumpSwitchHandler.sendEmptyMessage(1);
				}
				adapter.notifyDataSetChanged();

				if (!tcpdumpRunning) {
					handler.removeCallbacks(task);
				}
				list_temp.clear();
				list_temp = null;
			}

		}
	};

	public class TcpDumpRunnable implements Runnable {

		@Override
		public void run() {
			String realParam = param;
			// 如果是wifi，则适配为wlan0，对应小米等手机，其实大部分机型可用网卡名都是wlan0
			if (NetUtils.isWifiActive())
			{
				try {
					NetworkInterface network = NetworkInterface.getByName("wlan0");
					if (network != null && !param.contains("wlan0")) {
						realParam = "-i wlan0 " + param;
					}
				} catch (SocketException e) {
					// nothing should do
				}
			}

			GTCaptureEngine.getInstance().startTcpDump(
					filename_path + "_" + String.valueOf(count) + ".pcap", realParam,
					getApplicationContext(), true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		param = et_param.getText().toString();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 初始化时，参数开关是关着的
		cb_param_switch.setChecked(cur_param_switch_status);
		// 文件名保存下来
		if (foldername != null) {
			et_filename.setText(foldername);
		} else {
			et_filename.setText(filename);
		}

		// 维持控件状态
		cb_param_switch.setChecked(cur_param_switch_status);
		if (cur_param_switch_status) {
			tv_param_title.setVisibility(View.VISIBLE);
			fl_param.setVisibility(View.VISIBLE);
		} else {
			tv_param_title.setVisibility(View.GONE);
			fl_param.setVisibility(View.GONE);
		}

		// param保存下来
		et_param.setText(param);
		// 维持控件状态
		if (switch_tcpdump) {
			tv_switch.setBackgroundResource(R.drawable.switch_off_border);
			tv_switch.setText(getString(R.string.stop));
		} else {
			tv_switch.setBackgroundResource(R.drawable.switch_on_border);
			tv_switch.setText(getString(R.string.start));
		}

		adapter.notifyDataSetChanged();
	}

	private void checkNetworkInfo() {
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		// mobile 3G Data Network
		State mobileState = conMan.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();

		// wifi
		State wifiState = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();

		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {

			// Networkstatus.network_status = "mobile";
		}
		// 手机网络连接成功
		else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			network_status = "no_network";
			// 手机没有任何的网络
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			network_status = "wifi";
		}
		// 无线网络连接成功
		Log.d("network", network_status);
	}

	final class ProgressRunnable implements Runnable {
		@Override
		public void run() {
			try {
				if (!switch_tcpdump) {
					boolean result = checkTcpDump();
					if (result) {
						tcpdumpSwitchHandler.sendEmptyMessage(0);
					} else {
						tcpdumpSwitchHandler.sendEmptyMessage(1);
					}
				} else {
					endTcpDump();
					tcpdumpSwitchHandler.sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			proDialog.dismiss();
		}
	}
}
