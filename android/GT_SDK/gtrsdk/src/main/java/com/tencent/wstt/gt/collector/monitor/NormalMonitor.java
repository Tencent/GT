package com.tencent.wstt.gt.collector.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class NormalMonitor extends AbsMonitor {
    private static final String TAG = "NormalMonitor";

    private static Context applicationContext;

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(Context context) {
        if (started) {
            return;
        }

        if (handler == null) {
            HandlerThread handlerThread = new HandlerThread("GTRNormalMonitorThread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }
        stop();
        applicationContext = context;
        handler.postDelayed(normalMonitorRunnable, 0);

        GTRLog.d(TAG, "monitor started");
        started = true;
    }

    @Override
    public void stop() {
        if (!started) {
            return;
        }

        if (handler != null) {
            handler.removeCallbacks(normalMonitorRunnable);
        }

        GTRLog.d(TAG, "monitor stopped");
        started = false;
    }

    // 监控相关：
    private static int interval = 3000;

    private Runnable normalMonitorRunnable = new Runnable() {
        @Override
        public void run() {
            if (threadId == -1) {
                threadId = android.os.Process.myTid();
            }

            // 采集并上报基础信息
            long time = System.currentTimeMillis();
            long cpuTotal = getCPU_total();
            long cpuApp = getCPU_app(android.os.Process.myPid());
            String cpuThreads = getCPU_threads(android.os.Process.myPid());
            int memory = getMemory_app(android.os.Process.myPid(),applicationContext);
            long flowUpload = TrafficStats.getUidTxBytes(android.os.Process.myUid());
            long flowDownload  = TrafficStats.getUidRxBytes(android.os.Process.myUid());

            String gtrThreads = GTRClient.handlerThreadId + "," +
                    MonitorManager.getMonitor(MonitorManager.LOGCAT_MONITOR).getWorkThreadId() + "," +
                    MonitorManager.getMonitor(MonitorManager.CHORE_MONITOR).getWorkThreadId() + "," +
                    MonitorManager.getMonitor(MonitorManager.NORMAL_MONITOR).getWorkThreadId();

            String content = "normalCollect" +
                    GTConfig.separator + time +
                    GTConfig.separator + cpuTotal +
                    GTConfig.separator + cpuApp +
                    GTConfig.separator + cpuThreads +
                    GTConfig.separator + memory +
                    GTConfig.separator + flowUpload +
                    GTConfig.separator + flowDownload +
                    GTConfig.separator + gtrThreads;

            GTRClient.pushData(content);

            // 下一个采集
            if (handler != null) {
                handler.postDelayed(normalMonitorRunnable, interval);
            }
        }
    };

    // 系统总时间片
    private static final int BUFFER_SIZE = 1000;//文件读取缓存大小

    public static long getCPU_total() {
        long cpuTotal = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("/proc/stat"));
            scanner.next();
            long user = scanner.nextLong();
            long nice = scanner.nextLong();
            long system = scanner.nextLong();
            long idle = scanner.nextLong();
            long ioWait = scanner.nextLong();
            long irq = scanner.nextLong();
            long softirq = scanner.nextLong();
            cpuTotal = user + nice + system +
                    idle + ioWait + irq + softirq;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return cpuTotal;
    }

    // 进程时间片
    public static long getCPU_app(int pid) {
        Scanner scanner = null;
        long cpuApp = 0;
        try {
            scanner = new Scanner(new File("/proc/" + pid + "/stat"));

            int i = 0;
            while (scanner.hasNext() && i < 13) {
                scanner.next();
                i++;
            }

            cpuApp = scanner.nextLong() +
                    scanner.nextLong() +
                    scanner.nextLong() +
                    scanner.nextLong();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return cpuApp;
    }

    // 进程所有线程时间片
    public static String getCPU_threads(int pid) {
        String cpuThreads = "";
        File threadDir = new File("/proc/" + pid + "/task");
        if (!threadDir.exists()) {
            return cpuThreads;
        }

        File[] threadFiles = threadDir.listFiles();
        for(File threadFile: threadFiles) {
            BufferedReader pidReader = null;
            try {
                File statFile = new File(threadFile,"stat");
                pidReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(statFile)), BUFFER_SIZE);

                String line = pidReader.readLine();
                if (line != null) {
                    // 找到第一个'('和最后一个')'
                    int firstQianKuoHao = 0;
                    int lastHouKuoHao = 0;
                    for (int k = 0; k < line.length(); k++) {
                        if (line.charAt(k)=='(' && firstQianKuoHao==0) {
                            firstQianKuoHao = k;
                        }

                        if (line.charAt(k)==')' && k > lastHouKuoHao) {
                            lastHouKuoHao = k;
                        }
                    }

                    String threadId = line.substring(0, firstQianKuoHao - 1);
                    String threadName = line.substring(firstQianKuoHao + 1,lastHouKuoHao);
                    String lastLine = line.substring(lastHouKuoHao + 2,line.length());
                    String[] pidCpuInfoList = lastLine.split(" ");

                    if (pidCpuInfoList.length >= 17) {
                        long threadCpp = Long.parseLong(pidCpuInfoList[11]) +
                                Long.parseLong(pidCpuInfoList[12]);

                        cpuThreads = cpuThreads + threadId + ":" + threadCpp + ":" +
                                threadName.replace(",","@@@")
                                        .replace(":","%%%")
                                        .replace("\n","")+",";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (pidReader != null) {
                    try {
                        pidReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return cpuThreads;
    }

    // 进程总内存
    public static int getMemory_app(int pid, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int[] myMempid = new int[] { pid };
        assert am != null;
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
        return memoryInfo[0].getTotalPss();
    }
}




