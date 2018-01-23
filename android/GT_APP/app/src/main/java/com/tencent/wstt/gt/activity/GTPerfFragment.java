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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.internal.GTMemoryDaemonHelper;
import com.tencent.wstt.gt.log.GTTimeInternal;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.ui.model.NamedEntry;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;

public class GTPerfFragment extends Fragment {
	
	private ListView listView;
	private TimeAdapter timeAdapter;
	private NamedEntry[] dataSet;
	private TextView tv_perNoStartToast;
	
	private EditText et_savePath;
	private AlertDialog dlg_save;
	
	private ImageButton btn_delete;
	private ImageButton btn_save;
	private ImageButton btn_start;
	private ImageButton btn_stop;
	
	static final String TOAST_NOT_START = GTApp.getContext().getString(R.string.prof_notstart);
	static final String TOAST_STARTED = GTApp.getContext().getString(R.string.prof_started);

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View perfLayout = inflater.inflate(R.layout.gt_perfactivity,
				container, false);
		
		tv_perNoStartToast = (TextView)perfLayout.findViewById(R.id.perf_no_start_toast);
		
		btn_delete = (ImageButton)perfLayout.findViewById(R.id.perf_delete);
		btn_save = (ImageButton)perfLayout.findViewById(R.id.perf_save);
		btn_start = (ImageButton)perfLayout.findViewById(R.id.perf_start);
		btn_stop = (ImageButton)perfLayout.findViewById(R.id.perf_stop);
		
		listView = (ListView) perfLayout.findViewById(R.id.perf_list);
		dataSet = GTTimeInternal.getEntrys();
		timeAdapter = new TimeAdapter(dataSet);
		listView.setAdapter(timeAdapter);
		
		/*
		 * 初始化各UI控件初始状态
		 */
		if (GTTimeInternal.isETStarted())
		{
			if (dataSet != null && dataSet.length == 0)
			{
				tv_perNoStartToast.setText(TOAST_STARTED);
				tv_perNoStartToast.setVisibility(View.VISIBLE);
			}
			else
			{
				tv_perNoStartToast.setVisibility(View.GONE);
			}
			
			btn_start.setVisibility(View.INVISIBLE);
			btn_save.setVisibility(View.INVISIBLE);
			btn_delete.setVisibility(View.INVISIBLE);
			
			btn_stop.setVisibility(View.VISIBLE);
		}
		else
		{
			if (dataSet == null || dataSet.length == 0)
			{
				tv_perNoStartToast.setText(TOAST_NOT_START);
				tv_perNoStartToast.setVisibility(View.VISIBLE);
			}
			else
			{
				tv_perNoStartToast.setVisibility(View.GONE);
			}
			
			btn_start.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.VISIBLE);
			btn_delete.setVisibility(View.VISIBLE);
			
