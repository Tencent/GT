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
package com.tencent.wstt.gt.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tencent.wstt.gt.AidlEntry;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.activity.GTParamOutListAdapter;

public class OpUIManager {

	public static int delaytime = 1000;

	// 因为是UI列表，所以其本身需要是同步的
	public static List<OutPara> list_op =
			Collections.synchronizedList(new ArrayList<OutPara>());

	/**
	 *  标记gw是否开始采集，true 为运行
	 */
	public static boolean gw_running = false;

	/**
	 *  如果列表被滑动，则置为true
	 */
	public static boolean refresh_op_drag_conflict_flag;

	/**
	 *  标识outlist是否被修改过，点击checkbox及保存删除都会置状态为true
	 */
	public static boolean list_change = false;

	public static void setAUTitemTop(GTParamOutListAdapter outparam_adapter) {

		int pos_Divide = getOutListDividePosition(); // Optional
		Client autClient = ClientManager.getInstance().getAUTClient();
		if (null == autClient)
		{
			return;
		}
		for (OutPara op : autClient.getAllOutParas()) {
			if (AidlEntry.DISPLAY_NORMAL == op.getDisplayProperty()) {
				int listpos = getPosition(op.getKey());
				setOutListItemToTop(outparam_adapter, listpos, pos_Divide + 1);
			}
		}
	}

	public static void addItemToAC(OutPara op) {
		if (list_op.contains(op) || op.getDisplayProperty() != AidlEntry.DISPLAY_AC)
		{
			return;
		}
		
		int pos_AC = getOutListDividePosition(); // NormalTitle
		if (pos_AC < 4)
		{
			list_op.add(pos_AC, op);
		}
		else
		{
			op.setDisplayProperty(AidlEntry.DISPLAY_NORMAL);
		}
	}

	public static void setItemToNormal(OutPara op) {
		if (op.getDisplayProperty() != AidlEntry.DISPLAY_NORMAL)
		{
			return;
		}
		list_op.remove(op);
		int pos_Disable = getOutListDisableTitlePosition(); // DisableTitle
		list_op.add(pos_Disable, op);
	}

