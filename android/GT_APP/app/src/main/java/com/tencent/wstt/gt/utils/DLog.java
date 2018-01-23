package com.tencent.wstt.gt.utils;

import android.util.Log;

import com.tencent.wstt.gt.BuildConfig;

/**
 * Created by p_gumingcaion 2017/8/14.
 */
public class DLog {
	private static String mClassName;			//所在的类名
	private static String mMethodName;			//所在的方法名
	private static int mLineNumber;				//所在行号

    /**
	 * 私有化构造器
	 */
	private DLog() {}

	/**
	 * 是否处于调试模式
	 */
	public static boolean isDebuggable() {
		return BuildConfig.DEBUG;
//		return false;
	}

	/**
	 * 创建Log信息
	 */
	private static String createLog(String log) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(mMethodName);
		buffer.append(":");
		buffer.append(mLineNumber);
		buffer.append("]");
		buffer.append(log);

		return buffer.toString();
	}

	/**
	 * 获取类名,方法名,行号
	 */
	private static void getMethodNames(StackTraceElement[] sElements) {
		mClassName = sElements[1].getFileName();
		mMethodName = sElements[1].getMethodName();
		mLineNumber = sElements[1].getLineNumber();
	}
	
	public static void v(String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.v(mClassName, createLog(message));
	}

	public static void d(String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.d(mClassName, createLog(message));
	}

	public static void i(String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.i(mClassName, createLog(message));
	}

	public static void w(String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.w(mClassName, createLog(message));
	}
	
	public static void e(String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.e(mClassName, createLog(message));
	}
}