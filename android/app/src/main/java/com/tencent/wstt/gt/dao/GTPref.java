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
package com.tencent.wstt.gt.dao;

import com.tencent.wstt.gt.GTApp;

import android.content.SharedPreferences;

public class GTPref {
	public static final String GT_SP_NAME = "gt_SharedPreferences";
	
	// 日志用的开关，boolean型参数
	public static final String LOG_MASTER_SWITCH = "log_master_switch";
	public static final String LOG_AUTOSAVE_SWITCH = "log_autosave_switch";
	public static final String LOG_AUTOSAVEFLUSH_SWITCH = "log_autosaveflush_switch";
	public static final String LOG_SAVE_DEFAULT_SEG = "log_save_default_seg";
	
	// 性能统计间隔
	public static final String INTERVAL_PERF = "interval_perf";
	public static final String INTERVAL_PERF_POS = "interval_perf_pos";

	// FPS采集间隔
	public static final String INTERVAL_FPS = "interval_fps";
	public static final String INTERVAL_FPS_POS = "interval_fps_pos";

	// 性能统计用的开关
	public static final String PERF_MASTER_SWITCH = "perf_master_switch";
	
	// 控制台设置类型
	public static final String AC_SWITCH = "ac_switch_type";
	public static final String AC_SWITCH_FLAG = "ac_switch_type_flag"; // 是否启用悬浮图标的开关

	// 是否已授权弹出悬浮框标志，在API23以上起作用
	public static final String FLOAT_ALLOWED = "ac_float_allowed_flag";

	// 是否已授权允许WRITE_SETTINGS，在API23以上起作用
	public static final String WRITE_SETTINGS = "write_settings_allowed_flag";
	
	// 法律条款接受与否
	public static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";
	
	// 出参历史页的告警区域是否折叠的开关
//	public static final String OP_PERF_DETAIL_WARN_AREA_FOLD = "op_perf_detail_wf";
	
	private static SharedPreferences gt_SP =
			GTApp.getContext().getSharedPreferences(GT_SP_NAME, 0);
	
	public static SharedPreferences getGTPref()
	{
		return gt_SP;
	}
}
