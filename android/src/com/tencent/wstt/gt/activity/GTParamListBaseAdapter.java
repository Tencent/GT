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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tencent.wstt.gt.AidlEntry;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.manager.ParamConst;

public abstract class GTParamListBaseAdapter extends BaseAdapter {

	protected List<? extends AidlEntry> list;
	protected Context context;
	protected LayoutInflater mInflater;

	protected static final int TYPE_PROMPT_ITEM = 0;
	protected static final int TYPE_DIVIDE_ITEM = 1;
	protected static final int TYPE_DISABLE_ITEM = 2;
	protected static final int TYPE_SWITCH_ITEM = 3;
	protected static final int VIEW_TYPE_COUNT = 4; // 参数类型数

	// 分割线的convertView一开始就固化
	protected View PROMPT_ITEM_convertView;
	protected View DIVIDE_ITEM_convertView;
	protected View DISABLE_ITEM_convertView;

	protected TextView PROMPT_ITEM_title;

	protected TextView DIVIDE_ITEM_title;
	protected TextView DIVIDE_ITEM_top_border;

	protected TextView DISABLE_ITEM_title;
	protected TextView DISABLE_ITEM_top_border;

	public GTParamListBaseAdapter(Context context, List<? extends AidlEntry> list)
	{
		this.context = context == null ? GTApp.getContext() : context;
		this.list = list;
		this.mInflater = LayoutInflater.from(this.context);

		PROMPT_ITEM_convertView = mInflater.inflate(R.layout.gt_listrow_prompt_title, null);
		DIVIDE_ITEM_convertView = mInflater.inflate(R.layout.gt_listrow_title, null);
		DISABLE_ITEM_convertView = mInflater.inflate(R.layout.gt_listrow_title, null);

		PROMPT_ITEM_title = (TextView) PROMPT_ITEM_convertView.findViewById(R.id.draglist_title);
		PROMPT_ITEM_title.setText(ParamConst.PROMPT_INIT_TITLE);

		DIVIDE_ITEM_title = (TextView) DIVIDE_ITEM_convertView.findViewById(R.id.draglist_title);
		DIVIDE_ITEM_title.setText(ParamConst.DIVID_TITLE);
		DIVIDE_ITEM_top_border = (TextView) DIVIDE_ITEM_convertView.findViewById(R.id.listrow_top_border);

		DISABLE_ITEM_title = (TextView) DISABLE_ITEM_convertView.findViewById(R.id.draglist_title);
		DISABLE_ITEM_title.setText(ParamConst.PROMPT_DISABLE_TITLE);
		DISABLE_ITEM_top_border = (TextView) DISABLE_ITEM_convertView.findViewById(R.id.listrow_top_border);
	}

	@Override
	public int getCount() {
		if (null != list) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		AidlEntry o = list.get(position);
		String key = "";
		if (o instanceof OutPara)
		{
			key = ((OutPara)o).getKey();
		}
		else
		{
			key = ((InPara)o).getKey();
		}
		
		int result = TYPE_SWITCH_ITEM;
		if (key.equals(ParamConst.PROMPT_TITLE) || key.equals(ParamConst.PROMPT_INIT_TITLE)) {
			result = TYPE_PROMPT_ITEM;
		} else if (key.equals(ParamConst.DIVID_TITLE)) {
			result = TYPE_DIVIDE_ITEM;
		} else if (key.equals(ParamConst.PROMPT_DISABLE_TITLE)) {
			result = TYPE_DISABLE_ITEM;
		} else {
			result = TYPE_SWITCH_ITEM;
		}
		return result;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}
}
