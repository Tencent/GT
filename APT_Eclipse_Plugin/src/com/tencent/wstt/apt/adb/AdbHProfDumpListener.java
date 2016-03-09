/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.adb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import com.tencent.wstt.apt.cmdparse.HprofConv;
import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.android.ddmlib.Client;
import com.android.ddmlib.ClientData;
import com.android.ddmlib.IShellOutputReceiver;

/**
* @Description 实现ADB中的IHprofDump接口，用来保存对应的hprof文件 
* @date 2013年11月10日 下午5:04:39 
*
 */
public class AdbHProfDumpListener implements ClientData.IHprofDumpHandler {

	public static final Object sLock = new Object();
	@Override
	public void onEndFailure(Client client, String msg) {
		synchronized (sLock) {
			String pkgName = client.getClientData().getClientDescription();
			if(pkgName == null)
			{
				pkgName = "null";
			}
			APTConsoleFactory.getInstance().APTPrint("Dump hprof failed, pkg=:" + pkgName + ",msg=" + msg);
			sLock.notify();
		}
	}

	@Override
	public void onSuccess(String remoteFilePath, Client client) {
		
		synchronized (sLock) {
			APTConsoleFactory.getInstance().APTPrint(
					"onSuccess(String remoteFilePath, Client client)");
			try {
				APTConsoleFactory.getInstance().APTPrint(
						"remoteFilePath=" + remoteFilePath);
				String pkgName = client.getClientData().getClientDescription();
				if (pkgName == null) {
					pkgName = "null";
				}
				String filePath = getHprofFilePath(pkgName);
				client.getDevice().pullFile(remoteFilePath, filePath);
				client.getDevice().executeShellCommand("rm " + remoteFilePath,
						new IShellOutputReceiver() {
							public void addOutput(byte[] data, int offset,
									int length) {
								APTConsoleFactory.getInstance().APTPrint(
										"addOutput");
							}

							public void flush() {
								APTConsoleFactory.getInstance().APTPrint(
										"flush");
							}

							public boolean isCancelled() {
								APTConsoleFactory.getInstance().APTPrint(
										"isCancelled");
								return false;
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				APTConsoleFactory.getInstance().APTPrint(e.getMessage());
			}

			sLock.notify();
		}
	}

	/**
	 * 写文件的时候需要进行多线程处理，一次处理耗时太多
	 */
	@Override
	public void onSuccess(byte[] data, Client client) {
		//APTConsoleFactory.getInstance().APTPrint("onSuccess(byte[] data, Client client)");
		synchronized (sLock) {
			String hprofLogPath = Constant.HPROF_LOG_PATH_ON_PC;
			File file = new File(hprofLogPath);
			if (!file.isDirectory()) {
				if (!file.mkdirs()) {
					APTConsoleFactory.getInstance().APTPrint(
							"创建目录" + hprofLogPath + "失败");
					APTConsoleFactory.getInstance().APTPrint(
							"请检查创建失败的路径中是否已存在相同名字的文件，删除后重新测试");
					return;
				} else {
					APTConsoleFactory.getInstance().APTPrint(
							"创建目录" + hprofLogPath + "成功");
				}
			}

			String pkgName = client.getClientData().getClientDescription();
			if(pkgName == null)
			{
				pkgName = "null";
			}
			String hprofFileName = getHprofFilePath(pkgName);
			File fw = null;
			OutputStream out = null;
			try {
				fw = new File(hprofFileName);
				out = new FileOutputStream(fw);
				out.write(data);
			} catch (IOException e1) {
				e1.printStackTrace();
				APTConsoleFactory.getInstance().APTPrint(
						"写文件失败:" + hprofFileName);
				return;
			}

			finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
						APTConsoleFactory.getInstance().APTPrint(
								"文件关闭失败:" + hprofFileName);
						return;
					}
				}
			}
			
			String convFileName = hprofFileName + ".hprof";
			HprofConv.run(hprofFileName, convFileName);
			APTConsoleFactory.getInstance().APTPrint(
					"dump hprof completed:" + convFileName);

			sLock.notify();
		}

	}
	
	/**
	 * 返回hprof文件的完整保存路径
	 * @return
	 */
	private String getHprofFilePath(String pkgName)
	{
		String curDate = Constant.SIMPLE_DATE_FORMAT_SECOND.format(new Date(System.currentTimeMillis()));
		String hprofFileName = pkgName.replaceAll(":", ".") + "_" + curDate;
		String filePath = Constant.HPROF_LOG_PATH_ON_PC + File.separator + hprofFileName;
		return filePath;
	}

}
