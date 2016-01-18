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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tencent.wstt.gt.OutPara;

/**
 * 不考虑UI，纯粹的完整出参管理接口
 */
public abstract class IOutParaManager {

	protected Client client;
	protected Map<String, OutPara> outParaMap =
			Collections.synchronizedMap(new LinkedHashMap<String, OutPara>());

	public IOutParaManager(Client client)
	{
		this.client = client;
	}
	
	/**
	 * 由客户端首次注册，直接入缓存
	 * 
	 * @param para
	 */
	abstract public void register(OutPara para);

	public void register(String paraName, String alias) {
		if(alias.length() > 4){
			alias = alias.substring(0, 3) + ".";
		}
		
		OutPara para = new OutPara();
		para.setKey(paraName);
		para.setAlias(alias);
		para.setDisplayProperty(OutPara.DISPLAY_NORMAL);
		para.setClient(client.getKey());

		if(!contains(paraName)){
			outParaMap.put(paraName, para);
		}
	}

	public void removeOutPara(String paraName) {
		OutPara para = outParaMap.remove(paraName);
		OpPerfManager.getInstance().remove(paraName);
		OpUIManager.list_op.remove(para);
	}

	public OutPara getOutPara(String paraName) {
		return outParaMap.get(paraName);
	}

	public void setOutparaMonitor(String str, boolean flag) {
		OutPara ov = getOutPara(str);
		if (ov != null) {
			ov.setMonitor(flag);
		}
	}

	public boolean contains(String paraName) {
		return getOutPara(paraName) == null ? false : true;
	}

	public void clear() {
		List<OutPara> opList = getAll();

		// 需要循环一个个remove
		for (OutPara op : opList)
		{
			removeOutPara(op.getKey());
		}
	}

	public boolean isEmpty() {
		return outParaMap.isEmpty();
	}

	public List<OutPara> getAll() {
		List<OutPara> result = new ArrayList<OutPara>();
		result.addAll(outParaMap.values());
		return result;
	}
}
