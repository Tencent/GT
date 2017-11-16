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
import android.widget.TextView;

import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.manager.IpUIManager;
import com.tencent.wstt.gt.manager.ParamConst;

public class GTParamInEditListAdapter extends GTParamListBaseAdapter
{
	public GTParamInEditListAdapter(Context context, List<InPara> list)
	{
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ParamConst.ViewHolderDrag switch_drag = null;

		InPara o = (InPara)list.get(position);
		String key = o.getKey();

		final String alias = o.getAlias();
		final int type = getItemViewType(position);

		/*
		 * 根据每行UI的类型进行UI模型准备与数据准备
		 */
		switch (type) {
		case TYPE_PROMPT_ITEM:
			convertView = PROMPT_ITEM_convertView;
			if (((InPara)list.get(1)).getKey().equals(ParamConst.DIVID_TITLE))
			{
				PROMPT_ITEM_title.setText(ParamConst.PROMPT_INIT_TITLE);
			}
			else
			{
				PROMPT_ITEM_title.setText(ParamConst.PROMPT_TITLE);
			}
			break;
		case TYPE_DIVIDE_ITEM:
			convertView = DIVIDE_ITEM_convertView;
			if (position == 1)
			{
				DIVIDE_ITEM_top_border.setVisibility(View.GONE);
			}
			else
			{
				DIVIDE_ITEM_top_border.setVisibility(View.VISIBLE);
			}
			break;
		case TYPE_DISABLE_ITEM:
			convertView = DISABLE_ITEM_convertView;
			if (2 == position || position > 1 && ((InPara)list.get(position - 1)).getKey().equals(ParamConst.DIVID_TITLE))
			{
				DISABLE_ITEM_top_border.setVisibility(View.GONE);
			}
			else
			{
				DISABLE_ITEM_top_border.setVisibility(View.VISIBLE);
			}
			break;
		case TYPE_SWITCH_ITEM:
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.gt_edit_list_item, null);
				switch_drag = new ParamConst.ViewHolderDrag();
				switch_drag.tv_key = (TextView) convertView
						.findViewById(R.id.draglist_key);
				switch_drag.tv_alias = (TextView) convertView
						.findViewById(R.id.draglist_shotkey);
				switch_drag.tv_listview_bottom_border = (TextView) convertView
						.findViewById(R.id.listrow_bottom_border);
				convertView.setTag(switch_drag);
			}
			else
			{
				switch_drag = (ParamConst.ViewHolderDrag)convertView.getTag();
			}
			
			switch_drag.tv_key.setText(key);
			switch_drag.tv_alias.setText(alias);

			if (position == (list.size() - 1))
			{
				switch_drag.tv_listview_bottom_border.setVisibility(View.VISIBLE);
			}
			else
			{
				switch_drag.tv_listview_bottom_border.setVisibility(View.GONE);
			}

			if (list.size() == 2)
			{
				switch_drag.tv_listview_bottom_border.setVisibility(View.GONE);
			}

			int disable_titile_pos = IpUIManager.getInListDisableTitlePosition();

			if (position > disable_titile_pos)
			{
				switch_drag.tv_key.setTextColor(Color.GRAY);
				switch_drag.tv_alias.setTextColor(Color.GRAY);
			}
			else
			{
				switch_drag.tv_key.setTextColor(Color.WHITE);
				switch_drag.tv_alias.setTextColor(Color.WHITE);
			}
			break;
		}

		return convertView;
	}
}
