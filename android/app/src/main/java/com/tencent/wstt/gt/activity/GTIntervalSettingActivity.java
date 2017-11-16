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

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.dao.GTPref;

public class GTIntervalSettingActivity extends GTBaseActivity {

	private SeekBar sb_refresh;
	private TextView tv_op_refresh_inter_time;
	public static int msecond = GTPref.getGTPref().getInt(
			GTPref.INTERVAL_PERF, 1000);
	private static float second = (float)msecond / 1000f;
	private static int pos = GTPref.getGTPref().getInt(
			GTPref.INTERVAL_PERF_POS, 50);
	
	// FPS
	private SeekBar sb_refresh_FPS;
	private TextView tv_op_refresh_inter_time_FPS;
	public static int msecond_FPS = GTPref.getGTPref().getInt(
			GTPref.INTERVAL_FPS, 1000);
	private static float second_FPS = (float)msecond_FPS / 1000f;
	private static int pos_FPS = GTPref.getGTPref().getInt(
			GTPref.INTERVAL_FPS_POS, 50);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gt_gwsetting);

		TextView tv_back = (TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(back);

		tv_op_refresh_inter_time = (TextView) findViewById(R.id.op_refresh_inter_time);
		tv_op_refresh_inter_time.setText(getString(R.string.setting_intervals_gather) + second + "s");

		tv_op_refresh_inter_time_FPS = (TextView) findViewById(R.id.op_refresh_inter_time_FPS);
		tv_op_refresh_inter_time_FPS.setText(getString(R.string.setting_intervals_gather_FPS) + second_FPS + "s");
		
		sb_refresh = (SeekBar) findViewById(R.id.myseekbar);
		sb_refresh.setProgress(pos);
		sb_refresh.setOnSeekBarChangeListener(refresh);
		
		sb_refresh_FPS = (SeekBar) findViewById(R.id.myseekbar_FPS);
		sb_refresh_FPS.setProgress(pos_FPS);
		sb_refresh_FPS.setOnSeekBarChangeListener(refresh_FPS);
	}

	private OnSeekBarChangeListener refresh = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			pos = sb_refresh.getProgress();
			second = 0;
			int interval = 5;
			int cnt = pos / interval;
			if (cnt <= 9) {
				second = 10 - 1 * cnt;
			} else {
				second = (float) (1 - 0.1 * (cnt - 10));
				if (second < 0.2f) {
					second = 0.1f;
				}
			}
			tv_op_refresh_inter_time.setText(getString(R.string.setting_intervals_gather) + second + "s");
			msecond = (int) (second * 1000);
			GTPref.getGTPref().edit().putInt(GTPref.INTERVAL_PERF, msecond).commit();
			GTPref.getGTPref().edit().putInt(GTPref.INTERVAL_PERF_POS, pos).commit();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
	};

	private OnSeekBarChangeListener refresh_FPS = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			pos_FPS = sb_refresh_FPS.getProgress();
			second_FPS = 0;
			int interval = 5;
			int cnt = pos_FPS / interval;
			if (cnt <= 9) {
				second_FPS = 10 - 1 * cnt;
			} else {
				second_FPS = (float) (1 - 0.1 * (cnt - 10));
				if (second_FPS < 0.2f) {
					second_FPS = 0.1f;
				}
			}
			tv_op_refresh_inter_time_FPS.setText(getString(R.string.setting_intervals_gather_FPS) + second_FPS + "s");
			msecond_FPS = (int) (second_FPS * 1000);
			GTPref.getGTPref().edit().putInt(GTPref.INTERVAL_FPS, msecond_FPS).commit();
			GTPref.getGTPref().edit().putInt(GTPref.INTERVAL_FPS_POS, pos_FPS).commit();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}
	};

	private OnClickListener back = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};
}
