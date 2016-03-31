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
package com.tencent.wstt.gt.plugin.octopus;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.tencent.wstt.gt.api.utils.Env;

public class SavedGWDataHelper {
	public static FileFilter folderFilter = new FileFilter(){
		@Override
		public boolean accept(File f) {
			if (f != null && f.isDirectory())
			{
				return true;
			}
			return false;
		}};

	public static Comparator<File> comparator = new Comparator<File>(){
		@Override
		public int compare(File object1, File object2) {
			return Long.valueOf(object2.lastModified()).compareTo(object1.lastModified());
		}};

	public static File getGWDirectory() {
		File gwDir = Env.ROOT_GW_FOLDER;
		
		if (!gwDir.exists()) {
			gwDir.mkdir();
		}
		return gwDir;
	}

	public static List<File> getGWFolders()
	{
		File gwDir = getGWDirectory();
		
		File[] filesParentArray = gwDir.listFiles(folderFilter);
		
		if (filesParentArray == null) {
			return Collections.emptyList();
		}

		List<File> result = new ArrayList<File>();
		for (File folder1 : filesParentArray)
		{
			File[] folder2s = folder1.listFiles(folderFilter);
			
			List<File> folder2List = new ArrayList<File>(Arrays.asList(folder2s));

			Collections.sort(folder2List, comparator);
			
			List<File> invalidFolder2List = new ArrayList<File>();
			for (File folder2 : folder2List)
			{
				File[] leafs = folder2.listFiles(folderFilter);
				if (leafs == null || leafs.length == 0)
				{
					invalidFolder2List.add(folder2);
				}
			}
			folder2List.removeAll(invalidFolder2List);

			for (File folder2 : folder2List)
			{
				File[] leafs = folder2.listFiles(folderFilter);
				List<File> leafList = new ArrayList<File>(Arrays.asList(leafs));
				Collections.sort(leafList, comparator);
				
				result.add(folder2);
				result.addAll(leafList);
			}
		}

		return result;
	}
}
