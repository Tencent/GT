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

import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.client.OutParaManager;

public class OutParaManagerInternal {
	public OutParaManagerInternal()
	{
		temp = new ArrayList<OutPara>();
		userInterface = new OutParaManager();
	}
	
	private List<OutPara> temp;
	private OutParaManager userInterface;
	
	public OutParaManager getUserInterface()
	{
		return userInterface;
	}
	
	public void register(String ParaName, String alias, Object...extras){
		if(null == ParaName || null == alias){
			return;
		}
		if(alias.length() > 4){
			alias = alias.substring(0, 3) + ".";
		}
		
		OutPara Para = new OutPara();
		Para.setKey(ParaName);
		Para.setAlias(alias);
		Para.setRegistering(true);
		Para.setDisplayProperty(OutPara.DISPLAY_NORMAL);
		if (null != extras && extras.length > 0)
		{
			if (extras[0] instanceof Boolean)
			{
				boolean isGlobal = Boolean.TRUE.equals(extras[0]);
				Para.setGlobal(isGlobal);
			}
		}

		temp.add(Para);
	}
	
	public void defaultOutParasInAC(String... ParaNames){
		int len = ParaNames.length;
		for(int i = 0 ; i < temp.size() ; i++){
			if(OutPara.DISPLAY_DISABLE != temp.get(i).getDisplayProperty()){
				temp.get(i).setDisplayProperty(OutPara.DISPLAY_NORMAL);
			}
		}

		for(int i = 0 ; i < temp.size() ; i++){
			for(int j = 0 ; j < len ; j++){
				if(temp.get(i).getKey().equals(ParaNames[j])){
					temp.get(i).setDisplayProperty(OutPara.DISPLAY_AC);
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
		
		List<OutPara> s_temp = new ArrayList<OutPara>();
		for(int i = 0 ; i < len ;i++){
			s_temp.add(i, temp.get(pos[i]));
		}
		
		List<OutPara> t_temp = new ArrayList<OutPara>();
		t_temp.addAll(temp);
		List<OutPara> tl = new ArrayList<OutPara>();
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
	
	public void setOutParasDisable(){
		for(int i = 0 ; i < temp.size(); i++){
			temp.get(i).setDisplayProperty(OutPara.DISPLAY_DISABLE);
		}
	}
	
	public void setOutParasDisable(String... ParaNames){
		if(null != ParaNames){
			int len = ParaNames.length;
			
			for(int i = 0 ; i < temp.size(); i++){
				for(int j = 0 ; j < len ; j++){
					if(temp.get(i).getKey().equals(ParaNames[j])){
						temp.get(i).setDisplayProperty(OutPara.DISPLAY_DISABLE);
					}
				}
			}
		}
	}
	
//	public void setOutPara(String key, String value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, value, inlog);
//	}
//	
//	public void setOutPara(String key, int value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
//	
//	public void setOutPara(String key, boolean value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
//	
//	public void setOutPara(String key, long value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
//	
//	public void setOutPara(String key, short value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
//	
//	public void setOutPara(String key, double value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
//	
//	public void setOutPara(String key, float value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
//	
//	public void setOutPara(String key, char value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
//	
//	public void setOutPara(String key, byte value, boolean inlog){
//		BH.INSTANCE.resetOutPara(key, String.valueOf(value), inlog);
//	}
	
	public OutPara[] getAndClearTempParas()
	{
		OutPara[] result = temp.toArray(new OutPara[]{});
		temp.clear();
		return result;
	}
}
