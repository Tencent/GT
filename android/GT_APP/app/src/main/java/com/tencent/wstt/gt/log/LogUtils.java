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
package com.tencent.wstt.gt.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.gt.Functions;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.SMUtils;
import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.proInfo.floatView.GTMemHelperFloatview;
import com.tencent.wstt.gt.proInfo.floatView.MemInfo;
import com.tencent.wstt.gt.ui.model.GroupTimeEntry;
import com.tencent.wstt.gt.ui.model.LogEntry;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.ui.model.TimeEntry;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;

public class LogUtils {

	public static final long CAPACITY = 4 * 1024 * 1024;
	public static int CACHE = 1000;
	public static final String LOG_POSFIX = ".log";
	public static final String LOG_FILE_MATCHE = "\\d+.log";
	public static final String TLOG_POSFIX = ".csv";
	public static final String GW_POSFIX = ".csv";
	public static final String GW_DESC_PREFIX = "gtdesc_";
	public static final String GW_DESC_POSFIX = ".txt";

	// 是否即时将自动保存日志更新到存储
	private static boolean autoSaveQuickFlush = GTPref.getGTPref().getBoolean(GTPref.LOG_AUTOSAVEFLUSH_SWITCH, true);

	public static void setAutoSaveQuickFlush(boolean flag) {
		autoSaveQuickFlush = flag;
	}

	public static boolean isAutoSaveQuickFlush() {
		return autoSaveQuickFlush;
	}

