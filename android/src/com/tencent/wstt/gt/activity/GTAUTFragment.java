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
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.CpuUtils;
import com.tencent.wstt.gt.api.utils.NetUtils;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientFactory;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.SingleInstanceClientFactory;
import com.tencent.wstt.gt.plugin.PluginManager;
import com.tencent.wstt.gt.proInfo.floatView.GTMemHelperFloatview;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.utils.CommonString;
import com.tencent.wstt.gt.utils.ToastUtil;

public class GTAUTFragment extends Fragment {
//TOOD
	private static String pkn_old = null; // 保留上次选中的AUT的包名名称
	private TextView tv_AppName = null;
	private TextView tv_selectedApp = null;
	private TextView tv_Appstatus = null;
	private TextView tv_select = null; // 一个箭头
	private TextView tv_refresh = null;
	private TextView tv_PkName = null;

	private ProgressDialog proDialog;
	private int selectDrawable;
	private int defaultDrawable;
	private AlertDialog dlg_save;
	private EditText et_savePath;

	private CheckBox cb_cpu;
	private CheckBox cb_jiffies;
	private CheckBox cb_net;
	private CheckBox cb_pss;
	private CheckBox cb_pd;
	private CheckBox[] cb_boxs;

	private static boolean[] cb_status = new boolean[5]; //  持久化的CheckBox状态
	private static String[] cb_alias = {CommonString.pcpu_alias, CommonString.pjif_alias, CommonString.pnet_alias,
		CommonString.pm_pss_alias, CommonString.pm_pd_alias};
	private static String[] cb_key = {CommonString.pcpu_key, CommonString.pjif_key, CommonString.pnet_key,
		CommonString.pm_pss_key, CommonString.pm_pd_key};

	// 手动获取内存相关
	private TextView memOn;
	private TextView memOff;
	private View memSwitch;
	private static boolean isAutoGetMem = true;

	private Thread thread;

