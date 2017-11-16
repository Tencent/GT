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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import android.util.SparseArray;

import com.tencent.wstt.gt.InPara;

public class DefaultInParaManager implements IInParaManager {
	// 入参缓存容器
	protected LinkedHashMap<String, InPara> inParaMap = new LinkedHashMap<String, InPara>();
	// 保存UI对入参的显示序列
	protected SparseArray<InPara> sortedInParas = new SparseArray<InPara>();
	// 按sortedInParas顺序保存的列表，弥补SparseArray无法getAll的缺陷
	protected LinkedList<InPara> sortedInParaList = new LinkedList<InPara>();
	// 所属客户端
	protected Client client;
	
	public DefaultInParaManager(Client client)
	{
		this.client = client;
	}

	@Override
	public void register(String paraName, String alias, String defaultValue,
			String... optionalValues) {
		if (alias.length() > 4) {
			alias = alias.substring(0, 3) + ".";
		}

		InPara para = new InPara();
		para.setKey(paraName);
		para.setAlias(alias);
		para.setClient(client.getKey());
		List<String> vals = new ArrayList<String>();
		vals.add(defaultValue);
		for (String ov : optionalValues) {
			vals.add(ov);
		}
		para.setValues(vals);
		para.setDisplayProperty(InPara.DISPLAY_NORMAL);

		synchronized (inParaMap) {
			if (checkInParaAvilable(para)) {
				inParaMap.put(paraName, para);
				sortedInParas.put(sortedInParas.size(), para);
				sortedInParaList.add(para);
			}
		}
	}
	
	/**
	 * 由客户端首次注册，直接入缓存
	 * @param para
	 */
	@Override
	public void register(InPara para)
	{
		/*
		 * 只有当悬浮框出参为空时，才会更新悬浮框，这样有已关注参数时，新来的AC参数不会打扰用户
		 * 此时后来的参数应该主动设置为非AC状态，否则在将AC参数都拖下去后，后来的AC参数会立即
		 * 增补到悬浮框上，而参数列表不同步，会比较怪异
		 */
		synchronized (inParaMap) {
			if (para != null && null != para.getKey() && !contains(para.getKey()))
			{
				para.setClient(client.getKey());
				inParaMap.put(para.getKey(), para);
				sortedInParas.put(sortedInParas.size(), para);
				sortedInParaList.add(para);
				
				// 悬浮窗需要立即反应，所以如果是AC参数立即更新UI列表
				IpUIManager.addItemToAC(para);
			}
		}
	}
	
	@Override
	public void removeOutPara(String paraName){
		synchronized (inParaMap) {
			InPara para = inParaMap.remove(paraName);
			int position = sortedInParas.indexOfValue(para);
			if (position > 0)
			{
				sortedInParas.remove(sortedInParas.keyAt(position));
				sortedInParaList.remove(para);
			}
			
			if( null != IpUIManager.list_ip && IpUIManager.list_ip.contains(para)){
				IpUIManager.list_ip.remove(para);
			}
		}
	}

	private boolean checkInParaAvilable(InPara inPara) {
		String key = inPara.getKey();
		boolean result = true;

		if (null != inParaMap.get(key)) {
			result = false;
		}

		return result;
	}

	private boolean contains(String paraName)
	{
		return getInPara(paraName) == null ? false : true;
	}

	@Override
	public void clear()
	{
		synchronized (inParaMap) {
			List<InPara> tmpList = getAll();
			inParaMap.clear();
			sortedInParas.clear();
			sortedInParaList.clear();

			for (InPara para : tmpList)
			{
				IpUIManager.list_ip.remove(para);
			}
		}
	}

	@Override
	public boolean isEmpty()
	{
		synchronized (inParaMap) {
			return inParaMap.isEmpty();
		}
	}

	@Override
	public List<InPara> getAll()
	{
		List<InPara> result = new ArrayList<InPara>();
		result.addAll(sortedInParaList); // 考虑下直接返回sortedInParaList有无风险
		return result;
	}

	@Override
	public InPara getInPara(int positon)
	{
		return sortedInParas.get(positon);
	}
	
	public InPara getInPara(String paraName)
	{
		return inParaMap.get(paraName);
	}

	@Override
	public String getInPara(String paraName, String origVal) {
		InPara iv = inParaMap.get(paraName);
		String value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			value = vals.get(0);
		}

