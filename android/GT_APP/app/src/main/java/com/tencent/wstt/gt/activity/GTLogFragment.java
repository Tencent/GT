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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.base.GTLog;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.log.GTLogInternal;
import com.tencent.wstt.gt.log.LogListener;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.log.NormalLogAdapter;
import com.tencent.wstt.gt.log.logcat.LogFileAdapter;
import com.tencent.wstt.gt.log.logcat.LogcatReaderLoader;
import com.tencent.wstt.gt.log.logcat.SaveLogHelper;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;
import com.tencent.wstt.gt.views.GTCheckBox;

public class GTLogFragment extends Fragment implements LogListener, OnClickListener {

	public static final String TAG = "--GTLogFragment--";

	Handler handler;
	LogcatRunnable logcatTask;

	private RelativeLayout rl_log_filter;

	private GTCheckBox cb_logcatSwitch;
	
	private ImageButton btn_delete;
	private ImageButton btn_save;
	private ImageButton btn_open;

	private ImageView img_empty;

	private RelativeLayout rl_loglist;
	private ListView listView;
	private NormalLogAdapter logAdapter;

	private ListView filterListView;
	private ArrayAdapter<String> tagAdapter;
	private ArrayAdapter<CharSequence> levelAdapter;

	private ImageButton btn_search;

	private Button btn_level;
	private Button btn_tag;
	private EditText et_Msg;
	private ImageButton btn_msg_clear;
	private Button btn_msg_input_cancel;
	private ImageButton btn_level_toast;
	private ImageButton btn_tag_toast;

	// 用于过滤中间状态的数据源，必须有这个，否则过滤会和getView UI冲突
	private LinkedList<String> tempShowDownMsgList;

	private EditText et_savePath;
	private AlertDialog dlg_save;

	private float startY;
	int displayWidth = 0;

	private String currentlyOpenLog = null;

	private TextWatcher msg_watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			String sCurSelectedMsg = s.toString().trim();
			GTLogInternal.setCurFilterMsg(sCurSelectedMsg);
			((ArrayAdapter<?>) filterListView.getAdapter()).getFilter().filter(
					sCurSelectedMsg);
			if (filterListView.getAdapter().isEmpty()) {
				img_empty.setVisibility(View.GONE);
			}

			if (sCurSelectedMsg.length() > 0) {
				btn_msg_clear.setVisibility(View.VISIBLE);
			} else {
				btn_msg_clear.setVisibility(View.GONE);
			}

