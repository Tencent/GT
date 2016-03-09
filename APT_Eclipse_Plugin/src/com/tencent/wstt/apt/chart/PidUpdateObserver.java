/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;

import com.tencent.wstt.apt.data.PkgInfo;
import com.tencent.wstt.apt.data.TestSence;

/**
* @Description 进程pid更改观察者，对于测试进程启动过程或者测试过程中重启进程，pid会发生变化
* 用来更新pid 
* @date 2013年11月10日 下午5:31:03 
*
 */
public class PidUpdateObserver extends Observer {

	private String [] lastPids;
	private TableViewer viewer;
	public PidUpdateObserver(TableViewer viewer, String []pids)
	{
		lastPids = new String[pids.length];
		System.arraycopy(pids, 0, lastPids, 0, pids.length);
		this.viewer = viewer;
	}
	
	@Override
	public void update(Date time, Number[] datas) {
		//获取当前PID
		List<PkgInfo> pkgInfos = TestSence.getInstance().pkgInfos;
		for(int i = 0; i < pkgInfos.size(); i++)
		{
			final int index = i;
			if(!pkgInfos.get(i).contents[PkgInfo.PID_INDEX].equals(lastPids[i]))
			{
				//更新UI
				lastPids[i] = pkgInfos.get(i).contents[PkgInfo.PID_INDEX];
				if(!viewer.getTable().isDisposed())
				{
					viewer.getTable().getDisplay().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							if(!viewer.getTable().isDisposed())
							{
								viewer.getTable().getItem(index).setText(1, lastPids[index]);
							}							
						}
					});
				}	
			}
		}
		
	}

}
