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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.manager.IpUIManager;
import com.tencent.wstt.gt.manager.ParamConst;

public class GTParamInFragment extends ListFragment
{
	// 记录界面展示状态
	public static boolean gw_running = false; // 标记gw是否开始采集，true 为运行

	// 输入参数相关
	private ListView inList;
	private View layout_inparamlayout;
	private TextView tv_ip_title_toast;

	// 用于外部变化需要通知AUT页刷新的Handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			doResume();
		}
	};

	public GTParamInFragment()
	{
		super();
		GTApp.setIpHandler(handler);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		layout_inparamlayout = inflater.inflate(R.layout.gt_param_in, container, false);
		return layout_inparamlayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		inList = (ListView) getListView();
	}

	public void onResume()
	{
		super.onResume();
		doResume();
	}

	private void doResume()
	{
		getInputParamsList();
		GTParamInListAdapter inparam_adapter = new GTParamInListAdapter(getActivity(), IpUIManager.list_ip);
		setListAdapter(inparam_adapter);

		if (IpUIManager.list_ip != null)
		{
			if (inList != null)
				((BaseAdapter) inList.getAdapter()).notifyDataSetChanged();
		}
	}

	public static int getInListDividePosition()
	{
		int pos = 0;
		for (int i = 0; i < IpUIManager.list_ip.size(); i++)
		{
			if (IpUIManager.list_ip.get(i).getKey().equals(ParamConst.DIVID_TITLE))
			{
				pos = i;
				break;
			}
		}
		return pos;
	}

	public static int getInListAcDividePosition()
	{
		int pos = 0;
		for (int i = 0; i < IpUIManager.list_ip.size(); i++)
		{
			if (IpUIManager.list_ip.get(i).getKey().equals(ParamConst.PROMPT_INIT_TITLE))
			{
				pos = i;
				break;
			}
		}
		return pos;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		String key = IpUIManager.list_ip.get(position).getKey();
		if (key.equals(ParamConst.DIVID_TITLE) || key.equals(ParamConst.PROMPT_INIT_TITLE) || key.equals(ParamConst.PROMPT_TITLE)
				|| key.equals(ParamConst.PROMPT_DISABLE_TITLE))
		{
			return;
		}
		if (position > IpUIManager.getInListDisableTitlePosition())
		{
			return;
		}
		InPara ip = IpUIManager.list_ip.get(position);
		Bundle bundle = new Bundle();
		bundle.putString("ip_name", key);
		bundle.putString("ip_client", ip.getClient());
		bundle.putStringArrayList("ip_values", (ArrayList<String>) ip.getValues());

		Intent intent = new Intent(getActivity(), GTInputParamSetActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void getInputParamsList()
	{
		tv_ip_title_toast = (TextView) layout_inparamlayout.findViewById(R.id.ip_title_toast);
		IpUIManager.list_ip.clear();
		if (IpUIManager.isEmpty())
		{
			tv_ip_title_toast.setVisibility(View.VISIBLE);
		}
		else
		{
			tv_ip_title_toast.setVisibility(View.GONE);

			// 悬浮框分界线
			InPara iv_ac = new InPara();
			iv_ac.setKey(ParamConst.PROMPT_INIT_TITLE);
			iv_ac.setDisplayProperty(InPara.DISPLAY_TITLE);
			IpUIManager.list_ip.add(iv_ac);

			// 加悬浮框入参
			for (InPara iv : IpUIManager.getAll())
			{
				if (InPara.DISPLAY_AC == iv.getDisplayProperty())
				{
					IpUIManager.list_ip.add(iv);
				}
			}

			// 加普通关注分界线
			InPara iv_normalDivid = new InPara();
			iv_normalDivid.setKey(ParamConst.DIVID_TITLE);
			iv_normalDivid.setDisplayProperty(InPara.DISPLAY_TITLE);
			IpUIManager.list_ip.add(iv_normalDivid);

			// 加普通关注入参
			for (InPara iv : IpUIManager.getAll())
			{
				if (InPara.DISPLAY_NORMAL == iv.getDisplayProperty())
				{
					IpUIManager.list_ip.add(iv);
				}
			}

			// 加disable入参分界线
			InPara iv_disableDivid = new InPara();
			iv_disableDivid.setKey(ParamConst.PROMPT_DISABLE_TITLE);
			iv_disableDivid.setDisplayProperty(InPara.DISPLAY_TITLE);
			IpUIManager.list_ip.add(iv_disableDivid);

			// 加disable的入参
			for (InPara iv : IpUIManager.getAll())
			{
				if (InPara.DISPLAY_DISABLE == iv.getDisplayProperty())
				{
					IpUIManager.list_ip.add(iv);
				}
			}
		}
	}

	public void onShow(boolean show)
	{
		
	}
}
