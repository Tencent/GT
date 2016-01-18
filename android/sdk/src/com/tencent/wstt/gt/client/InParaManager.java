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
 * 输入参数操作管理器，该类型的对象只在GT.connect方法中有用，详情请见GT.connect方法说明。
 *
 */
public class InParaManager {
	
	/**
	 * 注册一个输入型参数，所有输入参数注册的值都是String，在被测代码中使用该入参时，
	 * 可用GT.getInPara系列方法获取不同基本类型的对应值。
	 * 
	 * @param ParaName
	 *            参数名称
	 * @param alias
	 *            参数名称简写（不能超过4个字母，便于在悬浮窗中显示，超过4个字母会被截短）
	 * @param defaultValue
	 *            默认值
	 * @param optionalValues
	 *            可选值（在入参设置界面显示在下拉列表中的值）
	 */
	public void register(String ParaName, String alias, String defaultValue, String... optionalValues){
		GTInternal.INSTANCE.getInParaManager().register(
				ParaName, alias, defaultValue, optionalValues);
	}
	
	/**
	 * 定义默认显示在悬浮窗的入参，最多只按序显示前3个。且第1个参数值会在悬浮窗中加粗加大显示。
	 * @param ParaNames 入参名称数组，用法请参考GT.connect方法说明。
	 */
	public void defaultInParasInAC(String... ParaNames){
		GTInternal.INSTANCE.getInParaManager().defaultInParasInAC(ParaNames);
	}
	
	/**
	 * 设置所有GT定义的入参失效。在设置失效后，
	 * 代码中调用GT.getInPara(paraName, origVal, inlog)方法时，会直接返回origVal。
	 */
	public void defaultInParasInDisableArea(){
		GTInternal.INSTANCE.getInParaManager().setInParasDisable();
	}
	
	/**
	 * 设置部分GT定义的入参失效，在设置失效后，
	 * 代码中调用对应paraName入参的GT.getInPara(paraName, origVal, inlog)方法时，会直接返回origVal。
	 * @param ParaNames 入参名数组
	 */
	public void defaultInParasInDisableArea(String... ParaNames){
		GTInternal.INSTANCE.getInParaManager().setInParasDisable(ParaNames);
	}
}
