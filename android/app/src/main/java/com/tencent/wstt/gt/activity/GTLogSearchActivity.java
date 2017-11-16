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

import java.util.Iterator;
import java.util.LinkedList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.log.GTLogInternal;
import com.tencent.wstt.gt.log.SearchLogAdapter;
import com.tencent.wstt.gt.log.logcat.SaveLogHelper;
import com.tencent.wstt.gt.ui.model.LogEntry;

public class GTLogSearchActivity extends GTBaseActivity {
	private ImageView img_empty;
	
	TextView tv_count;
	ImageButton btn_pre;
	ImageButton btn_next;

	private ListView listView;
	private SearchLogAdapter arrayAdapter;
	private LogEntry[] dataSet;

	private ListView filterListView;
	// 有时候用户操作果过快，会导致crash，在不使用handler的情况需要加锁，TODO 先不加锁看看效果
//	private Lock filterLock;

	private EditText et_Msg;
	private ImageButton btn_msg_clear;
	private Button btn_back;
	
	private Handler handler;

	private TextWatcher msg_watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			String sCurSelectedMsg = s.toString();
			GTLogInternal.setCurSearchMsg(sCurSelectedMsg);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_log_search_activity);
		
		handler = new Handler();
		
		tv_count = (TextView)findViewById(R.id.log_search_count);
		btn_pre = (ImageButton)findViewById(R.id.log_msg_pre);
		btn_next = (ImageButton)findViewById(R.id.log_msg_next);
		/*
		 * 用于覆盖整个屏幕的透明ImageView，
		 * 主要帮助点击非filterListView区域使filterListView消失
		 */
		img_empty = (ImageView) findViewById(R.id.log_search_view_empty);
		img_empty.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
			    case MotionEvent.ACTION_DOWN:
			    	img_empty.setVisibility(View.GONE);
					filterListView.setVisibility(View.GONE);
					cancelFilterMsgInput(v);
			        break;
			    case MotionEvent.ACTION_UP:
			        v.performClick();
			        break;
			    default:
			        break;
			    }
				return false;
			}
		});

		// 过滤数据展示列表
		filterListView = (ListView) findViewById(R.id.log_search_spinner_list);
//		filterLock = new ReentrantLock();
		
		filterListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long arg3) {
				img_empty.setVisibility(View.GONE);
				filterListView.setVisibility(View.GONE);
				
				String sCurSelectedMsg = (String) parent.getAdapter().getItem(position);
				LinkedList<String> curShowDownMsgList = GTLogInternal.getCurShowDownMsgList();
				LinkedList<String> msgHistory = GTLogInternal.getCurSearchMsgHistory();
				
				GTLogInternal.setCurSearchMsg(sCurSelectedMsg);
				msgWatched = false;
				et_Msg.removeTextChangedListener(msg_watcher);
				String s = curShowDownMsgList.remove(position);
				curShowDownMsgList.addFirst(s);
				msgHistory.remove(s);
				msgHistory.addFirst(s);
				et_Msg.setText(sCurSelectedMsg);
				btn_msg_clear.setVisibility(View.VISIBLE);
				cancelFilterMsgInput(parent);
				
//				filterLock.lock();
				((MsgAdaptor)filterListView.getAdapter()).getFilter().filter(sCurSelectedMsg);
//				filterLock.unlock();
				doSearch(sCurSelectedMsg);
			}
		});

		// 过滤文本
		et_Msg = (EditText) findViewById(R.id.log_search_msg);
//		et_Msg.setText(BHLog.getCurSearchMsg());
		et_Msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!msgWatched) {
					et_Msg.addTextChangedListener(msg_watcher);
					msgWatched = true;
				}

				MsgAdaptor adapter = new MsgAdaptor(GTLogSearchActivity.this);
				adapter.getFilter().filter(et_Msg.getText().toString());
//				filterLock.lock();
				filterListView.setAdapter(adapter);
