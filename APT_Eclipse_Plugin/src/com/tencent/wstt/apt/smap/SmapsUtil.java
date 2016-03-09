/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.smap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;


/**
 * 
 * @Description: smaps文件处理相关的工具类
 * @author: Administrator  
 * @date: 2014年1月1日 上午11:13:08
 */
public class SmapsUtil {
	
	public static final int HEAP_DALVIK = 0;
	public static final int HEAP_NATIVE = 1;
	public static final int HEAP_CURSOR = 2;
	public static final int HEAP_ASHMEM = 3;
	public static final int HEAP_UNKNOWN_DEV = 4;
	public static final int HEAP_SO = 5;
	public static final int HEAP_JAR = 6;
	public static final int HEAP_APK = 7;
	public static final int HEAP_TTF = 8;
	public static final int HEAP_DEX = 9;
	public static final int HEAP_UNKNOWN_MAP = 10;
	public static final int HEAP_UNKNOWN = 11;
	
	private static final String []MEM_NAMES = {"Dalvik", "Native", "Cursor", "Ashmem",
		"Other dev", ".so mmap", ".jar mmap", ".apk mmap", ".ttf mmap", ".dex mmmap", "Other mmap", "Unknown",};	
	
	public static final int NUM_HEAP = 12;
	public static final int NUM_CORE_HEAP = 3;
	
