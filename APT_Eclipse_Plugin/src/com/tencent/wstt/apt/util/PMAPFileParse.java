/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;

/**
* @Description 解析pmap pid命令输出 
* @date 2013年11月10日 下午6:21:43 
*
 */
public class PMAPFileParse {

	private static final int STARTLINE_INDEX = 2;
	private static final int ITEM_COUNT = 7;
	private static final int PSS_INDEX = 2;
	private static final int MAPPING_INDEX = 6;
	
	
	public Map<String, Integer> parse(String file)
	{
		Map<String, Integer> result = new HashMap<String, Integer>();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			APTConsoleFactory.getInstance().APTPrint("PMAPFileParse.parse FileNotFoundException:" + file);
			return null;
		}
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		String lastLine = "";
		int lineCount = 0;
		try {
			while((line = br.readLine()) != null)
			{
				lineCount++;
				if(lineCount > STARTLINE_INDEX)
				{
					String []columns = line.split(Constant.BLANK_SPLIT);
					if(columns != null && columns.length >= ITEM_COUNT)
					{
						String mapping = "";
						for(int i = MAPPING_INDEX; i < columns.length; i++)
						{
							mapping += columns[i].trim();
						}
						String sizeStr = columns[PSS_INDEX].trim();
						int size = Integer.parseInt(sizeStr);
						if(result.containsKey(mapping))
						{
							int curValue = result.get(mapping) + size;
							result.put(mapping, curValue);
						}
						else
						{
							result.put(mapping, size);
						}
					}
				}
				lastLine = line;
			}
			
			String arrs[] = lastLine.split(Constant.BLANK_SPLIT);
			if(arrs != null && arrs.length == 5 && arrs[0].equalsIgnoreCase("total"))
			{
				String valueStr = arrs[PSS_INDEX];
				int value = Integer.parseInt(valueStr);
				result.put("total", value);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			APTConsoleFactory.getInstance().APTPrint("PMAPFileParse.parse readLine IOException:" + file);
			return null;
		}
		
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
			APTConsoleFactory.getInstance().APTPrint("PMAPFileParse.parse close IOException:" + file);
			return null;
		}

		return result;
	}
}