//				filterLock.unlock();
				if (!filterListView.getAdapter().isEmpty()) {
					filterListView.setVisibility(View.VISIBLE);
					img_empty.setVisibility(View.VISIBLE);
				}
			}
		});

		et_Msg.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_ENTER:
					// 要先把过滤showdown去掉，否则会多弹出一次
					msgWatched = false;
					et_Msg.removeTextChangedListener(msg_watcher);

					String word = et_Msg.getText().toString();

					filterListView.setVisibility(View.GONE);
					img_empty.setVisibility(View.GONE);

					cancelFilterMsgInput(v);
					if (word.trim().length() == 0) {
						return true;
					}
					LinkedList<String> curShowDownMsgList = GTLogInternal.getCurShowDownMsgList();
					LinkedList<String> msgHistory = GTLogInternal.getCurSearchMsgHistory();
					
					msgHistory.remove(word);
					msgHistory.addFirst(word);
					curShowDownMsgList.remove(word);
					curShowDownMsgList.addFirst(word);
					
					doSearch(word);
					return true;
				}
				return false;
			}
		});

		// 过滤文本的清理
		btn_msg_clear = (ImageButton) findViewById(R.id.log_msg_search_clear);
		btn_msg_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_Msg.setText("");
				// 清理本次搜索标记
				GTLogInternal.clearLastSearchMarks();
				tv_count.setText("0 / 0");
				arrayAdapter.notifyDataSetChanged();
				btn_msg_clear.setVisibility(View.GONE);
			}
		});

		// 改成back功能了
		btn_back = (Button) findViewById(R.id.log_msg_search_cancel);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 20130404 改成back功能了
				finish();
			}
		});
		
		btn_pre.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int historySeq = GTLogInternal.getLastMatchedSeq();
				if (historySeq > 0)
				{
					GTLogInternal.setLastMatchedSeq(historySeq - 1);
					listView.setSelection(GTLogInternal.getLastMatchedEntryList().get(historySeq - 1).posionInDataSet);
					
					tv_count.setText(historySeq + // 这里注意显示序号要比存储位置多1，因为从1开始算
							" / " + GTLogInternal.getLastMatchedEntryList().size());

					handler.post(new Runnable(){

						@Override
						public void run() {
							// 这个刷新需要在UI线程排队，否则会被listView.setSelection方法冲突掉
							arrayAdapter.notifyDataSetChanged();
						}});
				}
				else if (historySeq == 0 && GTLogInternal.getLastMatchedEntryList().size() > 0)
				{
					GTLogInternal.setLastMatchedSeq(GTLogInternal.getLastMatchedEntryList().size() - 1);
					listView.setSelection(GTLogInternal.getLastMatchedEntryList().get(
							GTLogInternal.getLastMatchedEntryList().size() - 1).posionInDataSet);
					
					tv_count.setText(GTLogInternal.getLastMatchedEntryList().size() +
							" / " + GTLogInternal.getLastMatchedEntryList().size());

					handler.post(new Runnable(){

						@Override
						public void run() {
							// 这个刷新需要在UI线程排队，否则会被listView.setSelection方法冲突掉
							arrayAdapter.notifyDataSetChanged();
						}});
				}
				
			}});
		
		btn_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int historySeq = GTLogInternal.getLastMatchedSeq();
				if (historySeq < GTLogInternal.getLastMatchedEntryList().size() - 1)
				{
					GTLogInternal.setLastMatchedSeq(historySeq + 1);
					listView.setSelection(GTLogInternal.getLastMatchedEntryList().get(historySeq + 1).posionInDataSet);
					
					int viewSeq = historySeq + 2;
					tv_count.setText(viewSeq + // 这里注意显示序号要比存储位置多1，因为从1开始算
							" / " + GTLogInternal.getLastMatchedEntryList().size());
					
					handler.post(new Runnable(){

						@Override
						public void run() {
							// 这个刷新需要在UI线程排队，否则会被listView.setSelection方法冲突掉
							arrayAdapter.notifyDataSetChanged();
						}});
				}
				else if (historySeq == GTLogInternal.getLastMatchedEntryList().size() - 1)
				{
					GTLogInternal.setLastMatchedSeq(0);
					listView.setSelection(GTLogInternal.getLastMatchedEntryList().get(0).posionInDataSet);
					
					tv_count.setText(1 + // 这里注意显示序号要比存储位置多1，因为从1开始算
							" / " + GTLogInternal.getLastMatchedEntryList().size());
					
					handler.post(new Runnable(){

						@Override
						public void run() {
							// 这个刷新需要在UI线程排队，否则会被listView.setSelection方法冲突掉
							arrayAdapter.notifyDataSetChanged();
						}});
				}
				
			}});

		// 准备日志列表数据
		listView = (ListView) findViewById(R.id.log_search_list);

		Intent intent = getIntent();   
        String openFileName = intent.getStringExtra("openFileName");
        if (openFileName == null)
        {
        	// 日志列表
    		dataSet = GTLogInternal.getCurFilteredLogs();
    		arrayAdapter = new SearchLogAdapter(this, dataSet);
    		listView.setAdapter(arrayAdapter);
        }
        // 打开历史日志
        else
        {
        	openLog(openFileName);
        }
	}
	
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	public void onDestroy()
	{
		GTLogInternal.setLastSearchMsg("");
		GTLogInternal.setLastSearchDataSet(new LogEntry[]{});
		GTLogInternal.clearLastSearchMarks();
		super.onDestroy();
	}

	/*
	 * TODO 先准备数据，期间转菊花，数据完成后，跳到search页展示
	 * 注意日志的长度问题
	 */
	private ProgressDialog proDialog;
	private void openLog(final String filename)
	{
		final AsyncTask<Void, Void, LogEntry[]> openFileTask = new AsyncTask<Void, Void, LogEntry[]>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// 转菊花
				proDialog = ProgressDialog.show(GTLogSearchActivity.this,
						"get data..", "geting data..wait...", true, true);
			}

			@Override
			protected LogEntry[] doInBackground(Void... params) {
				final int maxLines = 1000;
				LogEntry[] logLines = SaveLogHelper.openLog(filename, maxLines);
				return logLines;
			}

			@Override
			protected void onPostExecute(LogEntry[] logLines) {
				super.onPostExecute(logLines);
				// 取消菊花
				proDialog.dismiss();
				proDialog = null;

				dataSet = logLines;
	    		arrayAdapter = new SearchLogAdapter(GTLogSearchActivity.this, dataSet);
	    		listView.setAdapter(arrayAdapter);
			}
		};

		openFileTask.execute((Void) null);
	}

	private void doSearch(String searchKey)
	{
		GTLogInternal.setLastSearchMsg(searchKey);
		GTLogInternal.setLastSearchDataSet(dataSet);
		
		arrayAdapter.notifyDataSetChanged();
		
		/*
		 *  TODO 下面这些应该写在Handler里
		 */
		handler.post(new Runnable(){

			@Override
			public void run() {
				if (GTLogInternal.getLastMatchedEntryList().size() > 0)
				{
					// 选中最后一个作为选中的高亮点
					listView.setSelection(GTLogInternal.getLastMatchedEntryList().get(
							GTLogInternal.getLastMatchedEntryList().size() - 1).posionInDataSet);
					GTLogInternal.setLastMatchedSeq(GTLogInternal.getLastMatchedEntryList().size() - 1);
				}
			}
		});

		tv_count.setText(GTLogInternal.getLastMatchedEntryList().size() +" / " + GTLogInternal.getLastMatchedEntryList().size());

	}

	private void cancelFilterMsgInput(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	}

	private class MsgAdaptor extends ArrayAdapter<String> {
		Filter filter;

		public MsgAdaptor(Context context) {
			super(context, R.layout.gt_simple_dropdown_item,
					GTLogInternal.getCurShowDownMsgList());
		}

		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new Filter() {
					@Override
					protected FilterResults performFiltering(
							CharSequence constraint) {
						FilterResults results = new FilterResults();
						LinkedList<String> curShowDownMsgList = GTLogInternal.getCurShowDownMsgList();
						LinkedList<String> msgHistory = GTLogInternal.getCurSearchMsgHistory();
						
						curShowDownMsgList.clear();
						if (constraint != null) {
							for (Iterator<String> iter = msgHistory.iterator(); iter
									.hasNext();) {
								String s = iter.next();
								if (s.contains(constraint)) {
									curShowDownMsgList.add(s);
								}
							}
						}

						results.values = curShowDownMsgList;
						results.count = curShowDownMsgList.size();
						return results;
					}

					@Override
					protected void publishResults(CharSequence constraint,
							FilterResults results) {
						if (results.count > 0) {
							notifyDataSetChanged();
							if (msgWatched) {
								filterListView.setVisibility(View.VISIBLE);
								img_empty.setVisibility(View.VISIBLE);
							}
						} else {
							filterListView.setVisibility(View.GONE);
							img_empty.setVisibility(View.GONE);
							notifyDataSetInvalidated();
						}
					}
				};
			}
			return filter;
		}
	}
}
