package com.tencent.wstt.gt.log.logcat;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import android.text.TextUtils;

import com.tencent.wstt.gt.utils.RootUtil;

/**
 * Helper functions for running processes.
 */
public class RuntimeHelper {
	/**
	 * Exec the arguments, using root if necessary.
	 * @param args
	 */
	public static Process exec(List<String> args) throws IOException {
		// since JellyBean, sudo is required to read other apps' logs
		if (VersionHelper.getVersionSdkIntCompat() >= VersionHelper.VERSION_JELLYBEAN
				&& RootUtil.isRooted()) {
			Process process = Runtime.getRuntime().exec("su");
			
			PrintStream outputStream = null;
			try {
				outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
				outputStream.println(TextUtils.join(" ", args));
				outputStream.flush();
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
			}
			
			return process;
		}
		return Runtime.getRuntime().exec(ArrayUtil.toArray(args, String.class));
	}
	
	public static void destroy(Process process) {
	    // if we're in JellyBean, then we need to kill the process as root, which requires all this
	    // extra UnixProcess logic
	    if (VersionHelper.getVersionSdkIntCompat() >= VersionHelper.VERSION_JELLYBEAN
	            && RootUtil.isRooted()) {
	    	RootUtil.destroy(process);
	    } else {
	        process.destroy();
	    }
	}
	
}