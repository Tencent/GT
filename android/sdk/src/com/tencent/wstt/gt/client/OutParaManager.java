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
package com.tencent.wstt.gt.client;

import com.tencent.wstt.gt.client.internal.GTInternal;

/**
 * 输出参数操作管理器，该类型的对象只在GT.connect方法中有用，详情请见GT.connect方法说明。
 *
 */
public class OutParaManager {
	
	/**
	 * 注册一个输出型参数。
	 * @param ParaName	参数名称
	 * @param alias     参数名称简写（不能超过4个字母，便于在悬浮窗中显示，超过4个字母会被截短）
	 * @param extras 扩展参数，如果extras[0]是boolean型，则标明该参数是否是Global类型的
	 */
	public void register(String ParaName, String alias, Object...extras){
		GTInternal.INSTANCE.getOutParaManager().register(ParaName, alias, extras);
	}
	
	/**
	 * 定义默认显示在控制台出参。
	 * @param ParaNames 出参名称数组，用法请参考GT.connect方法说明。
	 */
	public void defaultOutParasInAC(String... ParaNames){
		GTInternal.INSTANCE.getOutParaManager().defaultOutParasInAC(ParaNames);
	}
	
	/**
	 * 设置所有GT定义的出参失效。失效后，出参不会在控制台UI上刷新显示。
	 */
	public void defaultOutParasInDisableArea(){
		GTInternal.INSTANCE.getOutParaManager().setOutParasDisable();
	}
	
	/**
	 * 设置指定的GT定义的出参失效。失效后，出参不会在控制台UI上刷新显示。
	 * @param 指定的出参名称数组
	 */
	public void defaultOutParasInDisableArea(String... ParaNames){
		GTInternal.INSTANCE.getOutParaManager().setOutParasDisable(ParaNames);
	}
}
