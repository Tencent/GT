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
package com.tencent.wstt.gt.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.ui.model.LogEntry;

/**
 * 扩展可用于日志过滤展示的适配
 */
public class NormalLogAdapter extends BaseAdapter implements Filterable {
	
	private Context context;
	
	private int queryLevel = GTLogInternal.LOG_VERBOSE;
	private String queryTag = "";
	private String queryMsg = "";
	
	// 是否过滤条件变化了
	private boolean flag_filter_condition_changed = false;
	// 是否自动刷新
	private boolean flag_auto_refresh = true;

	// 实际显示用的数据源
	private List<LogEntry> dataSet;
	
	// 用于过滤中间状态的数据源，必须有这个，否则过滤会和getView UI冲突
	private RemoveRangeArrayList<LogEntry> tempDataSet;
	
	// TODO 针对dataSet的读写锁，暂时不用
	protected ReadWriteLock dataSetLock = new ReentrantReadWriteLock(false);
	
	private Filter filter;

	public NormalLogAdapter(Context context) {
		super();
		this.context = context;
		this.dataSet = GTLogInternal.getNormalLogList();
	}

	/**
	 * 查询，暂时只支持level和tag查询
	 * @param queryLevel
	 * @param queryGroup
	 * @param queryTag
	 * @param queryMsg
	 */
	public void query(int queryLevel, String queryTag, String queryMsg)
	{
		this.queryLevel = queryLevel;
		this.queryTag = queryTag;
		this.queryMsg = queryMsg.trim().toLowerCase(Locale.CHINA);

		this.getFilter().filter(null);
		
		// 查询条件重新设置时，需要重新加载数据显示
//		notifyDataSetChanged(); 在filter里控制刷新了
	}
	
	/**
	 * TODO 由UI驱动的清理，暂不加锁
	 */
	public void clear()
	{
		this.dataSet.clear();
		if (tempDataSet != null)
		{
			this.tempDataSet.clear();
		}
	}
	
	public void setFilter()
	{
		this.filter = new LogFilter();
	}

	// 返回false后Item间的分割线消失
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	/*
	 * 让ListView的item不可点击 
	 */
	@Override
	public boolean isEnabled(int position) {
		return false; 
	}

	@Override
	public int getCount() {
		return dataSet.size();
	}

