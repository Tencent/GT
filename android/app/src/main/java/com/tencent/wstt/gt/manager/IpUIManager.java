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
import com.tencent.wstt.gt.InPara;

public class IpUIManager {

	public static List<InPara> list_ip =
			Collections.synchronizedList(new ArrayList<InPara>());

	public static boolean isEmpty()
	{
		for (Client client : ClientManager.getInstance().getAllClient())
		{
			if (! client.isInParaEmpty())
			{
				return false;
			}
		}
		return true;
	}

	public static List<InPara> getAll()
	{
		List<InPara> result = new ArrayList<InPara>();
		for (Client client : ClientManager.getInstance().getAllClient())
		{
			result.addAll(client.getAllInParas());
		}
		return result;
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

	public static void addItemToAC(InPara ip)
	{
		if (list_ip.contains(ip) || ip.getDisplayProperty() != AidlEntry.DISPLAY_AC)
		{
			return;
		}
		
		int pos_AC = getInListDividePosition(); // AC

		/*
		 * list_ip为空，压根就没有线，这里先把三条基本线加上逻辑上比较好判断
		 * 其实加一条普通关注分界线为锚点即可
		 */
		if (pos_AC == 0)
		{
			// 悬浮框分界线
			InPara iv_ac = new InPara();
			iv_ac.setKey(ParamConst.PROMPT_INIT_TITLE);
			iv_ac.setDisplayProperty(InPara.DISPLAY_TITLE);
			list_ip.add(iv_ac);
			
			// 加普通关注分界线
			InPara iv_normalDivid = new InPara();
			iv_normalDivid.setKey(ParamConst.DIVID_TITLE);
			iv_normalDivid.setDisplayProperty(InPara.DISPLAY_TITLE);
			list_ip.add(iv_normalDivid);
			
			// 加disable入参分界线
			InPara iv_disableDivid = new InPara();
			iv_disableDivid.setKey(ParamConst.PROMPT_DISABLE_TITLE);
			iv_disableDivid.setDisplayProperty(InPara.DISPLAY_TITLE);
			IpUIManager.list_ip.add(iv_disableDivid);
			
			pos_AC = 1;
		}
		if (pos_AC < 4)
		{
			list_ip.add(pos_AC, ip);
		}
		else
		{
			ip.setDisplayProperty(AidlEntry.DISPLAY_NORMAL);
		}
	}

	public static int getInListDividePosition() {
		int pos = 0;
		for (int i = 0; i < list_ip.size(); i++) {
			if (list_ip.get(i).getKey().equals(ParamConst.DIVID_TITLE)) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	public static List<InPara> getACInputParams() {
		List<InPara> show_iv = new ArrayList<InPara>();
		for (int i = 1; i < list_ip.size(); i++) {
			
			InPara ip = list_ip.get(i);
			if (ip.getKey().equals(ParamConst.DIVID_TITLE)) {
				break;
			}
			show_iv.add(list_ip.get(i));
		}

		return show_iv;
	}
}
