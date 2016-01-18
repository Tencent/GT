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
 * 用于对GT应用进行设置
 * @since 2.1.1
 *
 */
public class GTSetting {
	/**
	 * 设置GT采集性能数据的频率，执行该命令前GT应已连接。
	 * @param ms 采样频率，单位毫秒,最小100毫秒
	 */
	public static void setPerfDataSampleRate(int ms)
	{
		if (!GT.isEnable())
		{
			return;
		}

		GTInternal.INSTANCE.setPerfDataSampleRate(ms);
	}
}
