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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.internal.GTMemoryDaemonHelper;
import com.tencent.wstt.gt.log.GTGWInternal;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.manager.ParamConst;
import com.tencent.wstt.gt.proInfo.floatView.GTMemHelperFloatview;
import com.tencent.wstt.gt.service.GTServiceController;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.utils.ToastUtil;

public class GTParamOutFragment extends ListFragment implements
		OnClickListener, OnTouchListener, OnScrollListener {
	private Button btn_gw_on;
	private Button btn_gw_off;
	private ImageButton save;
	private ImageButton cleardata;
	private EditText et_savePath;
	private AlertDialog gwhis_save;
	private ProgressDialog proDialog;

	private GTParamOutListAdapter outparam_adapter;
	
	public static boolean cb_all_status = false; // true为全选 false为全取消

	// 负责刷新UI的Handler
	private static Handler refreshHandler = new Handler();
	
	public GTParamOutFragment()
	{
		super();
		GTApp.setOpHandler(opHandler);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
//		// 全局执行一次即可
//		OpUIManager.getDefaultOutputParamList();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		doResume();
		refreshHandler.post(opRefreshRunnable);
		getListView().setOnTouchListener(this);
		getListView().setOnScrollListener(this);
	}
	
	private void doResume()
	{
		initGwOnOff();
		OpUIManager.refreshUIOpList();

		outparam_adapter =
				new GTParamOutListAdapter(getActivity(), OpUIManager.list_op);
		// 2.2中每次进页面都置顶体验不好，固取消这里的AUT参数置顶
//		OpUIManager.setAUTitemTop(outparam_adapter);
		setListAdapter(outparam_adapter);
		outparam_adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		refreshHandler.removeCallbacksAndMessages(null);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.gt_param_out,
				container, false);

		btn_gw_on = (Button) layout.findViewById(R.id.btn_monitor);
		btn_gw_off = (Button) layout.findViewById(R.id.btn_monitor_stop);
		save = (ImageButton) layout.findViewById(R.id.gwdata_save);
		cleardata = (ImageButton) layout.findViewById(R.id.gwdata_delete);

		btn_gw_on.setOnClickListener(this);
		btn_gw_off.setOnClickListener(this);
		save.setOnClickListener(this);
		cleardata.setOnClickListener(this);

		// 保存对话框
		View rl_save = inflater.inflate(R.layout.gt_save_editor, null, false);
		ImageButton btn_cleanSavePath = (ImageButton) rl_save.findViewById(R.id.save_clean);
		btn_cleanSavePath.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_savePath.setText("");
			}
		});
		et_savePath = (EditText) rl_save.findViewById(R.id.save_editor);

		gwhis_save = new Builder(getActivity())
				.setTitle(getString(R.string.save_folder))
				.setView(rl_save)
				.setPositiveButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						proDialog = ProgressDialog.show(getActivity(),
								"Saving..", "saving..wait....", true, true);
						Thread savedata = new Thread(saveDataHandler);
						savedata.start();
						dialog.dismiss();
					}
				}).create();
		
		return layout;
	}
	
	private void initGwOnOff()
	{
		if (OpUIManager.gw_running)
		{
			btn_gw_off.setVisibility(View.VISIBLE);
			btn_gw_on.setVisibility(View.GONE);
			save.setVisibility(View.GONE);
			cleardata.setVisibility(View.GONE);
		}
		else
		{
			btn_gw_off.setVisibility(View.GONE);
			btn_gw_on.setVisibility(View.VISIBLE);
			save.setVisibility(View.VISIBLE);
			cleardata.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		Message msg = opHandler.obtainMessage();
		switch (v.getId()) {
		case R.id.btn_monitor:
			msg.what = 0; // 启动gw
			msg.sendToTarget();
			break;
		case R.id.btn_monitor_stop: // 停止gw
			msg.what = 1;
			msg.sendToTarget();
			break;
		case R.id.gwdata_delete: // 删除数据
			msg.what = 2;
			msg.sendToTarget();
			break;
		case R.id.gwdata_save: // 保存数据
			msg.what = 3;
			msg.sendToTarget();
			break;
		}
	}

	private boolean isOneItemSelected()
	{
		TagTimeEntry[] te = OpPerfBridge.getAllEnableProfilerData();
		if (te.length == 0) {
			ToastUtil.ShowLongToast(GTApp.getContext(),
					getString(R.string.para_out_toast), "center");
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private Handler opHandler = new Handler() { // 处理switchtitle上按钮事件
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // run GW
				if (!GTMemoryDaemonHelper.startGWOrProfValid())
				{
					break;
				}
				
				if (isOneItemSelected()) {
					OpUIManager.gw_running = true;
					initGwOnOff();
				}
				break;
			case 1: // stop GW
				OpUIManager.gw_running = false;
				initGwOnOff();
				break;
			case 2: // cleardata
				if (isOneItemSelected()) {
					OpUIManager.list_change = true;
					ToastUtil.ShowLongToast(GTApp.getContext(),
							getString(R.string.para_out_toast_clearall));
					AlertDialog.Builder builder = new Builder(
							getActivity());
					builder.setMessage(getString(R.string.clear_tip));
					builder.setTitle(getString(R.string.clear));
					builder.setPositiveButton(getString(R.string.cancel),
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
									GTGWInternal.clearAllEnableGWData();
									
									// add on 20131225 有手动tag记录内存值的情况也清理了
									GTMemHelperFloatview.memInfoList.clear();
									GTMemHelperFloatview.tagTimes = 0;
									dialog.dismiss();
	
								}
							});
					builder.setCancelable(false);
					builder.show();
				}
				break;
			case 3: // save
				if (isOneItemSelected()) {
					OpUIManager.list_change = true;
					String lastSaveLog = GTGWInternal.getLastSaveFolder();
					et_savePath.setText(lastSaveLog);
					gwhis_save.show();
				}
				break;
			case 4: // 保存文件后 动画取消
				if (proDialog != null) {
					proDialog.dismiss();
				}
				ToastUtil.ShowLongToast(GTApp.getContext(),
						getString(R.string.para_out_toast_saveto) + "/GT/GW/"
						+ Env.CUR_APP_NAME+"/"+et_savePath.getText().toString(),"center");
				break;
			case 5: // 驱动列表刷新
				doResume();
			default:
				break;
			}
		}
	};

	// hidden该页时，需要把这个回调取消
	private Runnable opRefreshRunnable = new Runnable() {
		@Override
		public void run() {
			if (! OpUIManager.refresh_op_drag_conflict_flag) {
				OpUIManager.refreshOutputParam();

				if (outparam_adapter != null) {
					outparam_adapter.notifyDataSetChanged();
				}
			}

			refreshHandler.postDelayed(opRefreshRunnable, 1000);
		}
	};

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		OutPara ov = OpUIManager.list_op.get(position);
		final String key = ov.getKey();
		if (key.equals(ParamConst.PROMPT_TITLE) || key.equals(ParamConst.DIVID_TITLE)
				|| key.equals(ParamConst.PROMPT_DISABLE_TITLE)) {
			return;
		}
		ov.setAlert(false);
		GTServiceController.INSTANCE.show_alert = false;
		TagTimeEntry opProfilerData = OpPerfBridge
				.getProfilerData(key);
		if (null != opProfilerData
				&& OpPerfBridge.getProfilerData(key).hasChild()) {
			Intent intent = new Intent(getActivity(),
					GTOpMulPerfActivity.class);
			intent.putExtra("name", key);
			intent.putExtra("alias", ov.getAlias());
			intent.putExtra("client", ov.getClient());
			getActivity().startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(),
					GTOpSinglePerfActivity.class);
			
			if(ov.getAlias().equals("SM"))
			{
				intent = new Intent(getActivity(), GTOpSMActivity.class);
			}

			intent.putExtra("name", key);
			intent.putExtra("alias", ov.getAlias());
			intent.putExtra("client", ov.getClient());
			getActivity().startActivity(intent);
		}
	}
	
	
	Runnable saveDataHandler = new Runnable() {
		@Override
		public void run() {
			String path = et_savePath.getText().toString();
			path = path.trim();
			GTGWInternal.saveAllEnableGWData(path);
			Message message = new Message();
			message.what = 4;
			opHandler.sendMessage(message); // save 数据时的动画
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			OpUIManager.refresh_op_drag_conflict_flag = false;
			return false;
		case MotionEvent.ACTION_DOWN:
			OpUIManager.refresh_op_drag_conflict_flag = true;
			return false;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_CANCEL:
		default:
			return false;
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
			OpUIManager.refresh_op_drag_conflict_flag = false;
		}
		else
		{
			OpUIManager.refresh_op_drag_conflict_flag = true;
		}
	}

	public void onShow(boolean show)
	{
		if (show)
		{
			// 恢复数据刷新线线程
			refreshHandler.removeCallbacksAndMessages(null);
			refreshHandler.post(opRefreshRunnable);
		}
		else
		{
			// 停止数据刷新线程
			refreshHandler.removeCallbacksAndMessages(null);
		}
	}
}
