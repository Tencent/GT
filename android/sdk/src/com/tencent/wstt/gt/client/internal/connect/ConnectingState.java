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
package com.tencent.wstt.gt.client.internal.connect;

import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.data.control.DataCacheController;

/**
 * 注入ConnectingState走缓存的方法，除了注册，对其设置global或获取global参数是无效的
 */
public class ConnectingState extends AbsDataCachedConnState {
	
	public ConnectingState(DataCacheController dataCacheController)
	{
		super(dataCacheController);
	}
	
	@Override
	public void init(IConnState lastState) {
		dataCacheController.init();
	}

	@Override
	public void finish() {
		
	}
	
	@Override
	public void registerOutParas(OutPara[] outParas)
	{
		if (null == outParas)
		{
			return; 
		}
		
		for (OutPara outPara : outParas)
		{
			dataCacheController.registerOutParaToCache(outPara);
		}
	}

	@Override
	public void setOutPara(String paraName, Object value, boolean isGlobal) {
		// 存缓存的方法
		dataCacheController.setOutParaToCache(paraName, value);
	}

	@Override
	public void registerInParas(InPara[] inParas)
	{
		for (InPara inPara : inParas)
		{
			dataCacheController.registerInParaToCache(inPara);
		}
	}

	@Override
	public void setInPara(String paraName, Object newValue, boolean isGlobal) {
		dataCacheController.setInParaToCache(paraName, newValue);
	}

	@Override
	public String getInPara(String paraName, String origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		String result = origVal;
		if (null != resultInCache)
		{
			result = resultInCache;
		}

		return result;
	}

	@Override
	public boolean getInPara(String paraName, boolean origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		boolean result = origVal;
		if (null != resultInCache)
		{
			if(matchInParaType(resultInCache, "boolean")){
				result = Boolean.parseBoolean(resultInCache);
			}
		}

		return result;
	}

	@Override
	public int getInPara(String paraName, int origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		int result = origVal;
		if (null != resultInCache)
		{
			if(matchInParaType(resultInCache, "int")){
				result = Integer.parseInt(resultInCache);
			}
		}

		return result;
	}

	@Override
	public double getInPara(String paraName, double origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		double result = origVal;
		if (null != resultInCache)
		{
			if(matchInParaType(resultInCache, "double")){
				result = Double.parseDouble(resultInCache);
			}
		}

		return result;
	}

	@Override
	public float getInPara(String paraName, float origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		float result = origVal;
		if (null != resultInCache)
		{
			if(matchInParaType(resultInCache, "float")){
				result = Float.parseFloat(resultInCache);
			}
		}

		return result;
	}

	@Override
	public long getInPara(String paraName, long origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		long result = origVal;
		if (null != resultInCache)
		{
			if(matchInParaType(resultInCache, "long")){
				result = Long.parseLong(resultInCache);
			}
		}

		return result;
	}

	@Override
	public short getInPara(String paraName, short origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		short result = origVal;
		if (null != resultInCache)
		{
			if(matchInParaType(resultInCache, "short")){
				result = Short.parseShort(resultInCache);
			}
		}

		return result;
	}

	@Override
	public byte getInPara(String paraName, byte origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		byte result = origVal;
		if (null != resultInCache)
		{
			if(matchInParaType(resultInCache, "byte")){
				result = Byte.parseByte(resultInCache);
			}
		}

		return result;
	}

	@Override
	public char getInPara(String paraName, char origVal, boolean isGlobal) {
		// 从缓存里取的，取的必然是注册的，inlog是控制台的行为，此时是无效的
		String resultInCache = dataCacheController.getInParaFromCache(paraName);
		char result = origVal;
		if (null != resultInCache)
		{
			result = resultInCache.charAt(0);
		}

		return result;
	}
}
