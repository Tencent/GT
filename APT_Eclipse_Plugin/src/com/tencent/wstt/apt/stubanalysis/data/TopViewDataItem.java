/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.stubanalysis.data;


/**
* @Description APT插桩log统计页面展示的一条数据 
* @date 2013年11月10日 下午6:08:05 
*
 */
public class TopViewDataItem {
	//TODO是否详细视图中的结构体也要改成这种格式
	public static final int TAG = 0;
	public static final int COUNT = 1;
	public static final int MAXVALUE = 2;
	public static final int AVGVALUE = 3;
	public static final int TOTAL = 4;
	public String contents[] = new String[5];	
}
