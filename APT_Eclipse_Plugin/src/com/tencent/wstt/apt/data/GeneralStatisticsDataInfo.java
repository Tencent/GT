/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.data;

/**
 * CPU、内存等值统计模型
* @ClassName: StatisticsDataInfo  
* @Description: TODO  
* @date 2013-5-16 下午3:00:45  
*
 */
public class GeneralStatisticsDataInfo extends AbstractStatisticsDataInfo{
	
	public static final int CURVALUE_INDEX = 0;//当前值
	public static final int DVALUE_INDEX = 1;//增量值
	public static final int AVGVALUE_INDEX = 2;//平均值
	public static final int MAXVALUE_INDEX = 3;//最大值
	public static final int MINVALUE_INDEX = 4;//最小值
	public static final int FIRST_INDEX = 5;//第一个值
	public static final int SUM_INDEX = 6;//和

	public static final int VALUE_NUMBER = 7;
		
	
	public GeneralStatisticsDataInfo()
	{
		valueNumber = VALUE_NUMBER;
		contents = new long[VALUE_NUMBER];
	}
	
}
