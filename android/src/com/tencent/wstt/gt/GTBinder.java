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

import android.os.Bundle;
import android.os.RemoteException;

import com.tencent.wstt.gt.activity.GTIntervalSettingActivity;
import com.tencent.wstt.gt.autotest.GTAutoTestInternal;
import com.tencent.wstt.gt.communicate.ClientConnectGT;
import com.tencent.wstt.gt.log.GTLogInternal;
import com.tencent.wstt.gt.log.GTTimeInternal;
import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpPerfBridge;
import com.tencent.wstt.gt.receiver.BaseCommandReceiver;
import com.tencent.wstt.gt.service.GTServiceController;

public class GTBinder extends IService.Stub {
	@Override
	public void log(long pid, int level, String tag, String msg)
			throws RemoteException {
		GTLogInternal.log(pid, level, tag, msg, null);
	}

	@Override
	public void registerOutPara(OutPara para) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(getCallingUid());
		client.register(para);
	}

	@Override
	public void registerGlobalOutPara(OutPara para) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(ClientManager.GLOBAL_CLIENT);
		client.register(para);
	}

	@Override
	public void setOutPara(String key, String value)
			throws RemoteException {
		
		Client client = ClientManager.getInstance().getClient(getCallingUid());
		OutPara para = client.getOutPara(key);
		
		if(null != para){
			para.setValue(value);
			
			// add on 20130923 为了出参支持历史曲线
			if (para.isMonitor())
			{
				try
				{
					long lValue = Long.parseLong(value);
					OpPerfBridge.addHistory(para, value, lValue);
				}
				catch(Exception e)
				{
					// 非数字，直接重置
					para.setMonitor(false);
					OpPerfBridge.removeProfilerData(key);
				}
			}
		}
	}

	@Override
	public void setGlobalOutPara(String key, String value)
			throws RemoteException {
		Client client = ClientManager.getInstance().getClient(ClientManager.GLOBAL_CLIENT);
		OutPara para = client.getOutPara(key);
		
		if(null != para){
			para.setValue(value);
			
			// add on 20130923 为了出参支持历史曲线
			if (para.isMonitor())
			{
				try
				{
					long lValue = Long.parseLong(value);
					OpPerfBridge.addHistory(para, value, lValue);
				}
				catch(Exception e)
				{
					// 非数字，直接重置
					para.setMonitor(false);
					OpPerfBridge.removeProfilerData(key);
				}
			}
		}
	}

	@Override
	public void setTimedOutPara(String key, long time, String value)
			throws RemoteException {

		Client client = ClientManager.getInstance().getClient(getCallingUid());
		OutPara para = client.getOutPara(key);

		if(null != para){
			para.setValue(time, value);
			// add on 20130923 为了出参支持历史曲线
			if (para.isMonitor())
			{
				try
				{
					long lValue = Long.parseLong(value);
					OpPerfBridge.addHistory(para, value, time, lValue);
				}
				catch(Exception e)
				{
					// 非数字，直接直接重置
					para.setMonitor(false);
					OpPerfBridge.removeProfilerData(key);
				}
			}
		}
	}

	@Override
	public void registerInPara(InPara para) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(getCallingUid());
		client.register(para);
	}

	@Override
	public void registerGlobalInPara(InPara para) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(ClientManager.GLOBAL_CLIENT);
		client.register(para);
	}

	@Override
	public InPara getInPara(String key) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(getCallingUid());
		InPara para = client.getInPara(key);
		return para;
	}

	@Override
	public InPara getGlobalInPara(String key) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(ClientManager.GLOBAL_CLIENT);
		InPara para = client.getInPara(key);
		return para;
	}

	@Override
	public void initConnectGT(String pkgName, int pid) throws RemoteException {
		ClientConnectGT.initConnectGT(pkgName, getCallingUid(), pid);
	}

	@Override
	public boolean disconnectGT(String cur_pkgName) throws RemoteException {
		return ClientConnectGT.disconnectGT(cur_pkgName);
	}

	@Override
	public int checkIsCanConnect(String cur_pkgName, int versionId) throws RemoteException {
		return ClientConnectGT.checkIsCanConnect(cur_pkgName, versionId);
	}

	@Override
	public void setInPara(String key, String newValue) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(getCallingUid());
		InPara iv = client.getInPara(key);
		if (null != iv) {
			iv.getValues().remove(newValue);
			iv.getValues().add(0, newValue);
		}
	}

	@Override
	public void setGlobalInPara(String key, String newValue)
			throws RemoteException {
		Client client = ClientManager.getInstance().getClient(ClientManager.GLOBAL_CLIENT);
		InPara iv = client.getInPara(key);
		if (null != iv) {
			iv.getValues().remove(newValue);
			iv.getValues().add(0, newValue);
		}
	}

	@Override
	public String getOutPara(String key) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(getCallingUid());
		OutPara para = client.getOutPara(key);
		if (para != null)
		{
			return para.getValue();
		}
		return "";
	}

	@Override
	public String getGlobalOutPara(String key) throws RemoteException {
		Client client = ClientManager.getInstance().getClient(ClientManager.GLOBAL_CLIENT);
		OutPara para = client.getOutPara(key);
		if (para != null)
		{
			return para.getValue();
		}
		return "";
	}

	@Override
	public void setPerfDigitalEntry(PerfDigitalEntry task)
			throws RemoteException {
		switch (task.getFunctionId()) {
		case Functions.PERF_DIGITAL_NORMAL:
			GTTimeInternal.recordDigital(task);
			break;
		case Functions.PERF_REDUCE_TIME:
			GTTimeInternal.recordDigital(task);
			break;
		case Functions.PERF_START_TIME_GLOBAL:
			GTTimeInternal.startDigital(task);
			break;
		case Functions.PERF_END_TIME_GLOBAL:
			GTTimeInternal.endDigital(task);
			break;
		case Functions.PERF_START_DIGITAL_GLOBAL:
			GTTimeInternal.startDigital(task); // 直接复用startTime的逻辑
			break;
		case Functions.PERF_END_DIGITAL_GLOBAL:
			GTTimeInternal.endDigital(task); // 直接复用endTime的逻辑
			break;	
		}
	}

	@Override
	public void setPerfStringEntry(PerfStringEntry task) throws RemoteException {

	}

	@Override
	public void setBooleanEntry(BooleanEntry task) throws RemoteException {
		switch (task.getFunctionId()) {
		case Functions.SET_PROFILER_ENABLE:
			if (task.getData())
			{
				GTAutoTestInternal.startTimeStatistics();
			}
			else
			{
				GTAutoTestInternal.stopTimeStatistics();
			}
			break;
		case Functions.SET_FLOATVIEW_FRONT:
			GTServiceController.INSTANCE.setFloatViewFront(task.getData());
			break;
		}
	}

	@Override
	public void setCommond(Bundle bundle) throws RemoteException {
		if (null == bundle || null == bundle.getString(Functions.GT_COMMAND))
		{
			return;
		}
		String sReceiver = bundle.getString(Functions.GT_COMMAND);
		
		// 宿主处理的
		if ("".equals(sReceiver))
		{
			int iCmd = bundle.getInt(Functions.GT_COMMAND_KEY);

			switch(iCmd)
			{
			case Functions.GT_CMD_SET_SAMPLE_RATE:
				int sampleRate = bundle.getInt("sampleRate");
				sampleRate = sampleRate / 100 * 100;
				GTIntervalSettingActivity.msecond = sampleRate >= 100 ? sampleRate : 100;
				break;
			}
		}
		else
		{
			GTTimeInternal.dispatchPiCommand(sReceiver, bundle);
		}
	}

	@Override
	public void setCommondSync(Bundle bundle) throws RemoteException {
		if (null == bundle || null == bundle.getString(Functions.GT_COMMAND))
		{
			return;
		}
		String sReceiver = bundle.getString(Functions.GT_COMMAND);
		
		// 宿主处理的
		if ("".equals(sReceiver))
		{
			int iCmd = bundle.getInt(Functions.GT_COMMAND_KEY);
			String pkgName = bundle.getString("pkgName");
			String verName = bundle.getString("verName");
			int pid = bundle.getInt("pid");
			String saveFolderName = bundle.getString(BaseCommandReceiver.INTENT_KEY_SAVE_FOLDER);
			String desc = bundle.getString(BaseCommandReceiver.INTENT_KEY_SAVE_DESC);

			switch(iCmd)
			{
			case Functions.GT_CMD_GET_VERSION:
				bundle.putString(Functions.GT_CMD_KEY_VERSION, GTConfig.VERSION);
				break;
			case Functions.GT_CMD_START_PROCTEST:
				if (pkgName != null)
				{
					GTAutoTestInternal.startProcTest(pkgName, verName, pid);
				}
				break;
			case Functions.GT_CMD_END_PROCTEST:
				if (saveFolderName != null)
				{
					GTAutoTestInternal.endProcTest(pkgName, pid, saveFolderName, desc, false);
				}
				break;
			case Functions.GT_CMD_END_TEST_AND_CLEAR:
				if (saveFolderName != null)
				{
					GTAutoTestInternal.endProcTest(pkgName, pid, saveFolderName, desc, true);
				}
				break;
			case Functions.GT_CMD_TEST_DATA_CLEAR:
				GTAutoTestInternal.clearDatas();
				break;
			case Functions.GT_CMD_START_SAMPLE:
				if (pkgName != null)
				{
					String targetStart = bundle.getString("target");
					GTAutoTestInternal.startSample(pkgName, pid, targetStart);
				}
				break;
			case Functions.GT_CMD_STOP_SAMPLE:
				if (pkgName != null)
				{
					String targetStop = bundle.getString("target");
					GTAutoTestInternal.stopSample(pkgName, pid, targetStop);
				}
				break;
			case Functions.GT_CMD_SAMPLE:
				if (pkgName != null)
				{
					String targetSample = bundle.getString("target");
					GTAutoTestInternal.sample(pkgName, pid, targetSample);
				}
				break;
			// 该命令已转到异步命令处理，这里保留对老版本兼容
			case Functions.GT_CMD_SET_SAMPLE_RATE:
				int sampleRate = bundle.getInt("sampleRate");
				sampleRate = sampleRate / 100 * 100;
				GTIntervalSettingActivity.msecond = sampleRate >= 100 ? sampleRate : 100;
				break;
			// 结束并保存清理耗时统计
			case Functions.GT_CMD_END_ET_AND_CLEAR:
				String filename = bundle.getString("filename");
				GTAutoTestInternal.endTimeStatistics(filename);
				break;
			}
		}
		else
		{
			GTTimeInternal.dispatchPiCommandSync(sReceiver, bundle);
		}
	}
}
