/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.stubanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.stubanalysis.data.SourceDataItem;
import com.tencent.wstt.apt.stubanalysis.data.TopViewDataItem;

/**
* @Description 解析APT插桩log的工具类 
* @date 2013年11月10日 下午6:06:18 
*
 */
public class StubLogParseUtil {

	public static Object[][] getData(String fileName) {
		File file = null;
		if (fileName == null) {
			return null;
		}

		file = new File(fileName);
		if (!file.exists()) {
			return null;
		}

		LinkedList<SourceDataItem> cacheList = new LinkedList<SourceDataItem>();
		List<SourceDataItem> resultPairList = new ArrayList<SourceDataItem>();// 配对成功数据
		List<SourceDataItem> resultSingleList = new ArrayList<SourceDataItem>();// 配对失败数据
		BufferedReader br = null;

		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);

			long index = 0;
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				/**
				 * TODO这里需要对数据格式的有效性进行判断
				 */
				String[] ss = line.split("\\|");
				SourceDataItem curDataItem = new SourceDataItem();
				curDataItem.contents = ss;
				curDataItem.index = index++;
				/**
				 * 这里增加index的目的是为了保证数据的顺序跟原始log中数据一致；没有拿时间排列的原因是
				 * 极个别的情况会出现时间相同的数据行 另外一个 TODO：就是差值行的index设置 跟开始行一致还是跟结束行一致
				 * 本来是跟开始行一致的，但是感觉还是设置成跟结束行一致比较好，符合顺序；而且后面树化的
				 * 时候可以完全根据差值行来进行树化，而不需要结束行
				 */

