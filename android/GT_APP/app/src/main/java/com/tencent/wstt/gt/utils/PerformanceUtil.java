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
package com.tencent.wstt.gt.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PerformanceUtil {
	//===========================================================关于性能信息=================================================================
	/**
	 * 获取性能信息:RSS、VSS、CPU
	 * @param procName
	 * @return
	 */
    public static String[] getPerformInfo(String procName) {
		String[] result = new String[]{"","",""};
		String tempString = "";
		List<String[]> PMUList = Collections.synchronizedList(new ArrayList<String[]>());
		int INDEX_FIRST = -1;
		int INDEX_CPU = INDEX_FIRST + 3;
		int INDEX_VSS = INDEX_FIRST + 6;
		int INDEX_RSS = INDEX_FIRST + 7;
		int INDEX_NAME = INDEX_FIRST + 10;
		synchronized (PMUList) {
			for (Iterator<String[]> iterator = PMUList.iterator(); iterator
					.hasNext();) {
				String[] item = (String[]) iterator.next();
				tempString = item[INDEX_NAME];
				if (tempString != null && tempString.equals(procName)) {
					// result = "CPU:" + item[INDEX_CPU]
					// + "  Mem:" + item[INDEX_RSS];
					result[0] = item[INDEX_RSS];
					result[1] = item[INDEX_VSS];
					result[2] = item[INDEX_CPU];
					break;
				}
			}
		}
        return result;
	}

}
