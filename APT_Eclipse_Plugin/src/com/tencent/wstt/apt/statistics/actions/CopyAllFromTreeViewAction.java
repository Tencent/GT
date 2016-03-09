/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.statistics.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.tencent.wstt.apt.stubanalysis.data.SourceDataItem;

/**
* @Description 从树形表格中拷贝数据 
* @date 2013年11月10日 下午6:02:38 
*
 */
public class CopyAllFromTreeViewAction extends Action {
	private TreeViewer viewer;
	private Clipboard clipboard;
	
	public CopyAllFromTreeViewAction(TreeViewer viewer)
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
	 * 获取treeview控件的所有数据
	 * @return
	 */
	private StringBuffer getContents()
	{
		StringBuffer sb = new StringBuffer();
		int columnCount = viewer.getTree().getColumnCount();
		// 表头
		for (TreeColumn column : viewer.getTree().getColumns()) {
			sb.append(column.getText());
			sb.append(SourceDataItem.SPLIT);
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\r\n");
		
		for (TreeItem tableItem : viewer.getTree().getItems()) {
			parseTree(tableItem, columnCount, sb);
		}
		return sb;
	}
	
	/*
	 * 递归解析树
	 * @param parent
	 * @param sb
	 */
	private void parseTree(TreeItem curItem, int columnCount, StringBuffer sb)
	{
		if (null == curItem.getData())
		{
			return;
		}
		
		// 打层次点"."
		int deep = calcDeep(curItem);
		for (int i = 0; i < deep; i++)
		{
			sb.append('.');
		}
		
		// 内容
		for(int i = 0; i < columnCount; i++)
		{
			sb.append(curItem.getText(i));
			sb.append(SourceDataItem.SPLIT);
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\r\n");

		for (TreeItem item : curItem.getItems())
		{
			parseTree(item, columnCount, sb);
		}
	}
	
	
	/**
	 * 判断当前条目的深度
	 * @param curItem
	 * @return
	 */
	private int calcDeep(TreeItem curItem)
	{
		int result = 0;
		TreeItem tmp = curItem;
		while(tmp.getParentItem() != null)
		{
			result++;
			tmp = tmp.getParentItem();
		}
		return result;
	}
}
