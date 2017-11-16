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

/**
 * 控制台配置文件，注意保持客户端和控制台该配置文件一致
 */
public class GTConfig {
	/**
	 * GT 类型
	 */
	public static final byte RELEASE = 0;
	public static final byte DEVELOP = 1;

	// 如果无需推送，版本号只在manifest.xml中变化即可，应用宝会自动提示更新
	public static String VERSION = "2.2";
	// 内部用于和控制台端判断兼容性的标识，应随着aidl变化而变化
	public final static int INTERVAL_VID = 2200;
	public static byte VERSION_TYPE = RELEASE;
	public static String PLATFORM = "android";
	
	/*
	 * 控制台判断客户端连接状态对应的常量，两边注意对应一致
	 * 对客户端来说，是连接GT服务，可能得到的返回码
	 */
	public final static int CONNECT_RES_CODE_UNCONNECT = -1; // 尚未连接
	public final static int CONNECT_RES_CODE_OK = 200; // 可以连接
	public final static int CONNECT_RES_CODE_REFUSE = 403; // 已有其他应用连到GT,所以拒绝
	public final static int CONNECT_RES_CODE_VERSION_INVALID = 406; // 版本不配套
}
