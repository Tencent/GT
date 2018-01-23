package com.tencent.wstt.gt;

import android.os.Environment;

import java.io.File;

public class Env {
    public static String GT_SERVICE_PACKAGE = "com.tencent.wstt.gt";
    public static String GT_SERVICE_ACTION = "GTR.GTRService";
    public static String GTR_SERVICE_NAME = "com.tencent.wstt.gt.service.GTRService";

    public static String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String GTR_CONFIG_PATH = STORAGE_PATH + File.separator + "GTR/config/";
}
