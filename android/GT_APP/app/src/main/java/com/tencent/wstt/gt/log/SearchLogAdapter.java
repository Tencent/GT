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

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.ui.model.LogEntry;
import com.tencent.wstt.gt.ui.model.MatchedEntry;

public class SearchLogAdapter extends BaseAdapter {

	private Context context;

	// 实际显示用的数据源
	private LogEntry[] dataSet;

	public SearchLogAdapter(Context context, LogEntry[] dataSet) {
		super();
		this.context = context;
		this.dataSet = dataSet;
	}

	@Override
	public int getCount() {
		return dataSet.length;
	}

	@Override
	public LogEntry getItem(int position) {
		return dataSet[position];
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LinearLayout ll;
		if (convertView == null) {
			ll = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.gt_log_search_list_item, parent, false);
		} else {
			ll = (LinearLayout) convertView;
		}

		TextView tvSeq = (TextView) ll.findViewById(R.id.log_search_list_item_seq);
		tvSeq.setText(position + ".");

		TextView tv = (TextView) ll.findViewById(R.id.log_search_list_item);

		String target = dataSet[position].msg;
		int tagStart = target.indexOf("/") + 1;
		int tagEnd = target.indexOf("(", tagStart + 1);
		int tidStart = tagEnd + 1;
		int tidEnd = target.indexOf(")", tidStart + 1);
		
		if (tagStart < tagEnd && tidStart < tidEnd)
		{
			SpannableString ss = new SpannableString(target);
			ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0x9f, 0x9f, 0x9e)),
					0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 日期浅灰色
			ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0xcb, 0x74, 0x18)),
					20, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 级别橘红色
			ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0xcb, 0x74, 0x18)),
					tagStart, tagEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // TAG橘红色
			ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0xcb, 0x74, 0x18)),
					tidStart, tidEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 线程号橘红色

			// TODO 搜索字符串高亮部分处理
			if (GTLogInternal.getLastMatchedEntryList().size() > 0)
			{
				List<MatchedEntry> matchedList = GTLogInternal.getLastMatchedEntryList();
				
				for (int matchedSeq = 0; matchedSeq < matchedList.size(); matchedSeq++)
				{
					MatchedEntry mached = matchedList.get(matchedSeq);
					if (mached.posionInDataSet > position) // 比当前显示位置远的部分不需要即时处理
					{
						break;
					}
					if (mached.posionInDataSet == position)
					{
						ss.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0x00, 0x00, 0x00)),
								mached.start, mached.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 前景色黑色
						
						
						if (matchedSeq == GTLogInternal.getLastMatchedSeq())
						{
							ss.setSpan(new BackgroundColorSpan(Color.argb(0xff, 0xdd, 0xff, 0x43)),
									mached.start, mached.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 背景色黄色
						}
						else
						{
							ss.setSpan(new BackgroundColorSpan(Color.argb(0xff, 0x38, 0xad, 0x29)),
									mached.start, mached.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 背景色绿色
						}
					}
				}
			}

			tv.setText(ss);
			return ll;
		}

		// 如果无法解析，直接返回文本内容
		tv.setText(target);
		return ll;
	}

}