	@Override
	public LogEntry getItem(int position) {
		return dataSet.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
		if (convertView == null) {
			tv = (TextView) LayoutInflater.from(context).inflate(
					R.layout.gt_loglist_item, parent,false);
		} else {
			tv = (TextView) convertView;
			tv.setTextColor(Color.GREEN); // 恢复绿色底
		}

		try{
			String target;
			target = getItem(position).msg;

			int tagStart = target.indexOf("/") + 1;
			int tagEnd = target.indexOf("(", tagStart + 1);
			int pidStart = tagEnd + 1;
			int pidEnd = target.indexOf(")", pidStart + 1);

			if (tagStart < tagEnd && pidStart < pidEnd)
			{
				SpannableString ss = new SpannableString(target);
				ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0x9f, 0x9f, 0x9e)),
						0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 日期浅灰色
				ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0xcb, 0x74, 0x18)),
						20, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 级别橘红色
				ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0xcb, 0x74, 0x18)),
						tagStart, tagEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // TAG橘红色
				ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0xcb, 0x74, 0x18)),
						pidStart, pidEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 线程号橘红色
				
				tv.setText(ss);
				return tv;
			}

			// 如果无法解析，直接返回文本内容
			tv.setText(target);
		}
		catch(Exception e)
		{
			tv.setText("can't parse log text!");
			tv.setTextColor(Color.RED); // 红色
		}
		return tv;
	}
	
	@Override
	public Filter getFilter() {
		return filter;
	}
	
	// TODO 返回可见的日志集合,如果频繁使用可能需要加dataSet自己的锁
	public List<LogEntry> getUIEntryList(){
		return new ArrayList<LogEntry>(dataSet);
	}
	
	class LogFilter extends Filter {
		private int lastLevel;
		private CharSequence lastTag;
		private CharSequence lastMsg;
		
		public LogFilter()
		{

		}

		/**
		 * 这里的constraint参数不用，满足不了需求，还是用query方法的参数
		 * NOTE: this function is always called from a background thread, and
         * not the UI thread.
		 */
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			
			// 先重置查询条件变化标志位
			flag_filter_condition_changed = false;
			
			if (queryLevel == GTLogInternal.LOG_VERBOSE
				&& (null == NormalLogAdapter.this.queryTag ||  NormalLogAdapter.this.queryTag.length() == 0)
				&& (null == NormalLogAdapter.this.queryMsg ||  NormalLogAdapter.this.queryMsg.length() == 0))
			{
				GTLogInternal.getLogListReadLock().lock();
				results.values = GTLogInternal.getNormalLogList();
				results.count = GTLogInternal.getNormalLogList().size();
				GTLogInternal.getLogListReadLock().unlock();
			}
			// 查询条件没变化的情况,需要增量查询数据源并增量追加FilterResults
			else if ((queryLevel == lastLevel)
					&& (queryTag == lastTag || null != queryTag && queryTag.equals(lastTag))
					&& (queryMsg == lastMsg || null != queryMsg && queryMsg.equals(lastMsg))) 
			{
				GTLogInternal.getLogListReadLock().lock();
				try
				{
					int i = Math.max(GTLogInternal.getNormalLogLastFilterEndLocation(), 0);
					for (; i < GTLogInternal.getNormalLogList().size(); i++)
					{
						LogEntry entry = GTLogInternal.getNormalLogList().get(i);
						if ((null == lastTag
								|| null == entry.tag
								|| entry.tag.contains(lastTag))
								&& (null == lastMsg
										|| null == entry.msg
										|| entry.msg.toLowerCase(Locale.CHINA).contains(lastMsg))
								// level是不可缺的
								&& entry.level >= lastLevel)
						{
							tempDataSet.add(entry);
						}
					}
					// 要保证UI展示时也不超过1000条
					int length = tempDataSet.size();
					if (length > LogUtils.CACHE)
					{
						tempDataSet.remove(0, length -  LogUtils.CACHE);
					}
					
					GTLogInternal.resetNormalLogLastFilterEndLocation();
					
					results.values = tempDataSet;
					results.count = tempDataSet.size();
				}
				finally
				{
					GTLogInternal.getLogListReadLock().unlock();
				}
			}
			// 查询条件有变化，需要重新在数据源中查询
			else
			{
				flag_filter_condition_changed = true;
				
				lastLevel = queryLevel;
				lastTag = queryTag;
				lastMsg = queryMsg;
				
				if (tempDataSet != null)
				{
					tempDataSet.clear();
				}
				tempDataSet = new RemoveRangeArrayList<LogEntry>();
				
				GTLogInternal.getLogListReadLock().lock();
				try
				{
					for (LogEntry entry : GTLogInternal.getNormalLogList())
					{
						if ((null == lastTag
								|| null == entry.tag
								|| (null != entry.tag && entry.tag.contains(lastTag)))
								&& (null == lastMsg
										|| null == entry.msg
										|| (null != entry.msg
										&& entry.msg.toLowerCase(
												Locale.CHINA).contains(lastMsg)))
								// level是不可缺的
								&& entry.level >= lastLevel)
						{
							tempDataSet.add(entry);
						}
					}
					GTLogInternal.resetNormalLogLastFilterEndLocation();
					results.values = tempDataSet;
					results.count = tempDataSet.size();
				}
				finally
				{
					GTLogInternal.getLogListReadLock().unlock();
				}
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		/*
		 * NOTE: this function is always called from the UI thread.
		 */
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			if (results.count == 0)
			{
				dataSet = Collections.EMPTY_LIST;
				GTLogInternal.setFilterdLogList(dataSet);
				notifyDataSetInvalidated();
			}
			else
			{
				dataSet = new ArrayList<LogEntry>((List<LogEntry>) results.values);	
				GTLogInternal.setFilterdLogList(dataSet);
				if(flag_auto_refresh || flag_filter_condition_changed){
					notifyDataSetChanged();
				}
			}
		}
		
	}
	
	// TODO 是否自动刷新，包子新增，待检视
	public void setAutoRefresh(boolean flag){
		flag_auto_refresh = flag;
	}
}