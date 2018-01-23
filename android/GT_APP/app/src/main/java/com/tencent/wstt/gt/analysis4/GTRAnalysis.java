package com.tencent.wstt.gt.analysis4;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.activity.GTAUTFragment1;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.utils.FileUtils;
import com.tencent.wstt.gt.service.GTRBinder;
import com.tencent.wstt.gt.controller.GTRControllerServer;

import java.io.File;

/**
 * Created by p_hongjcong on 2017/8/10.
 */

public class GTRAnalysis extends GTRAnalysisCallBackManager {
    private static Context applicationContext = null;
    private static Handler mainThreadHandler = null;
    public static String packageName = null;   //被测进程
    public static long startTestTime;
    public static int pid = -1;                //当前测试进程的pid
    private static int lastPid = -1;           //上一个测试进程的pid -用于过滤上一个进程的数据

    public static void start(Context context, String packageName) {
        start(context, packageName, true);
    }

    /**
     * Restart a new application monitoring
     * @param context
     * @param packageName
     * @param autoStartStop true if restart the application and reset the data collection
     */
    public static void start(Context context, String packageName, boolean autoStartStop) {
        if (autoStartStop) {
            GTRControllerServer.killAppWithSDK(context, packageName);
        }

        //设置初始化数据分析器：
        GTRAnalysis.applicationContext = (context != null)
                        ? context.getApplicationContext()
                        : null;

        if (GTRAnalysis.mainThreadHandler == null) {
            GTRAnalysis.mainThreadHandler = new Handler(Looper.getMainLooper());
        }

        GTRAnalysis.lastPid = GTRAnalysis.pid;
        GTRAnalysis.pid = -1;
        GTRAnalysis.packageName = packageName;
        GTRBinder.setPackageName(packageName);
        GTRAnalysis.init();

        if (autoStartStop) {
            // 打开被测应用
            GTRControllerServer.openApp(context, packageName);
        }
    }

    /**
     * Start the analysis with the target app process id, and
     * the process must exists when this method is called, so
     * no need to handle the launch thing.
     * @param context
     * @param pid application process id
     */
    public static void start(Context context, int pid, String pkgName) {
        GTRAnalysis.applicationContext = (context != null)
                ? context.getApplicationContext()
                : null;

        if (GTRAnalysis.mainThreadHandler == null) {
            GTRAnalysis.mainThreadHandler = new Handler(Looper.getMainLooper());
        }

        GTRAnalysis.lastPid = GTRAnalysis.pid;
        GTRAnalysis.pid = pid;
        GTRAnalysis.packageName = pkgName;
        GTRBinder.setPackageName(packageName);
        GTRAnalysis.init();

        // for UI display
        initAUTAppInfo(context, pkgName);
    }

    private static void initAUTAppInfo(Context context, String pkg) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(pkg, 0);
            AUTManager.pkn = pkg;
            AUTManager.appic = info.applicationInfo.loadIcon(context.getPackageManager());
            AUTManager.apn = info.applicationInfo.loadLabel(context.getPackageManager()).toString();

            GTAUTFragment1.resetAppInfo();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        stop(false);
    }

    public static void stop(boolean stopApp) {
        String name = GTRAnalysis.packageName;
        GTRAnalysis.packageName = null;
        GTRAnalysis.startTestTime = -1;
        GTRBinder.unregisterClient();
        GTRBinder.setPackageName(null);
        GTRAnalysis.init();

        // 关闭被测应用
        if (stopApp && applicationContext != null) {
            GTRControllerServer.killAppWithSDK(applicationContext, name);
        }
    }

    public static void clear() {
        init();
        try {
            //GTRServerSave.saveData(packageName, startTestTime, pid, GTRConfig.gtClearDataFlag);
            FileUtils.deleteAllFiles(new File(GTConfig.gtrDirPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*  *
     * 数据结果和分析管理器
     */
    private static GTRAnalysisResult gtrAnalysisResult;
    private static GTRAnalysisManager gtrAnalysisManager;
    private static boolean initialized = false;

    private static void init() {
        gtrAnalysisResult = new GTRAnalysisResult();
        gtrAnalysisManager = new GTRAnalysisManager(gtrAnalysisResult);
        initialized = true;
    }

    /**
     * 数据分析：
     */
    public static void analysis(final String packageName, final long startTestTime, final int pid, final String data) {
        if (!initialized || GTRAnalysis.packageName == null ||
                packageName == null || !GTRAnalysis.packageName.equals(packageName)) {
            return;
        }

        if (pid == lastPid) {
            return;
        }

        if (GTRAnalysis.pid == -1) {
            showToast("被测应用已打开：pid=" + pid);
            GTRAnalysis.pid = pid;
            gtrAnalysisResult.pId = pid;
            refreshPid();
        } else if (pid != GTRAnalysis.pid) {
            showToast("被测应用已重启：pid=" + pid);
            GTRAnalysis.pid = pid;
            GTRAnalysis.init();
            gtrAnalysisResult.pId = pid;
            refreshPid();
        }

        if (GTRAnalysis.startTestTime != startTestTime) {
            GTRAnalysis.startTestTime = startTestTime;
        }

        if (GTRAnalysis.pid != -1) {
            gtrAnalysisResult.pId = pid;
        }

        gtrAnalysisManager.analysisData(data);
    }

    private static void showToast(final String toast) {
        if (applicationContext != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(applicationContext, toast, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static GTRAnalysisResult getGtrAnalysisResult() {
        return gtrAnalysisResult;
    }
}
