package com.tencent.wstt.gt.log.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

public class SingleLogcatReader extends AbsLogcatReader {
	
	private Process logcatProcess;
	private BufferedReader bufferedReader;
	private String logBuffer;
	private String lastLine;
	
	public SingleLogcatReader(boolean recordingMode, String logBuffer, String lastLine) throws IOException {
		super(recordingMode);
		this.logBuffer = logBuffer;
		this.lastLine = lastLine;
		init();
	}

	private void init() throws IOException {
		// use the "time" log so we can see what time the logs were logged at
		logcatProcess = LogcatHelper.getLogcatProcess(logBuffer);
		
		bufferedReader = new BufferedReader(new InputStreamReader(logcatProcess
				.getInputStream()), 8192);
	}
	

	public String getLogBuffer() {
		return logBuffer;
	}


	@Override
	public void killQuietly() {
		if (logcatProcess != null) {
			RuntimeHelper.destroy(logcatProcess);
		}
		
		// post-jellybean, we just kill the process, so there's no need
		// to close the bufferedReader.  Anyway, it just hangs.
		if (VersionHelper.getVersionSdkIntCompat() < VersionHelper.VERSION_JELLYBEAN
		        && bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String readLine() throws IOException {
		String line = bufferedReader.readLine();
		
		if (recordingMode && lastLine != null) { // still skipping past the 'last line'
			if (lastLine.equals(line) || isAfterLastTime(line)) {
				lastLine = null; // indicates we've passed the last line
			}
		}
		
		return line;
		
	}
	
	private boolean isAfterLastTime(String line) {
		// doing a string comparison is sufficient to determine whether this line is chronologically
		// after the last line, because the format they use is exactly the same and 
		// lists larger time period before smaller ones
		return isDatedLogLine(lastLine) && isDatedLogLine(line) && line.compareTo(lastLine) > 0;
		
	}
	
	private boolean isDatedLogLine(String line) {
		// 18 is the size of the logcat timestamp
		return (!TextUtils.isEmpty(line) && line.length() >= 18 && Character.isDigit(line.charAt(0)));
	}


	@Override
	public boolean readyToRecord() {
		if (!recordingMode) {
			return false;
		}
		return lastLine == null;
	}
	
	@Override
	public List<Process> getProcesses() {
		return Collections.singletonList(logcatProcess);
	}
}
