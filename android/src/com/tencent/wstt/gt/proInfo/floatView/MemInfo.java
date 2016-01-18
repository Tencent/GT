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
package com.tencent.wstt.gt.proInfo.floatView;

import com.tencent.wstt.gt.api.utils.Env;

public class MemInfo {
	final public static MemInfo EMPTY = new MemInfo();
	
	public long time = 0;
	public long dalvikHeapSize = 0;
	public long dalvikAllocated = 0;
	
	public long nativeHeapSize = 0;
	public long nativeAllocated = 0;
	
	public long pss_total = 0;
	public long pss_Native = 0;
	public long pss_Dalvik = 0;
	public long pss_OtherDev = 0;
	public long pss_UnKnown = 0;
	public long pss_Ashmem = 0;
	public long pss_Stack = 0;
	public long pss_graphics = 0; // since Android4.4
	public long pss_gl = 0; // since Android4.4
	
	public long private_dirty = 0;
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (Env.API < 14)
		{
			sb.append("dHeapSize：");
			sb.append(dalvikHeapSize);
			sb.append(" KB");
			sb.append("\ndAllocated：");
			sb.append(dalvikAllocated);
			sb.append(" KB");
			sb.append("\nnHeapSize：");
			sb.append(nativeHeapSize);
			sb.append(" KB");
			sb.append("\nnAllocated：");
			sb.append(nativeAllocated);
			sb.append(" KB");
			sb.append("\npri_dirty : ");
			sb.append(private_dirty);
			sb.append(" KB");
			sb.append("\npss：");
			sb.append(pss_total);
			sb.append(" KB");
		}
		else if(Env.API < 19)
		{
			sb.append("dHeapSize：");
			sb.append(dalvikHeapSize);
			sb.append(" KB");
			sb.append("\ndAllocated：");
			sb.append(dalvikAllocated);
			sb.append(" KB");
			sb.append("\npri_dirty : ");
			sb.append(private_dirty);
			sb.append(" KB");
			sb.append("\npss_T：");
			sb.append(pss_total);
			sb.append(" KB");
			sb.append("\npss_D：");
			sb.append(pss_Dalvik);
			sb.append(" KB");
			sb.append("\npss_N：");
			sb.append(pss_Native);
			sb.append(" KB");
			sb.append("\npss_O：");
			sb.append(pss_OtherDev);
			sb.append(" KB");
			sb.append("\npss_U：");
			sb.append(pss_UnKnown);
			sb.append(" KB");
		}
		else
		{
			sb.append("dHeapSize：");
			sb.append(dalvikHeapSize);
			sb.append(" KB");
			sb.append("\ndAllocated：");
			sb.append(dalvikAllocated);
			sb.append(" KB");
			sb.append("\npri_dirty : ");
			sb.append(private_dirty);
			sb.append(" KB");
			sb.append("\npss_T：");
			sb.append(pss_total);
			sb.append(" KB");
			sb.append("\npss_D：");
			sb.append(pss_Dalvik);
			sb.append(" KB");
			sb.append("\npss_N：");
			sb.append(pss_Native);
			sb.append(" KB");
			sb.append("\npss_O：");
			sb.append(pss_OtherDev);
			sb.append(" KB");
			sb.append("\npss_U：");
			sb.append(pss_UnKnown);
			sb.append(" KB");
			sb.append("\npss_GR：");
			sb.append(pss_graphics);
			sb.append(" KB");
			sb.append("\npss_GL：");
			sb.append(pss_gl);
			sb.append(" KB");
		}

		return sb.toString();
	}
}