	// 用于外部变化需要通知AUT页刷新的Handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			doResume();
		}
	};
	
	public GTAUTFragment()
	{
		super();
		GTApp.setAUTHandler(handler);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View autLayout = inflater.inflate(R.layout.gt_settingactivity,
				container, false);

		tv_Appstatus = (TextView) autLayout.findViewById(R.id.app_status);
		tv_select = (TextView) autLayout.findViewById(R.id.selected_app_bg);
		tv_select.setOnClickListener(select);
		tv_PkName = (TextView) autLayout.findViewById(R.id.select_tested_pkn);
		tv_selectedApp = (TextView) autLayout.findViewById(R.id.app_pic);
		tv_AppName = (TextView) autLayout.findViewById(R.id.selected_apn);
		tv_refresh = (TextView) autLayout.findViewById(R.id.app_refresh);

		tv_refresh.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					proDialog = ProgressDialog.show(getActivity(),
							"Searching..", "searching..wait....", true, false);

					tv_refresh.setTextColor(Color.GREEN);
					Thread loginThread = new Thread(new ProcessRefresher());
					loginThread.start();
					v.performClick();
				}

				return true;
			}
		});

		tv_refresh.setVisibility(View.GONE);
		cb_cpu = (CheckBox) autLayout.findViewById(R.id.cb_cpu);
		cb_jiffies = (CheckBox) autLayout.findViewById(R.id.cb_jiffies);
		cb_net = (CheckBox) autLayout.findViewById(R.id.cb_net);
		cb_pss = (CheckBox) autLayout.findViewById(R.id.cb_pss);
		cb_pd = (CheckBox) autLayout.findViewById(R.id.cb_pd);
		cb_boxs = new CheckBox[]{cb_cpu, cb_jiffies, cb_net, cb_pss, cb_pd};

		cb_cpu.setOnClickListener(cb_check);
		cb_jiffies.setOnClickListener(cb_check);
		cb_net.setOnClickListener(cb_check);
		cb_pss.setOnClickListener(cb_check);
		cb_pd.setOnClickListener(cb_check);

		memOn = (TextView) autLayout.findViewById(R.id.btn_memon);
		memOff = (TextView) autLayout.findViewById(R.id.btn_memoff);
		memSwitch = autLayout.findViewById(R.id.memswitch);
		selectDrawable = R.drawable.swbtn_selected;
		defaultDrawable = R.drawable.swbtn_default;
		
		if (isAutoGetMem) {
			memOn.setText("");
			memOn.setBackgroundResource(selectDrawable);
			memOff.setText("off");
			memOff.setBackgroundResource(defaultDrawable);
		}
		else
		{
			memOn.setText("on");
			memOn.setBackgroundResource(selectDrawable);
			memOff.setText("");
			memOff.setBackgroundResource(defaultDrawable);
		}
		
		RelativeLayout rl_save = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(
				R.layout.gt_dailog_save, container, false);
		et_savePath = (EditText) rl_save.findViewById(R.id.save_editor);
		
		dlg_save = new Builder(getActivity())
		.setTitle(getString(R.string.save))
		.setView(rl_save)
		.setPositiveButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
						GTMemHelperFloatview.memInfoList.clear();
					}
				})
		.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 简单保存
				String path = et_savePath.getText().toString();
				LogUtils.writeTagMemData(tv_PkName.getText().toString(), path + ".csv");
				GTMemHelperFloatview.memInfoList.clear();
				dialog.dismiss();
			}
		}).create();
		
		memSwitch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!tv_PkName.getText().toString().equals("n/a")) {
					if (isAutoGetMem) {
						isAutoGetMem = false;
						memOn.setText("on");
						memOn.setBackgroundResource(defaultDrawable);
						memOff.setText("");
						memOff.setBackgroundResource(selectDrawable);
	
						// if(!tv_PkName.getText().toString().equals("n/a")){
						Intent intent = new Intent(GTApp.getContext(),
								GTMemHelperFloatview.class);
						intent.putExtra("pName", tv_PkName.getText().toString());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						PluginManager
								.getInstance()
								.getPluginControler()
								.startService(
										GTMemHelperFloatview.getInstance(),
										intent);
						// }
					} else {
						isAutoGetMem = true;
						memOn.setText("");
						memOn.setBackgroundResource(selectDrawable);
						memOff.setText("off");
						memOff.setBackgroundResource(defaultDrawable);
						
						GTMemHelperFloatview.tagTimes = 0;
						
						PluginManager
								.getInstance()
								.getPluginControler()
								.stopService(
										GTMemHelperFloatview.getInstance());
						dlg_save.show();
					}
				}
				v.performClick();
				return false;
			}
		});
		
		return autLayout;
	}
	
	@Override
	public void onHiddenChanged(boolean newHiddenState) {
		if (!newHiddenState)
		{
			doResume();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		doResume();
	}
	
	private void doResume() {
		// 判断是否选择了AUT
		tv_Appstatus.setText(AUTManager.appstatus);
		tv_refresh.setBackgroundResource(R.drawable.textview_bg_shape);

		if (AUTManager.appic != null) {
			syncProcessRunPkgState();
			tv_selectedApp.setBackgroundDrawable(AUTManager.appic);
			tv_PkName.setText(AUTManager.pkn);
			tv_AppName.setText(AUTManager.apn);
			tv_PkName.setVisibility(View.VISIBLE);
		}

		if (AUTManager.appstatus.equals("--")) {
			setAllCheckbox(0); // 　setenable false
		} else {
			tv_Appstatus.setText(AUTManager.appstatus); // 在前面getAllRunProcesses方法被重置
			tv_Appstatus.setClickable(true);
			tv_Appstatus.setOnClickListener(launchapp);

			if (OpUIManager.gw_running) {
				setAllCheckbox(0); // 　setenable false
			} else {
				setAllCheckbox(1); // setenable true
			}
		}

		/* 
		 * 判断每次选择的应用是否为同样的，若不一样清空进程采集设置
		 */
		if (pkn_old != null && !pkn_old.equals(AUTManager.pkn)) {
			AUTManager.proNameIdMap.clear();
			for (int i = 0; i < cb_status.length; i++) {
				cb_status[i] = false;
			}
			for (int i = 0; i < cb_status.length; i++) {
				unregisterOutpara(i);
			}
			AUTManager.proNameList.clear();
			AUTManager.proPidList.clear();
			// 清除旧的AUT_CLIENT
			ClientManager.getInstance().removeClient(ClientManager.AUT_CLIENT);
			// 创建新的AUT_CLIENT
			ClientFactory cf = new SingleInstanceClientFactory();
			cf.orderClient(
					ClientManager.AUT_CLIENT, ClientManager.AUT_CLIENT.hashCode(), null, null);
		}

		// 保证GT读取到最新的应用状态
		AUTManager.findProcess();
		pkn_old = AUTManager.pkn;

		// UI同步一下状态
		for (int count = 0; count < cb_status.length; count++) {
			cb_boxs[count].setChecked(cb_status[count]);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.setGroupVisible(0, true); // 屏蔽设置主菜单
	}

	private OnClickListener select = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!OpUIManager.gw_running) {
				Intent intent = new Intent(getActivity(),
						GTShowPackageActivity.class);
				if (pkn_old != null) {
					boolean previous = true;
					for (int i = 0; i < cb_status.length; i++) {
						if (hasAppHistory(i)) {
							break;
						}
					}

					if (previous) {
						AlertDialog.Builder builder = new Builder(
								getActivity());
						builder.setMessage(getString(R.string.AUT_page_tip1));
						builder.setTitle(getString(R.string.AUT_page_tip_title));
						builder.setPositiveButton(R.string.cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.setNegativeButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// UI需要清理dataSet
										Intent intent = new Intent(
												getActivity(),
												GTShowPackageActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(intent);
										dialog.dismiss();

									}
								});
						builder.setCancelable(false);
						builder.show();
						// }
					} else {
						intent = new Intent(getActivity(),
								GTShowPackageActivity.class);

						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);

					}
				} else {
					intent = new Intent(getActivity(),
							GTShowPackageActivity.class);

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					// finish();
				}
			} else {
				ToastUtil.ShowLongToast(getActivity(),
						getString(R.string.AUT_page_tip2), "center");
			}
		}
	};

	private OnClickListener launchapp = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try
			{
				AUTManager.openApp(AUTManager.pkn);
				thread = new Thread(new Runnable() {
					public void run() {
						AUTManager.appstatus = "running";
						checkRegist();
					}
				});
				thread.start();
			}
			catch(Exception e)
			{
				Log.w("GTSettingActivity", "can not start App:" + AUTManager.pkn);
				ToastUtil.ShowLongToast(getActivity(), getString(R.string.AUT_page_tip3) + AUTManager.pkn);
			}
		}
	};

	private static boolean hasAppHistory(int type) {
		if (!(type >= 0 && type < cb_alias.length))
		{
			return false;
		}
		String outparaname = cb_alias[type];

		boolean hasdata = true;
		ArrayList<String> tempL = new ArrayList<String>();
		tempL = (ArrayList<String>) AUTManager.registOpTable.get(outparaname);
		if (tempL != null) {
			for (int i = 0; i < tempL.size(); i++) {
				TagTimeEntry tte = OpPerfBridge
						.getProfilerData((String) tempL.get(i));
				if (tte == null) {
					hasdata = false;
				} else {
					if (tte.hasChild()
							&& tte.getChildren()[0].getRecordSize() <= 0) {
						hasdata = false;
					} else if (tte.getRecordSize() <= 0) {
						hasdata = false;
					}
				}
			}
		} else {
			hasdata = false;
		}
		return hasdata;
	}

	private static void checkRegist() {
		int count = 0;
		AUTManager.pIds = null;
		while (AUTManager.pIds == null) {
			AUTManager.findProcess();
			count++;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count > 20) {
				AUTManager.appstatus = GTApp.getContext().getString(R.string.AUT_app_lanuch);
				break;
			}
		}

		if (AUTManager.appstatus.equals("running")) {
			for (int num = 0; num < cb_status.length; num++) {
				if (cb_status[num] == true) {
					registerOutpara(num);
				}
			}
		}
	}

	private void syncProcessRunPkgState() {

		if (AUTManager.pkn != null) {
			if (ProcessUtils.hasProcessRunPkg(AUTManager.pkn.toString())) {
				AUTManager.appstatus = "running";
				// tv_Appstatus.setBackgroundColor(Color.TRANSPARENT);
				tv_Appstatus.setTextColor(Color.GREEN);
				tv_refresh.setVisibility(View.VISIBLE);
				tv_refresh.setEnabled(true);
			} else {
				// 如果直接调Activity的getString，有时会因Fragment GTAUTFragment{41afc180} not attached to Activity而crash
				// http://bugly.qq.com/detail?app=900010910&pid=1&ii=116#stack
				AUTManager.appstatus = GTApp.getContext().getString(R.string.AUT_app_lanuch);
				tv_Appstatus
						.setBackgroundResource(R.drawable.textview_bg_shape);
				tv_Appstatus.setTextColor(this.getResources().getColor(
						R.color.orange));
				tv_refresh.setVisibility(View.GONE);
			}
		}

	}

	/**
	 * 注册选中应用的指定性能出参，2.1.1版本开始支持指定进程
	 * @param type 指定的性能参数类型
	 * @param designatedPid 指定进程的pid
	 * @return 返回指定pid进程在出参记录中的序号
	 */
	public static List<OutPara> registerOutpara(int type, int designatedPid) {
		List<OutPara> result = new ArrayList<OutPara>();
				
		OpUIManager.list_change = true;
		cb_status[type] = true;
		if (! AUTManager.appstatus.equals("running")) {
			return result;
		}

		List<String> registOutList = new ArrayList<String>();
		AUTManager.registOpTable.put(cb_alias[type], registOutList);
		
		if (AUTManager.pIds != null) {
			String[] tempPids = AUTManager.pIds;
			if (designatedPid >= 0) // 指定pid的情况
			{
				tempPids = new String[1];
				for (int i = 0; i < AUTManager.pIds.length; i++)
				{
					if (AUTManager.pIds[i].equals(Integer.toString(designatedPid)))
					{
						tempPids[0] = AUTManager.pIds[i];
						break;
					}
				}
			}
			
			for (int i = 0; i < tempPids.length; i++) {
				String preOpName; // 出参的前缀
				String key;
				String alias;
				if (type == AUTManager.SEQ_NET)
				{
					preOpName = cb_key[type] + ":";
					key = preOpName + AUTManager.pkn;
					alias = cb_alias[type];
				}
				else
				{
					preOpName = cb_key[type] + i + ":";
					key = preOpName + AUTManager.pNames[i];
					alias = cb_alias[type] + i;
				}
				
				Client autClient = ClientManager.getInstance().getAUTClient();
				autClient.registerOutPara(key, alias);
				autClient.setOutparaMonitor(key, true); // FIXME 默认设置为已采集，应和上一步合并
				registOutList.add(key);
				OutPara op = autClient.getOutPara(key);
				result.add(op);

				// 对应的统计对象
				switch (type) {
				case AUTManager.SEQ_CPU:
					OpPerfBridge.startProfier(op,
							Functions.PERF_DIGITAL_CPU,
							"Process CPU occupy", "%");
					// 设置CPU记录对象
					CpuUtils.cpuInfoMap.put(key, new CpuUtils());
					break;
				case AUTManager.SEQ_JIF:
					// 需要有对应的CPU记录对象，JIF才可用
					String keyCpu = cb_key[AUTManager.SEQ_CPU] + i + ":" + AUTManager.pNames[i];
					
					if (null == CpuUtils.cpuInfoMap.get(keyCpu))
					{
						// 设置CPU记录对象
						CpuUtils.cpuInfoMap.put(keyCpu, new CpuUtils());
					}
					
					OpPerfBridge.startProfier(op,
							Functions.PERF_DIGITAL_NORMAL,
							"", "");
					break;
				case AUTManager.SEQ_NET:
					String[] subKeys = { "transmitted", "received"};
					int[] funIds = { Functions.PERF_DIGITAL_MULT,
							Functions.PERF_DIGITAL_MULT};

					OpPerfBridge.startProfier(op,
							subKeys, funIds,"", "KB");
					NetUtils mNetUtils = new NetUtils(AUTManager.pkn.toString());
					NetUtils.netInfoMap.put(AUTManager.pkn, mNetUtils);
					break;
				case AUTManager.SEQ_PSS:
					String[] subKeys_pss = { "total", "dalvik", "native" };
					int[] funIds_pss = { Functions.PERF_DIGITAL_MULT_MEM,
							Functions.PERF_DIGITAL_MULT_MEM,
							Functions.PERF_DIGITAL_MULT_MEM };
					OpPerfBridge.startProfier(op, subKeys_pss, funIds_pss,
							"", "MB");
					break;
				case AUTManager.SEQ_PD:
					String[] subKeys_pd = { "total", "dalvik", "native" };
					int[] funIds_pd = { Functions.PERF_DIGITAL_MULT_MEM,
							Functions.PERF_DIGITAL_MULT_MEM,
							Functions.PERF_DIGITAL_MULT_MEM };
					OpPerfBridge.startProfier(op, subKeys_pd,
							funIds_pd, "", "MB");
					break;
				}
			}
		}
		return result;
	}
	
	private static void registerOutpara(int type) {
		registerOutpara(type, -1);
	}

	private void setAllCheckbox(int type) {

		switch (type) {
		case 0: // 设置checkbox 不可点
			for (CheckBox cb : cb_boxs)
			{
				if (null != cb) cb.setEnabled(false);
			}
			tv_refresh.setEnabled(false);
			break;
		case 1: // 设置所有check可点
			for (CheckBox cb : cb_boxs)
			{
				if (null != cb) cb.setEnabled(true);
			}
			tv_refresh.setEnabled(true);
			break;
		case 2: // 设置所有checkbox set checked false
			for (CheckBox cb : cb_boxs)
			{
				if (null != cb) cb.setEnabled(false);
			}
			for (int i = 0; i < cb_status.length; i++) {
				unregisterOutpara(i);
			}
			break;
		}
	}

	private void unregisterOutpara(int type) {

		OpUIManager.list_change = true;
		cb_status[type] = false;
		
		if (AUTManager.pIds != null) {
			for (int i = 0; i < AUTManager.pIds.length; i++) {
				String preOpName; // 出参的前缀
				if (type == AUTManager.SEQ_NET)
				{
					preOpName = cb_key[type] + ":";
				}
				else
				{
					preOpName = cb_key[type] + i + ":";
				}
				
				Client autClient = ClientManager.getInstance().getAUTClient();
				autClient.unregisterOutPara(preOpName + AUTManager.pNames[i]);
			}
			
			AUTManager.registOpTable.remove(cb_alias[type]);
		}
	}

	private OnClickListener cb_check = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cb_cpu:
				if (cb_status[AUTManager.SEQ_CPU] == false) {
					registerOutpara(AUTManager.SEQ_CPU);
				} else {
					hashistory(AUTManager.SEQ_CPU);
				}
				break;
			case R.id.cb_jiffies:
				if (cb_status[AUTManager.SEQ_JIF] == false) {
					registerOutpara(AUTManager.SEQ_JIF);
				} else {
					hashistory(AUTManager.SEQ_JIF);
				}
				break;
			case R.id.cb_net:
				if (cb_status[AUTManager.SEQ_NET] == false) {
					registerOutpara(AUTManager.SEQ_NET);
					cb_status[AUTManager.SEQ_NET] = true;
				} else {
					hashistory(AUTManager.SEQ_NET);
				}
				break;
			case R.id.cb_pss:
				if (cb_status[AUTManager.SEQ_PSS] == false) {
					registerOutpara(AUTManager.SEQ_PSS);
					cb_status[AUTManager.SEQ_PSS] = true;
				} else {
					hashistory(AUTManager.SEQ_PSS);
				}
				break;
			case R.id.cb_pd:
				if (cb_status[AUTManager.SEQ_PD] == false) {
					registerOutpara(AUTManager.SEQ_PD);
					cb_status[AUTManager.SEQ_PD] = true;
				} else {
					hashistory(AUTManager.SEQ_PD);
				}
				break;
			}
			
			if (GTApp.getOpHandler() != null)
			{
				GTApp.getOpHandler().sendEmptyMessage(5); // 通知出参界面刷新下UI
			}
			if (GTApp.getOpEditHandler() != null)
			{
				GTApp.getOpEditHandler().sendEmptyMessage(0);
			}
		}

	};

	private void hashistory(final int type) { // 当去除性能指标项的勾选时，查看是否该项存在历史数据，有则提示，无则直接删除
		String outparaname = cb_alias[type];
		AlertDialog.Builder builder = new Builder(getActivity());
		boolean hasdata = true;
		ArrayList<String> tempL = new ArrayList<String>();
		tempL = (ArrayList<String>) AUTManager.registOpTable.get(outparaname);
		if (tempL != null) {
			for (int i = 0; i < tempL.size(); i++) {
				TagTimeEntry tte = OpPerfBridge
						.getProfilerData((String) tempL.get(i));
				if (tte == null) {
					hasdata = false;
				} else {
					if (tte.hasChild()
							&& tte.getChildren()[0].getRecordSize() > 0) {
						hasdata = true;
					} else if (tte.getRecordSize() <= 0) {
						hasdata = false;
					}
				}
			}

			if (!hasdata) {
				unregisterOutpara(type);
			} else {
				builder.setMessage(getString(R.string.AUT_page_tip1));
				builder.setTitle(getString(R.string.AUT_page_tip_title));
				builder.setPositiveButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								cb_boxs[type].setChecked(true);
							}
						});
				builder.setNegativeButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// UI需要清理dataSet
								unregisterOutpara(type);
								dialog.dismiss();

							}
						});
				builder.setCancelable(false);
				builder.show();
			}
		}
	}

	private class ProcessRefresher implements Runnable {
		@Override
		public void run() {
			AUTManager.findProcess();
			if (AUTManager.appstatus.equals("running")) {
				for (int num = 0; num < cb_status.length; num++) {
					if (cb_status[num] == true) {
						registerOutpara(num);
					}
				}
			}
			try {
				Thread.sleep(2000);
				if (proDialog != null) {
					proDialog.dismiss();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			getActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					tv_refresh.setTextColor(Color.WHITE);
				}});
		}
	}
}
