/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 * 
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 * 
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tencent.wstt.gt;
import com.tencent.wstt.gt.AidlTask;
import com.tencent.wstt.gt.PerfDigitalEntry;
import com.tencent.wstt.gt.PerfStringEntry;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.BooleanEntry;

interface IService
{
	// 连接
	int checkIsCanConnect(String cur_pkgName, int versionId);
	void initConnectGT(String pkgName, int pid);
	boolean disconnectGT(String cur_pkgName);
	
	// 出参操作
	oneway void registerOutPara(in OutPara outPara); // 注册
	oneway void registerGlobalOutPara(in OutPara outPara); // 注册
	String getOutPara(String key); // 获取
	String getGlobalOutPara(String key); // 获取
	void setOutPara(String key, String value); // 设置
	void setGlobalOutPara(String key, String value); // 设置
	void setTimedOutPara(String key, long time, String value); // 设置用户指定时间的
	
	// 入参操作
	oneway void registerInPara(in InPara inPara); // 注册
	oneway void registerGlobalInPara(in InPara inPara); // 注册
	InPara getInPara(String key); // 获取
	InPara getGlobalInPara(String key); // 获取
	void setInPara(String key, String value); // 设置
	void setGlobalInPara(String key, String value); // 设置
	
	// 日志
	void log(long tid, int level, String tag, String msg);
	
	// 异步传输性能数据到控制台，字符串类型的暂无需求
	void setPerfDigitalEntry(in PerfDigitalEntry task); // 数字类型
	void setPerfStringEntry(in PerfStringEntry task); // 字符串类型
	
	// 传boolean开关
	void setBooleanEntry(in BooleanEntry task);
	
	// 传通用命令，receiver是插件名字，也支持GT控制本身作为命令接受者
	void setCommond(in Bundle bundle);
	
	// 传通用命令，receiver是插件名字，也支持GT控制本身作为命令接受者,同步方法
	void setCommondSync(inout Bundle bundle);
}