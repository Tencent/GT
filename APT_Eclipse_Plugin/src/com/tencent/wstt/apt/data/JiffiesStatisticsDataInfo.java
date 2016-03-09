/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.data;


/**
 * Jiffies值统计模型
* @ClassName: JiffiesStatisticsDataInfo  
* @Description: TODO  
* @date 2013-5-23 上午9:34:28  
*
 */
public class JiffiesStatisticsDataInfo extends AbstractStatisticsDataInfo{
	public static final int INITVALUE_INDEX = 0;
	public static final int CURVALUE_INDEX = 1;
	public static final int DVALUE_INDEX = 2;
	public static final int VALUE_NUMBER = 3;
	
	public JiffiesStatisticsDataInfo()
	{
		valueNumber = VALUE_NUMBER;
		contents = new long[VALUE_NUMBER];
	}

}