				if (curDataItem.contents[SourceDataItem.SAMPLING_POSITION]
						.equals(SourceDataItem.SAMPLING_POSITION_START)) {
					cacheList.add(curDataItem);
				} else if (curDataItem.contents[SourceDataItem.SAMPLING_POSITION]
						.equals(SourceDataItem.SAMPLING_POSITION_END)) {

					SourceDataItem lastStartDataItem = findAndRemoveItem(
							cacheList, curDataItem.contents[SourceDataItem.SID]);
					// 没有对应start的情况，不做差值计算处理
					if (null == lastStartDataItem) {
						// 此时是只有结束的情况
						resultSingleList.add(curDataItem);
						continue;
					} else {
						// 弹栈
						resultPairList.add(lastStartDataItem);
						resultPairList.add(curDataItem);
					}

					// 新建差值数据行,差值行的头部数据跟开始行数据头部保持一致
					SourceDataItem reduceDataItem = new SourceDataItem();
					reduceDataItem.contents = new String[curDataItem.contents.length];
					System.arraycopy(lastStartDataItem.contents,
							SourceDataItem.TIME, reduceDataItem.contents,
							SourceDataItem.TIME, SourceDataItem.COLUMUNS_NUM);
					reduceDataItem.contents[SourceDataItem.SAMPLING_POSITION] = SourceDataItem.SAMPLING_POSITION_DIFFERENCE;

					long start = Long
							.parseLong(lastStartDataItem.contents[SourceDataItem.VALUE]);
					long end = Long
							.parseLong(curDataItem.contents[SourceDataItem.VALUE]);
					long reduce = end - start;
					reduceDataItem.contents[SourceDataItem.VALUE] = Long
							.toString(reduce);
					if (!curDataItem.contents[SourceDataItem.PID]
							.equals(lastStartDataItem.contents[SourceDataItem.PID])
							|| !curDataItem.contents[SourceDataItem.TID]
									.equals(lastStartDataItem.contents[SourceDataItem.TID])) {
						// 保证这种节点不存在子节点
						printDataItem("跨开始", lastStartDataItem);
						printDataItem("跨结束", curDataItem);
						reduceDataItem.index = lastStartDataItem.index;
					} else {
						reduceDataItem.index = curDataItem.index;
					}

					resultPairList.add(reduceDataItem);
				}
			}

			// 将只有开始没有配对成功的数据加入到resultSingle中
			for (SourceDataItem o : cacheList) {
				resultSingleList.add(o);
			}

			Collections.sort(resultPairList, new Comparator<SourceDataItem>() {

				@Override
				public int compare(SourceDataItem arg0, SourceDataItem arg1) {
					// TODO Auto-generated method stub
					return (int) (((SourceDataItem) arg0).index - ((SourceDataItem) arg1).index);
				}
			});

			Collections.sort(resultSingleList,
					new Comparator<SourceDataItem>() {

						@Override
						public int compare(SourceDataItem arg0,
								SourceDataItem arg1) {
							// TODO Auto-generated method stub
							return (int) (((SourceDataItem) arg0).index - ((SourceDataItem) arg1).index);
						}
					});

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				return null;
			}
		}

		return new Object[][] { resultPairList.toArray(),
				resultSingleList.toArray() };

	}

	/**
	 * 查找最新的指定sid的DataItem，查找并进行删除
	 * 
	 * @param list
	 * @param sid
	 * @return
	 */
	private static SourceDataItem findAndRemoveItem(
			LinkedList<SourceDataItem> list, String sid) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		int len = list.size();
		for (int i = len - 1; i >= 0; i--) {
			if (list.get(i).contents[SourceDataItem.SID].equals(sid)) {
				SourceDataItem result = list.get(i);
				list.remove(i);
				return result;
			}
		}
		return null;
	}

	private static void printDataItem(String tag, SourceDataItem item) {
		String str = tag + "_";
		for (int i = 0; i < item.contents.length; i++) {
			str += item.contents[i] + "|";
		}
		System.out.println(str);
	}

	/**
	 * 解析原始数据，返回统计数据
	 * 
	 * @param objs
	 * @return
	 */
	public static Object[] statisticsData(Object objs[]) {
		Map<String, TopViewDataItem> result = new HashMap<String, TopViewDataItem>();

		for (Object o : objs) {
			SourceDataItem timeInfo = (SourceDataItem) o;

			// 过滤，留下差值项
			if (!timeInfo.contents[SourceDataItem.SAMPLING_POSITION]
					.equals(SourceDataItem.SAMPLING_POSITION_DIFFERENCE)) {
				continue;
			}

			String curTagName = timeInfo.contents[SourceDataItem.TAG];
			// 存在，更新数据
			if (result.containsKey(curTagName)) {
				TopViewDataItem data = result.get(curTagName);
				long count = Long
						.parseLong(data.contents[TopViewDataItem.COUNT]) + 1;
				data.contents[TopViewDataItem.COUNT] = count + "";

				long curValue = Long
						.parseLong(timeInfo.contents[SourceDataItem.VALUE]);
				long maxValue = Long
						.parseLong(data.contents[TopViewDataItem.MAXVALUE]);
				long totalValue = Long
						.parseLong(data.contents[TopViewDataItem.TOTAL]);

				if (curValue > maxValue) {
					data.contents[TopViewDataItem.MAXVALUE] = curValue + "";
				}

				data.contents[TopViewDataItem.TOTAL] = (curValue + totalValue)
						+ "";

				result.put(curTagName, data);
			}
			// 不存在，直接添加
			else {
				TopViewDataItem data = new TopViewDataItem();
				data.contents[TopViewDataItem.TAG] = curTagName;
				data.contents[TopViewDataItem.COUNT] = "1";

				// TODO 查看此处是否存在问题
				data.contents[TopViewDataItem.MAXVALUE] = timeInfo.contents[SourceDataItem.VALUE];
				data.contents[TopViewDataItem.TOTAL] = timeInfo.contents[SourceDataItem.VALUE];

				result.put(curTagName, data);
			}

		}

		// 计算平均值
		List<TopViewDataItem> temp = new ArrayList<TopViewDataItem>(
				result.values());
		for (TopViewDataItem item : temp) {
			long totalValue = Long
					.parseLong(item.contents[TopViewDataItem.TOTAL]);
			long count = Long.parseLong(item.contents[TopViewDataItem.COUNT]);
			long avgValue = totalValue / count;
			item.contents[TopViewDataItem.AVGVALUE] = avgValue + "";
		}

		Collections.sort(temp, new Comparator<TopViewDataItem>() {

			@Override
			public int compare(TopViewDataItem o1, TopViewDataItem o2) {
				// 默认按照PSSTotal降序排列
				return (int) (Long
						.parseLong(o1.contents[TopViewDataItem.TOTAL]) - Long
						.parseLong(o2.contents[TopViewDataItem.TOTAL]));
			}
		});

		return temp.toArray();
	}

	public static Object parseSourceDataAsTree(Object objs[]) {
		// 按照线程id进行分组
		Map<String, List<SourceDataItem>> cacheMap = new HashMap<String, List<SourceDataItem>>();

		// 初始化根节点
		SourceDataItem root = new SourceDataItem();
		root.contents = new String[] { SourceDataItem.NO_SUPPORT_VALUE,
				SourceDataItem.NO_SUPPORT_VALUE,
				SourceDataItem.NO_SUPPORT_VALUE,
				SourceDataItem.NO_SUPPORT_VALUE,
				SourceDataItem.NO_SUPPORT_VALUE, "根节点",
				SourceDataItem.NO_SUPPORT_VALUE };
		// root.otherValues = new String[]{Utils.NO_SUPPORT_VALUE};
		root.setParent(null);

		redefineTISFilter(objs);

		for (Object o : objs) {
			SourceDataItem dataItem = (SourceDataItem) o;
			String tid = dataItem.contents[SourceDataItem.TID];
			List<SourceDataItem> threadDataItemList = cacheMap.get(tid);
			if (threadDataItemList == null) {
				threadDataItemList = new ArrayList<SourceDataItem>();
				cacheMap.put(tid, threadDataItemList);
			}
			threadDataItemList.add(dataItem);
		}

		// 树化
		for (Entry<String, List<SourceDataItem>> entry : cacheMap.entrySet()) {
			List<SourceDataItem> curThreadDataItemList = entry.getValue();
			// 其实这里可以完全用严格意义的栈来代替
			LinkedList<SourceDataItem> cachedStack = new LinkedList<SourceDataItem>();

			// 初始化线程根节点
			SourceDataItem threadRoot = new SourceDataItem();
			threadRoot.contents = new String[] {
					SourceDataItem.NO_SUPPORT_VALUE,
					curThreadDataItemList.get(0).contents[SourceDataItem.PID],
					curThreadDataItemList.get(0).contents[SourceDataItem.TID],
					SourceDataItem.NO_SUPPORT_VALUE,
					SourceDataItem.NO_SUPPORT_VALUE,
					"线程"
							+ curThreadDataItemList.get(0).contents[SourceDataItem.TID],
					SourceDataItem.NO_SUPPORT_VALUE };
			// threadRoot.otherValues = new String[]{Utils.NO_SUPPORT_VALUE};
			threadRoot.setParent(root);
			root.addChild(threadRoot);

			// 对每个线程的DataItem进行树化
			for (SourceDataItem curDataItem : curThreadDataItemList) {
				String curSid = curDataItem.contents[SourceDataItem.SID];

				// 如果堆栈为空，说明是树的顶层节点的start项，入栈
				// TODO 这里是否要做一些处理，不配对的情况
				// 解决：之前进行了过滤操作
				if (curDataItem.contents[SourceDataItem.SAMPLING_POSITION]
						.equals(SourceDataItem.SAMPLING_POSITION_END)) {
					// 仅仅利用开始行和差值行，来进行树化，因为差值行的index和结束行的一致
					continue;
				}

				if (cachedStack.isEmpty()) {
					cachedStack.add(curDataItem);
					if (!curDataItem.contents[SourceDataItem.SAMPLING_POSITION]
							.equals(SourceDataItem.SAMPLING_POSITION_START)) {
						// TODO:
						System.out.println("这不可能，栈空的时候，来的元素不可能是差值元素");
					}
				}
				// 如果栈不为空，则和栈头的DataItem对比
				else {
					SourceDataItem cacahedHead = cachedStack.getFirst();
					String cachedHeadSid = cacahedHead.contents[SourceDataItem.SID];

					if (curSid.equals(cachedHeadSid)) {
						if (curDataItem.contents[SourceDataItem.SAMPLING_POSITION]
								.equals(SourceDataItem.SAMPLING_POSITION_START)) {
							APTConsoleFactory.getInstance().APTPrint(
									"当前元素的sid跟栈头元素sid相同，但是当前元素却是开始数据行");
						} else {
							cachedStack.removeFirst();
							curDataItem.children = cacahedHead.children;
							if (curDataItem.hasChildren()) {
								long otherValue = 0;
								for (SourceDataItem m : curDataItem.children) {
									m.setParent(curDataItem);
									otherValue += Long
											.parseLong(m.contents[SourceDataItem.VALUE]);
								}
								// TODO 更改othervalue
								otherValue = Long
										.parseLong(curDataItem.contents[SourceDataItem.VALUE])
										- otherValue;
								// curDataItem.otherValues = new
								// String[]{otherValue + ""};
							}

							if (!cachedStack.isEmpty()) {
								cachedStack.getFirst().addChild(curDataItem);
							} else {
								threadRoot.addChild(curDataItem);
								curDataItem.setParent(threadRoot);
							}
						}
					} else {
						if (curDataItem.contents[SourceDataItem.SAMPLING_POSITION]
								.equals(SourceDataItem.SAMPLING_POSITION_START)) {
							cachedStack.addFirst(curDataItem);
						} else {
							printDataItem("栈头", cacahedHead);
							printDataItem("当前元素", curDataItem);
						}
					}
				}

			}

			// 计算线程节点
			if (threadRoot.hasChildren()) {

				long sumValue = 0;
				for (SourceDataItem m : threadRoot.children) {
					sumValue += Long
							.parseLong(m.contents[SourceDataItem.VALUE]);
				}
				threadRoot.contents[SourceDataItem.VALUE] = sumValue + "";
			}

		}

		// root内部要按线程号排序
		Collections.sort(root.children, new Comparator<SourceDataItem>() {

			@Override
			public int compare(SourceDataItem m1, SourceDataItem m2) {

				return m1.contents[SourceDataItem.TID]
						.compareTo(m2.contents[SourceDataItem.TID]);
			}
		});
		return root;
	}

	/**
	 * 对成对的数据，进行处理 （1）应对多进程的情况，将TID=PID+TID（因为不同PID中的TID有可能相同）
	 * （2）应对开始和结束跨进称和跨线程的情况，树化的事将结束数据的TID和PID设置成和开始数据相同
	 * （3）不可以用严格意义的栈，因为存在跨线程和快进程的情况
	 * 
	 * @param sourc
	 */
	private static void redefineTISFilter(Object[] sourc) {
		for (Object item : sourc) {
			/**
			 * 更改TID=PID+TID
			 */
			SourceDataItem curItem = (SourceDataItem) item;
			curItem.contents[SourceDataItem.TID] = curItem.contents[SourceDataItem.PID]
					+ ":" + curItem.contents[SourceDataItem.TID];
		}
	}

}
