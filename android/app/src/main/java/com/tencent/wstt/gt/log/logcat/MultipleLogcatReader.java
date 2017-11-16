package com.tencent.wstt.gt.log.logcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.os.AsyncTask;

/**
 * Combines multipe buffered readers into a single reader that merges all input synchronously.
 */
public class MultipleLogcatReader extends AbsLogcatReader {

	private static final String DUMMY_NULL = new String("");
	
	private List<ReaderThread> readerThreads = new LinkedList<ReaderThread>();
	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
	
	public MultipleLogcatReader(boolean recordingMode, 
			Map<String,String> lastLines) throws IOException {
		super(recordingMode);
		// read from all three buffers at once
		for (Entry<String,String> entry : lastLines.entrySet()) {
			String logBuffer = entry.getKey();
			String lastLine = entry.getValue();
			ReaderThread readerThread = new ReaderThread(logBuffer, lastLine);
			readerThread.start();
			readerThreads.add(readerThread);
		}
	}

	public String readLine() throws IOException {
		
		try {
			String value = queue.take();
			if (value != DUMMY_NULL) {
				return value;
			}
		} catch (InterruptedException e) {

		}
		return null;
	}
	

	@Override
	public boolean readyToRecord() {
		for (ReaderThread thread : readerThreads) {
			if (!thread.reader.readyToRecord()) {
				return false;
			}
		}
		return true;
	}	
	
	@Override
	public void killQuietly() {
		for (ReaderThread thread : readerThreads) {
			thread.killed = true;
		}
		
		// do in background, because otherwise we might hang
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				for (ReaderThread thread : readerThreads) {
					thread.reader.killQuietly();
				}
				queue.offer(DUMMY_NULL);
				return null;
			}
		}.execute((Void)null);
	}
	
	
	@Override
	public List<Process> getProcesses() {
		List<Process> result = new ArrayList<Process>();
		for (ReaderThread thread : readerThreads) {
			result.addAll(thread.reader.getProcesses());
		}
		return result;
	}
	
	private class ReaderThread extends Thread {

		SingleLogcatReader reader;
		
		private boolean killed;
		
		public ReaderThread(String logBuffer, String lastLine) throws IOException {
			this.reader = new SingleLogcatReader(recordingMode, logBuffer, lastLine);
		}

		@Override
		public void run() {
			String line;
			
			try {
				while (!killed && (line = reader.readLine()) != null && !killed) {
					queue.put(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
