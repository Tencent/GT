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
 * 用于自动化测试控制GT采集/保存手机的数据。
 * 自动化测试相关能力目前尚在调整完善中，本接口仅供试用。
 * @since 2.1.1
 *
 */
public class GTAutoTesterForApp {

	/**
	 * 启动GT进行<b>指定包名</b>各进程的数据采集，执行该命令前GT应已连接
	 * @param pkgName 指定包名
	 */
	public static void startTest(String pkgName)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.startProcTest(-1, pkgName);
	}
	
	/**
	 * 结束GT进行当前进程的数据采集，执行该命令前GT应已开始采集
	 * @param saveFolderName
	 *            保存目录的名称，此目录会保存在/sdcard/GT/default/下或/sdcard/GT/pkgName/，
	 *            如果目录名为null，默认的目录名是GW_DATA
	 */
	public static void endTest(String saveFolderName)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.endProcTest(-1, saveFolderName);
	}
	
	/** 
	 * 结束GT进行当前进程的数据采集，执行该命令前GT应已开始采集
	 * @param saveFolderName
	 *            保存目录的名称，此目录会保存在/sdcard/GT/default/下或/sdcard/GT/pkgName/，
	 *            如果目录名为null，默认的目录名是GW_DATA，
	 *            每次保存后，GT会把缓存的本次测试数据清空。
	 */
	public static void endTestAndClear(String saveFolderName)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.endTestAndClear(-1, saveFolderName);
	}
	
	/**
	 * 把GT缓存的出参数据清空。
	 */
	public static void clear()
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.clearTestDatas();
	}

	/**
	 * 开始采集某一指标
	 * @param target 指标标识，要与控制台对应。
	 * 目前target可以是"cpu"、"jif"、"pss"、"pri"、"net"、"fps"，
	 * 因为FPS时会弹授权窗口卡住自动化进程，所以建议用TowerRoot这样不弹框的授权工具。
	 */
	public static void startSample(String target)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.startSample(-1, target);
	}

	/**
	 * 停止采集某一指标
	 * @param target 指标标识，要与控制台对应
	 */
	public static void stopSample(String target)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.stopSample(-1, target);
	}

	/**
	 * 直接采集某一指标
	 * @param target 指标标识，要与控制台对应
	 */
	public static void sample(String target)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.sample(-1, target);
	}

	/**
	 * 开始耗时统计
	 * @since 2.2.6.3
	 */
	public static void startTimeStatistics()
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.startTimeStatistics();
	}

	/**
	 * 暂停耗时统计
	 * @since 2.2.6.3
	 */
	public static void stopTimeStatistics()
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.stopTimeStatistics();
	}

	/**
	 * 结束耗时统计并保存
	 * @param filename 保存的文件名
	 * @since 2.2.6.3
	 */
	public static void endTimeStatistics(String filename)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.endTimeStatistics(filename);
	}
}