			onLogChanged();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};

	private boolean msgWatched = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View logLayout = inflater.inflate(R.layout.gt_logactivity, container, false);
		displayWidth = DeviceUtils.getDisplayWidth(getActivity());

		rl_log_filter = (RelativeLayout) logLayout.findViewById(R.id.rl_log_filter);
		
		cb_logcatSwitch = (GTCheckBox)logLayout.findViewById(R.id.cb_logcat_switch);
		cb_logcatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					// TODO logcat的简单实现
					logcatTask = new LogcatRunnable();
					new Thread(logcatTask).start();
				}else{
					 logcatTask.killReader();
				}
			}
		});

		btn_delete = (ImageButton) logLayout.findViewById(R.id.gtlog_delete);
		btn_save = (ImageButton) logLayout.findViewById(R.id.gtlog_save);
		btn_open = (ImageButton) logLayout.findViewById(R.id.gtlog_open);
		
		btn_level_toast = (ImageButton) logLayout.findViewById(R.id.log_level_toast);
		btn_tag_toast = (ImageButton) logLayout.findViewById(R.id.log_tag_toast);

		/*
		 * 用于覆盖整个屏幕的透明ImageView， 主要帮助点击非filterListView区域使filterListView消失
		 */
		img_empty = (ImageView) logLayout.findViewById(R.id.view_empty);
		img_empty.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				img_empty.setVisibility(View.GONE);
				filterListView.setVisibility(View.GONE);
				cancelFilterMsgInput(v);
				return false;
			}
		});

		/*
		 * 保存相关控件
		 */
		RelativeLayout rl_save = (RelativeLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.gt_dailog_save, null, false);
		ImageButton btn_cleanSavePath = (ImageButton) rl_save
				.findViewById(R.id.save_clean);
		btn_cleanSavePath.setOnClickListener(this);

		et_savePath = (EditText) rl_save.findViewById(R.id.save_editor);
		String lastSaveLog = GTLogInternal.getLastSaveLog();
		if (lastSaveLog != null && lastSaveLog.contains(".")
				&& lastSaveLog.endsWith(LogUtils.LOG_POSFIX)) {
			lastSaveLog = lastSaveLog
					.substring(0, lastSaveLog.lastIndexOf("."));
		}
		et_savePath.setText(lastSaveLog);
		dlg_save = new Builder(getActivity())
				.setTitle(getString(R.string.save_file))
				.setView(rl_save)
				.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 简单过滤保存
						String path = et_savePath.getText().toString();
						try {
							File f = null;
							if (FileUtil.isPathStringValid(path)) {
								String validPath = FileUtil
										.convertValidFilePath(path,
												LogUtils.LOG_POSFIX);
								if (FileUtil.isPath(validPath)) {
									f = new File(validPath);
									f.mkdirs();
								} else {
									f = new File(Env.ROOT_LOG_FOLDER, validPath);
								}
								GTLogInternal.setLastSaveLog(validPath);
							}

							if (f.exists()) {
								f.delete();
							}

							LogUtils.writeLog(logAdapter.getUIEntryList(), f,
									false);
						} catch (Exception e) {
							e.printStackTrace();
						}

						dialog.dismiss();
					}
					
				}).create();

		btn_save.setOnClickListener(this);

		// 删除按钮
		btn_delete.setOnClickListener(this);
		
		// 打开日志文件按钮
		btn_open.setOnClickListener(this);

		// 日志列表
		rl_loglist = (RelativeLayout) logLayout.findViewById(R.id.rl_loglist);
		listView = (ListView) logLayout.findViewById(R.id.loglist);
		initCurLogAdapter();
		logAdapter.setFilter();
		listView.setAdapter(logAdapter);
		// 滑动时，把置顶置底button隐藏起来
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				rl_log_filter.setVisibility(View.GONE);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (visibleItemCount + firstVisibleItem == totalItemCount) {
					logAdapter.setAutoRefresh(true);
				} else {
					logAdapter.setAutoRefresh(false);
				}
			}
		});
		// 点触时，把置顶置底button呼唤出来
		rl_loglist.setOnClickListener(this);
		listView.setOnTouchListener(logListTouchListener);

		// 过滤数据展示列表
		filterListView = (ListView) logLayout.findViewById(R.id.spinner_list);
		tagAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.gt_simple_dropdown_item);
		filterListView.setAdapter(tagAdapter);

		filterListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long arg3) {
				img_empty.setVisibility(View.GONE);
				filterListView.setVisibility(View.GONE);

				if (parent.getAdapter() == tagAdapter) {
					if (position == 0)
						GTLogInternal.setCurFilterTag("");
					else
						GTLogInternal.setCurFilterTag((String) parent
								.getAdapter().getItem(position));

					btn_tag.setText(tagAdapter.getItem(position));
				} else if (parent.getAdapter() instanceof MsgAdaptor) {
					String sCurSelectedMsg = (String) parent.getAdapter()
							.getItem(position);
					LinkedList<String> curShowDownMsgList = GTLogInternal
							.getCurFilterShowDownMsgList();
					LinkedList<String> msgHistory = GTLogInternal
							.getCurFilterMsgHistory();

					GTLogInternal.setCurFilterMsg(sCurSelectedMsg);
					msgWatched = false;
					et_Msg.removeTextChangedListener(msg_watcher);
					String s = curShowDownMsgList.remove(position);

					curShowDownMsgList.addFirst(s);
					msgHistory.remove(s);
					msgHistory.addFirst(s);
					et_Msg.setText(sCurSelectedMsg);
					btn_msg_clear.setVisibility(View.VISIBLE);
					cancelFilterMsgInput(parent);
				} else {
					GTLogInternal.setCurFilterLevel(position);
					btn_level.setText(levelAdapter.getItem(position));
				}

				onLogChanged();
			}
		});

		// 将本Activity作为日志数据源的监听
		GTLogInternal.addLogListener(this);

		// 搜索按钮
		
		//btn_search = (ImageButton) findViewById(R.id.log_search);
		
		btn_search = (ImageButton) logLayout.findViewById(R.id.gtlog_search);
		btn_search.setOnClickListener(this);

		// 过滤级别
		btn_level = (Button) logLayout.findViewById(R.id.log_level);
		levelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.log_level,
				R.layout.gt_simple_dropdown_item);
		btn_level.setText(levelAdapter.getItem(GTLogInternal
				.getCurFilterLevel()));

		btn_level.setOnClickListener(this);

		// 过滤TAG
		btn_tag = (Button) logLayout.findViewById(R.id.log_tag);
		if (GTLogInternal.getCurFilterTag().length() == 0) {
			btn_tag.setText("TAG");
		} else {
			btn_tag.setText(GTLogInternal.getCurFilterTag());
		}

		btn_tag.setOnClickListener(this);

		// 过滤文本
		et_Msg = (EditText) logLayout.findViewById(R.id.log_msg);
		et_Msg.setText(GTLogInternal.getCurFilterMsg());

		et_Msg.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && !logAdapter.isEmpty())// 因为日志空时，edit会自动获取焦点
				{
					msgEtOnFocusOrClick();
				}
			}
		});

		et_Msg.setOnClickListener(this);

		et_Msg.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_ENTER:
					// 要先把过滤showdown去掉，否则会多弹出一次
					msgWatched = false;
					et_Msg.removeTextChangedListener(msg_watcher);

					String word = et_Msg.getText().toString();
					if (!word.equals("")) {
						LinkedList<String> curShowDownMsgList = GTLogInternal
								.getCurFilterShowDownMsgList();
						LinkedList<String> msgHistory = GTLogInternal
								.getCurFilterMsgHistory();

						msgHistory.remove(word);
						msgHistory.addFirst(word);
						curShowDownMsgList.remove(word);
						curShowDownMsgList.addFirst(word);
					}

					filterListView.setVisibility(View.GONE);
					img_empty.setVisibility(View.GONE);

					cancelFilterMsgInput(v);

					return true;
				}
				return false;
			}
		});

		// 过滤文本的清理
		btn_msg_clear = (ImageButton) logLayout.findViewById(R.id.log_msg_clear);
		btn_msg_clear.setOnClickListener(this);

		if (et_Msg.getText().toString().length() > 0) {
			btn_msg_clear.setVisibility(View.VISIBLE);
		} else {
			btn_msg_clear.setVisibility(View.GONE);
		}

		// 过滤文本输入的取消
		btn_msg_input_cancel = (Button) logLayout.findViewById(R.id.log_msg_cancel);
		btn_msg_input_cancel.setOnClickListener(this);

		handler = new Handler();
		
		return logLayout;
	}

	private void msgEtOnFocusOrClick() {
		if (!msgWatched) {
			et_Msg.addTextChangedListener(msg_watcher);
			msgWatched = true;
		}

		filterMsgInput();
		MsgAdaptor ma = new MsgAdaptor(getActivity());
		filterListView.setAdapter(ma);
		ma.getFilter().filter(GTLogInternal.getCurFilterMsg());
		if (!filterListView.getAdapter().isEmpty()) {
			filterListView.setVisibility(View.VISIBLE);
			img_empty.setVisibility(View.VISIBLE);
		}
	}

	private void filterMsgInput() {
		LayoutParams laParams = (LayoutParams) et_Msg.getLayoutParams();
		laParams.width = (int) (displayWidth - displayWidth / 4.0f);// 4.737);
		et_Msg.setLayoutParams(laParams);

		btn_msg_input_cancel.setVisibility(View.VISIBLE);
		btn_level.setVisibility(View.INVISIBLE);
		btn_tag.setVisibility(View.INVISIBLE);
		btn_level_toast.setVisibility(View.INVISIBLE);
		btn_tag_toast.setVisibility(View.INVISIBLE);
	}

	private void cancelFilterMsgInput(View v) {
		btn_msg_input_cancel.setVisibility(View.GONE);
		btn_level.setVisibility(View.VISIBLE);
		btn_tag.setVisibility(View.VISIBLE);
		btn_level_toast.setVisibility(View.VISIBLE);
		btn_tag_toast.setVisibility(View.VISIBLE);

		LayoutParams laParams = (LayoutParams) et_Msg.getLayoutParams();
		laParams.width = (int) (displayWidth / 2.74);
		et_Msg.setLayoutParams(laParams);

		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	}

	private OnTouchListener logListTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startY = event.getY();
				return false;
			case MotionEvent.ACTION_UP:
				float currentY = event.getY();
				final ViewConfiguration configuration = ViewConfiguration
						.get(getActivity());
				int mTouchSlop = configuration.getScaledTouchSlop();
				if (Math.abs(currentY - startY) < mTouchSlop * 2) {
					if (rl_log_filter.getVisibility() == View.VISIBLE) {
						rl_log_filter.setVisibility(View.GONE);
						filterListView.setVisibility(View.GONE);
					} else {
						rl_log_filter.setVisibility(View.VISIBLE);
					}
					return true;
				} else {
					return false;
				}
			default:
				return false;
			}
		}
	};
	
	// TODO
	private void showOpenLogDialog() {
		if (! GTUtils.isSDCardExist()) {
			return;
		}

		final List<CharSequence> filenames = new ArrayList<CharSequence>(
				SaveLogHelper.getLogFilenames());

		if (filenames.isEmpty()) {
			Toast.makeText(getActivity(), R.string.no_saved_logs, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		int logToSelect = currentlyOpenLog != null ? filenames
				.indexOf(currentlyOpenLog) : -1;

		ArrayAdapter<CharSequence> dropdownAdapter = new LogFileAdapter(getActivity(),
				filenames, logToSelect, false);

		Builder builder = new Builder(getActivity());

		builder.setTitle(R.string.open_log)
				.setCancelable(true)
				.setSingleChoiceItems(dropdownAdapter,
						logToSelect == -1 ? 0 : logToSelect,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								String filename = filenames.get(which)
										.toString();
								openLog(filename);
							}
						});

		builder.show();
	}

	/*
	 * 实际上用search页来展示，所以跳到search页处理
	 */
	private void openLog(final String filename) {
		if (filename == null)
		{
			return;
		}
		currentlyOpenLog = filename;
		Intent intent = new Intent(getActivity(), GTLogSearchActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("openFileName", filename);
		startActivity(intent);
	}

	private void query() {
		logAdapter.query(GTLogInternal.getCurFilterLevel(),
				GTLogInternal.getCurFilterTag(),
				GTLogInternal.getCurFilterMsg());
	}

	@Override
	public void onResume() {
		super.onResume();
		GTLogInternal.addLogListener(this);
		query();
	}

	@Override
	public void onPause() {
		GTLogInternal.removeLogListener(this);
		// 当log页面被切走时顺道让下拉列表gone掉，为的解决切到别的页面有列表遗留占位的问题，导致其他界面出现一块空白区。
		// 为啥会有这个问题，俺也不知道
		// 反正这么处理下，好了
		filterListView.setVisibility(View.GONE);
		super.onDestroy();
	}

	@Override
	public void onLogChanged() {
		if (getActivity() == null) return;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 用过滤取代直接刷新
				query();
			}
		});

	}

	private void initCurLogAdapter() {
		logAdapter = new NormalLogAdapter(getActivity());
		GTLogInternal.setCurLogAdapter(logAdapter);
	}

	private class MsgAdaptor extends ArrayAdapter<String> {
		Filter filter;

		public MsgAdaptor(Context context) {
			super(context, R.layout.gt_simple_dropdown_item, GTLogInternal
					.getCurFilterShowDownMsgList());
		}

		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new Filter() {
					@Override
					protected FilterResults performFiltering(
							CharSequence constraint) {
						FilterResults results = new FilterResults();
						if (tempShowDownMsgList == null) {
							tempShowDownMsgList = new LinkedList<String>();
						}
						tempShowDownMsgList.clear();

						LinkedList<String> msgHistory = GTLogInternal
								.getCurFilterMsgHistory();

						if (constraint == null || "".equals(constraint)) {
							tempShowDownMsgList.addAll(msgHistory);
						} else {
							for (Iterator<String> iter = msgHistory.iterator(); iter
									.hasNext();) {
								String s = iter.next();
								if (s.contains(constraint)) {
									tempShowDownMsgList.add(s);
								}
							}
						}

						results.values = tempShowDownMsgList;
						results.count = tempShowDownMsgList.size();
						return results;
					}

					@Override
					protected void publishResults(CharSequence constraint,
							FilterResults results) {
						LinkedList<String> curShowDownMsgList = GTLogInternal
								.getCurFilterShowDownMsgList();

						if (results.count > 0) {
							curShowDownMsgList.clear();
							curShowDownMsgList.addAll(tempShowDownMsgList);
							notifyDataSetChanged();
							if (msgWatched) {
								filterListView.setVisibility(View.VISIBLE);
								img_empty.setVisibility(View.VISIBLE);
							}
						} else {
							filterListView.setVisibility(View.GONE);
							img_empty.setVisibility(View.GONE);
							curShowDownMsgList.clear();
							notifyDataSetInvalidated();
						}
					}
				};
			}
			return filter;
		}
	}
	
	public class LogcatRunnable implements Runnable {
		private final Object lock = new Object();
		private boolean killed;
		private com.tencent.wstt.gt.log.logcat.LogcatReader reader;
	
		@Override
		public void run() {
			try {
				LogcatReaderLoader loader = LogcatReaderLoader.create(
						GTApp.getContext(), true);
				reader = loader.loadReader();
	
				while (cb_logcatSwitch.isChecked()) {
					String line = reader.readLine();
					//  如果第一次Reader读不不出数据，则重试一次
					if (line == null)
					{
						reader = loader.loadReader();
						line = reader.readLine();
					}
					GTLog.logCat(line);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			} finally {
				killReader();
			}
		}
	
		public void killReader() {
			if (!killed) {
				synchronized (lock) {
					if (!killed && reader != null) {
						reader.killQuietly();
						killed = true;
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.gtlog_search:
			Intent intent = new Intent(getActivity(),
					GTLogSearchActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		case R.id.gtlog_delete:
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setMessage(getString(R.string.delete_tip));
			builder.setTitle(getString(R.string.delete));
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
							// do delete
							GTLogInternal.clearLog();
							// 注意如果没有过滤，这时候这句和BHLog.clear()等效
							logAdapter.clear();
							// tagAdapter.clear(); 还是不要清理tag的好
							dialog.dismiss();
						}
					});
			builder.show();
			break;
		case R.id.save_clean:
			et_savePath.setText("");
			break;
		case R.id.gtlog_save:
			String lastSaveLog = GTLogInternal.getLastSaveLog();
			if (lastSaveLog != null && lastSaveLog.contains(".")
					&& lastSaveLog.endsWith(LogUtils.LOG_POSFIX)) {
				lastSaveLog = lastSaveLog.substring(0,
						lastSaveLog.lastIndexOf("."));
			}
			et_savePath.setText(lastSaveLog);
			dlg_save.show();
			break;
		case R.id.log_level:
			btn_level.setBackgroundResource(R.drawable.selected_Blue);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					btn_level
							.setBackgroundResource(R.drawable.a_gt_log_btn_default_border);

				}
			}, 20);

			if (filterListView.getVisibility() == View.VISIBLE) {
				img_empty.setVisibility(View.GONE);
				filterListView.setVisibility(View.GONE);
			} else {
				filterListView.setAdapter(levelAdapter);
				filterListView.setVisibility(View.VISIBLE);
				img_empty.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.log_tag:
			btn_tag.setBackgroundResource(R.drawable.selected_Blue);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					btn_tag.setBackgroundResource(R.drawable.a_gt_log_btn_default_border);

				}
			}, 20);

			if (filterListView.getVisibility() == View.VISIBLE) {
				filterListView.setVisibility(View.GONE);
				img_empty.setVisibility(View.GONE);
			} else {
				final List<String> curTagList = new ArrayList<String>();
				curTagList.add("TAG");
				curTagList.addAll(GTLogInternal.getTags());
				tagAdapter = new ArrayAdapter<String>(getActivity(),
						R.layout.gt_simple_dropdown_item, curTagList);

				filterListView.setAdapter(tagAdapter);
				filterListView.setVisibility(View.VISIBLE);
				img_empty.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.rl_loglist:
			if (rl_log_filter.getVisibility() == View.VISIBLE) {
				rl_log_filter.setVisibility(View.GONE);
				filterListView.setVisibility(View.GONE);
			} else {
				rl_log_filter.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.log_msg:
			msgEtOnFocusOrClick();
			break;
		case R.id.log_msg_clear:
			et_Msg.setText("");
			GTLogInternal.setCurFilterMsg("");
			btn_msg_clear.setVisibility(View.GONE);
			// ((ArrayAdapter<?>)filterListView.getAdapter()).getFilter(
			// ).filter(BHLog.getCurFilterMsg());
			onLogChanged();
			break;
		case R.id.log_msg_cancel:
			cancelFilterMsgInput(v);
			break;
		case R.id.gtlog_open:
			showOpenLogDialog();
			break;
		}
	}
}