			btn_stop.setVisibility(View.INVISIBLE);
		}
		
		// 删除相关控件
		btn_delete.setOnClickListener(showDeleteDlg);
		
		/*
		 * 保存相关控件
		 */
		RelativeLayout rl_save = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(
				R.layout.gt_dailog_save, null, false);
		ImageButton btn_cleanSavePath = (ImageButton) rl_save.findViewById(R.id.save_clean);
		btn_cleanSavePath.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_savePath.setText("");
			}
		});
		
		et_savePath = (EditText) rl_save.findViewById(R.id.save_editor);
		String lastSaveLog = GTTimeInternal.getLastSaveTimeLog();
		if (lastSaveLog != null && lastSaveLog.contains(".")
				&& lastSaveLog.endsWith(LogUtils.TLOG_POSFIX))
		{
			lastSaveLog = lastSaveLog.substring(0, lastSaveLog.lastIndexOf("."));
		}
		lastSaveLog = lastSaveLog.trim();
		et_savePath.setText(lastSaveLog);
		dlg_save = new Builder(getActivity())
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
				GTTimeInternal.saveTimeLog(et_savePath.getText().toString().trim());
				dialog.dismiss();
			}
		}).create();
		
		btn_save.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				String lastSaveLog = GTTimeInternal.getLastSaveTimeLog();
				if (lastSaveLog != null && lastSaveLog.contains(".")
						&& lastSaveLog.endsWith(LogUtils.TLOG_POSFIX))
				{
					lastSaveLog = lastSaveLog.substring(0, lastSaveLog.lastIndexOf("."));
				}
				et_savePath.setText(lastSaveLog);
				dlg_save.show();
			}
		});
		
		btn_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// UI需要隐藏save、delete、start，显示end
				if (!GTTimeInternal.isETStarted())
				{
					// 如果想开启，需要先校验
					if (!GTMemoryDaemonHelper.startGWOrProfValid())
					{
						return;
					}
					
					btn_start.setVisibility(View.INVISIBLE);
					btn_save.setVisibility(View.INVISIBLE);
					btn_delete.setVisibility(View.INVISIBLE);
					
					btn_stop.setVisibility(View.VISIBLE);
					
					// 这个属性需要交给控制器去做业务逻辑相关处理
					GTTimeInternal.setETStarted(true);
					
					handler.postDelayed(task, delaytime);
					
					if (dataSet != null && dataSet.length == 0)
					{
						tv_perNoStartToast.setText(TOAST_STARTED);
						tv_perNoStartToast.setVisibility(View.VISIBLE);
					}
					else
					{
						tv_perNoStartToast.setVisibility(View.GONE);
					}
				}
			}
		});
		
		btn_stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// UI需要隐藏save、delete、start，显示end
				if (GTTimeInternal.isETStarted())
				{
					btn_save.setVisibility(View.VISIBLE);
					btn_delete.setVisibility(View.VISIBLE);
					btn_start.setVisibility(View.VISIBLE);
					
					btn_stop.setVisibility(View.INVISIBLE);
					
					// 这个属性需要交给控制器去做业务逻辑相关处理
					GTTimeInternal.setETStarted(false);
					handler.removeCallbacks(task);
					
					if (dataSet == null || dataSet.length == 0)
					{
						tv_perNoStartToast.setText(TOAST_NOT_START);
						tv_perNoStartToast.setVisibility(View.VISIBLE);
					}
					else
					{
						tv_perNoStartToast.setVisibility(View.GONE);
					}
				}
			}
		});

		return perfLayout;
	}

	@Override
	public void onResume() {
		super.onResume();

		// 启动实时刷新
		if (GTTimeInternal.isETStarted())
		{
			handler.postDelayed(task, delaytime);
		}
		
		// 因为从home键出去不会走onCreat，所以这里要重新初始化一遍UI
		if (GTTimeInternal.isETStarted())
		{
			if (dataSet != null && dataSet.length == 0)
			{
				tv_perNoStartToast.setText(TOAST_STARTED);
				tv_perNoStartToast.setVisibility(View.VISIBLE);
			}
			else
			{
				tv_perNoStartToast.setVisibility(View.GONE);
			}
			
			btn_start.setVisibility(View.INVISIBLE);
			btn_save.setVisibility(View.INVISIBLE);
			btn_delete.setVisibility(View.INVISIBLE);
			
			btn_stop.setVisibility(View.VISIBLE);
		}
		else
		{
			if (dataSet == null || dataSet.length == 0)
			{
				tv_perNoStartToast.setText(TOAST_NOT_START);
				tv_perNoStartToast.setVisibility(View.VISIBLE);
			}
			else
			{
				tv_perNoStartToast.setVisibility(View.GONE);
			}
			
			btn_start.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.VISIBLE);
			btn_delete.setVisibility(View.VISIBLE);
			
			btn_stop.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onPause() {
		if (GTTimeInternal.isETStarted())
		{
			handler.removeCallbacks(task);
		}
		super.onStop();
	}
	
	public class TimeAdapter extends ArrayAdapter<NamedEntry>
	{
		public static final int TYPE_GROUP = 0;
		public static final int TYPE_TAG = 1;
		
		public static final int TYPE_SUM = 2;
		
		LayoutInflater inflater;
		NamedEntry[] dataSet;
		
		public TimeAdapter(NamedEntry[] data) {
			super(getActivity(), 0, data);
			inflater = LayoutInflater.from(getActivity());
			this.dataSet = data;
		}
		
		@Override  
		public int getCount() {
			return dataSet.length;
		}
		
		//每个convert view都会调用此方法，获得当前所需要的view样式  
		@Override
		public int getItemViewType(int position) {
			NamedEntry entry = getItem(position);
			if (entry instanceof TagTimeEntry)
			{
				return TYPE_TAG;
			}
			else
			{
				return TYPE_GROUP;
			}
		}

		@Override  
		public NamedEntry getItem(int i) {
			return dataSet[i];  
		}  

		
		@Override
		public int getViewTypeCount() {
			return TYPE_SUM;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderGroup holderGroup = null;
			ViewHolderTag holderTag = null;
			int type = getItemViewType(position);
			
			ViewGroup rl = null;
			if (convertView == null) {
				switch(type)
				{
				case TYPE_GROUP:
					rl = (ViewGroup) inflater.inflate(
							R.layout.gt_perf_list_item, parent, false);
					holderGroup = new ViewHolderGroup();
					holderGroup.tvGroup = (TextView) rl.findViewById(R.id.tid_group);
					rl.setTag(holderGroup);
					break;
				case TYPE_TAG:
					rl = (ViewGroup) inflater.inflate(
							R.layout.gt_perf_group_tag_list_item, parent, false);
					holderTag = new ViewHolderTag();
					holderTag.tvTimeTag = (TextView) rl.findViewById(R.id.time_tag);
					holderTag.tvTimeTimes = (TextView) rl.findViewById(R.id.time_times);
					holderTag.tvTimeTotaltime = (TextView) rl.findViewById(R.id.time_total_time);
					holderTag.tvTimeMax = (TextView) rl.findViewById(R.id.time_max);
					holderTag.tvTimeAve = (TextView) rl.findViewById(R.id.time_ave);
					rl.setTag(holderTag);
					break;
				}
			} else {
				switch(type)
				{
				case TYPE_GROUP:
					holderGroup = (ViewHolderGroup)convertView.getTag();
					break;
				case TYPE_TAG:
					holderTag = (ViewHolderTag)convertView.getTag();
					break;
				}
				
				rl = (ViewGroup) convertView;
			}
			
			// 赋值
			switch(type)
			{
			case TYPE_GROUP:
				holderGroup.tvGroup.setText(dataSet[position].getName());
				break;
			case TYPE_TAG:
				final TagTimeEntry entry = (TagTimeEntry)getItem(position);
				if (entry.getTid() == 0) // 全局类统计
				{
					holderTag.tvTimeTag.setText(entry.getName());
				}
				else // 区分线程类统计
				{
					holderTag.tvTimeTag.setText(entry.getNameT());
				}
				
				holderTag.tvTimeMax.setText(entry.getMax());
				holderTag.tvTimeTimes.setText(entry.getRecordSizeText());
				if (entry.getFunctionId() == Functions.PERF_DIGITAL_NORMAL
						|| entry.getFunctionId() == Functions.PERF_DIGITAL_CPU
						|| entry.getFunctionId() == Functions.PERF_DIGITAL_MULT
						|| entry.getFunctionId() == Functions.PERF_DIGITAL_MULT_MEM
						|| entry.getFunctionId() == Functions.PERF_START_DIGITAL_GLOBAL)
				{
					holderTag.tvTimeTotaltime.setText("--");
				}
				else
				{
					holderTag.tvTimeTotaltime.setText(entry.getTotal());
				}
				
				holderTag.tvTimeAve.setText(entry.getAve());
				
				// TODO 这个可以放到ListView的onItemClick事件中
				rl.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								GTPerfGroupDetailActivity.class);

						intent.putExtra("name", entry.getName());
						intent.putExtra("tid", entry.getTid());
						intent.putExtra("parent_name", entry.getParent().getName());
						
						getActivity().startActivity(intent);
					}});
				break;
			}

			return rl;
		}
	}
	
	class ViewHolderGroup {
		TextView tvGroup;
	}
	
	class ViewHolderTag {
		TextView tvTimeTag;
		TextView tvTimeTimes;
		TextView tvTimeTotaltime;
		TextView tvTimeMax;
		TextView tvTimeAve;
	}
	
	private OnClickListener showDeleteDlg = new OnClickListener() {
		public void onClick(View v) {
			
			if (null == dataSet || dataSet.length == 0)
			{
				return;
			}
			
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setMessage(getString(R.string.clear_tip));
			builder.setTitle(getString(R.string.clear));
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
							//UI需要清理dataSet
							GTTimeInternal.cleartimeInfo();
							dialog.dismiss();
							dataSet = new NamedEntry[]{};
							timeAdapter = new TimeAdapter(dataSet);
							listView.setAdapter(timeAdapter);
							
							tv_perNoStartToast.setText(TOAST_NOT_START);
							tv_perNoStartToast.setVisibility(View.VISIBLE);

						}
					});
			builder.show();
		}
	};

	private int delaytime = 1000;
	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			if (GTTimeInternal.isETStarted()) {
				dataSet = GTTimeInternal.getEntrys();
				
				// 当开关刚打开时，timeAdapter会是null,正好这里初始化
				// 当tag和group数变化时，需要重新setAdapter
				if (timeAdapter == null || dataSet.length != timeAdapter.getCount())
				{
					timeAdapter = new TimeAdapter(dataSet);
					listView.setAdapter(timeAdapter);
				}
				
				if (dataSet != null && dataSet.length == 0)
				{
					tv_perNoStartToast.setText(TOAST_STARTED);
					tv_perNoStartToast.setVisibility(View.VISIBLE);
				}
				else
				{
					tv_perNoStartToast.setVisibility(View.GONE);
				}
				
				timeAdapter.notifyDataSetChanged();

				handler.postDelayed(this, delaytime);
			}
			else
			{
				if (dataSet == null || dataSet.length == 0)
				{
					tv_perNoStartToast.setText(TOAST_NOT_START);
					tv_perNoStartToast.setVisibility(View.VISIBLE);
				}
				else
				{
					tv_perNoStartToast.setVisibility(View.GONE);
				}
			}
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.setGroupVisible(0, true); // 屏蔽设置主菜单
	}

}
