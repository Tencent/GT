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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.log.GTTimeInternal;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.views.GTPerfDetailView;

public class GTPerfGroupDetailActivity extends GTBaseActivity {
	
	TagTimeEntry dataSet;
	
	private ImageButton btn_back;
	private ImageButton btn_save;
	private EditText et_savePath;
	private AlertDialog dlg_save;

	private TextView tvGroup;
	private TextView tvTag;
	private TextView tvTimes;
	private TextView tvMin;
	private TextView tvMax;
	private TextView tvAve;
	
	// 辅助图标的参数
	private int lastdataSetLength = 0;
	
	LinkedList<String> chartData = new LinkedList<String>();
	
	GTPerfDetailView chartView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_perf_detail);
		
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
		String name = extras.getString("name");
		String parentName = extras.getString("parent_name");
		long tid = extras.getLong("tid");

		dataSet = GTTimeInternal.findTagTimeEntry(tid, parentName, name);
		
		btn_back = (ImageButton)findViewById(R.id.perf_detail_back);
		btn_back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		
		btn_save = (ImageButton)findViewById(R.id.perf_detail_save);
		btn_save.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String lastSaveLog = GTTimeInternal.getLastSaveTimeDetail();
				if (lastSaveLog != null && lastSaveLog.contains(".")
						&& lastSaveLog.endsWith(LogUtils.TLOG_POSFIX))
				{
					lastSaveLog = lastSaveLog.substring(0, lastSaveLog.lastIndexOf("."));
				}
				lastSaveLog = lastSaveLog.trim();
				et_savePath.setText(lastSaveLog);
				dlg_save.show();
			}
		});
		
		RelativeLayout rl_save = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.gt_dailog_save, null, false);
		ImageButton btn_cleanSavePath = (ImageButton) rl_save.findViewById(R.id.save_clean);
		btn_cleanSavePath.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_savePath.setText("");
			}
		});
		
		et_savePath = (EditText) rl_save.findViewById(R.id.save_editor);
		String lastSaveLog = GTTimeInternal.getLastSaveTimeDetail();
		if (lastSaveLog != null && lastSaveLog.contains(".")
				&& lastSaveLog.endsWith(LogUtils.TLOG_POSFIX))
		{
			lastSaveLog = lastSaveLog.substring(0, lastSaveLog.lastIndexOf("."));
		}
		et_savePath.setText(lastSaveLog);

		dlg_save = new Builder(this)
		.setTitle(getString(R.string.save_file))
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
				GTTimeInternal.saveTimeLogDetail(
						et_savePath.getText().toString().trim(), dataSet);
				dialog.dismiss();
			}
		}).create();
		
		tvGroup = (TextView) findViewById(R.id.bh_perf_detail_group);
		tvGroup.setMovementMethod(ScrollingMovementMethod.getInstance());
		tvTag = (TextView) findViewById(R.id.bh_perf_detail_tag);
		tvTag.setMovementMethod(ScrollingMovementMethod.getInstance());
		tvTimes = (TextView) findViewById(R.id.bh_perf_detail_times);
		tvMin = (TextView) findViewById(R.id.bh_perf_detail_min);
		tvMax = (TextView) findViewById(R.id.bh_perf_detail_max);
		tvAve = (TextView) findViewById(R.id.bh_perf_detail_ave);
		
		tvGroup.setText(parentName);
		if (tid != 0)
		{
			tvTag.setText(dataSet.getNameT());
		}
		else
		{
			tvTag.setText(name);
		}
		tvTimes.setText(dataSet.getRecordSizeText());
		tvMin.setText(dataSet.getMin());
		tvMax.setText(dataSet.getMax());
		tvAve.setText(dataSet.getAve());
		
		LinearLayout ll_chart = (LinearLayout) findViewById(R.id.bh_perf_detail_chart);
		
		chartView = new GTPerfDetailView(this, dataSet);
		chartView.setInput(0);
		ll_chart.addView(chartView);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		chartView.postInvalidate(); // 至少先把x、y轴画上

		// 启动图表实时刷新
//		handler.postDelayed(task, delaytime);
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
		
		if (dataSet.getRecordSize() == lastdataSetLength)
		{
			return;
		}
		
		lastdataSetLength = dataSet.getRecordSize();
		int start = lastdataSetLength > GTPerfDetailView.xMax ? lastdataSetLength - GTPerfDetailView.xMax : 0;
		chartView.setInput(start);
		chartView.postInvalidate();
	}
	
	private int delaytime = 1000;
	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			tvTimes.setText(dataSet.getRecordSizeText());
			tvMin.setText(dataSet.getMin());
			tvMax.setText(dataSet.getMax());
			tvAve.setText(dataSet.getAve());
			
			if (chartView.isAutoRefresh())
			{
				createRealtimeData();
			}

			handler.postDelayed(this, delaytime);
		}
	};
}
