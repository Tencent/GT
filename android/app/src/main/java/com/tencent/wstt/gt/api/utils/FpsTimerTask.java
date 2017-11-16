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
package com.tencent.wstt.gt.api.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimerTask;

import com.tencent.wstt.gt.manager.Client;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.utils.GTFrameUtils;

/**
 * Andorid4.0以上版本，使用此FPS获取的实现，要比原有方案简单稳定
 */
public class FpsTimerTask extends TimerTask {
	private static long startTime = 0L;
	private static int lastFrameNum = 0;
	private static long testCount = 0;
	
	private static Process process;
	private static DataOutputStream os;
	private static BufferedReader ir;

	// 插件参数的管理使用全局客户端
	private Client defaultClient = ClientManager.getInstance().getDefaultClient();

	/*
	 * 主循环，和2.3方案不同的是，本主循环1s执行一次，2.3方案是0.5s执行一次
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		/*
		 * Timer的启动无法避免，所以这里加root保护
		 * 若未root，直接返回，减少空消耗
		 */
		if (! GTFrameUtils.isHasSu())
		{
			return;
		}

		long end = 0L;
		float realCostTime = 0.0F;
		end = System.nanoTime();
		if (testCount != 0) {
			realCostTime = (float) (end - startTime) / 1000000.0F;
		}

		startTime = System.nanoTime();
		if (testCount == 0) {
			try {
				lastFrameNum = getFrameNum();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int currentFrameNum = 0;
		try {
			currentFrameNum = getFrameNum();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int FPS = currentFrameNum - lastFrameNum;
		if (realCostTime > 0.0F) {
			int fpsResult = (int) (FPS * 1000 / realCostTime);
			defaultClient.setOutPara("FPS", fpsResult);
		}
		lastFrameNum = currentFrameNum;

		testCount += 1;
	}

	public static synchronized int getFrameNum() throws IOException {
		String frameNumString = "";
		String getFps40 = "service call SurfaceFlinger 1013";
		
		if (process == null)
		{
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			ir = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
		}

		os.writeBytes(getFps40 + "\n");
		os.flush();

		String str = "";
		int index1 = 0;
		int index2 = 0;
		while ((str = ir.readLine()) != null) {
			if (str.indexOf("(") != -1) {
				index1 = str.indexOf("(");
				index2 = str.indexOf("  ");

				frameNumString = str.substring(index1 + 1, index2);
				break;
			}
		}

		int frameNum;
		if (!frameNumString.equals("")) {
			frameNum = Integer.parseInt(frameNumString, 16);
		} else {
			frameNum = 0;
		}
		return frameNum;
	}
	
	public static synchronized void stopCurrentTask()
	{
		try {
			os.writeBytes("exit\n");
			os.flush();
			os.close();
			ir.close();
		} catch (IOException e) {
		}
		process.destroy();
		process = null;
	}
}