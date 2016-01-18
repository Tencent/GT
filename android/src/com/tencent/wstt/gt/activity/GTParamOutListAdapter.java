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
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.internal.GTMemoryDaemonThread;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.manager.ParamConst;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.utils.ToastUtil;

public class GTParamOutListAdapter extends GTParamListBaseAdapter {

	public GTParamOutListAdapter(Context context, List<OutPara> list) {
		super(context, list);
	}

	@Override
	public void notifyDataSetChanged() {
		if (!OpUIManager.refresh_op_drag_conflict_flag) {
			super.notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ParamConst.ViewHolderDrag_nopic switch_drag = null;

		final OutPara ov = (OutPara)list.get(position);
		final String key = ov.getKey();
		String value = ov.getFreezValue();

		// 已记录历史数据个数的获取
		TagTimeEntry tte = OpPerfBridge.getProfilerData(key);
		int his_count = 0;
		if (tte != null) {
			if (tte.hasChild()) {
				his_count = tte.getChildren()[0].getRecordSize();
			} else {
				his_count = tte.getRecordSize();
			}
		}
		final String alias = ov.getAlias();
		final int type = getItemViewType(position);

		/*
		 * 根据每行UI的类型进行UI模型准备与数据准备
		 */
		int disable_pos = OpUIManager.getOutListDisableTitlePosition();
		switch (type) {
		case TYPE_PROMPT_ITEM:
			convertView = PROMPT_ITEM_convertView;
			break;
		case TYPE_DIVIDE_ITEM:
			convertView = DIVIDE_ITEM_convertView;
			break;
		case TYPE_DISABLE_ITEM:
			convertView = DISABLE_ITEM_convertView;
			break;
		case TYPE_SWITCH_ITEM:
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.gt_outlistrow_value, null);
				switch_drag = new ParamConst.ViewHolderDrag_nopic();
				switch_drag.tv_key = (TextView) convertView
						.findViewById(R.id.draglist_key);
				switch_drag.tv_alias = (TextView) convertView
						.findViewById(R.id.draglist_shotkey);
				switch_drag.tv_value = (TextView) convertView
						.findViewById(R.id.draglist_value);
				switch_drag.tv_listview_bottom_border = (TextView) convertView
						.findViewById(R.id.listrow_bottom_border);

				switch_drag.tv_his_data = (TextView) convertView
						.findViewById(R.id.data_flag);
				switch_drag.tv_listrowbg = (TextView) convertView
						.findViewById(R.id.listrow_bg);
				switch_drag.cb = (CheckBox) convertView
						.findViewById(R.id.cb_tick);
			}
			else
			{
				switch_drag = (ParamConst.ViewHolderDrag_nopic)convertView.getTag();
			}

			if (ov.isAlert()) {
				switch_drag.tv_listrowbg.setBackgroundColor(context.getResources()
						.getColor(R.color.orange));
				switch_drag.tv_listrowbg.getBackground().setAlpha(80);
			} else {
				switch_drag.tv_listrowbg
						.setBackgroundColor(Color.TRANSPARENT);

			}

			if (OpUIManager.gw_running) {
				switch_drag.cb.setClickable(false);
				switch_drag.cb.setEnabled(false);
			} else {
				switch_drag.cb.setEnabled(true);
				switch_drag.cb.setClickable(true);
			}
			switch_drag.cb.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!ov.isMonitor()) {
						/*
						 * 当ov的数据长度大于15万条时，提示用户先保存和清除
						 */
						TagTimeEntry tte = OpPerfBridge.getProfilerData(ov.getKey());
						if (ov.hasMonitorOnce && null != tte)
						{
							int size = 0;
							
							if (tte.hasChild())
							{
								size = tte.getChildren()[0].getRecordSize();
							}
							else
							{
								size = tte.getRecordSize();
							}
							
							if (size >= GTMemoryDaemonThread.singleLimit)
							{
								ToastUtil.ShowLongToast(context, "OutPara " + ov.getAlias()
										+ " has more than " + GTMemoryDaemonThread.singleLimit + " records."
										+ "You should save and clear records first.");
								
								return;
							}
						}

						ov.setMonitor(true);
						OpUIManager.list_change = true;

						// 如果是第一次启动监视，需要初始化出参对应的性能数据对象
						if (!ov.hasMonitorOnce) {
							OpPerfBridge.registMonitor(ov);
						}

					} else {
						ov.setMonitor(false);
						OpUIManager.list_change = true;
						OpPerfBridge.endProfier(ov);
					}
				}
			});

			if (position > disable_pos) {
				switch_drag.tv_key.setTextColor(Color.GRAY);
				switch_drag.tv_alias.setTextColor(Color.GRAY);
				switch_drag.tv_value.setTextColor(Color.GRAY);
				switch_drag.cb.setChecked(ov.isMonitor());
				
				switch_drag.cb.setEnabled(false);
				switch_drag.cb.setVisibility(View.GONE);
			} else {
				switch_drag.cb.setVisibility(View.VISIBLE);
			}

			convertView.setTag(switch_drag);
			break;
		}

		// 数据修改部分
		switch (type) {
		case TYPE_PROMPT_ITEM:
			if (((OutPara)list.get(1)).getKey().equals(ParamConst.DIVID_TITLE)) {
				PROMPT_ITEM_title.setText(ParamConst.PROMPT_INIT_TITLE);
			} else {
				PROMPT_ITEM_title.setText(ParamConst.PROMPT_TITLE);
			}
			break;
		case TYPE_DIVIDE_ITEM:
			if (position > 1) {
				DIVIDE_ITEM_top_border.setVisibility(View.VISIBLE);
			} else {
				DIVIDE_ITEM_top_border.setVisibility(View.GONE);
			}
			break;
		case TYPE_SWITCH_ITEM:
			switch_drag.tv_key.setText(key);
			switch_drag.tv_alias.setText(alias);
			switch_drag.tv_value.setText(value);
			if (his_count <= 0) {
				switch_drag.tv_his_data.setVisibility(View.GONE);
			} else if (his_count < 100) {

				switch_drag.tv_his_data.setText(Integer.toString(his_count));
				switch_drag.tv_his_data.setVisibility(View.VISIBLE);
			} else {
				switch_drag.tv_his_data.setText("99+");
				switch_drag.tv_his_data.setVisibility(View.VISIBLE);
			}
			if (position == (list.size() - 1)) {
				switch_drag.tv_listview_bottom_border
						.setVisibility(View.VISIBLE);
			} else {
				switch_drag.tv_listview_bottom_border.setVisibility(View.GONE);
			}

			if (position > disable_pos) {
				switch_drag.tv_key.setTextColor(Color.GRAY);
				switch_drag.tv_alias.setTextColor(Color.GRAY);
				switch_drag.tv_value.setTextColor(Color.GRAY);
			} else {
				switch_drag.tv_key.setTextColor(Color.WHITE);
				switch_drag.tv_alias.setTextColor(Color.WHITE);
				switch_drag.tv_value.setTextColor(Color.WHITE);

			}

			switch_drag.cb.setChecked(ov.isMonitor());
			break;
		case TYPE_DISABLE_ITEM:
			if (2 == position
				|| position > 1 && ((OutPara)list.get(position - 1)).getKey().equals(ParamConst.DIVID_TITLE)) {
				DISABLE_ITEM_top_border.setVisibility(View.GONE);
			} else {
				DISABLE_ITEM_top_border.setVisibility(View.VISIBLE);
			}
			break;
		}

		return convertView;
	}
}
