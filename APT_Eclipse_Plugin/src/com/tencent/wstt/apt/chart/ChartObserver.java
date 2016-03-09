/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.util.Date;


/**
* @Description 曲线监听者，用来更新CPU或者内存曲线
* @date 2013年11月10日 下午5:26:53 
*
 */
public class ChartObserver extends Observer {

	protected AbstractRealTimeLineChart chart;
	public ChartObserver(AbstractRealTimeLineChart chart)
	{
		this.chart = chart;
	}

	@Override
	public void update(Date time, Number[] datas) {
		chart.update(time, datas);
	}

}
