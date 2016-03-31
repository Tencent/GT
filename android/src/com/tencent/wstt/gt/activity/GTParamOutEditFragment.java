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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortListView;
import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.ParamConst;

public class GTParamOutEditFragment
	extends ListFragment implements DragSortListView.DropListener {

	DragSortListView outList; 
	private GTParamOutEditListAdapter outparam_adapter;

	// 用于外部变化需要通知AUT页刷新的Handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// 清理累积的消息，保留一次即可
			removeCallbacksAndMessages(null);
			doResume();
		}
	};

	public GTParamOutEditFragment()
	{
		super();
		GTApp.setOpEditHandler(handler);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View layout = inflater.inflate(R.layout.gt_param_out_edit,
				container, false);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		outList = (DragSortListView) getListView();
		outList.setDropListener(this);
	}

	public void onResume()
	{
		super.onResume();
		doResume();
	}

	private void doResume()
	{
		outparam_adapter =
				new GTParamOutEditListAdapter(getActivity(), OpUIManager.list_op);
		setListAdapter(outparam_adapter);
		outparam_adapter.notifyDataSetChanged();
	}

	@Override
	public void drop(int from, int to) {
		if (OpUIManager.list_op.get(from).getDisplayProperty() == OutPara.DISPLAY_TITLE) {
			outparam_adapter.notifyDataSetChanged();
		} else if (OpUIManager.list_op.get(4).getKey().equals(ParamConst.DIVID_TITLE)
				&& from > to && from > 4 && to <= 4) {
			outparam_adapter.notifyDataSetChanged();
		} else {
			int direction = -1;
			int loop_start = from;
			int loop_end = to;

			if (0 == to) {
				to = 1;
			}

			if (from < to) {
				direction = 1;
			}

			OutPara ov_target = OpUIManager.list_op.get(from);

			for (int i = loop_start; i != loop_end; i = i + direction) {
				OpUIManager.list_op.set(i, OpUIManager.list_op.get(i + direction));
			}

			OpUIManager.list_op.set(to, ov_target);
			if (to > OpUIManager.getOutListDisableTitlePosition()) {
				ov_target.setDisplayProperty(OutPara.DISPLAY_DISABLE);
			} else if (to < OpUIManager.getOutListDividePosition()) {
				ov_target.setDisplayProperty(OutPara.DISPLAY_AC);
			} else {
				ov_target.setDisplayProperty(OutPara.DISPLAY_NORMAL);
			}
			outparam_adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
	}
	
	public void onShow(boolean show)
	{
		
	}
}
