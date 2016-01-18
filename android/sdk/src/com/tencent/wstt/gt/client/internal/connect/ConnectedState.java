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

import java.util.List;

import android.os.RemoteException;

import com.tencent.wstt.gt.IService;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.client.GTConnectListener;
import com.tencent.wstt.gt.client.internal.GTInternal;
import com.tencent.wstt.gt.data.control.CommandTaskConsumer;
import com.tencent.wstt.gt.data.control.DataCacheController;
import com.tencent.wstt.gt.data.control.GTLogTaskConsumer;
import com.tencent.wstt.gt.data.control.ParaTaskConsumer;
import com.tencent.wstt.gt.data.control.PerfTaskConsumer;

public class ConnectedState extends AbsDataCachedConnState {

	GTLogTaskConsumer gTLogTaskConsumer; // 本状态生命周期独有的
	PerfTaskConsumer perfTaskConsumer; // 本状态生命周期独有的
	ParaTaskConsumer paraTaskConsumer;
	CommandTaskConsumer commandTaskConsumer;

	/**
	 * 构造方法
	 * 
	 * @param dataCacheController
	 *            在Connecting态时就起作用了，所以其生命周期要长，应该传进来
	 */
	public ConnectedState(DataCacheController dataCacheController)
	{
		super(dataCacheController);
	}
	
	@Override
	public void init(IConnState lastState) {
		// 明文，注明这个不能调super.init(lastState);
	}

	@Override
	public void init(IConnState lastState, IService gtService) {
		/*
		 * 1.将缓存积压的参数发到控制台，之后才启动消费者消费，
		 * 至于Connecting态时set的出入参，都会积压在缓存中，所以不需担心时序
		 */
		dataCacheController.transParasToConsole();
		
		// 2.已连接态，启动日志消费者
		gTLogTaskConsumer = new GTLogTaskConsumer(gtService, dataCacheController);
		gTLogTaskConsumer.start();
		
		// 3.启动性能与参数数据消费者
		paraTaskConsumer = new ParaTaskConsumer(gtService, dataCacheController);
		paraTaskConsumer.start();

		perfTaskConsumer = new PerfTaskConsumer(gtService, dataCacheController);
		perfTaskConsumer.start();
		
		// 4.启动命令任务消费者
		commandTaskConsumer = new CommandTaskConsumer(gtService, dataCacheController);
		commandTaskConsumer.start();
		
		/*
		 * 5.执行用户注入的服务连接上的逻辑
		 */
		GTConnectListener gTConnectedListener =
				GTInternal.getInstance().getGTConnectListener();
		if (gTConnectedListener != null)
		{
			gTConnectedListener.onGTConnected();
		}
	}

	@Override
	public void finish() {
		// 也许应该放到下个状态，有可能是DisconnectingState态
		gTLogTaskConsumer.stop(dataCacheController);
		perfTaskConsumer.stop(dataCacheController);
		try {
			/*
			 * 停一会，否则消费者未取到stop生成的结束任务时，该任务就被清理了
			 * TODO sleep不保险，应该用信号量
			 */
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dataCacheController.dispose();
	}

	@Override
	public void setOutPara(String paraName, Object value, boolean isGlobal) {
		// 这个状态不走缓存，直接入队
		dataCacheController.setOutPara(paraName, value, isGlobal);
	}

	@Override
	public void setInPara(String paraName, Object newValue, boolean isGlobal) {
		dataCacheController.setInPara(paraName, newValue, isGlobal);
	}

	/**
	 * 这个对应的AIDL接口,TODO 如需性能最优应该返回字符串，不过入参本身不会超长
	 * @param key
	 * @param inlog
	 * @return
	 */
	private InPara getInPara(String key, boolean isGlobal){
		try {
			return isGlobal ? GTInternal.getInstance().getGTService().getGlobalInPara(key)
					: GTInternal.getInstance().getGTService().getInPara(key);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 下面这些对应的用户接口
	 * @Override
	 */
	public String getInPara(String paraName, String origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		String result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = null;
				}else{
					result = val; 
				}
			}
		}

		return result;
	}

	@Override
	public boolean getInPara(String paraName, boolean origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		boolean result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = false;
				}else if(matchInParaType(val, "boolean")){
					result = Boolean.parseBoolean(vals.get(0));
				}
			}
		}

		return result;
	}

	@Override
	public int getInPara(String paraName, int origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		int result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = 0;
				}else if(matchInParaType(val, "int")){
					result = Integer.parseInt(vals.get(0));
				}
			}
		}

		return result;
	}

	@Override
	public double getInPara(String paraName, double origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		double result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);				
				if(val.equals("<null>")){
					result = 0;
				}else if(matchInParaType(val, "double")){
					result = Double.parseDouble(vals.get(0));
				}
			}
		}

		return result;
	}

	@Override
	public float getInPara(String paraName, float origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		float result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = 0;
				}else if(matchInParaType(val, "float")){
					result = Float.parseFloat(vals.get(0));
				}
			}
		}

		return result;
	}

	@Override
	public long getInPara(String paraName, long origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		long result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = 0;
				}else if(matchInParaType(val, "long")){
					result = Long.parseLong(vals.get(0));
				}
			}
		}

		return result;
	}

	@Override
	public short getInPara(String paraName, short origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		short result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = 0;
				}else if(matchInParaType(val, "short")){
					result = Short.parseShort(vals.get(0));
				}
			}
		}

		return result;
	}

	@Override
	public byte getInPara(String paraName, byte origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		byte result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = 0;
				}else if(matchInParaType(val, "byte")){
					result = Byte.parseByte(vals.get(0));
				}
			}
		}

		return result;
	}

	@Override
	public char getInPara(String paraName, char origVal, boolean isGlobal) {
		// connected态的这个方法需要直接调aidl接口
		InPara iv = getInPara(paraName, isGlobal);
		char result = origVal;
		if(null != iv){
			if(InPara.DISPLAY_DISABLE == iv.getDisplayProperty()){
				result = origVal;
			}else{
				List<String> vals = iv.getValues();
				String val = vals.get(0);
				if(val.equals("<null>")){
					result = 0;
				}else {
					result = val.charAt(0);
				}
			}
		}

		return result;
	}
}
