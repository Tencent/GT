package com.tencent.wstt.gt.log.logcat;

import java.lang.reflect.Field;

import android.os.Build;

public class VersionHelper {

	public static final int VERSION_CUPCAKE = 3;
	public static final int VERSION_DONUT = 4;
	public static final int VERSION_FROYO = 8;
	public static final int VERSION_JELLYBEAN = 16;
	
	private static Field sdkIntField = null;
	private static boolean fetchedSdkIntField = false;
	
	public static int getVersionSdkIntCompat() {
		try {
			Field field = getSdkIntField();
			if (field != null) {
				return (Integer) field.get(null);
			}
		} catch (IllegalAccessException ignore) {
			// ignore
		}
		return VERSION_CUPCAKE; // cupcake
	}

	private static Field getSdkIntField() {
		if (!fetchedSdkIntField) {
			try {
				sdkIntField = Build.VERSION.class.getField("SDK_INT");
			} catch (NoSuchFieldException ignore) {
				// ignore
			}
			fetchedSdkIntField = true;
		}
		return sdkIntField;
	}
}

