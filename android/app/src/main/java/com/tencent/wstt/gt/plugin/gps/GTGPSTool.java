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
package com.tencent.wstt.gt.plugin.gps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;

import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.utils.FileUtil;

public class GTGPSTool {

	private static String[] getGPSData(String gpsfile) {
		BufferedReader reader = null;
		try {
			List<String> data = new ArrayList<String>();
			String filePath = gpsfile;
			InputStream is = new FileInputStream(filePath);
			reader = new BufferedReader(
					new InputStreamReader(is));

			// add each line in the file to the list
			String line = null;
			while ((line = reader.readLine()) != null) {
				data.add(line);
			}
			if (data.size() == 0)
				return null;
			// convert to a simple array so we can pass it to the AsyncTask
			String[] coordinates = new String[data.size()];
			data.toArray(coordinates);
			return coordinates;
		} catch (Exception e) {
			return null;
		}
		finally
		{
			FileUtil.closeReader(reader);
		}
	}

	private static float getDistance(double lat1, double lon1, double lat2,
			double lon2) {
		float results[] = new float[10];
		Location.distanceBetween(lat1, lon1, lat2, lon2, results);
		return results[0];
	}

	public static int compareAsDouble(String str1, String str2) {
		float v1 = Float.valueOf(str1);
		float v2 = Float.valueOf(str2);
		if (v1 > v2) {
			return 1;
		} else if (v1 < v2) {
			return -1;
		} else
			return 0;
	}

	public static void sortAsDouble(ArrayList<String> mylist) {
		String min = new String();
		int len = mylist.size();
		for (int i = 0; i < len; i++) {
			min = mylist.get(i);
			for (int j = i + 1; j < len; j++) {
				if (compareAsDouble(min, mylist.get(j)) == 1) {
					mylist.set(i, mylist.get(j));
					mylist.set(j, min);
					min = mylist.get(i);
				}
			}
		}
	}

	public static ArrayList<String> compareGPS(String gpsfile) {
		ArrayList<String> resultList = new ArrayList<String>();
		ArrayList<String> percentList = new ArrayList<String>();
		String[] data = getGPSData(gpsfile);
		if (data == null)
			return null;
		for (String str : data) {
			// 定义顺序：ss，gps
			String[] parts = str.split(",");
			if (parts.length != 4) {
				continue;
			}
			double lon = Float.valueOf(parts[2]);
			double lat = Float.valueOf(parts[3]);
			resultList.add(String.valueOf(getDistance(Float.valueOf(parts[1]),
					Float.valueOf(parts[0]), lat, lon)));
		}

		sortAsDouble(resultList);

		if (resultList.size() >= 4) {
			int n50 = (int) (resultList.size() * 0.5);
			int n68 = (int) (resultList.size() * 0.68);
			int n95 = (int) (resultList.size() * 0.95);
			resultList.add(resultList.get(n50 - 1));
			resultList.add(resultList.get(n68 - 1));
			resultList.add(resultList.get(n95 - 1));
		}
		String newfile = gpsfile + ".result";
		File f = new File(newfile);
		LogUtils.writeFilterLog(resultList, f, false);
		for (int i = 0; i < 10; i++) {
			int num = (int) (resultList.size() * (0.5 + i * 0.05));
			percentList.add(resultList.get(num - 1));
		}
		return percentList;
	}
}
