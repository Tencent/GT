/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.smap;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: SMAPS中的一条数据
 * @author: Administrator  
 * @date: 2014年1月1日 上午10:48:45
 */
public class SMAPSSourceDataItem {
	public String[] contents = new String[COLUMUNS_NUM];
	
	public static final int NAME = 0;
	public static final int PSS = 1;
	public static final int SD = 2;
	public static final int PD = 3;
	public static final int COLUMUNS_NUM = 4;
	
	public SMAPSSourceDataItem(String name, String pss, String sd, String pd)
	{
		contents[NAME] = name;
		contents[PSS] = pss;
		contents[SD] = sd;
		contents[PD] = pd;
	}
	
	public SMAPSSourceDataItem(String name)
	{
		contents[NAME] = name;
		contents[PSS] = "0";
		contents[SD] = "0";
		contents[PD] = "0";
	}
	
	public SMAPSSourceDataItem()
	{
		contents[NAME] = "default";
		contents[PSS] = "0";
		contents[SD] = "0";
		contents[PD] = "0";
	}
	
	public void setValue(String pss, String sd, String pd)
	{
		contents[PSS] = pss ;
		contents[SD] = sd ;
		contents[PD] = pd ;
	}
	// 树型结构属性
	private SMAPSSourceDataItem parent = null;
	private List<SMAPSSourceDataItem> children = null; 
	

	public SMAPSSourceDataItem getParent() {
		return parent;
	}
	public void setParent(SMAPSSourceDataItem parent) {
		this.parent = parent;
	}
	
	
	public List<SMAPSSourceDataItem> getChildren() {
		return children;
	}
	public void addChild(SMAPSSourceDataItem child) {
		if (null == children)
		{
			children = new ArrayList<SMAPSSourceDataItem>();
		}
		
		children.add(child);
	}
	
	public boolean hasChildren()
	{
		return children != null && !children.isEmpty();
	}
	
	/**
	 * 根据名字返回对应的孩子
	 * @param name
	 * @return
	 */
	public SMAPSSourceDataItem getChildByName(String name)
	{
		if(children != null && !children.isEmpty())
		{
			for(int i = 0; i < children.size(); i++)
			{
				if(children.get(i).contents[NAME].equals(name))
				{
					return children.get(i);
				}
			}
		}
		return null;
	}
	
	
	/**
	 * 返回孩子的数量
	 * @return
	 */
	public int getChildrenNum()
	{
		if(children == null)
		{
			return 0;
		}
		else
		{
			return children.size();
		}
	}
}