	/**
	 * GT中的日志保存
	 * 
	 * @param list
	 * @param path
	 */
	public static void writeLog(List<LogEntry> list, String fileName, boolean append) {

		if (!GTUtils.isSDCardExist()) {
			return;
		}

		File f = null;
		if (fileName.contains(FileUtil.separator) || fileName.contains("\\")) {
			f = new File(fileName);
		} else {
			f = new File(Env.ROOT_LOG_FOLDER, fileName);
		}

		if (f.exists() && !append) {
			f.delete();
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (LogEntry entry : list) {
			writeNotClose(entry.msg, f, fw);
			writeNotClose("\r\n", f, fw);
		}
		FileUtil.closeWriter(fw);
	}

	public static void writeLog(List<LogEntry> list, File f, boolean append) {

		if (!GTUtils.isSDCardExist()) {
			return;
		}

		if (f.exists() && !append) {
			f.delete();
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (LogEntry entry : list) {
			writeNotClose(entry.msg, f, fw);
			writeNotClose("\r\n", f, fw);
		}
		FileUtil.closeWriter(fw);
	}

	public static void writeFilterLog(List<String> list, File f, boolean append) {
		if (!GTUtils.isSDCardExist()) {
			return;
		}

		if (f.exists() && !append) {
			f.delete();
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			writeNotClose(list.get(i), f, fw);
			writeNotClose("\r\n", f, fw);
		}
		FileUtil.closeWriter(fw);
	}

	public static void writeTimeLog(List<GroupTimeEntry> list, String fileName) {

		if (!GTUtils.isSDCardExist()) {
			return;
		}

		int la = fileName.lastIndexOf(".");
		if (la < 0) {
			fileName = fileName + LogUtils.TLOG_POSFIX;
		} else {
			String temp = fileName.substring(la);
			if (temp.contains(FileUtil.separator) || temp.contains("\\")) {
				// "."是目录名的一部分而不是后缀名的情况
				fileName = fileName + LogUtils.TLOG_POSFIX;
			}
			// else fileName = fileName
		}

		File f = null;
		if (fileName.contains(FileUtil.separator) || fileName.contains("\\")) {
			f = new File(fileName);
		} else {
			Env.ROOT_TIME_FOLDER.mkdirs();
			f = new File(Env.ROOT_TIME_FOLDER, fileName);
		}

		if (f.exists()) {
			f.delete();
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringBuffer sb = null;

		for (GroupTimeEntry gte : list) {
			for (TagTimeEntry tte : gte.entrys()) {
				sb = new StringBuffer();
				sb.append("group,");
				sb.append(gte.getName());
				sb.append("\r\n");
				sb.append("tag,");
				sb.append(tte.getName());
				sb.append("\r\n");
				sb.append("isInThread,");
				sb.append(tte.getTid() == 0 ? "false" : "true");
				sb.append("\r\n");

				ArrayList<TimeEntry> tempRecordList = tte.getRecordList();

				for (TimeEntry time : tempRecordList) {
					if (sb.length() > 8192) {
						writeNotClose(sb.toString(), f, fw);
						sb = new StringBuffer();
					}
					sb.append(time);
					// sb.append(",");
					sb.append("\r\n");
				}

				if (!tempRecordList.isEmpty()) {
					sb.deleteCharAt(sb.length() - 1);
				}
				tempRecordList = null; // 可以及时释放
				sb.append("\r\n");
				writeNotClose(sb.toString(), f, fw);
			}
		}

		FileUtil.closeWriter(fw);
	}

	public static void writeTimeDetail(TagTimeEntry tte, String fileName) {

		if (!GTUtils.isSDCardExist()) {
			return;
		}

		int la = fileName.lastIndexOf(".");
		if (la < 0) {
			fileName = fileName + LogUtils.TLOG_POSFIX;
		} else {
			String temp = fileName.substring(la);
			if (temp.contains(FileUtil.separator) || temp.contains("\\")) {
				// "."是目录名的一部分而不是后缀名的情况
				fileName = fileName + LogUtils.TLOG_POSFIX;
			}
			// else fileName = fileName
		}

		File f = null;
		if (fileName.contains(FileUtil.separator) || fileName.contains("\\")) {
			f = new File(fileName);
		} else {
			Env.ROOT_TIME_FOLDER.mkdirs();
			f = new File(Env.ROOT_TIME_FOLDER, fileName);
		}

		if (f.exists()) {
			f.delete();
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();

		if (null != tte.getParent() && tte.getParent() instanceof GroupTimeEntry) {
			sb.append("group,");
			sb.append(tte.getParent().getName());
			sb.append("\r\n");
		}
		sb.append("tag,");
		sb.append(tte.getName());
		sb.append("\r\n");
		sb.append("isInThread,");
		sb.append(tte.getTid() == 0 ? "false" : "true");
		sb.append("\r\n");

		if (!tte.hasChild()) {
			for (TimeEntry time : tte.getRecordList()) {
				if (sb.length() > 8192) {
					writeNotClose(sb.toString(), f, fw);
					sb = new StringBuffer();
				}
				sb.append(time);
				sb.append("\r\n");
			}
		} else // 支持多组数据的保存
		{
			for (int i = 0; i < tte.getSubTagEntrys()[0].getRecordSize(); i++) {
				for (int j = 0; j < tte.getSubTagEntrys().length; j++) {
					TagTimeEntry subEntry = tte.getSubTagEntrys()[j];

					TimeEntry time = subEntry.getRecord(i);

					if (sb.length() > 8192) {
						writeNotClose(sb.toString(), f, fw);
						sb = new StringBuffer();
					}

					if (j == 0) {
						sb.append(time);
					} else {
						sb.append(time.reduce);
					}

					if (j == tte.getSubTagEntrys().length - 1) {
						sb.append("\r\n");
					} else {
						sb.append(",");
					}
				}
			}
		}

		if (tte.getRecordSize() != 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("\r\n");
		writeNotClose(sb.toString(), f, fw);

		FileUtil.closeWriter(fw);
	}

	public static void writeGWData(final GWSaveEntry saveEntry, TagTimeEntry tte) {

		if (!GTUtils.isSDCardExist() || tte == null) {
			return;
		}

		if (!tte.hasChild() && tte.getRecordSize() == 0) {
			return;
		} else if (tte.hasChild() && tte.getSubTagEntrys()[0].getRecordSize() == 0) {
			return;
		}

		String sFolder = Env.S_ROOT_GW_FOLDER +
				saveEntry.path1 + FileUtil.separator + saveEntry.path2 + FileUtil.separator + saveEntry.path3 + FileUtil.separator;
		File folder = new File(sFolder);
		folder.mkdirs();

		String fName = getTagTimeEntryFileName(tte, saveEntry);
		File f = new File(folder, fName);
		if (f.exists())
		{
			return;
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(f, true);

			StringBuffer sb = new StringBuffer();
			sb.append("key,");
			sb.append(tte.getName());
			sb.append("\r\n");
			sb.append("alias,");
			sb.append(tte.getAlias());
			sb.append("\r\n");
			sb.append("unit,");

			// PSS和PD的单位特殊，保存的KB，曲线图上显示的MB
			if (tte.getFunctionId() == Functions.PERF_DIGITAL_MULT_MEM) {
				sb.append("(KB)");
			} else {
				sb.append(tte.getUnit());
			}
			sb.append("\r\n");

			if (!tte.hasChild()) {
				int size = tte.getRecordSize();
				long firstTime = tte.getRecord(0).time;
				long lastTime = tte.getRecord(size - 1).time;
				ArrayList<TimeEntry> tempRecordList = tte.getRecordList();

				sb.append("begin date,");
				sb.append(GTUtils.getDate(firstTime));
				sb.append("\r\n");
				sb.append("end date,");
				sb.append(GTUtils.getDate(lastTime));
				sb.append("\r\n");
				sb.append("count,");
				sb.append(size);
				sb.append("\r\n");
				sb.append("\r\n");

				sb.append("min,");
				sb.append(tte.getMin());
				sb.append("\r\n");
				sb.append("max,");
				sb.append(tte.getMax());
				sb.append("\r\n");
				sb.append("avg,");
				sb.append(tte.getAve());
				sb.append("\r\n");
				sb.append("\r\n");

				for (TimeEntry time : tempRecordList) {
					if (sb.length() > 8192) {
						writeNotClose(sb.toString(), f, fw);
						sb = new StringBuffer();
					}
					sb.append(time);
					sb.append("\r\n");
				}
			} else // 支持多组数据的保存
			{
				TagTimeEntry temp = tte.getChildren()[0];
				int size = temp.getRecordSize();
				long firstTime = temp.getRecord(0).time;
				long lastTime = temp.getRecord(size - 1).time;

				sb.append("begin date,");
				sb.append(GTUtils.getDate(firstTime));
				sb.append("\r\n");
				sb.append("end date,");
				sb.append(GTUtils.getDate(lastTime));
				sb.append("\r\n");
				sb.append("count,");
				sb.append(size);
				sb.append("\r\n");
				sb.append("\r\n");

				sb.append(",");
				for (TagTimeEntry child : tte.getChildren()) {
					sb.append(child.getName());
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\r\n");

				sb.append("min,");
				for (TagTimeEntry child : tte.getChildren()) {
					sb.append(child.getMin());
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\r\n");

				sb.append("max,");
				for (TagTimeEntry child : tte.getChildren()) {
					sb.append(child.getMax());
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\r\n");

				sb.append("avg,");
				for (TagTimeEntry child : tte.getChildren()) {
					sb.append(child.getAve());
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\r\n");
				sb.append("\r\n");

				for (int i = 0; i < size; i++) {
					for (int j = 0; j < tte.getSubTagEntrys().length; j++) {
						TagTimeEntry subEntry = tte.getSubTagEntrys()[j];
						TimeEntry time = subEntry.getRecord(i);

						if (sb.length() > 8192) {
							writeNotClose(sb.toString(), f, fw);
							sb = new StringBuffer();
						}

						if (j == 0) {
							sb.append(time);
						} else {
							sb.append(time.reduce);
						}

						if (j == tte.getSubTagEntrys().length - 1) {
							sb.append("\r\n");
						} else {
							sb.append(",");
						}
					}
				}
			}
			if (tte.getRecordSize() != 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("\r\n");
			writeNotClose(sb.toString(), f, fw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FileUtil.closeWriter(fw);
		}

		// add on 20131225 有手动tag记录内存值的情况，先把tag的内存值保存了 start
		// 简单过滤保存
		if (GTMemHelperFloatview.memInfoList.size() <= 0) {
			return; // 不存在这种数据直接return
		}
		File tagFile = new File(folder, "tagMem_" + saveEntry.now + LogUtils.GW_POSFIX);
		if (tagFile.exists()) {
			tagFile.delete();
		}

		FileWriter fwTagFile = null;
		try {
			fwTagFile = new FileWriter(tagFile, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringBuffer sb = null;

		sb = new StringBuffer();
		sb.append("time(ms)");
		sb.append(",");
		sb.append("DalvikHeapSize(KB)");
		sb.append(",");
		sb.append("DalvikAllocated(KB)");
		sb.append(",");
		sb.append("private_dirty(KB)");
		sb.append(",");
		sb.append("PSS_Total(KB)");
		sb.append(",");
		sb.append("PSS_Dalvik(KB)");
		sb.append(",");
		sb.append("PSS_Native(KB)");
		sb.append(",");
		sb.append("PSS_OtherDev(KB)");
		sb.append(",");
		sb.append("PSS_Graphics(KB)");
		sb.append(",");
		sb.append("PSS_GL(KB)");
		sb.append(",");
		sb.append("PSS_Unknow(KB)");
		sb.append("\r\n");

		for (MemInfo mem : GTMemHelperFloatview.memInfoList) {
			if (sb.length() > 8192) {
				writeNotClose(sb.toString(), tagFile, fwTagFile);
				sb = new StringBuffer();
			}
			sb.append(GTUtils.getSaveTime(mem.time));
			sb.append(",");
			sb.append(mem.dalvikHeapSize);
			sb.append(",");
			sb.append(mem.dalvikAllocated);
			sb.append(",");
			sb.append(mem.private_dirty);
			sb.append(",");
			sb.append(mem.pss_total);
			sb.append(",");
			sb.append(mem.pss_Dalvik);
			sb.append(",");
			sb.append(mem.pss_Native);
			sb.append(",");
			sb.append(mem.pss_OtherDev);
			sb.append(",");
			sb.append(mem.pss_graphics);
			sb.append(",");
			sb.append(mem.pss_gl);
			sb.append(",");
			sb.append(mem.pss_UnKnown);
			sb.append("\r\n");
		}
		writeNotClose(sb.toString(), tagFile, fwTagFile);
		FileUtil.closeWriter(fwTagFile);
		// add on 20131225 有手动tag记录内存值的情况，先把tag的内存值保存了 end
	}

	public static void writeGWDataForSM(final GWSaveEntry saveEntry, TagTimeEntry tte) {

		if (!GTUtils.isSDCardExist() || tte == null) {
			return;
		}

		if (!tte.hasChild() && tte.getRecordSize() == 0) {
			return;
		} else if (tte.hasChild() && tte.getSubTagEntrys()[0].getRecordSize() == 0) {
			return;
		}

		String sFolder = Env.S_ROOT_GW_FOLDER 
				+ saveEntry.path1 + FileUtil.separator + saveEntry.path2 + FileUtil.separator + saveEntry.path3 + FileUtil.separator;
		File folder = new File(sFolder);
		folder.mkdirs();

		// SM的保存文件名使用出参名，这样多个Client同时保存不会出现重名覆盖问题
		String fName = getTagTimeEntryFileName(tte, saveEntry);
		File f = new File(folder, fName);
		if (f.exists())
		{
			return;
		}

		FileWriter fw = null;

		// 在这里最后算一下分，否则没进具体页没有算分的机会
		List<Integer> smrs = SMUtils.getSmDetail(tte.getRecordList());
		tte.exInt_1 = smrs.get(1);
		tte.exInt_2 = smrs.get(3);
		tte.exInt_3 = smrs.get(5);

		try {
			fw = new FileWriter(f, true);

			StringBuffer sb = new StringBuffer();
			sb.append("key,");
			sb.append(tte.getName());
			sb.append("\r\n");
			sb.append("alias,");
			sb.append(tte.getAlias());
			sb.append("\r\n");
			sb.append("unit,");

			// PSS和PD的单位特殊，保存的KB，曲线图上显示的MB
			sb.append(tte.getUnit());
			sb.append("\r\n");

			int size = tte.getRecordSize();
			long firstTime = tte.getRecord(0).time;
			long lastTime = tte.getRecord(size - 1).time;
			ArrayList<TimeEntry> tempRecordList = tte.getRecordList();

			sb.append("begin date,");
			sb.append(GTUtils.getDate(firstTime));
			sb.append("\r\n");
			sb.append("end date,");
			sb.append(GTUtils.getDate(lastTime));
			sb.append("\r\n");
			sb.append("count,");
			sb.append(size);
			sb.append("\r\n");
			sb.append("\r\n");

			sb.append("bad time,");
			sb.append(tte.exInt_1);
			sb.append("\r\n");
			sb.append("good time,");
			sb.append(tte.exInt_2);
			sb.append("\r\n");
			sb.append("score,");
			sb.append(tte.exInt_3);
			sb.append("\r\n");
			sb.append("min,");
			sb.append(tte.getMin());
			sb.append("\r\n");
			sb.append("avg,");
			sb.append(tte.getAve());
			sb.append("\r\n");
			sb.append("\r\n");

			for (TimeEntry time : tempRecordList) {
				if (sb.length() > 8192) {
					writeNotClose(sb.toString(), f, fw);
					sb = new StringBuffer();
				}
				sb.append(time);
				sb.append("\r\n");
			}
			if (tte.getRecordSize() != 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("\r\n");
			writeNotClose(sb.toString(), f, fw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FileUtil.closeWriter(fw);
		}
	}

	public static void writeGWDesc(final GWSaveEntry saveEntry, final TagTimeEntry...ttes) {
		String sFolder = Env.S_ROOT_GW_FOLDER
				+ saveEntry.path1 + FileUtil.separator + saveEntry.path2 + FileUtil.separator + saveEntry.path3 + FileUtil.separator;
		File folder = new File(sFolder);
		folder.mkdirs();

		String fName =  GW_DESC_PREFIX + saveEntry.now + GW_DESC_POSFIX;
		File f = new File(folder, fName);

		FileWriter fw = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("gtdesc:=");
			sb.append(saveEntry.desc);
			sb.append("\r\n");
			sb.append("\r\n");
			sb.append("opfiles:=");
			sb.append("\r\n");
			// 本次测试提交的文件
			boolean hasValidData = false;
			for (TagTimeEntry tte : ttes)
			{
				String tteFileName = getTagTimeEntryFileName(tte, saveEntry);
				if (!tte.hasChild() && tte.getRecordSize() > 0
						|| tte.hasChild() && tte.getSubTagEntrys()[0].getRecordSize() > 0)
				{
					hasValidData = true;
					sb.append(tteFileName);
					sb.append("\r\n");
				}
			}
			if (hasValidData)
			{
				fw = new FileWriter(f, true);
				writeNotClose(sb.toString(), f, fw);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			FileUtil.closeWriter(fw);
		}
	}

	private static String getTagTimeEntryFileName(final TagTimeEntry tte, final GWSaveEntry saveEntry)
	{
		long lastDataTime = 0;
		if (!tte.hasChild() && tte.getRecordSize() > 0)
		{
			ArrayList<TimeEntry> dataList = tte.getRecordList();
			lastDataTime = dataList.get(dataList.size() -1).time;
		}
		else if (tte.hasChild() && tte.getSubTagEntrys()[0].getRecordSize() > 0)
		{
			ArrayList<TimeEntry> dataList = tte.getSubTagEntrys()[0].getRecordList();
			lastDataTime = dataList.get(dataList.size() -1).time;
		}

		String recordTime = lastDataTime == 0 ? saveEntry.now : GTUtils.getSaveDate(lastDataTime);
		String tteFileName = tte.getName() + "_" + recordTime + LogUtils.GW_POSFIX;
		tteFileName = tteFileName.replace(':', '_');
		return tteFileName;
	}

	public static void writeTagMemData(String pkgName, String fileName) {
		if (!GTUtils.isSDCardExist()) {
			return;
		}

		int la = fileName.lastIndexOf(".");
		if (la < 0) {
			fileName = fileName + LogUtils.TLOG_POSFIX;
		} else {
			String temp = fileName.substring(la);
			if (temp.contains(FileUtil.separator) || temp.contains("\\")) {
				// "."是目录名的一部分而不是后缀名的情况
				fileName = fileName + LogUtils.TLOG_POSFIX;
			}
			// else fileName = fileName
		}

		File f = null;
		if (fileName.contains(FileUtil.separator) || fileName.contains("\\")) {
			f = new File(fileName);
		} else {
			String parentFolder = Env.S_ROOT_GW_MAN_FOLDER + Env.CUR_APP_NAME + FileUtil.separator;
			File folder = new File(parentFolder);
			folder.mkdirs();

			f = new File(folder, fileName);
		}

		if (f.exists()) {
			f.delete();
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringBuffer sb = null;

		sb = new StringBuffer();
		sb.append(pkgName);
		sb.append("\r\n");
		sb.append("time(ms)");
		sb.append(",");
		sb.append("DalvikHeapSize(KB)");
		sb.append(",");
		sb.append("DalvikAllocated(KB)");
		sb.append(",");
		sb.append("NativeHeapSize(KB)");
		sb.append(",");
		sb.append("NativeAllocated(KB)");
		sb.append(",");
		sb.append("private_dirty(KB)");
		sb.append(",");
		sb.append("PSS_Total(KB)");
		sb.append(",");
		sb.append("PSS_Dalvik(KB)");
		sb.append(",");
		sb.append("PSS_Native(KB)");
		sb.append(",");
		sb.append("PSS_OtherDev(KB)");
		sb.append(",");
		sb.append("PSS_Graphics(KB)");
		sb.append(",");
		sb.append("PSS_GL(KB)");
		sb.append(",");
		sb.append("PSS_Unknow(KB)");
		sb.append("\r\n");

		for (MemInfo mem : GTMemHelperFloatview.memInfoList) {
			if (sb.length() > 8192) {
				writeNotClose(sb.toString(), f, fw);
				sb = new StringBuffer();
			}
			sb.append(GTUtils.getSaveTime(mem.time));
			sb.append(",");
			sb.append(mem.dalvikHeapSize);
			sb.append(",");
			sb.append(mem.dalvikAllocated);
			sb.append(",");
			sb.append(mem.nativeHeapSize);
			sb.append(",");
			sb.append(mem.nativeAllocated);
			sb.append(",");
			sb.append(mem.private_dirty);
			sb.append(",");
			sb.append(mem.pss_total);
			sb.append(",");
			sb.append(mem.pss_Dalvik);
			sb.append(",");
			sb.append(mem.pss_Native);
			sb.append(",");
			sb.append(mem.pss_OtherDev);
			sb.append(",");
			sb.append(mem.pss_graphics);
			sb.append(",");
			sb.append(mem.pss_gl);
			sb.append(",");
			sb.append(mem.pss_UnKnown);
			sb.append("\r\n");
		}
		writeNotClose(sb.toString(), f, fw);
		FileUtil.closeWriter(fw);
	}

	/**
	 * 用于自动保存，一般writer在上层长时间不会关闭
	 */
	public static void writeBuff(CharSequence sb, File f, FileWriter writer) {
		if (!f.exists()) {
			return; // 放弃本次记录直接返回，等待下次保存时重新选定自动保存的日志文件
		}

		try {
			// TODO writer如果长时间不写只能等关闭GT时做writer的全close操作
			writer.write(sb.toString());
			if (autoSaveQuickFlush) {
				writer.flush(); // 不flush是8k一存
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 不关闭输入输出流连接的写文件方式，用于保存日志快速读写的方式 但要保证调用该方法的事务都是完成即关闭输出流。 20140517
	 * 已查明本日以前调用该方法的地方，都是事务完成即关闭输出流的，所以不需要flush
	 */
	private static void writeNotClose(CharSequence sb, File f, FileWriter writer) {
		if (!f.exists()) {
			try {
				f.getParentFile().mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		try {
			// TODO writer如果长时间不写只能等关闭GT时做writer的全close操作
			writer.write(sb.toString());
			// writer.flush(); // 不flush是8k一存
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
