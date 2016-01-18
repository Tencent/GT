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

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortListView;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.manager.IpUIManager;
import com.tencent.wstt.gt.manager.ParamConst;

public class GTParamInEditFragment extends ListFragment implements DragSortListView.DropListener {

	private View layout_inparamlayout;

	private DragSortListView inList;
	public static List<InPara> list_ip = new ArrayList<InPara>();

	// 用于外部变化需要通知本页页刷新的Handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			doResume();
		}
	};

	public GTParamInEditFragment()
	{
		super();
		GTApp.setIpEditHandler(handler);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		layout_inparamlayout = inflater.inflate(R.layout.gt_param_in_edit, container, false);
		return layout_inparamlayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		inList = (DragSortListView) getListView();
		inList.setDropListener(this);
		registerForContextMenu(inList);
	}

	public void onResume()
	{
		super.onResume();

		list_ip = IpUIManager.list_ip;
		doResume();
	}

	private void doResume()
	{
		GTParamInEditListAdapter inparam_adapter = new GTParamInEditListAdapter(getActivity(), list_ip);
		setListAdapter(inparam_adapter);
	}

	public static int getInListDisableTitlePosition()
	{
		int pos = 0;
		for (int i = 0; i < list_ip.size(); i++)
		{
			if (list_ip.get(i).getKey().equals(ParamConst.PROMPT_DISABLE_TITLE))
			{
				pos = i;
				break;
			}
		}
		return pos;
	}

	public static int getInListDividePosition()
	{
		int pos = 0;
		for (int i = 0; i < list_ip.size(); i++)
		{
			if (list_ip.get(i).getKey().equals(ParamConst.DIVID_TITLE))
			{
				pos = i;
				break;
			}
		}
		return pos;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

	}

	public void onShow(boolean show)
	{
		
	}

	@Override
	public void drop(int from, int to) {
		String condition_key1 = list_ip.get(from).getKey();
		String condition_key2 = "";
		if (list_ip.size() == 4)
		{
			condition_key2 = list_ip.get(3).getKey();
		}
		if (list_ip.size() > 4)
		{
			condition_key2 = list_ip.get(4).getKey();
		}

		if (condition_key1.equals(ParamConst.DIVID_TITLE) || condition_key1.equals(ParamConst.PROMPT_INIT_TITLE)
				|| condition_key1.equals(ParamConst.PROMPT_TITLE))
		{
			((BaseAdapter) inList.getAdapter()).notifyDataSetChanged();
		}
		else if (condition_key2.equals(ParamConst.DIVID_TITLE) && from > to && from > 4 && to <= 4)
		{
			((BaseAdapter) inList.getAdapter()).notifyDataSetChanged();
		}
		else
		{
			// Assuming that item is moved up the list
			int direction = -1;
			int loop_start = from;
			int loop_end = to;

			if (0 == to)
			{
				to = 1;
			}

			// For instance where the item is dragged down the list
			if (from < to)
			{
				direction = 1;
			}

			InPara iv_target = list_ip.get(from);

			for (int i = loop_start; i != loop_end; i = i + direction)
			{
				list_ip.set(i, list_ip.get(i + direction));
			}

			list_ip.set(to, iv_target);

			if (to > getInListDisableTitlePosition())
			{
				iv_target.setDisplayProperty(InPara.DISPLAY_DISABLE);
			}
			else if (to < getInListDividePosition())
			{
				iv_target.setDisplayProperty(InPara.DISPLAY_AC);
			}
			else
			{
				iv_target.setDisplayProperty(InPara.DISPLAY_NORMAL);
			}
			((BaseAdapter) inList.getAdapter()).notifyDataSetChanged();
		}
		
		GTApp.getIpHandler().sendEmptyMessage(0);
	}
}
