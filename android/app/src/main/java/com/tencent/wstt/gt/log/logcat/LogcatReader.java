package com.tencent.wstt.gt.log.logcat;

import java.io.IOException;
import java.util.List;

public interface LogcatReader {

	/**
	 * Read a single log line, ala BufferedReader.readLine().
	 * @return
	 * @throws IOException
	 */
	public String readLine() throws IOException;
	
	/**
	 * Kill the reader and close all resources without throwing any exceptions.
	 */
	public void killQuietly();
	
	public boolean readyToRecord();
	
	public List<Process> getProcesses();
	
}
