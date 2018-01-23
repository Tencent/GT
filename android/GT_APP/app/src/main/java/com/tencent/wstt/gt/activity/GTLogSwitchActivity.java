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
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.log.GTLogInternal;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.views.GTCheckBox;

public class GTLogSwitchActivity extends GTBaseActivity {
	
	private GTCheckBox cb_masterSwitch;
	private GTCheckBox cb_autoSave;
	private GTCheckBox cb_autoSaveQuickFlush;
//	private GTCheckBox cb_saveDefaultSeg; // 2.2.1版本日志重构后，强制必须符合Logcat格式

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_logswitch);
		
		TextView tv_back = (TextView)findViewById(R.id.tv_back);
		tv_back.setOnClickListener(back); 
		
		cb_masterSwitch = (GTCheckBox)findViewById(R.id.cb_master_switch);
		cb_autoSave = (GTCheckBox)findViewById(R.id.cb_auto_save);
		cb_autoSaveQuickFlush = (GTCheckBox)findViewById(R.id.cb_auto_save_flush);
//		cb_saveDefaultSeg = (GTCheckBox)findViewById(R.id.cb_save_default);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		cb_masterSwitch.setChecked(GTLogInternal.isEnable());
		cb_autoSave.setChecked(GTLogInternal.isAutoSave());
		cb_autoSaveQuickFlush.setChecked(LogUtils.isAutoSaveQuickFlush());
//		cb_saveDefaultSeg.setChecked(GTLogInternal.isSaveDefaultSeg());
		
		cb_masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					GTLogInternal.enable();
				}else{
					GTLogInternal.disable();
					//如果总开关关了，自动把autosave开关置成关
					cb_autoSave.setChecked(false);
					GTLogInternal.setAutoSave(false);
				}
			}
		});

//		cb_saveDefaultSeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {	
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				GTLogInternal.setSaveDefaultSeg(isChecked);
//			}
//		});
		
		cb_autoSaveQuickFlush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LogUtils.setAutoSaveQuickFlush(isChecked);
				GTPref.getGTPref().edit().putBoolean(GTPref.LOG_AUTOSAVEFLUSH_SWITCH, isChecked).commit();
			}
		});

		cb_autoSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//如果总开打开了，autosave开关才能响应打开操作
					if(GTLogInternal.isEnable()){
						GTLogInternal.setAutoSave(true);
					}else{
						cb_autoSave.setChecked(false);
						GTLogInternal.setAutoSave(false);
					}
				}else{
					GTLogInternal.setAutoSave(false);
				}
			}
		});
	}
	
	private OnClickListener back = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
}
