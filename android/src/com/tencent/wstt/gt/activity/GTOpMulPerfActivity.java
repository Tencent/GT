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

import java.util.LinkedList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.NetUtils;
import com.tencent.wstt.gt.log.GTGWInternal;
import com.tencent.wstt.gt.log.GWSaveEntry;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.utils.StringUtil;
import com.tencent.wstt.gt.utils.ToastUtil;
import com.tencent.wstt.gt.views.GTMulOpPerfDetailView;
import com.tencent.wstt.gt.views.GTOutParaPerfDialog;

/**
 * 能用多条曲线展示历史值的出参详情页面
 */
public class GTOpMulPerfActivity extends GTBaseActivity {
	
	private TagTimeEntry dataSet;
	private TagTimeEntry attentEntry;
	private String about = "";
	private OutPara op; // 保存对应的出参变量，以便刷值

	private ImageButton btn_back;
	private ImageButton btn_delete;
//	private ImageButton btn_save;
	private EditText et_savePath1;
	private EditText et_savePath2;
	private EditText et_savePath3;
	private EditText et_saveTestDesc;
	private AlertDialog dlg_save;
	
	private Button btn_attent;
	private ListView lv_attentAttr;
	private ArrayAdapter<String> attrAdapter;
	private ImageView img_empty;
	
	private TextView tvKey;
	private TextView tvAbout;

	private TextView tvTitle;
	private TextView tvValue;
	private TextView tvTimes;
	private TextView tvMin;
	private TextView tvMax;
	private TextView tvAve;
	private TextView tvWarningCntToast;
	private TextView tvWarningCnt;
	private TextView tvWaringArea;
	
	private EditText etUpperInterval;
	private EditText etUpperValue;
	private EditText etLowerValue;

	private LinearLayout ll_fold;
	private ImageView img_bottom_arrow;

	// 辅助图标的参数
	private int lastdataSetLength = 0;

	LinkedList<String> chartData = new LinkedList<String>();

	GTMulOpPerfDetailView chartView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_perf_op_mul_detail);
		
		Intent intent = this.getIntent();
		if (intent == null)
		{
			finish();
		}

		Bundle extras = intent.getExtras();
		if (extras == null)
		{
			finish();
		}
		final String name = extras.getString("name");
		final String alias = extras.getString("alias");
		final String client = extras.getString("client");
		
		// 出参的性能数据源取法
		dataSet = OpPerfBridge.getProfilerData(name);

		// 在onCreat之前数据源可能被清理了，这样就不打开页面
		if (null == dataSet)
		{
			finish();
			return;
		}
		// 保存对应的出参变量，以便刷值
		op = ClientManager.getInstance().getClient(client).getOutPara(name);
		
		attentEntry = dataSet.getSubTagEntrys()[0];
		about = dataSet.getDesc();
		
		tvTitle = (TextView)findViewById(R.id.perf_detail_title);
		tvTitle.setText(alias);
		
		tvKey = (TextView)findViewById(R.id.op_perf_detail_key);
		tvKey.setText(name);
		
		tvAbout = (TextView)findViewById(R.id.op_perf_detail_about);
		tvAbout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				GTOutParaPerfDialog dialog =
						new GTOutParaPerfDialog(v.getContext(), alias, name, about);
				dialog.show();
			}
		});
		
		lv_attentAttr = (ListView)findViewById(R.id.op_perf_detail_attent_list);
		
		attrAdapter = new ArrayAdapter<String>(this, R.layout.gt_simple_dropdown_item);
		for (TagTimeEntry itemName : dataSet.getChildren())
		{
			attrAdapter.add(itemName.getName());
		}
		lv_attentAttr.setAdapter(attrAdapter);
		
		lv_attentAttr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long arg3) {
				img_empty.setVisibility(View.GONE);
				lv_attentAttr.setVisibility(View.GONE);
				String lastSelectedItem = btn_attent.getText().toString();
				btn_attent.setText(attrAdapter.getItem(position));
				attentEntry = dataSet.getSubTagEntrys()[position];
				if (! lastSelectedItem.equals(attentEntry.getName()))
				{
					refreshAttent();
					refreshThreshold();
				}
			}
		});
		
		/*
		 * 用于覆盖整个屏幕的透明ImageView，
		 * 主要帮助点击非lv_attentAttr区域使lv_attentAttr消失
		 */
		img_empty = (ImageView) findViewById(R.id.view_empty);
		img_empty.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				img_empty.setVisibility(View.GONE);
				lv_attentAttr.setVisibility(View.GONE);
				return true;
			}
		});
		
		btn_attent = (Button)findViewById(R.id.op_perf_detail_attent);
		btn_attent.setText(attrAdapter.getItem(0));
		btn_attent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_attent.setBackgroundResource(R.drawable.a_gt_perf_op_btn_selected_border);
				handler.postDelayed(new Runnable(){

					@Override
					public void run() {
						btn_attent.setBackgroundResource(R.drawable.a_gt_perf_op_btn_default_border);
					}
				}, 200);

				if (lv_attentAttr.getVisibility() == View.VISIBLE)
				{
					img_empty.setVisibility(View.GONE);
					lv_attentAttr.setVisibility(View.GONE);
				}
				else
				{
					lv_attentAttr.setVisibility(View.VISIBLE);
					img_empty.setVisibility(View.VISIBLE);
				}
			}});
		
		btn_back = (ImageButton)findViewById(R.id.perf_detail_back);
		btn_back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		
