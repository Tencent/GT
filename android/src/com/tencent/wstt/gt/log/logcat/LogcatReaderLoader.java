package com.tencent.wstt.gt.log.logcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class LogcatReaderLoader implements Parcelable {

	private Map<String,String> lastLines = new HashMap<String, String>();
	private boolean recordingMode;
	private boolean multiple;
	
	private LogcatReaderLoader(Parcel in) {
		this.recordingMode = in.readInt() == 1;
		this.multiple = in.readInt() == 1;
		Bundle bundle = in.readBundle();
		for (String key : bundle.keySet()) {
			lastLines.put(key, bundle.getString(key));
		}
	}
	
	private LogcatReaderLoader(List<String> buffers, boolean recordingMode) {
		this.recordingMode = recordingMode;
		this.multiple = buffers.size() > 1;
		for (String buffer : buffers) {
			// no need to grab the last line if this isn't recording mode
			String lastLine = recordingMode ? LogcatHelper.getLastLogLine(buffer) : null;
			lastLines.put(buffer, lastLine);
		}
	}
	
	public LogcatReader loadReader() throws IOException {
		LogcatReader reader;
		if (!multiple) {
			// single reader
			String buffer = lastLines.keySet().iterator().next();
			String lastLine = lastLines.values().iterator().next();
			reader = new SingleLogcatReader(recordingMode, buffer, lastLine);
		} else {
			// multiple reader
			reader = new MultipleLogcatReader(recordingMode, lastLines);
		}
		
		return reader;
	}
	
	public static LogcatReaderLoader create(Context context, boolean recordingMode) {
		List<String> buffers = new ArrayList<String>();
		buffers.add("main");
		LogcatReaderLoader loader = new LogcatReaderLoader(buffers, recordingMode);
		return loader;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(recordingMode ? 1 : 0);
		dest.writeInt(multiple ? 1 : 0);
		Bundle bundle = new Bundle();
		for (Entry<String,String> entry : lastLines.entrySet()) {
			bundle.putString(entry.getKey(), entry.getValue());
		}
		dest.writeBundle(bundle);
	}
	
	public static final Parcelable.Creator<LogcatReaderLoader> CREATOR = new Parcelable.Creator<LogcatReaderLoader>() {
		public LogcatReaderLoader createFromParcel(Parcel in) {
			return new LogcatReaderLoader(in);
		}

		public LogcatReaderLoader[] newArray(int size) {
			return new LogcatReaderLoader[size];
		}
	};
}
