/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;

import org.eclipse.swt.custom.CTabItem;

public class CTabFolderItemUtil {

	/**
	 * 返回名字等于tabItemTitle的CTabItem对象
	 * @param openedTabItems
	 * @param tabItemTitle
	 * @return
	 */
	public static CTabItem getTabItem(CTabItem []openedTabItems, String tabItemTitle)
	{
		for(int i = 0; i < openedTabItems.length; i++)
		{
			if(tabItemTitle.equalsIgnoreCase(openedTabItems[i].getText()))
			{
				return openedTabItems[i];
			}
		}
		return null;
	}
}