//		btn_save = (ImageButton)findViewById(R.id.perf_detail_save);
//		btn_save.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				String lastSaveLog = GTGWInternal.getLastSaveFolder();
//				if (lastSaveLog != null && lastSaveLog.contains(".")
//						&& lastSaveLog.endsWith(LogUtils.TLOG_POSFIX))
//				{
//					lastSaveLog = lastSaveLog.substring(0, lastSaveLog.lastIndexOf("."));
//				}
//				et_savePath3.setText(lastSaveLog.trim());
//				dlg_save.show();
//			}
//		});

		RelativeLayout rl_save = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.gt_dailog_save_gw, null, false);
		ImageButton btn_cleanSavePath = (ImageButton) rl_save.findViewById(R.id.save_clean);
		btn_cleanSavePath.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_savePath3.setText("");
			}
		});

		et_savePath1 = (EditText) rl_save.findViewById(R.id.save_editor_folder_parent1);
		et_savePath2 = (EditText) rl_save.findViewById(R.id.save_editor_folder_parent2);
		et_savePath3 = (EditText) rl_save.findViewById(R.id.save_editor);
		et_saveTestDesc = (EditText) rl_save.findViewById(R.id.save_editor_desc);

		String lastSaveLog = GTGWInternal.getLastSaveFolder();
		if (lastSaveLog != null && lastSaveLog.contains(".")
				&& lastSaveLog.endsWith(LogUtils.TLOG_POSFIX))
		{
			lastSaveLog = lastSaveLog.substring(0, lastSaveLog.lastIndexOf("."));
		}
		et_savePath3.setText(lastSaveLog);
		et_savePath1.setText(Env.CUR_APP_NAME);
		et_savePath2.setText(Env.CUR_APP_VER);

		dlg_save = new Builder(this)
		.setTitle(getString(R.string.save))
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
				String path1 = et_savePath1.getText().toString().trim();
				if (!StringUtil.isLetter(path1))
				{
					ToastUtil.ShowShortToast(
							GTOpMulPerfActivity.this, getString(R.string.save_folder_valid));
					return;
				}

				String path2 = et_savePath2.getText().toString().trim();
				if (!StringUtil.isLetter(path2))
				{
					ToastUtil.ShowShortToast(
							GTOpMulPerfActivity.this, getString(R.string.save_folder_valid));
					return;
				}

				String path3 = et_savePath3.getText().toString().trim();
				if (!StringUtil.isLetter(path3))
				{
					ToastUtil.ShowShortToast(
							GTOpMulPerfActivity.this, getString(R.string.save_folder_valid));
					return;
				}

				String testDesc = et_saveTestDesc.getText().toString().trim();

				GWSaveEntry saveEntry = new GWSaveEntry(path1, path2, path3, testDesc);
				GTGWInternal.saveGWData(saveEntry, dataSet);
				dialog.dismiss();
			}
		}).create();
		
		btn_delete = (ImageButton)findViewById(R.id.perf_detail_delete);
		btn_delete.setOnClickListener(showDeleteDlg);
		
		// 告警区操作的UI
		tvWarningCntToast = (TextView) findViewById(R.id.op_perf_detail_warning_cnt_toast);
		tvWarningCnt = (TextView) findViewById(R.id.bh_perf_detail_warning_cnt);

		// 告警区
		final LinearLayout ll_warnArea = (LinearLayout)findViewById(R.id.op_perf_detail_warnarea);
		img_bottom_arrow = (ImageView)findViewById(R.id.bottom_arrow);
		if (dataSet.getThresholdEntry().isEnable())
		{
			img_bottom_arrow.setBackgroundResource(R.drawable.unfold_arrow);
			ll_warnArea.setVisibility(View.VISIBLE);
		}
		else
		{
			img_bottom_arrow.setBackgroundResource(R.drawable.fold_arrow);
			ll_warnArea.setVisibility(View.GONE);
		}

		// 告警抬头文本
		tvWaringArea = (TextView)findViewById(R.id.op_perf_detail_interval_toast);

		// 折叠线
		ll_fold = (LinearLayout)findViewById(R.id.warning_fold);

		// 因为多曲线情况的各子项的告警的enable值是一起变化的，所以从一个判断决定UI初始状态即可
		if (!attentEntry.getThresholdEntry().isEnable())
		{
			img_bottom_arrow.setBackgroundResource(R.drawable.fold_arrow);
			ll_warnArea.setVisibility(View.GONE);
			tvWarningCntToast.setVisibility(View.GONE);
			tvWarningCnt.setVisibility(View.GONE);
			tvWaringArea.setText(getString(R.string.warning_title_mul_disable));
		}

		ll_fold.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 需要对所有的子项告警全开和全屏蔽,UI与业务逻辑混一起了，需要重构
				if(attentEntry.getThresholdEntry().isEnable()){
					for (TagTimeEntry temp : dataSet.getSubTagEntrys())
					{
						temp.getThresholdEntry().setEnable(false);
					}
					img_bottom_arrow.setBackgroundResource(R.drawable.fold_arrow);
					ll_warnArea.setVisibility(View.GONE);
					tvWarningCntToast.setVisibility(View.GONE);
					tvWarningCnt.setVisibility(View.GONE);
					tvWaringArea.setText(getString(R.string.warning_title_mul_disable));
				}else{
					for (TagTimeEntry temp : dataSet.getSubTagEntrys())
					{
						temp.getThresholdEntry().setEnable(true);
					}
					img_bottom_arrow.setBackgroundResource(R.drawable.unfold_arrow);
					ll_warnArea.setVisibility(View.VISIBLE);
					tvWarningCntToast.setVisibility(View.VISIBLE);
					tvWarningCnt.setVisibility(View.VISIBLE);
					tvWaringArea.setText(getString(R.string.warning_title_mul));
				}
			}
		});

		// 从未开始统计过的出参，不允许设置告警
		if (!op.hasMonitorOnce)
		{
			etUpperInterval.setEnabled(false);
			etUpperValue.setEnabled(false);
			etLowerValue.setEnabled(false);
		}
		else
		{
			etUpperInterval = (EditText)findViewById(R.id.op_perf_detail_upper_interval);
			etUpperValue = (EditText)findViewById(R.id.op_perf_detail_upper_value);
			etLowerValue = (EditText)findViewById(R.id.op_perf_detail_lower_value);
			
			etUpperInterval.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			etUpperValue.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			etLowerValue.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			
			etUpperInterval.setOnKeyListener(thresholdKeyListener);
			etUpperValue.setOnKeyListener(thresholdKeyListener);
			etLowerValue.setOnKeyListener(thresholdKeyListener);
			
			refreshThreshold();
		}

		tvValue = (TextView) findViewById(R.id.op_perf_detail_value);
		tvTimes = (TextView) findViewById(R.id.bh_perf_detail_times);
		tvMin = (TextView) findViewById(R.id.bh_perf_detail_min);
		tvMax = (TextView) findViewById(R.id.bh_perf_detail_max);
		tvAve = (TextView) findViewById(R.id.bh_perf_detail_ave);

