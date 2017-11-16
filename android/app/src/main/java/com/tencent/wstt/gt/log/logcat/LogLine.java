package com.tencent.wstt.gt.log.logcat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;


public class LogLine {

	public static final String LOGCAT_DATE_FORMAT = "MM-dd HH:mm:ss.SSS";
	
	private static final int TIMESTAMP_LENGTH = 19;
	
	private static Pattern logPattern = Pattern.compile(
			// log level
			"(\\w)" +
			"/" +
			// tag
			"([^(]+)" +
			"\\(\\s*" +
			// pid
			"(\\d+)" +
			// optional weird number that only occurs on ZTE blade
			"(?:\\*\\s*\\d+)?" +
			"\\): ");
	
	private int logLevel;
	private String tag;
	private String logOutput;
	private int processId = -1;
	private String timestamp;
	private boolean expanded = false;
	private boolean highlighted = false;
	
	public CharSequence getOriginalLine() {
		
		if (logLevel == -1) { // starter line like "begin of log etc. etc."
			return logOutput;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		if (timestamp != null) {
			stringBuilder.append(timestamp).append(' ');
		}
		
		stringBuilder.append(convertLogLevelToChar(logLevel))
			.append('/')
			.append(tag)
			.append('(')
			.append(processId)
			.append("): ")
			.append(logOutput);
		
		return stringBuilder;
	}

	public int getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getLogOutput() {
		return logOutput;
	}
	public void setLogOutput(String logOutput) {
		this.logOutput = logOutput;
	}
	
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public static LogLine newLogLine(String originalLine, boolean expanded) {
		
		LogLine logLine = new LogLine();
		logLine.setExpanded(expanded);
		
		int startIdx = 0;
		
		// if the first char is a digit, then this starts out with a timestamp
		// otherwise, it's a legacy log or the beginning of the log output or something
		if (!TextUtils.isEmpty(originalLine) 
				&& Character.isDigit(originalLine.charAt(0))
				&& originalLine.length() >= TIMESTAMP_LENGTH) {
			String timestamp = originalLine.substring(0, TIMESTAMP_LENGTH - 1);
			logLine.setTimestamp(timestamp);
			startIdx = TIMESTAMP_LENGTH; // cut off timestamp
		}
		
		Matcher matcher = logPattern.matcher(originalLine);
		
		if (matcher.find(startIdx)) {
			char logLevelChar = matcher.group(1).charAt(0);
			
			logLine.setLogLevel(convertCharToLogLevel(logLevelChar));
			logLine.setTag(matcher.group(2));
			logLine.setProcessId(Integer.parseInt(matcher.group(3)));
			
			logLine.setLogOutput(originalLine.substring(matcher.end()));
			
		} else {
			logLine.setLogOutput(originalLine);
			logLine.setLogLevel(-1);
		}
		
		return logLine;
		
	}
	public static int convertCharToLogLevel(char logLevelChar) {
		
		switch (logLevelChar) {
			case 'D':
				return Log.DEBUG;
			case 'E':
				return Log.ERROR;
			case 'I':
				return Log.INFO;
			case 'V':
				return Log.VERBOSE;
			case 'W':
				return Log.WARN;
		}
		return -1;
	}
	
	public static char convertLogLevelToChar(int logLevel) {

		switch (logLevel) {
			case Log.DEBUG:
				return 'D';
			case Log.ERROR:
				return 'E';
			case Log.INFO:
				return 'I';
			case Log.VERBOSE:
				return 'V';
			case Log.WARN:
				return 'W';
		}
		return ' ';
	}
}
