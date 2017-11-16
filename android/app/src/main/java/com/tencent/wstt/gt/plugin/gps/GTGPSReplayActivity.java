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
package com.tencent.wstt.gt.plugin.gps;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class GTGPSReplayActivity extends GTBaseActivity {

	/** 返回按钮 */
	private TextView back_gt;
	/** 保存按钮 */
	private Button save_gt;

	/** gps文件名 */
	private TextView gps_replay_name;

	/** 进度条显示gps百分比 */
	private TextView gps_replay_percent;

	/** 进度条 */
	private SeekBar mSeekBarf;

	/** 进度条的progress */
	private int mProgress;
	
	/** 控制回放进度的单选框 */
	private RadioGroup mRadioGroup_replayspeed; 
	private int mreplayspeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pi_gps_replay);

		back_gt = (TextView) findViewById(R.id.frame_back_gt);
		back_gt.setOnClickListener(back);
		save_gt = (Button) findViewById(R.id.frame_save_gt);
		save_gt.setOnClickListener(save);
		gps_replay_name = (TextView) findViewById(R.id.gpsreplayname);
		mSeekBarf = (SeekBar) findViewById(R.id.seekBar_percent);
		mSeekBarf.setOnSeekBarChangeListener(new SeekbarChange());
		gps_replay_percent = (TextView) findViewById(R.id.percent);
		mRadioGroup_replayspeed = (RadioGroup)findViewById(R.id.radioGroup_replyspeed);
		mRadioGroup_replayspeed.setOnCheckedChangeListener(mChangeRadio);

		try
		{
			Intent intent = getIntent();
			String gpsName = intent.getStringExtra("gpsname");
			double gpsPercent = intent.getDoubleExtra("gpspercent", 0) * 100;
			mreplayspeed = intent.getIntExtra("relpayspeed", 1);

			gps_replay_name.setText(gpsName);
			mProgress = (int) (gpsPercent);

			mSeekBarf.setProgress(mProgress);
			gps_replay_percent.setText(mProgress + "%");
			InitRadio(mreplayspeed);
		}
		catch (Exception e)
		{
			finish();
		}
	}

	class SeekbarChange implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			mProgress = progress;
			gps_replay_percent.setText(mProgress + "%");
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}

	private OnClickListener back = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	private OnClickListener save = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("progress", mProgress);
			intent.putExtra("replayspeed", mreplayspeed);
			setResult(GTGPSActivity.RES_GPSREPALY_ACTIVITY, intent);
			finish();
		}
	};
	
	void InitRadio(int replayspeed)
	{
		switch (replayspeed )
		{
		case  1:	((RadioButton)findViewById(R.id.radio_replayspeed_normal)).setChecked(true); break;
		case  2:	((RadioButton)findViewById(R.id.radio_replayspeed_double)).setChecked(true); break;
		case  4:((RadioButton)findViewById(R.id.radio_replayspeed_treble)).setChecked(true); break;
		default:
			break;
		}
	}
	
	private RadioGroup.OnCheckedChangeListener mChangeRadio = new 
	           RadioGroup.OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) 
		{
			switch (checkedId )
			{
			case  R.id.radio_replayspeed_normal:	mreplayspeed =1; break;
			case  R.id.radio_replayspeed_double:	mreplayspeed =2; break;
			case  R.id.radio_replayspeed_treble:	mreplayspeed =4; break;
			default:
				break;
			}
			
		}
		
	};
}
