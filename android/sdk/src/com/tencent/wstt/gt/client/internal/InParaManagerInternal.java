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
package com.tencent.wstt.gt.client.internal;

import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.client.InParaManager;

public class InParaManagerInternal {
	
	private List<InPara> temp;
	private InParaManager userInterface;
	
	public InParaManagerInternal()
	{
		temp = new ArrayList<InPara>();
		userInterface = new InParaManager();
	}
	
	public InParaManager getUserInterface()
	{
		return userInterface;
	}
	
	public void register(String ParaName, String alias, String defaultValue, String... optionalValues){
		if(null == ParaName || null == alias || null == defaultValue || null == optionalValues){
			return;
		}
		
		if(alias.length() > 4){
			alias = alias.substring(0, 3) + ".";
		}
		
		InPara Para = new InPara();
		Para.setKey(ParaName);
		Para.setAlias(alias);
		Para.setDisplayProperty(InPara.DISPLAY_NORMAL);
		List<String> vals = new ArrayList<String>();
		vals.add(defaultValue);
		for(int i = 0 ; i < optionalValues.length ; i ++){
			vals.add(optionalValues[i]);		
		}
		//在最后加入disable，选择disable，则入参使用被测工程代码中的原值
		if (! vals.contains("<null>"))
		{
			vals.add("<null>");
		}
		Para.setValues(vals);
		Para.setRegistering(true);
		temp.add(Para);
	}
	
	public void defaultInParasInAC(String... ParaNames){
		int len = ParaNames.length;
		for(int i = 0 ; i < temp.size() ; i++){
			if(InPara.DISPLAY_DISABLE != temp.get(i).getDisplayProperty()){
				temp.get(i).setDisplayProperty(InPara.DISPLAY_NORMAL);
			}
		}
		
		for(int i = 0 ; i < temp.size() ; i++){
			for(int j = 0 ; j < len ; j++){
				if(temp.get(i).getKey().equals(ParaNames[j])){
					temp.get(i).setDisplayProperty(InPara.DISPLAY_AC);
				}
			}
		}
		
		int[] pos = new int[temp.size()];
		for(int i = 0 ; i < len ; i++){
			for(int j = 0 ; j < temp.size() ; j++){
				if(ParaNames[i].equals(temp.get(j).getKey())){
					pos[i] = j;
				}
			}
		}
		
		List<InPara> s_temp = new ArrayList<InPara>();
		for(int i = 0 ; i < len ;i++){
			s_temp.add(i, temp.get(pos[i]));
		}
		
		List<InPara> t_temp = new ArrayList<InPara>();
		t_temp.addAll(temp);
		List<InPara> tl = new ArrayList<InPara>();
		for(int i = 0 ; i < len ;i++){
			tl.add(t_temp.get(pos[i]));
		}
		for(int i = 0 ; i < len ;i++){
			t_temp.remove(tl.get(i));
		}
		
		temp.clear();
		temp.addAll(s_temp);
		temp.addAll(t_temp);
		
	}
	
	public void setInParasDisable(){
		for(int i = 0 ; i < temp.size(); i++){
			temp.get(i).setDisplayProperty(InPara.DISPLAY_DISABLE);
		}
	}
	
	public void setInParasDisable(String... ParaNames){
		if(null != ParaNames){
			int len = ParaNames.length;
			
			for(int i = 0 ; i < temp.size(); i++){
				for(int j = 0 ; j < len ; j++){
					if(temp.get(i).getKey().equals(ParaNames[j])){
						temp.get(i).setDisplayProperty(InPara.DISPLAY_DISABLE);
					}
				}
			}
		}
	}

	public InPara[] getAndClearTempParas()
	{
		InPara[] result = temp.toArray(new InPara[]{});
		temp.clear();
		return result;
	}
}