//		tvValue.setText(dataSet.getLastValue());
		tvValue.setText(op.getValue());

		refreshAttent();
		
		LinearLayout ll_chart = (LinearLayout) findViewById(R.id.bh_perf_detail_chart);
		
		chartView = new GTMulOpPerfDetailView(this, dataSet);
		chartView.setInput(0);
		ll_chart.addView(chartView);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		chartView.postInvalidate(); // 至少先把x、y轴画上

		// 启动图表实时刷新
		handler.post(task);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStop() {
		handler.removeCallbacks(task);
		super.onStop();
	}

	/*
	 * 用实时数据更新图表数据
	 */
	private void createRealtimeData() { 

		if (attentEntry.getRecordSize() == lastdataSetLength)
		{
			return;
		}
		
		lastdataSetLength = attentEntry.getRecordSize();
		
		int start = lastdataSetLength > GTMulOpPerfDetailView.xMax ?
				lastdataSetLength - GTMulOpPerfDetailView.xMax : 0;
		chartView.setInput(start);
		chartView.postInvalidate();
	}
	
	private int delaytime = 1000;
	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			refreshAttent();
//			tvValue.setText(dataSet.getLastValue());
			tvValue.setText(op.getValue());
			
			if (chartView.isAutoRefresh())
			{
				createRealtimeData();
			}

			handler.postDelayed(this, delaytime);
		}
	};
	
	private OnClickListener showDeleteDlg = new OnClickListener() {
		public void onClick(View v) {
			
			if (null == dataSet || dataSet.getSubTagEntrys().length <= 0
					|| dataSet.getSubTagEntrys()[0].getRecordSize() == 0)
			{
				return;
			}
			
			AlertDialog.Builder builder = new Builder(GTOpMulPerfActivity.this);
			builder.setMessage(getString(R.string.clear_and_reset_tip));
			builder.setTitle(getString(R.string.clear_and_reset));
			builder.setPositiveButton(getString(R.string.cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setNegativeButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// UI需要清理dataSet
							dataSet.clear();
							tvTimes.setText("");
							tvMin.setText("");
							tvMax.setText("");
							tvAve.setText("");
							tvWarningCnt.setText("");
							tvValue.setText("");

							// 图表恢复自动刷新，否则当图表之前没刷时，需要碰一下界面才刷
							chartView.setAutoRefresh(true);
							chartView.setInput(0);
							chartView.postInvalidate();

							// 如果是流量数据，还要同时reset
							String key = dataSet.getName();
							NetUtils.clearNetValue(key);
							
							dialog.dismiss();
						}
					});
			builder.show();
		}
	};

	private void cancelFilterMsgInput(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	}

	private OnKeyListener thresholdKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_ENTER:
				
				cancelFilterMsgInput(v);
				
				String sUpperDuration = etUpperInterval.getText().toString();
				String sUpperValue = etUpperValue.getText().toString();
				String sLowerValue = etLowerValue.getText().toString();
				
				int duration = attentEntry.getThresholdEntry().getduration();
				double upperValue = attentEntry.getThresholdEntry().getUpperValue();
				double lowerValue = attentEntry.getThresholdEntry().getLowerValue();
				
				try
				{
					if (!"".equals(sUpperDuration))
					{
						duration = Integer.parseInt(sUpperDuration);
					}
					else // 空字符串的话就是重置
					{
						duration = Integer.MAX_VALUE;
					}
					
					if (!"".equals(sUpperValue))
					{
						upperValue = Double.parseDouble(sUpperValue);
					}
					else // 空字符串的话就是重置
					{
						upperValue = Integer.MAX_VALUE;
					}
					
					if (!"".equals(sLowerValue))
					{
						lowerValue = Double.parseDouble(sLowerValue);
					}
					else // 空字符串的话就是重置
					{
						lowerValue = Integer.MIN_VALUE;
					}

					attentEntry.getThresholdEntry().setThreshold(
							duration, upperValue, lowerValue);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ToastUtil.ShowLongToast(
							GTOpMulPerfActivity.this, getString(R.string.digit_valid));
				}

				return true;
			}
			return false;
		}
	};
	
	private void refreshAttent()
	{
		tvTimes.setText(attentEntry.getRecordSizeText());
		tvMin.setText(attentEntry.getMin());
		tvMax.setText(attentEntry.getMax());
		tvAve.setText(attentEntry.getAve());
		tvWarningCnt.setText(Integer.toString(
				attentEntry.getThresholdEntry().getUpperWariningCount()
				+ attentEntry.getThresholdEntry().getLowerWariningCount()));
	}
	
	private void refreshThreshold()
	{
		int upperDuration = attentEntry.getThresholdEntry().getduration();
		if (upperDuration != Integer.MAX_VALUE)
		{
			etUpperInterval.setText(Integer.toString(upperDuration));
		}
		else
		{
			etUpperInterval.setText("");
		}
		
		double upperValue = attentEntry.getThresholdEntry().getUpperValue();
		if (upperValue != Integer.MAX_VALUE)
		{
			etUpperValue.setText(Double.toString(upperValue));
		}
		else
		{
			etUpperValue.setText("");
		}
		
		double lowerValue = attentEntry.getThresholdEntry().getLowerValue();
		if (lowerValue != Integer.MIN_VALUE)
		{
			etLowerValue.setText(Double.toString(lowerValue));
		}
		else
		{
			etLowerValue.setText("");
		}
	}
}
