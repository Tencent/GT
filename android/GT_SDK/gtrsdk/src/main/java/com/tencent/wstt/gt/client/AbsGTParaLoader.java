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


/**
 * 输入输出参数注册器抽象类。
 * 其具体实例在GT.connect方法中作为参数使用，详见GT.connect方法。
 */
public interface AbsGTParaLoader {
	
	/**
	 * 注册输入参数。
	 * @param im 输入参数管理器，在使用一个输入参数之前，需要先调用InParaManager.register方法进行注册。
	 */
	abstract public void loadInParas(InParaManager im);
	
	/**
	 * 注册输出参数。
	 * @param om 输出参数管理器，在使用一个输出参数之前，需要先调用OutParaManager.register方法进行注册。
	 */
	abstract public void loadOutParas(OutParaManager om);
}