	/**
	 * 从smaps文件中读取数据，处理得到可以treeviewer可以显示的数据
	 * @param fileName
	 * @return
	 */
	public static Object getSmapsShowDataFromFile(String fileName)
	{
		/**
		 * 打开文件
		 */
		File file = new File(fileName);
		if(!file.exists())
		{
			//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "文件不存在" + fileName);
			return null;
		}
		FileReader fr = null;
		try {
				fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_CRASH] + "文件读取失败" + fileName);
			return null;
		}
		
		BufferedReader br = new BufferedReader(fr);
		
		
		/**
		 * 解析前初始化操作
		 */
		String line = "";
		
		SMAPSSourceDataItem rootNode = new SMAPSSourceDataItem();//根节点
		rootNode.setParent(null);
		SMAPSSourceDataItem totalNode = new SMAPSSourceDataItem("Total");//根节点
		rootNode.addChild(totalNode);
		
		SMAPSSourceDataItem []memDatas = new SMAPSSourceDataItem[NUM_HEAP];
		for(int i = 0; i < NUM_HEAP; i++)
		{
			memDatas[i] = new SMAPSSourceDataItem();
			memDatas[i].contents[SMAPSSourceDataItem.NAME] = MEM_NAMES[i];	
			totalNode.addChild(memDatas[i]);
		}
		
	    /**
	     * 只读取一块内存中的这三个数据
	     * TODO 后续可以读取其他的数据
	     */
	    int pss = 0;
	    int shared_dirty = 0;
	    int private_dirty = 0;
	      
	    /**
	     * 跳过没有内存快开始描述的内存数据
	     * 主要是为了避免有些情况出现的内存数据不完整的情况
	     */
	    boolean skip = false;
	    boolean done = false;
	    
	    
	    int prevHeap = HEAP_UNKNOWN;//上一内存块的类型
	    int curHeap = HEAP_UNKNOWN;//当前内存块的类型
	    
	    /**
	     *  内存块地址
	     */
	    long curStartAdr = 0;
	    long curEndAdr = 0;
	    long prevEndAdr = 0;

		try {
			if((line = br.readLine()) == null)
			{
				br.close();
				fr.close();
				//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "文件第一行读取失败" + fileName);
				return null;
			}
			
			while(!done)
			{
				/**
				 * 新内存块的开始；初始化变量
				 */
				prevHeap = curHeap;
				prevEndAdr = curEndAdr;
				curHeap = HEAP_UNKNOWN;
				skip = true;
				
				/**
				 * 判断内存块的开始是否完整
				 */
				String memBolckName = "NoneTag";
				String []columns = line.split(Constant.BLANK_SPLIT);
				if(columns != null && columns.length >= 5)
				{
					String adr = columns[0];
					String []adrs = adr.split("-");
					if(adrs != null && adrs.length == 2)
					{
						curStartAdr = Long.parseLong(adrs[0], 16);
						curEndAdr = Long.parseLong(adrs[1], 16);
						skip = false;
					}		
				}
				
				
				/**
				 * 解析内存块名字
				 */
				if(!skip)
				{
					for(int i = 0; i < columns.length; i++)
					{
						//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_DEBUG] + columns[i]);
					}
					if(columns.length == 5)
					{
						memBolckName = "NoneTag";
					}
					else if(columns.length > 5)
					{
						int nameIndex = line.indexOf(columns[5]);
						memBolckName = line.substring(nameIndex, line.length());
						//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_DEBUG] + "MemBlockName=" + memBolckName);
					}
					
					
					if(memBolckName.startsWith("[heap]"))
					{
						curHeap = HEAP_NATIVE;
					}
					else if(memBolckName.startsWith("/dev/ashmem/dalvik-"))
					{
						curHeap = HEAP_DALVIK;
					}
					else if(memBolckName.startsWith("/dev/ashmem/CursorWindow"))
					{
						curHeap = HEAP_CURSOR;
					}
					else if(memBolckName.startsWith("/dev/ashmem/"))
					{
						curHeap = HEAP_ASHMEM;
					}
					else if(memBolckName.startsWith("/dev/"))
					{
						curHeap = HEAP_UNKNOWN_DEV;
					}
					else if(memBolckName.endsWith(".so"))
					{
						curHeap = HEAP_SO;
					}
					else if(memBolckName.endsWith(".jar"))
					{
						curHeap = HEAP_JAR;
					}
					else if(memBolckName.endsWith(".apk"))
					{
						curHeap = HEAP_APK;
					}
					else if(memBolckName.endsWith(".ttf"))
					{
						curHeap = HEAP_TTF;
					}
					else if(memBolckName.endsWith(".dex"))
					{
						curHeap = HEAP_DEX;
					}
					else if(!memBolckName.equalsIgnoreCase("NoneTag") && memBolckName.length() > 0)
					{
						curHeap = HEAP_UNKNOWN_MAP;
					}
					//没有名字的内存块分成了两部分
					else if(curStartAdr == prevEndAdr && prevHeap == HEAP_SO)
					{
						//bss section of a shared library
						//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_DEBUG] + "MemBlockName=" + memBolckName + "[SO]");
						curHeap = HEAP_SO;
					}
					else
					{
						curHeap = HEAP_UNKNOWN;
						//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_DEBUG] + "MemBlockName=" + memBolckName + "[Unknown]");
					}
				}
				
				//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_DEBUG] + "HEAP_TYPE=" + curHeap);
				
				/**
				 * 解析内存块对应的各个内存类型的数据
				 */
				while(true)
				{
					if((line = br.readLine()) == null)
					{
						done = true;
						break;
					}
					
					String []dataColumns = line.split(Constant.BLANK_SPLIT);
					if(dataColumns != null && dataColumns.length == 3 && dataColumns[2].equalsIgnoreCase("kB"))
					{
						String memType = dataColumns[0];
						int size = Integer.parseInt(dataColumns[1]);
						
						if(memType.startsWith("Pss"))
						{
							pss = size;
						}
						else if(memType.startsWith("Shared_Dirty"))
						{
							shared_dirty = size;
						}
						else if(memType.startsWith("Private_Dirty"))
						{
							private_dirty = size;
						}
						//目前不采集其他类型的内存数据
					}
					else if(dataColumns != null && dataColumns.length >= 5 && dataColumns[0].charAt(8) == '-')
					{
						//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_DEBUG] + "smaps内存块tag行" + line);
						break;
					}
					else
					{
						//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "smaps其他数据行" + line);
					}
				}
				
				/**
				 * 上面取到的数据是符合要求的
				 */
				if(!skip)
				{					
					SMAPSSourceDataItem child = memDatas[curHeap].getChildByName(memBolckName);
					if(child == null)
					{
						child = new SMAPSSourceDataItem(memBolckName);
						memDatas[curHeap].addChild(child);
					}
					
					child.addChild(new SMAPSSourceDataItem(child.getChildrenNum()+"", pss+"", shared_dirty+"", private_dirty+""));
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
			//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_CRASH] + "文件关闭失败" + fileName);
			return null;
		}
		
		calcParentNodeValue(totalNode);
		
		return rootNode;
	}
	
	
	/**
	 * 求父节点value的值
	 *	TODO 由叶子到跟的树广度优先遍历（倒序） 
	 * @param root
	 */
	public static void calcParentNodeValue(SMAPSSourceDataItem root)
	{
		if(root != null && root.hasChildren())
		{
			int pss2 = 0;
			int sd2 = 0;
			int pd2 = 0;
			List<SMAPSSourceDataItem> memTypes = root.getChildren();
			for(int i = 0; i < memTypes.size(); i++)
			{
				SMAPSSourceDataItem memType = memTypes.get(i);//Dalvik、Native之类的
				int pss1 = 0;
				int sd1 = 0;
				int pd1 = 0;
				if(memType != null && memType.hasChildren())
				{
					List<SMAPSSourceDataItem> memDetailItems = memType.getChildren();
					for(int j = 0; j < memDetailItems.size(); j++)
					{
						SMAPSSourceDataItem memDetailItem = memDetailItems.get(j);///dev/ashmem之类的
						int pss = 0;
						int sd = 0;
						int pd = 0;
						if(memDetailItem != null && memDetailItem.hasChildren())
						{
							List<SMAPSSourceDataItem> memBlocks = memDetailItem.getChildren();
							for(int m = 0; m < memBlocks.size(); m++)
							{
								pss += Integer.parseInt(memBlocks.get(m).contents[SMAPSSourceDataItem.PSS]);
								sd += Integer.parseInt(memBlocks.get(m).contents[SMAPSSourceDataItem.SD]);
								pd += Integer.parseInt(memBlocks.get(m).contents[SMAPSSourceDataItem.PD]);
							}
						}
						memDetailItem.setValue(pss+"", sd+"", pd+"");
						pss1 += pss;
						sd1 += sd;
						pd1 += pd;
					}
					
				}
				memType.setValue(pss1+"", sd1+"", pd1+"");
				pss2 += pss1;
				sd2 += sd1;
				pd2 += pd1;
			}
			root.setValue(pss2+"", sd2+"", pd2+"");
		}
	}
	
	
	/**
	 * 求差值
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static SMAPSSourceDataItem getDiff(SMAPSSourceDataItem obj1, SMAPSSourceDataItem obj2)
	{
		SMAPSSourceDataItem root = null;
		if(isASmapsTree(obj1) && isASmapsTree(obj2))
		{		
			root = getDiffItem(obj1, obj2);	
			
			//计算12种内存
			for(int i = 0; i < NUM_HEAP; i++)
			{
				SMAPSSourceDataItem item = getDiffItem(obj1.getChildByName("Total").getChildByName(MEM_NAMES[i]), obj2.getChildByName("Total").getChildByName(MEM_NAMES[i]));
				root.getChildByName("Total").addChild(item);
			}
			return root;
		}
		//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "比较的两个smaps文件不全合法");
		return root;
	}
	
	
	/**
	 * 判断该树是否是一个合法的smaps树
	 * @param root
	 * @return
	 */
	public static boolean isASmapsTree(SMAPSSourceDataItem root)
	{
		if(root == null)
		{
			//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "smaps树为空");
			return false;
		}
		
		SMAPSSourceDataItem totalNode = root.getChildByName("Total");
		
		if(root.hasChildren() && root.getChildrenNum() == 1 && totalNode != null)
		{
			if(totalNode.hasChildren() && totalNode.getChildrenNum() == 12)
			{
				return true;
			}
			//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "smaps数内存类型节点存在问题");
		}
		//APTConsoleFactory.getInstance().APTPrint(Constant.APTCONSOLE_LOG_TAGS[Constant.APTCONSOLE_LOG_TAG_ERR] + "smaps数Total节点存在问题");
		return false;
	}
	/**
	 * 计算两个对象差值对象（仅仅计算到孩子这一层的差别）
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static SMAPSSourceDataItem getDiffItem(SMAPSSourceDataItem obj1, SMAPSSourceDataItem obj2)
	{
		SMAPSSourceDataItem node = new SMAPSSourceDataItem(obj1.contents[SMAPSSourceDataItem.NAME]);
		int pss = Integer.parseInt(obj1.contents[SMAPSSourceDataItem.PSS]) - Integer.parseInt(obj2.contents[SMAPSSourceDataItem.PSS]);
		int sd = Integer.parseInt(obj1.contents[SMAPSSourceDataItem.SD]) - Integer.parseInt(obj2.contents[SMAPSSourceDataItem.SD]);
		int pd = Integer.parseInt(obj1.contents[SMAPSSourceDataItem.PD]) - Integer.parseInt(obj2.contents[SMAPSSourceDataItem.PD ]);
		node.setValue(pss+"", sd+"", pd+"");
		
		List<SMAPSSourceDataItem> children1 = obj1.getChildren();
		List<SMAPSSourceDataItem> children2 = obj2.getChildren();
		
		List<String> names = new ArrayList<String>();
		if(children1 != null)
		{
			for(int i = 0; i < children1.size(); i++)
			{
				names.add(children1.get(i).contents[SMAPSSourceDataItem.NAME]);
			}
		}

		if(children2 != null)
		{
			for(int i = 0; i < children2.size(); i++)
			{
				if(!names.contains(children2.get(i).contents[SMAPSSourceDataItem.NAME]))
				{
					names.add(children2.get(i).contents[SMAPSSourceDataItem.NAME]);
				}
			}
		}

		
		for(int i = 0; i < names.size(); i++)
		{
			SMAPSSourceDataItem item = new SMAPSSourceDataItem(names.get(i));
			MemItem item1 = getValsByName(names.get(i), children1);
			MemItem item2 = getValsByName(names.get(i), children2);
			item.setValue((item1.vals[MemItem.PSS]-item2.vals[MemItem.PSS])+"", (item1.vals[MemItem.SD]-item2.vals[MemItem.SD])+"", (item1.vals[MemItem.PD]-item2.vals[MemItem.PD])+"");
			node.addChild(item);	
		}	
		
		return node;
	}
	
	
	/**
	 * 根据名字返回对应的内存值
	 * @param name
	 * @param sourceDatas
	 * @return
	 */
	private static MemItem getValsByName(String name, List<SMAPSSourceDataItem> sourceDatas)
	{
		MemItem res = new MemItem();
		if(sourceDatas == null)
		{
			return res;
		}
		
		for(int i = 0; i < sourceDatas.size(); i++)
		{
			if(name.equals(sourceDatas.get(i).contents[SMAPSSourceDataItem.NAME]))
			{
				int pss = Integer.parseInt(sourceDatas.get(i).contents[SMAPSSourceDataItem.PSS]);
				int sd = Integer.parseInt(sourceDatas.get(i).contents[SMAPSSourceDataItem.SD]);
				int pd = Integer.parseInt(sourceDatas.get(i).contents[SMAPSSourceDataItem.PD]);
				res.setVal(pss, sd, pd);
				return res;
			}
		}
		return res;
	}

}
