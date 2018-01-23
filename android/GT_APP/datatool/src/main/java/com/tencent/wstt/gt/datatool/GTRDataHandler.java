package com.tencent.wstt.gt.datatool;

public class GTRDataHandler {

//	/********************************************************************************************/
//	/**************** 公共函数 *****************************************************/
//	/********************************************************************************************/
//
//
//	/* 获取指定时间区域的sm值 */
//	public static long getSM(ArrayList<Long> frames, long startTime, long endTime) {
//		if (startTime < frames.get(0)) {
//			startTime = frames.get(0);
//		}
//		if (endTime > frames.get(frames.size() - 1)) {
//			endTime = frames.get(frames.size() - 1);
//		}
//		int thisSM = 0;
//		for (Long fLong : frames) {
//			if (fLong > startTime && fLong <= endTime) {
//				thisSM++;
//			}
//		}
//
//		return (long) (thisSM / ((double) (endTime - startTime) / 1000));
//	}
//
//	private static String toRealTime(long time) {
//		String realTime = "";
//		if (time >= 86400000) {
//			long day = time / 86400000;
//			realTime = realTime + day + "天";
//			time = time - day * 86400000;
//		}
//		if (time >= 3600000) {
//			long hour = time / 3600000;
//			realTime = realTime + hour + "时";
//			time = time - hour * 3600000;
//		}
//		if (time >= 60000) {
//			long min = time / 60000;
//			realTime = realTime + min + "分";
//			time = time - min * 60000;
//		}
//		double sec = (double) time / 1000;
//		realTime = realTime + sec + "秒";
//		return realTime;
//	}
//
//	/********************************************************************************************/
//	/**************** 基础性能页的数据 *****************************************************/
//	/********************************************************************************************/
//	/* baseSummaryTable */
//	public static String get_baseSummaryTable_data(ArrayList<NormalInfo> normalInfos, ArrayList<Long> frames) {
//
//		ArrayList<SMInfo> smInfos = getSMInfos(frames);
//
//		double cpuSum = 0;
//		double cpuMax = 0;
//		double memorySum = 0;
//		double memoryMax = 0;
//		double flowSum = 0;
//		for (NormalInfo normalInfo : normalInfos) {
//			cpuSum = cpuSum + normalInfo.cpu;
//			if (normalInfo.cpu > cpuMax) {
//				cpuMax = normalInfo.cpu;
//			}
//
//			memorySum = memorySum + normalInfo.memory;
//			if (normalInfo.memory > memoryMax) {
//				memoryMax = normalInfo.memory;
//			}
//
//			if (normalInfo.flowUpload + normalInfo.flowDownload > flowSum) {
//				flowSum = normalInfo.flowUpload + normalInfo.flowDownload;
//			}
//
//		}
//		double smSum = 0;
//		double smMax = 0;
//		double smMin = 60;
//		for (SMInfo smInfo : smInfos) {
//			smSum = smSum + smInfo.sm;
//			if (smInfo.sm > smMax) {
//				smMax = smInfo.sm;
//			}
//			if (smInfo.sm < smMin) {
//				smMin = smInfo.sm;
//			}
//
//		}
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("cpuAverage", cpuSum / normalInfos.size());
//		jsonObject.put("cpuMax", cpuMax);
//		jsonObject.put("memoryAverage", memorySum / normalInfos.size());
//		jsonObject.put("memoryMax", memoryMax);
//		jsonObject.put("flowSum", flowSum);
//		jsonObject.put("flowSpeed", flowSum / normalInfos.size());
//		jsonObject.put("smAverage", smSum / smInfos.size());
//		jsonObject.put("smMax", smMax);
//		jsonObject.put("smMin", smMin);
//
//		return jsonObject.toJSONString();
//	}
//
//	private static ArrayList<SMInfo> getSMInfos(ArrayList<Long> frames) {
//		ArrayList<SMInfo> smInfos = new ArrayList<>();
//		long thisTime = 0;
//		int thisSM = 0;
//		for (Long fLong : frames) {
//			if (thisTime == 0) {
//				thisTime = fLong;
//				thisSM = 0;
//			}
//
//			if (fLong - thisTime < 1000) {
//				thisSM++;
//			} else {
//				SMInfo smInfo = new SMInfo();
//				smInfo.time = thisTime + 500;
//				smInfo.sm = thisSM;
//				smInfos.add(smInfo);
//				thisTime = fLong;
//				thisSM = 0;
//			}
//		}
//		return smInfos;
//	}
//
//
//
//
//	/* baseSummaryChart */
//	public static String get_baseSummaryChart_data(ArrayList<NormalInfo> normalInfos, ArrayList<Long> frames,
//			AppInfo appInfo) {
//
//		ArrayList<Long> xAxis = new ArrayList<>();
//		ArrayList<Long> cpuData = new ArrayList<>();
//		ArrayList<Long> memoryData = new ArrayList<>();
//		ArrayList<Long> flowData = new ArrayList<>();
//		ArrayList<Long> smData = new ArrayList<>();
//		ArrayList<String> describeData = new ArrayList<>();
//
//		long lastTime = 0;
//		for (NormalInfo normalInfo : normalInfos) {
//			long cpu = normalInfo.cpu;
//			long memory = normalInfo.memory;
//			long flow = normalInfo.flowUpload + normalInfo.flowDownload;
//			long sm = getSM(frames, lastTime, normalInfo.time);
//			String describe = "";
//			{
//				StringBuilder describeBuilder = new StringBuilder();
//				long time = normalInfo.time - appInfo.startTestTime;
//				describeBuilder.append("当前时间:" + toRealTime(time));
//				describeBuilder.append(" <br />");
//				describeBuilder.append("sm:" + sm + "帧/s");
//				describeBuilder.append(" <br />");
//				describeBuilder.append("cpu:" + normalInfo.cpu + "%");
//				describeBuilder.append(" <br />");
//				describeBuilder.append("memory:" + normalInfo.memory + "MB");
//				describeBuilder.append(" <br />");
//				describeBuilder.append("flow:" + (normalInfo.flowUpload + normalInfo.flowDownload) + "KB");
//				describe = describeBuilder.toString();
//
//
//			}
//			xAxis.add(normalInfo.time);
//			cpuData.add(cpu);
//			memoryData.add(memory);
//			flowData.add(flow);
//			smData.add(sm);
//			describeData.add(describe);
//
//			lastTime = normalInfo.time;
//		}
//
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("xAxis", xAxis);
//		jsonObject.put("cpuData", cpuData);
//		jsonObject.put("memoryData", memoryData);
//		jsonObject.put("flowData", flowData);
//		jsonObject.put("smData", smData);
//		jsonObject.put("describeData", describeData);
//
//
//
//		return jsonObject.toJSONString();
//	}
//
//	/********************************************************************************************/
//	/**************** 卡顿检测页的数据 *****************************************************/
//	/********************************************************************************************/
//
//	public static String get_blockLowSMTable_data(ArrayList<BlockInfo> lowSMBlockInfos, ArrayList<Long> frames,
//			AppInfo appInfo) {
//
//		JSONArray blockTableDataObject = new JSONArray();
//		for (int i = 0; i < lowSMBlockInfos.size(); i++) {
//			BlockInfo blockInfo = lowSMBlockInfos.get(i);
//			JSONObject blockJsonObject = new JSONObject();
//			blockJsonObject.put("blockId", i);
//			blockJsonObject.put("startTime", blockInfo.startTime);
//			blockJsonObject.put("endTime", blockInfo.endTime);
//			blockJsonObject.put("blockTime", blockInfo.endTime - blockInfo.startTime);
//			blockJsonObject.put("averageSm", getSM(frames, blockInfo.startTime - 500, blockInfo.endTime + 500));
//			blockJsonObject.put("page", "页面定位");
//			{
//				JSONObject areaChartDataJSONObject = new JSONObject();
//				ArrayList<Long> xAxis = new ArrayList<>();
//				ArrayList<Long> frameData = new ArrayList<>();
//				for (long x = blockInfo.startTime; x <= blockInfo.endTime; x++) {
//					xAxis.add(x);
//				}
//				for (long x = blockInfo.startTime; x <= blockInfo.endTime; x++) {
//					long frameStartTime = 0;
//					long frameEndTime = 0;
//					for (Long fLong : frames) {
//						if (fLong <= x && fLong > frameStartTime) {
//							frameStartTime = fLong;
//						}
//						if (fLong > x && (frameEndTime == 0 || fLong < frameEndTime)) {
//							frameEndTime = fLong;
//						}
//					}
//					frameData.add(frameEndTime - frameStartTime);
//				}
//				areaChartDataJSONObject.put("xAxis", xAxis);
//				areaChartDataJSONObject.put("frameData", frameData);
//				blockJsonObject.put("areaChartData", areaChartDataJSONObject);
//			}
//			{
//				JSONObject codeChartDataJSONObject = new JSONObject();
//				ArrayList<String[]> codeList = getCodeList(blockInfo.stackInfoList);
//				codeChartDataJSONObject.put("deep", codeList.size());
//				codeChartDataJSONObject.put("data", getCodeChartData(codeList));
//				blockJsonObject.put("codeChartData", codeChartDataJSONObject);
//			}
//
//			blockTableDataObject.add(blockJsonObject);
//		}
//		return blockTableDataObject.toJSONString();
//
//	}
//
//	public static String get_blockBigTable_data(ArrayList<BlockInfo> bigBlockInfos, ArrayList<Long> frames,
//			AppInfo appInfo) {
//
//		JSONArray blockTableDataObject = new JSONArray();
//		for (int i = 0; i < bigBlockInfos.size(); i++) {
//			BlockInfo blockInfo = bigBlockInfos.get(i);
//			JSONObject blockJsonObject = new JSONObject();
//			blockJsonObject.put("blockId", i);
//			blockJsonObject.put("startTime", blockInfo.startTime);
//			blockJsonObject.put("endTime", blockInfo.endTime);
//			blockJsonObject.put("blockTime", blockInfo.endTime - blockInfo.startTime);
//			blockJsonObject.put("averageSm", getSM(frames, blockInfo.startTime - 500, blockInfo.endTime + 500));
//			blockJsonObject.put("code", getEffectiveCode(blockInfo.stackInfoList));
//
//			{
//				JSONObject areaChartDataJSONObject = new JSONObject();
//				ArrayList<Long> xAxis = new ArrayList<>();
//				ArrayList<Long> frameData = new ArrayList<>();
//				for (long x = blockInfo.startTime; x <= blockInfo.endTime; x++) {
//					xAxis.add(x);
//				}
//				for (long x = blockInfo.startTime; x <= blockInfo.endTime; x++) {
//					long frameStartTime = 0;
//					long frameEndTime = 0;
//					for (Long fLong : frames) {
//						if (fLong <= x && fLong > frameStartTime) {
//							frameStartTime = fLong;
//						}
//						if (fLong > x && (frameEndTime == 0 || fLong < frameEndTime)) {
//							frameEndTime = fLong;
//						}
//					}
//					frameData.add(frameEndTime - frameStartTime);
//				}
//				areaChartDataJSONObject.put("xAxis", xAxis);
//				areaChartDataJSONObject.put("frameData", frameData);
//				blockJsonObject.put("areaChartData", areaChartDataJSONObject);
//			}
//			{
//				JSONObject codeChartDataJSONObject = new JSONObject();
//				ArrayList<String[]> codeList = getCodeList(blockInfo.stackInfoList);
//				codeChartDataJSONObject.put("deep", codeList.size());
//
//				codeChartDataJSONObject.put("data", getCodeChartData(codeList));
//
//				blockJsonObject.put("codeChartData", codeChartDataJSONObject);
//
//			}
//
//			blockTableDataObject.add(blockJsonObject);
//		}
//		return blockTableDataObject.toJSONString();
//
//	}
//
//	/* 对stack进行代码定位 */
//	private static String getEffectiveCode(String stack) {
//		String effectiveCode = null;
//		String[] lineArray = stack.split("\n");
//		for (int i = lineArray.length - 2; i >= 0; i--) {
//			if (!lineArray[i].startsWith("com.android.internal.") && !lineArray[i].startsWith("android.os.")
//					&& !lineArray[i].startsWith("android.view.") && !lineArray[i].startsWith("android.widget.")
//					&& !lineArray[i].startsWith("android.support.") && !lineArray[i].startsWith("android.app.")
//					&& !lineArray[i].startsWith("java.io.") && !lineArray[i].startsWith("libcore.io.")
//					&& !lineArray[i].startsWith("java.lang.") && !lineArray[i].startsWith("java.util.")
//					&& !lineArray[i].startsWith("sun.misc.Unsafe.") && !lineArray[i].startsWith("com.kunpeng.")
//					&& !lineArray[i].startsWith("com.matt.") && !lineArray[i].startsWith("com.tencent.bugly.")
//					&& !lineArray[i].startsWith("com.utest.pdm.")) {
//				effectiveCode = lineArray[i];
//				break;
//			}
//		}
//		if (effectiveCode == null) {
//			if (lineArray.length > 0) {
//				effectiveCode = lineArray[0];
//			} else {
//				effectiveCode = "";
//			}
//		}
//		return effectiveCode;
//	}
//
//	private static String getEffectiveCode(ArrayList<StackInfo> stackInfos) {
//		HashMap<String, Integer> effectiveCodes = new HashMap<>();
//		for (StackInfo stackInfo : stackInfos) {
//			String effectiveCode = getEffectiveCode(stackInfo.stack);// Json转换
//			if (effectiveCode.equals("")) {
//				continue;
//			}
//			Integer number = effectiveCodes.get(effectiveCode);
//			if (number == null) {
//				effectiveCodes.put(effectiveCode, 1);
//			} else {
//				effectiveCodes.put(effectiveCode, number + 1);
//			}
//		}
//		String mostEffectiveCode = "";
//		int mostNumber = 0;
//		for (String effectiveCode : effectiveCodes.keySet()) {
//			if (effectiveCodes.get(effectiveCode) > mostNumber) {
//				mostEffectiveCode = effectiveCode;
//				mostNumber = effectiveCodes.get(effectiveCode);
//			}
//		}
//		return mostEffectiveCode;
//	}
//
//	/* 获取stack的甘特图数据 */
//	private static ArrayList<String[]> getCodeList(ArrayList<StackInfo> stackInfos) {
//		// 遍历所有代码，映射到codeList表中
//		ArrayList<String[]> codeList = new ArrayList<String[]>();
//		for (int i = 0; i < stackInfos.size(); i++) {
//			String[] lineArray = stackInfos.get(i).stack.split("\n");
//			for (int k = 0; k < lineArray.length; k++) {
//				if (k >= codeList.size()) {
//					codeList.add(new String[stackInfos.size()]);
//				}
//				codeList.get(k)[i] = lineArray[k];
//			}
//		}
//		return codeList;
//	}
//
//	private static JSONArray getCodeChartData(ArrayList<String[]> codeList) {
//		// 从表格中解析数据，形成表格数据
//		JSONArray dataArray = new JSONArray();
//		for (int i = 0; i < codeList.size(); i++) {
//			JSONObject nowBlock = null;
//			for (String s : codeList.get(i)) {
//				if (s == null) {
//					s = "";
//				}
//				// 第一次：
//				if (nowBlock == null) {
//					nowBlock = new JSONObject();
//					if (s.equals("")) {
//						nowBlock.put("name", "空位符");
//						nowBlock.put("type", "bar");
//						nowBlock.put("stack", "总量");
//						nowBlock.put("code", "");
//						nowBlock.put("time", 0);
//					} else {
//						nowBlock.put("name", "代码块");
//						nowBlock.put("type", "bar");
//						nowBlock.put("stack", "总量");
//						nowBlock.put("code", s);
//						nowBlock.put("time", 0);
//					}
//				}
//				// 每次：
//				if (s.equals((String) nowBlock.get("code"))) {
//					nowBlock.put("time", 30 + (int) nowBlock.get("time"));
//				} else {
//					// data:
//					{
//						long[] dataAAAAA = new long[codeList.size()];
//						dataAAAAA[i] = (int) nowBlock.get("time");
//						nowBlock.put("data", dataAAAAA);
//					}
//					dataArray.add(nowBlock);
//					// 新的代码
//					nowBlock = new JSONObject();
//					if (s.equals("")) {
//						nowBlock.put("name", "空位符");
//						nowBlock.put("type", "bar");
//						nowBlock.put("stack", "总量");
//						nowBlock.put("code", "");
//						nowBlock.put("time", 30);
//					} else {
//						nowBlock.put("name", "代码块");
//						nowBlock.put("type", "bar");
//						nowBlock.put("stack", "总量");
//						nowBlock.put("code", s);
//						nowBlock.put("time", 30);
//					}
//				}
//			}
//			if (nowBlock != null && i > 0) {
//				long[] dataAAAAA = new long[codeList.size()];
//				dataAAAAA[i - 1] = (int) nowBlock.get("time");
//				nowBlock.put("data", dataAAAAA);
//				dataArray.add(nowBlock);
//			}
//
//		}
//		return dataArray;
//
//	}
//
//	/********************************************************************************************/
//	/**************** 页面测速页的数据 *****************************************************/
//	/********************************************************************************************/
//
//	public static String get_pageLoadOverTable_data(ArrayList<PageLoadInfo> pageLoadInfos, ArrayList<Long> frames,
//			AppInfo appInfo) {
//
//		// 寻找超时Activty，对应加载时长最长的页面
//		HashMap<String, PageLoadInfo> activityMap = new HashMap<>();
//		for (PageLoadInfo pageLoadInfo : pageLoadInfos) {
//			long loadTime = getPageLoadStartFinishTime(pageLoadInfo) - getPageLoadStartTime(pageLoadInfo);
//			if (loadTime > 300) {
//				PageLoadInfo pageLoadInfoInActivityMap = activityMap.get(pageLoadInfo.activityClassName);
//				if (pageLoadInfoInActivityMap == null) {
//					activityMap.put(pageLoadInfo.activityClassName, pageLoadInfo);
//				} else {
//					long oldLoadTime = getPageLoadStartFinishTime(pageLoadInfoInActivityMap)
//							- getPageLoadStartTime(pageLoadInfoInActivityMap);
//					if (oldLoadTime > loadTime) {
//						activityMap.put(pageLoadInfo.activityClassName, pageLoadInfo);
//					}
//				}
//			}
//		}
//		// 分析页面加载数据
//		JSONArray pageLoadAllTable_dataJSONArray = new JSONArray();
//		for (String key : activityMap.keySet()) {
//			PageLoadInfo pageLoadInfo = activityMap.get(key);
//
//			long startTime = getPageLoadStartTime(pageLoadInfo);
//			long startFinishTime = getPageLoadStartFinishTime(pageLoadInfo);
//			long endTime = getPageLoadEndTime(pageLoadInfo);
//			long page3SM = getSM(frames, startFinishTime,
//					(endTime > startFinishTime + 3000) ? startFinishTime + 3000 : endTime);
//			long page30SM = getSM(frames, startFinishTime,
//					(endTime > startFinishTime + 30000) ? startFinishTime + 30000 : endTime);
//
//			JSONObject pageLoadJSONObject = new JSONObject();
//			pageLoadJSONObject.put("startOrderId", pageLoadInfo.startOrderId);
//			pageLoadJSONObject.put("activityClassName", pageLoadInfo.activityClassName);
//			pageLoadJSONObject.put("cold_hot", isPageLoadCold(pageLoadInfo) ? "冷启动" : "热启动");
//			pageLoadJSONObject.put("startTime", startTime);
//			pageLoadJSONObject.put("startFinishTime", startFinishTime);
//			pageLoadJSONObject.put("endTime", endTime);
//			pageLoadJSONObject.put("loadTime", startFinishTime - startTime);
//			pageLoadJSONObject.put("page3SM", page3SM);
//			pageLoadJSONObject.put("page30SM", page30SM);
//
//			pageLoadAllTable_dataJSONArray.add(pageLoadJSONObject);
//		}
//
//		return pageLoadAllTable_dataJSONArray.toJSONString();
//	}
//
//	public static String get_pageLoadAllTable_data(ArrayList<PageLoadInfo> pageLoadInfos, ArrayList<Long> frames,
//			AppInfo appInfo) {
//
//		JSONArray pageLoadOverTable_dataJSONArray = new JSONArray();
//
//		for (PageLoadInfo pageLoadInfo : pageLoadInfos) {
//
//			long startTime = getPageLoadStartTime(pageLoadInfo);
//			long startFinishTime = getPageLoadStartFinishTime(pageLoadInfo);
//			long endTime = getPageLoadEndTime(pageLoadInfo);
//			long page3SM = getSM(frames, startFinishTime,
//					(endTime > startFinishTime + 3000) ? startFinishTime + 3000 : endTime);
//			long page30SM = getSM(frames, startFinishTime,
//					(endTime > startFinishTime + 30000) ? startFinishTime + 30000 : endTime);
//
//			JSONObject pageLoadJSONObject = new JSONObject();
//			pageLoadJSONObject.put("startOrderId", pageLoadInfo.startOrderId);
//			pageLoadJSONObject.put("activityClassName", pageLoadInfo.activityClassName);
//			pageLoadJSONObject.put("cold_hot", isPageLoadCold(pageLoadInfo) ? "冷启动" : "热启动");
//			pageLoadJSONObject.put("startTime", startTime);
//			pageLoadJSONObject.put("startFinishTime", startFinishTime);
//			pageLoadJSONObject.put("endTime", endTime);
//			pageLoadJSONObject.put("loadTime", startFinishTime - startTime);
//			pageLoadJSONObject.put("page3SM", page3SM);
//			pageLoadJSONObject.put("page30SM", page30SM);
//
//			pageLoadOverTable_dataJSONArray.add(pageLoadJSONObject);
//		}
//
//		return pageLoadOverTable_dataJSONArray.toJSONString();
//	}
//
//	/* pageLoad数据处理 */
//	private static boolean isPageLoadCold(PageLoadInfo pageLoadInfo) {
//		for (LifecycleMethod lifecycleMethod : pageLoadInfo.lifecycleMethodList) {
//			if (lifecycleMethod.methodName.equals(LifecycleMethod.ONCREATE)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private static long getPageLoadStartTime(PageLoadInfo pageLoadInfo) {
//		long begin = 0;
//		for (LifecycleMethod lifecycleMethod : pageLoadInfo.lifecycleMethodList) {
//			if (begin == 0 || lifecycleMethod.methodStartTime < begin) {
//				begin = lifecycleMethod.methodStartTime;
//			}
//		}
//		return begin;
//	}
//
//	private static long getPageLoadStartFinishTime(PageLoadInfo pageLoadInfo) {
//		long startFinishTime = 0;
//		for (LifecycleMethod lifecycleMethod : pageLoadInfo.lifecycleMethodList) {
//			if (lifecycleMethod.methodName.equals(LifecycleMethod.ONRESUME)) {
//				startFinishTime = lifecycleMethod.methodEndTime;
//			}
//		}
//		if (pageLoadInfo.drawInfoList != null && pageLoadInfo.drawInfoList.size() > 0) {
//			startFinishTime = pageLoadInfo.drawInfoList.get(0).drawEnd;
//		}
//		return startFinishTime;
//	}
//
//	private static long getPageLoadEndTime(PageLoadInfo pageLoadInfo) {
//		long end = 0;
//		for (LifecycleMethod lifecycleMethod : pageLoadInfo.lifecycleMethodList) {
//			if (end == 0 || lifecycleMethod.methodEndTime > end) {
//				end = lifecycleMethod.methodEndTime;
//			}
//		}
//		return end;
//	}
//
//	/********************************************************************************************/
//	/****************
//	 * I O检测页的数据
//	 *****************************************************/
//	/********************************************************************************************/
//
//	public static String get_ioAllFileTable_data(ArrayList<FileInfo> fileInfos,AppInfo appInfo) {
//
//		JSONArray ioAllFileTable_dataJSONArray = new JSONArray();
//
//		HashMap<Integer, JSONObject> fileOperations = new HashMap<>();// 此时处于打开状态的文件
//		for (int i = 0; i < fileInfos.size(); i++) {
//			FileInfo fileInfo = fileInfos.get(i);
//			if (fileInfo.filePath.startsWith("/data/")) {//不显示数据库相关的行为
//				continue;
//			}
//
//			if (fileInfo.actionName.equals(FileInfo.OPEN)) {
//				JSONObject lastOperation = fileOperations.get(fileInfo.fd);
//				if (lastOperation != null) {
//					ArrayList<FileInfo> lastFileInfoList = (ArrayList<FileInfo>) lastOperation.get("fileInfoList");
//					long startTime = 0;
//					long endTime = 0;
//					boolean isMainThread = false;
//					boolean isMutilThread = false;
//					int nowThreadId = -1;
//					for (FileInfo tempFileInfo : lastFileInfoList) {
//						if (startTime == 0 || tempFileInfo.actionStart < startTime) {
//							startTime = tempFileInfo.actionStart;
//						}
//						if (endTime == 0 || tempFileInfo.actionEnd > endTime) {
//							endTime = tempFileInfo.actionEnd;
//						}
//						if (nowThreadId == -1) {
//							nowThreadId = tempFileInfo.threadID;
//						} else {
//							if (tempFileInfo.threadID != nowThreadId) {
//								isMutilThread = true;
//							}
//						}
//						if (tempFileInfo.threadID == appInfo.mainThreadId) {
//							isMainThread = true;
//						}
//					}
//					lastOperation.put("startTime", startTime);
//					lastOperation.put("endTime", endTime);
//					lastOperation.put("totalTime", endTime - startTime);
//					lastOperation.put("isMainThread", isMainThread);
//					lastOperation.put("isMutilThread", isMutilThread);
//
//					ioAllFileTable_dataJSONArray.add(lastOperation);
//				}
//				JSONObject thisOperation = new JSONObject();
//				thisOperation.put("fd", fileInfo.fd);
//				thisOperation.put("fileName", fileInfo.fileName);
//				thisOperation.put("filePath", fileInfo.filePath);
//				thisOperation.put("writeSize", 0);
//				thisOperation.put("readSize", 0);
//				thisOperation.put("writeNum", 0);
//				thisOperation.put("readNum", 0);
//				ArrayList<FileInfo> fileInfoList = new ArrayList<>();
//				fileInfoList.add(fileInfo);
//				thisOperation.put("fileInfoList", fileInfoList);
//				fileOperations.put(fileInfo.fd, thisOperation);
//			} else if (fileInfo.actionName.equals(FileInfo.WRITE)) {
//				JSONObject thisOperation = fileOperations.get(fileInfo.fd);
//				if (thisOperation == null) {
//					thisOperation = new JSONObject();
//					thisOperation.put("fd", fileInfo.fd);
//					thisOperation.put("fileName", fileInfo.fileName);
//					thisOperation.put("filePath", fileInfo.filePath);
//					thisOperation.put("writeSize", fileInfo.actionSize);
//					thisOperation.put("readSize", 0);
//					thisOperation.put("writeNum", 1);
//					thisOperation.put("readNum", 0);
//					ArrayList<FileInfo> fileInfoList = new ArrayList<>();
//					fileInfoList.add(fileInfo);
//					thisOperation.put("fileInfoList", fileInfoList);
//					fileOperations.put(fileInfo.fd, thisOperation);
//				} else {
//					int lastWriteSize = thisOperation.getIntValue("writeSize");
//					thisOperation.put("writeSize", lastWriteSize + fileInfo.actionSize);
//					int lastWriteNum = thisOperation.getIntValue("writeNum");
//					thisOperation.put("writeNum", lastWriteNum + 1);
//					ArrayList<FileInfo> lastFileInfoList = (ArrayList<FileInfo>) thisOperation.get("fileInfoList");
//					lastFileInfoList.add(fileInfo);
//					thisOperation.put("fileInfoList", lastFileInfoList);
//				}
//			} else if (fileInfo.actionName.equals(FileInfo.READ)) {
//				JSONObject thisOperation = fileOperations.get(fileInfo.fd);
//				if (thisOperation == null) {
//					thisOperation = new JSONObject();
//					thisOperation.put("fd", fileInfo.fd);
//					thisOperation.put("fileName", fileInfo.fileName);
//					thisOperation.put("filePath", fileInfo.filePath);
//					thisOperation.put("writeSize", 0);
//					thisOperation.put("readSize", fileInfo.actionSize);
//					thisOperation.put("writeNum", 0);
//					thisOperation.put("readNum", 1);
//					ArrayList<FileInfo> fileInfoList = new ArrayList<>();
//					fileInfoList.add(fileInfo);
//					thisOperation.put("fileInfoList", fileInfoList);
//					fileOperations.put(fileInfo.fd, thisOperation);
//				} else {
//					int lastReadSize = thisOperation.getIntValue("readSize");
//					thisOperation.put("readSize", lastReadSize + fileInfo.actionSize);
//					int lastlastReadSizeNum = thisOperation.getIntValue("readNum");
//					thisOperation.put("readNum", lastlastReadSizeNum + 1);
//					ArrayList<FileInfo> lastFileInfoList = (ArrayList<FileInfo>) thisOperation.get("fileInfoList");
//					lastFileInfoList.add(fileInfo);
//					thisOperation.put("fileInfoList", lastFileInfoList);
//				}
//			}
//		}
//		for (Integer fd : fileOperations.keySet()) {
//			JSONObject operation = fileOperations.get(fd);
//			ArrayList<FileInfo> lastFileInfoList = (ArrayList<FileInfo>) operation.get("fileInfoList");
//			long startTime = 0;
//			long endTime = 0;
//			boolean isMainThread = false;
//			boolean isMutilThread = false;
//			int nowThreadId = -1;
//			for (FileInfo tempFileInfo : lastFileInfoList) {
//				if (startTime == 0 || tempFileInfo.actionStart < startTime) {
//					startTime = tempFileInfo.actionStart;
//				}
//				if (endTime == 0 || tempFileInfo.actionEnd > endTime) {
//					endTime = tempFileInfo.actionEnd;
//				}
//				if (nowThreadId == -1) {
//					nowThreadId = tempFileInfo.threadID;
//				} else {
//					if (tempFileInfo.threadID != nowThreadId) {
//						isMutilThread = true;
//					}
//				}
//				if (tempFileInfo.threadID == appInfo.mainThreadId) {
//					isMainThread = true;
//				}
//			}
//			operation.put("startTime", startTime);
//			operation.put("endTime", endTime);
//			operation.put("totalTime", endTime - startTime);
//			operation.put("isMainThread", isMainThread);
//			operation.put("isMutilThread", isMutilThread);
//
//			ioAllFileTable_dataJSONArray.add(operation);
//		}
//
//		return ioAllFileTable_dataJSONArray.toJSONString();
//	}
//
//	public static String get_ioAllDBTable_data(ArrayList<DBInfo> dbInfos ,ArrayList<FileInfo> fileInfos,AppInfo appInfo) {
//
//		JSONArray ioAllDBTable_dataJSONArray = new JSONArray();
//
//		HashMap<Integer, JSONObject> fileOperations = new HashMap<>();// 此时处于打开状态的文件
//		for (int i = 0; i < fileInfos.size(); i++) {
//			FileInfo fileInfo = fileInfos.get(i);
//			if (fileInfo.filePath.startsWith("/data/")) {//不显示数据库相关的行为
//				continue;
//			}
//		}
//		return null;
//	}

}
