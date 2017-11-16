package com.tencent.wstt.gt.log.logcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.ui.model.LogEntry;

public class SaveLogHelper {

	private static final int BUFFER = 0x1000; // 4K

	public static File getFile(String filename) {
		
		File catlogDir = getCatlogDirectory();
		
		File file = new File(catlogDir, filename);
	
		return file;
	}
	
	public static Date getLastModifiedDate(String filename) {
		
		File catlogDir = getCatlogDirectory();
		
		File file = new File(catlogDir, filename);
		
		if (file.exists()) {
			return new Date(file.lastModified());
		} else {
			return new Date();
		}
	}
	
	/**
	 * Get all the log filenames, order by last modified descending
	 * @return
	 */
	public static List<String> getLogFilenames() {
		
		File catlogDir = getCatlogDirectory();
		
		File[] filesArray = catlogDir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if (f != null && f.isFile() && f.getName().endsWith(LogUtils.LOG_POSFIX))
				{
					return true;
				}
				return false;
			}});
		
		if (filesArray == null) {
			return Collections.emptyList();
		}
		
		List<File> files = new ArrayList<File>(Arrays.asList(filesArray));
		
		Collections.sort(files, new Comparator<File>(){

			@Override
			public int compare(File object1, File object2) {
				return Long.valueOf(object2.lastModified()).compareTo(object1.lastModified());
			}});
		
		List<String> result = new ArrayList<String>();
		
		for (File file : files) {
			result.add(file.getName());
		}
		
		return result;
		
	}
	
	public static LogEntry[] openLog(String filename, int maxLines) {
		
		File catlogDir = getCatlogDirectory();
		File logFile = new File(catlogDir, filename);	
		
		List<LogEntry> logLines = new ArrayList<LogEntry>();
		
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(logFile)), BUFFER);
			
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				if (null != line)
				{
					LogEntry entry = new LogEntry();
					entry.msg = line;
					logLines.add(entry);
					if (logLines.size() > maxLines) {

					}
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return logLines.toArray(new LogEntry[]{});
	}

	private static File getCatlogDirectory() {
		File catlogDir = Env.ROOT_LOG_FOLDER;
		
		if (!catlogDir.exists()) {
			catlogDir.mkdir();
		}
		return catlogDir;
	}
}
