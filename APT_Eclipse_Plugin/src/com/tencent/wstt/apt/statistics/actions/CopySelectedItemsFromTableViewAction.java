/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.statistics.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


import com.tencent.wstt.apt.stubanalysis.data.SourceDataItem;


/**
* @Description 拷贝表格中选中的数据 
* @date 2013年11月10日 下午6:02:58 
*
 */
public class CopySelectedItemsFromTableViewAction extends Action {
	
	TableViewer viewer;
	private Clipboard clipboard;
	
	public CopySelectedItemsFromTableViewAction(TableViewer viewer)
	{
		this.viewer = viewer;
	}
	
	@Override
	public void run() {		
		StringBuffer sb = getContents();
		clipboard = new Clipboard(Display.getCurrent());
		Transfer[] transfers = new Transfer[] { TextTransfer
				.getInstance() };
		clipboard.setContents(new String[]{sb.toString()}, transfers);
	}
	
	/**
	 * 获取tableview控件的所有数据
	 * @return
	 */
	private StringBuffer getContents()
	{
		StringBuffer sb = new StringBuffer();
		Table table = viewer.getTable();
		TableItem[] selectedItems = table.getSelection(); 
		int itemCount = selectedItems.length;
		int columnCount = table.getColumnCount();
		
		for(int i = 0; i < itemCount; i++)
		{
			for(int j = 0; j < columnCount; j++)
			{
				sb.append(selectedItems[i].getText(j));
				sb.append(SourceDataItem.SPLIT);
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\r\n");
		}
		return sb;
	}
}
