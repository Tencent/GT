/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.util.Date;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.file.WriteFileUtil;

/**
* @Description 写文件观察者，定时保存数据 
* @date 2013年11月10日 下午5:28:40 
*
 */
public class FileObserver extends Observer {

	protected String tag;
	
	public FileObserver(String tag)
	{
		this.tag = tag;
	}
	@Override
	public void update(Date time, Number[] datas) {
		
		String strTime = Constant.SIMPLE_DATE_FORMAT_MILLISECOND.format(time);
		String str = strTime + Constant.APTLOG_FILECONTENT_SPLIT;
		for(int i = 0; i < datas.length; i++)
		{
			str += datas[i] + Constant.APTLOG_FILECONTENT_SPLIT;
		}
		str += Constant.APTLOG_FILECONTENT_NEWLINE;
		WriteFileUtil.getInstance().append(tag, str);
	}

}
