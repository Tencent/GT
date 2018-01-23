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
package com.tencent.wstt.gt.manager;

import java.util.List;

import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;

public abstract class Client {
	protected String key;
	protected IInParaManager inParaManager;
	protected IOutParaManager outParaManager;

	public Client(String key)
	{
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	// 内部使用方法，包作用域即可
	void setInParaManager(IInParaManager inParaManager) {
		this.inParaManager = inParaManager;
	}
	// 内部使用方法，包作用域即可
	void setOutParaManager(IOutParaManager outParaManager) {
		this.outParaManager = outParaManager;
	}

	/**
	 * 注册初始入参，并返回这些入参的管理对象
	 * @param inParas
	 * @return 入参的管理对象
	 */
	public abstract IInParaManager initInParas(InPara[] inParas);

	/**
	 * 注册初始出参，并返回这些出参的管理对象
	 * @param outParas
	 * @return 出参的管理对象
	 */
	public abstract IOutParaManager initOutParas(OutPara[] outParas);

	public void clear()
	{
		inParaManager.clear();
		outParaManager.clear();
	}

	//==============================出参相关方法===============================
	public void register(OutPara para)
	{
		outParaManager.register(para);
	}

	public void registerOutPara(String paraName, String alias)
	{
		outParaManager.register(paraName, alias);
	}

	public void unregisterOutPara(String paraName)
	{
		outParaManager.removeOutPara(paraName);
	}

	public boolean isOutParaEmpty()
	{
		return outParaManager.isEmpty();
	}

	public OutPara getOutPara(String paraName) {
		return outParaManager.getOutPara(paraName);
	}

	public void setOutparaMonitor(String str, boolean flag) {
		outParaManager.setOutparaMonitor(str, flag);
	}

	public List<OutPara> getAllOutParas()
	{
		return outParaManager.getAll();
	}
	
	/**
	 * 设置输出参数值，值类型为String型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, String value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(value);
		}
	}
	
	/**
	 * 设置输出参数值，值类型为boolean型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, boolean value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
		}

	}

	/**
	 * 设置输出参数值，值类型为int型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, int value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
			
			// add on 20130923 为了出参支持历史曲线
			OpPerfBridge.addHistory(outPara, outPara.getValue(), value);
		}
		
	}
	
	public void setOutPara(String paraName, long time, int value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(time, String.valueOf(value));
			
			// add on 20130923 为了出参支持历史曲线
			OpPerfBridge.addHistory(outPara, outPara.getValue(), time, value);
		}
	}
	
	/**
	 * 设置输出参数值，值类型为long型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, long value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
			
			// add on 20130923 为了出参支持历史曲线
			OpPerfBridge.addHistory(outPara, outPara.getValue(), value);
		}
	}
	
	public void setOutPara(String paraName, long time, long value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(time, String.valueOf(value));
			
			// add on 20130923 为了出参支持历史曲线
			OpPerfBridge.addHistory(outPara, outPara.getValue(), time, value);
		}
	}
	
	/**
	 * 设置输出参数值，值类型为short型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, short value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
			
			// add on 20130923 为了出参支持历史曲线
			OpPerfBridge.addHistory(outPara, outPara.getValue(), value);
		}
	}

	public void setOutPara(String paraName, long time, short value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(time, String.valueOf(value));
			
			// add on 20130923 为了出参支持历史曲线
			OpPerfBridge.addHistory(outPara, outPara.getValue(), time, value);
		}
	}
	
	/**
	 * 设置输出参数值，值类型为char型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, char value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
		}
	}
	
	/**
	 * 设置输出参数值，值类型为double型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, double value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
		}
	}
	
	/**
	 * 设置输出参数值，值类型为float型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, float value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
		}
	}

	/**
	 * 设置输出参数值，值类型为object型，更新的输出参数值会在控制台和输出界面中展示。
	 * 
	 * @param ParaName
	 *            输出参数的名称
	 * @param value
	 *            输出参数的值
	 * @param inlog
	 *            本次设置操作是否打印到日志中记录
	 */
	public void setOutPara(String paraName, Object value){
		OutPara outPara = getOutPara(paraName);
		if(null != outPara && OutPara.DISPLAY_DISABLE != outPara.getDisplayProperty()){
			outPara.setValue(String.valueOf(value));
		}
	}
	//==============================入参相关方法===============================
	public void registerInPara(String paraName, String alias, String defaultValue,
			String... optionalValues)
	{
		inParaManager.register(paraName, alias, defaultValue, optionalValues);
	}
	
	/**
	 * 由客户端首次注册，直接入缓存
	 * @param para
	 */
	public void register(InPara para)
	{
		inParaManager.register(para);
	}

	public void unregisterInPara(String paraName)
	{
		inParaManager.removeOutPara(paraName);
	}

	public boolean isInParaEmpty()
	{
		return inParaManager.isEmpty();
	}

	public List<InPara> getAllInParas()
	{
		return inParaManager.getAll();
	}

	public InPara getInPara(int positon)
	{
		return inParaManager.getInPara(positon);
	}
	
	public InPara getInPara(String paraName)
	{
		return inParaManager.getInPara(paraName);
	}

	public String getInPara(String paraName, String origVal)
	{
		return inParaManager.getInPara(paraName, origVal);
	}

	public boolean getInPara(String paraName, boolean origVal){
		return inParaManager.getInPara(paraName, origVal);
	}

	public int getInPara(String paraName, int origVal){
		return inParaManager.getInPara(paraName, origVal);
	}

	public float getInPara(String paraName, float origVal){
		return inParaManager.getInPara(paraName, origVal);
	}

	public double getInPara(String paraName, double origVal){
		return inParaManager.getInPara(paraName, origVal);
	}

	public short getInPara(String paraName, short origVal){
		return inParaManager.getInPara(paraName, origVal);
	}

	public byte getInPara(String paraName, byte origVal){
		return inParaManager.getInPara(paraName, origVal);
	}

	public long getInPara(String paraName, long origVal){
		return inParaManager.getInPara(paraName, origVal);
	}
}