	public static int getOutListDividePosition() {
		int pos = 0;
		for (int i = 0; i < OpUIManager.list_op.size(); i++) {
			if (OpUIManager.list_op.get(i).getKey().equals(ParamConst.DIVID_TITLE)) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	private static void setOutListItemToTop(
			GTParamOutListAdapter outparam_adapter, int from, int to) {

		int direction = -1;
		int loop_start = from;
		int loop_end = to;

		if (from < to) {
			direction = 1;
		}

		OutPara ov_target = OpUIManager.list_op.get(from);

		for (int i = loop_start; i != loop_end; i = i + direction) {
			OpUIManager.list_op.set(i, OpUIManager.list_op.get(i + direction));
		}

		OpUIManager.list_op.set(to, ov_target);
		if (null != outparam_adapter)
		{
			outparam_adapter.notifyDataSetChanged();
		}
	}

	private static int getPosition(String str) {
		int pos = 0;
		for (int p = 0; p < OpUIManager.list_op.size(); p++) {
			if (OpUIManager.list_op.get(p).getKey() == str) {
				pos = p;
			}
		}
		return pos;
	}

	/**
	 * 默认必备输出信息：在GT启动时完成注册
	 * 如果需要新增默认输出，请在GTApp中进行统一添加
	 * 此方法全局执行一次即可
	 */
	public static void initDefaultOutputParamList()
	{
		list_op.clear();
		List<OutPara> outParaList = getAll();

		OutPara ov_title = new OutPara();
		ov_title.setKey(ParamConst.PROMPT_TITLE);
		ov_title.setDisplayProperty(OutPara.DISPLAY_TITLE);
		list_op.add(ov_title);

		// 添加默认显示在AC中的出参
		for (OutPara ov : outParaList) {
			if (OutPara.DISPLAY_AC == ov.getDisplayProperty()) {
				list_op.add(ov);
			}
		}

		// 添加关注线
		OutPara ov_title_divid = new OutPara();
		ov_title_divid.setKey(ParamConst.DIVID_TITLE);
		ov_title_divid.setDisplayProperty(OutPara.DISPLAY_TITLE);
		list_op.add(ov_title_divid);

		// 普通关注出参
		for (OutPara ov : outParaList) {
			if (ov.getDisplayProperty() == AidlEntry.DISPLAY_NORMAL) {
				list_op.add(ov);
			}
		}

		// 不关注线
		OutPara op_title_disable = new OutPara();
		op_title_disable.setKey(ParamConst.PROMPT_DISABLE_TITLE);
		op_title_disable.setDisplayProperty(OutPara.DISPLAY_TITLE);
		list_op.add(op_title_disable);

		// 不关注的出参
		for (OutPara ov : outParaList) {
			if (!list_op.contains(ov)
					&& AidlEntry.DISPLAY_DISABLE == ov.getDisplayProperty()) {
				list_op.add(ov);
			}
		}
	}

	/**
	 * 通过此方法更新UI要展示的出参
	 */
	public static void refreshUIOpList()
	{
		List<OutPara> outParaList = getAll();

		// 尚未挂到UI上的非悬浮窗参数、非不关注参数，一律挂到已关注参数的最上面
		for (OutPara ov : outParaList) {
			if (!list_op.contains(ov) && ov.getDisplayProperty() == AidlEntry.DISPLAY_NORMAL)
			{
				if (-1 != OpUIManager.getOutListDisableTitlePosition()) {
					list_op.add(OpUIManager.getOutListDisableTitlePosition(), ov);
				} else {
					list_op.add(ov);
				}
	
			}
		}
	
		// 添加默认显示在AC中的出参
		// 统计list_op中可以在悬浮窗中默认显示的出参的空位
		int pos_divid_line = getOutListDividePosition();
		// >1 认为默认显示中有GT默认输出参数
		if (pos_divid_line > 1) {
			// 将AUT中的参数直接加载列表后面
			for (OutPara ov : outParaList) {
				if (-1 != getOutListDisableTitlePosition()
						&& OutPara.DISPLAY_AC == ov.getDisplayProperty()
						&& !list_op.contains(ov)) {
					list_op.add(getOutListDisableTitlePosition(), ov);
				} else if (AidlEntry.DISPLAY_NORMAL == ov.getDisplayProperty()
						&& !list_op.contains(ov)) {
					list_op.add(ov);
				}
			}
		}
		// =1认为默认显示悬浮窗中没有GT默认输出参数
		else {
			int pos = 1;
			for (OutPara ov : outParaList) {
				if (pos < 4 && OutPara.DISPLAY_AC == ov.getDisplayProperty()
						&& !list_op.contains(ov)) {
					list_op.add(pos, ov);
					pos++;
				} else {
					if (-1 != getOutListDisableTitlePosition()
							&& AidlEntry.DISPLAY_NORMAL == ov
									.getDisplayProperty()
							&& !list_op.contains(ov)) {
						list_op.add(getOutListDisableTitlePosition(), ov);
					} else if (AidlEntry.DISPLAY_NORMAL == ov
							.getDisplayProperty()
							&& !list_op.contains(ov)) {
						list_op.add(ov);
					}
				}
			}
		}

		for (OutPara ov : outParaList) {
			if (!list_op.contains(ov)
					&& AidlEntry.DISPLAY_DISABLE == ov.getDisplayProperty()) {
				list_op.add(ov);
			}
		}
	}

	public static int getOutListAcDividePosition() {
		int pos = 0;
		for (int i = 0; i < list_op.size(); i++) {
			if (list_op.get(i).getKey().equals(ParamConst.PROMPT_INIT_TITLE)) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	public static int getOutListDisableTitlePosition() {
		int pos = -1;
		for (int i = 0; i < list_op.size(); i++) {
			if (list_op.get(i).getKey().equals(ParamConst.PROMPT_DISABLE_TITLE)) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	public static void refreshOutputParam() {
	
		for (OutPara op : list_op) {
			if (op.getDisplayProperty() < AidlEntry.DISPLAY_DISABLE) {
				String value = op.getValue();
				op.setFreezValue(value);
				op.setValue(value);
			}
		}
	}

	public static List<OutPara> getACOutputParams() {
		List<OutPara> show_ov = new ArrayList<OutPara>();
		int len = OpUIManager.list_op.size();
		for (int i = 1; i < len; i++)
		{
			OutPara op = OpUIManager.list_op.get(i);
			if (op.getKey().equals(ParamConst.DIVID_TITLE)) {
				break;
			}
			show_ov.add(op);
		}

		return show_ov;
	}

	public static List<OutPara> getAll()
	{
		List<OutPara> result = new ArrayList<OutPara>();
		for (Client client : ClientManager.getInstance().getAllClient())
		{
			result.addAll(client.getAllOutParas());
		}
		return result;
	}
}
