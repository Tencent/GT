/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.cmdparse;

import com.tencent.wstt.apt.data.PCInfo;

/**
* @Description 将Dalvik的hprof转化为JVM格式的 
* @date 2013年11月10日 下午5:50:19 
*
 */
public class HprofConv {
	public static void run(String inputFilePath, String outFilePath)
	{
		//TODO 这里没有对程序执行的返回进行判断，默认都成功
		CMDExecute.runCMD(PCInfo.hprofConv + " " + inputFilePath + " " + outFilePath);
	}

}
