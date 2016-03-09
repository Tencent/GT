/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.data;

import java.util.TimerTask;

/**
* @ClassName: TestTask  
* @Description: 描述测试任务的结构体  
* @date 2013-4-13 上午11:44:07  
*
 */
public class TestTask {
	public TimerTask task;
	public long period;
	public TestTask(TimerTask task, long period)
	{
		this.task = task;
		this.period = period;
	}
}