		return value;
	}

	@Override
	public boolean getInPara(String paraName, boolean origVal) {
		InPara iv = inParaMap.get(paraName);
		boolean value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			String val = vals.get(0);
			if (matchInParaType(val, "boolean")) {
				value = Boolean.parseBoolean(val);
			}
		}

		return value;
	}

	@Override
	public int getInPara(String paraName, int origVal) {
		InPara iv = inParaMap.get(paraName);
		int value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			String val = vals.get(0);
			if (matchInParaType(val, "int")) {
				value = Integer.parseInt(val);
			}
		}

		return value;
	}

	@Override
	public float getInPara(String paraName, float origVal) {
		InPara iv = inParaMap.get(paraName);
		float value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			String val = vals.get(0);
			if (matchInParaType(val, "float")) {
				value = Float.parseFloat(val);
			}
		}

		return value;
	}

	@Override
	public double getInPara(String paraName, double origVal) {
		InPara iv = inParaMap.get(paraName);
		double value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			String val = vals.get(0);
			if (matchInParaType(val, "double")) {
				value = Double.parseDouble(val);
			}
		}

		return value;
	}

	@Override
	public short getInPara(String paraName, short origVal) {
		InPara iv = inParaMap.get(paraName);
		short value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			String val = vals.get(0);
			if (matchInParaType(val, "short")) {
				value = Short.parseShort(val);
			}
		}

		return value;
	}

	@Override
	public byte getInPara(String paraName, byte origVal) {
		InPara iv = inParaMap.get(paraName);
		byte value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			String val = vals.get(0);
			if (matchInParaType(val, "byte")) {
				value = Byte.parseByte(val);
			}
		}

		return value;
	}

	@Override
	public long getInPara(String paraName, long origVal) {
		InPara iv = inParaMap.get(paraName);
		long value = origVal;
		if (null != iv) {
			List<String> vals = iv.getValues();
			String val = vals.get(0);
			if (matchInParaType(val, "long")) {
				value = Long.parseLong(val);
			}
		}

		return value;
	}

	private static boolean matchInParaType(String str, String type) {
		boolean result = false;
		if (type.equals("int")) {
			result = determineInParaType(str, 0);
		}
		if (type.equals("boolean")) {
			result = determineInParaType(str, 1);
		}
		if (type.equals("long")) {
			result = determineInParaType(str, 2);
		}
		if (type.equals("double")) {
			result = determineInParaType(str, 3);
		}
		if (type.equals("float")) {
			result = determineInParaType(str, 4);
		}
		if (type.equals("short")) {
			result = determineInParaType(str, 5);
		}
		if (type.equals("byte")) {
			result = determineInParaType(str, 6);
		}
		return result;
	}

	private static boolean determineInParaType(String str, int type) {
		boolean result = true;

		switch (type) {
		case 0:
			char[] cs = str.toCharArray();
			for (int i = 0; i < cs.length; i++) {
				int ascii = (int) cs[i];
				if (ascii < 48 || ascii > 57) {
					result = false;
					break;
				}
			}
			break;
		case 1:
			if (!str.equals("true") && !str.equals("false")) {
				result = false;
			}
			break;
		case 2:
			char[] cs_long = str.toCharArray();
			for (int i = 0; i < cs_long.length; i++) {
				int ascii = (int) cs_long[i];
				if (ascii < 48 || ascii > 57) {
					result = false;
					break;
				}
			}
			break;
		case 3:
			char[] cs_double = str.toCharArray();
			for (int i = 0; i < cs_double.length; i++) {
				int ascii = (int) cs_double[i];
				if (ascii < 48 || ascii > 57) {
					result = false;
					break;
				}
			}
			break;
		case 4:
			char[] cs_float = str.toCharArray();
			for (int i = 0; i < cs_float.length; i++) {
				int ascii = (int) cs_float[i];
				if (ascii < 48 || ascii > 57) {
					result = false;
					break;
				}
			}
			break;
		case 5:
			char[] cs_short = str.toCharArray();
			for (int i = 0; i < cs_short.length; i++) {
				int ascii = (int) cs_short[i];
				if (ascii < 48 || ascii > 57) {
					result = false;
					break;
				}
			}
			break;
		case 6:
			char[] cs_byte = str.toCharArray();
			for (int i = 0; i < cs_byte.length; i++) {
				int ascii = (int) cs_byte[i];
				if (ascii < 48 || ascii > 57) {
					result = false;
					break;
				}
			}
			break;
		}

		return result;
	}
}
