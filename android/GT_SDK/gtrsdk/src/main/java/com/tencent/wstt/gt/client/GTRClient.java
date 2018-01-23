package com.tencent.wstt.gt.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.IGTR;
import com.tencent.wstt.gt.IRemoteClient;
import com.tencent.wstt.gt.Env;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.collector.GTRCollector;
import com.tencent.wstt.gt.collector.util.PackageUtil;
import com.tencent.wstt.gt.controller.GTRBroadcastReceiver;

import java.util.ArrayList;

/**
 * 代表Binder通信的客户端逻辑，负责向服务端发送性能数据。
 * 处理SDK接收到的广播：开启或停止当前App测试，暂停或重启
 * 发送数据。
 * Created by p_hongjcong on 2017/7/13.
 */
public class GTRClient {
    private static String TAG = "GTRClient";

    public static final int MSG_CLIENT_OP = 1;

    private static final int MSG_SERVER_DISCONNECTED = 2;

    private static final int MSG_SERVER_CONNECTED = 3;

    private static final int MSG_STOP_COLLECT = 4;

    private static final int DATA_LIST_THRESHOLD = 15000;

    private static final int DATA_LIST_MAX = 20000;

    private static final int REG_SUCCESS = 1;

    private static final int REG_EXIST = 2;

    private static final int REG_FAIL = 3;

    private static String CLIENT_THREAD_NAME = "GTRClientThread";

    private static int pid = android.os.Process.myPid();
    private static Context applicationContext = null;
    private static String packageName;
    private static Handler handler = null;
    public static int handlerThreadId = -1;

    public static void init(Context context) {
        createHandlerIfNeeded();
        applicationContext = context.getApplicationContext();
        packageName = applicationContext.getPackageName();

        GTRLog.d(TAG, "client init");
    }

    public static Handler getHandler() {
        return handler;
    }

    private static void createHandlerIfNeeded() {
        if (handler == null) {
            HandlerThread handlerThread = new HandlerThread(CLIENT_THREAD_NAME);
            handlerThread.start();

            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_CLIENT_OP:
                            GTRLog.d(TAG, "MSG_CLIENT_OP handled");

                            Intent intent = (Intent)msg.obj;
                            String action = intent.getAction();

                            if (action.equals(GTRBroadcastReceiver.ACTION_GTR_BROADCAST)) {
                                checkConnectState();
                            }

                            break;
                        case MSG_SERVER_DISCONNECTED:
                        case MSG_STOP_COLLECT:
                            GTRLog.d(TAG, "msg=stop pushing");
                            stopPushing();
                            break;
                        case MSG_SERVER_CONNECTED:
                            GTRLog.d(TAG, "Service connect successfully");
                            if (isConnected && iGTR != null) {
                                new Handler(applicationContext.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        GTRCollector.startMonitorsIfNeeded();
                                        GTRLog.d(TAG, "All monitors started.");
                                        dataId = 0;
                                    }
                                });
                            }

                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    private static void checkConnectState() {
        if (isConnected && iGTR != null) {
            GTRLog.d(TAG, "Service is connected");
            return;
        }

        GTRLog.d(TAG, "try connect service");
        tryConnectServer(applicationContext);
    }

    private static void stopPushing() {
        if (applicationContext != null) {
            applicationContext.unbindService(conn);
        }
    }

    //////////////////////////////////////////////////////////////
    ////                    AIDL相关：                         ////
    //////////////////////////////////////////////////////////////

    //AIDL连接：
    private static boolean isConnected = false;
    private static IGTR iGTR;

    private static ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GTRLog.d(TAG, "onServiceConnected");

            // 获取remote GT service, 并检查GT是否在监听别的App,
            // 如果正在监听别的应用，则停止对本应用的一切收集工作,
            // 等待下一次连接的机会
            IGTR gtr = IGTR.Stub.asInterface(service);
            try {
                int res = gtr.register(remoteClient, packageName);
                GTRLog.d(TAG, "register result:" + res);

                if (res == REG_FAIL) {
                    GTRLog.i(TAG, "Register service failed, stop current monitors.");
                    iGTR = null;
                    isConnected = false;
                    handler.sendEmptyMessage(MSG_STOP_COLLECT);
                } else {
                    iGTR = gtr;
                    if (res == REG_SUCCESS) {
                        GTRLog.i(TAG, "start new process:" + packageName);
                        iGTR.startGTRAnalysis(packageName, pid);
                    }
                    handler.sendEmptyMessage(MSG_SERVER_CONNECTED);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iGTR = null;
            isConnected = false;
            handler.sendEmptyMessage(MSG_SERVER_DISCONNECTED);
        }
    };

    public static synchronized boolean tryConnectServer(Context context) {
        if (!isConnected) {
            Intent intent = new Intent();
            intent.setPackage(Env.GT_SERVICE_PACKAGE);
            intent.setAction(Env.GT_SERVICE_ACTION);
            intent.setComponent(new ComponentName(
                    Env.GT_SERVICE_PACKAGE, Env.GTR_SERVICE_NAME));
            intent.putExtra("PID", android.os.Process.myPid());
            intent.putExtra("PKG", packageName);
            context.startService(intent);
            isConnected = context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }

        return isConnected;
    }

    // AIDL数据发送：
    // 给每条数据加上一个ID，用于数据顺序追溯！
    private static long dataId = 0;
    private static ArrayList<String> dataList = new ArrayList<>();

    private static synchronized void addData(String data) {
        if (dataList.size() > DATA_LIST_THRESHOLD) {
            // if the list size is larger than the threshold,
            // ignore those data types.
            if (data.startsWith("logcatCollect") ||
                    data.startsWith("stackCollect") ||
                    data.startsWith("frameCollect")) {
                return;
            }
        }

        if (dataList.size() >= DATA_LIST_MAX) {
            // if reaches to the maximum capacity of the list
            // remove the front one data from the list,
            // put the newest to the tail
            dataList.remove(0);
        }

        StringBuilder builder = new StringBuilder()
                .append(dataId)
                .append(GTConfig.separator)
                .append(System.currentTimeMillis())
                .append(GTConfig.separator)
                .append(data);
        dataList.add(builder.toString());
        dataId++;
    }

    public static void pushData(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (handlerThreadId == -1) {
                    handlerThreadId = android.os.Process.myTid();
                }

                if (dataId == 0) {
                    addData(getAppInfo());
                    addData(getDeviceInfo());
                }

                addData(data);

                if (isConnected && iGTR != null) {
                    try {
                        while (dataList.size() > 0) {
                            String data = dataList.get(0);
                            iGTR.pushData(packageName, GTRCollector.getStartTime(), pid, data);
                            dataList.remove(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static String getAppInfo() {
        String appName = PackageUtil.getAPPName(packageName, applicationContext);
        int versionCode = PackageUtil.getAppVersionCode(packageName, applicationContext);
        String versionName = PackageUtil.getAppVersionName(packageName, applicationContext);
        int gtrVersionCode = GTConfig.GTRVersionCode;
        long startTestTime = GTRCollector.getStartTime();
        int mainThreadId = GTRCollector.mainThreadId;
        return new StringBuilder().append("appCollect")
                .append(GTConfig.separator).append(packageName)
                .append(GTConfig.separator).append(appName)
                .append(GTConfig.separator).append(versionName)
                .append(GTConfig.separator).append(versionCode)
                .append(GTConfig.separator).append(gtrVersionCode)
                .append(GTConfig.separator).append(startTestTime)
                .append(GTConfig.separator).append(mainThreadId)
                .toString();
    }

    private static String getDeviceInfo() {
        return new StringBuilder().append("deviceCollect")
                .append(GTConfig.separator).append(Build.MANUFACTURER)
                .append(GTConfig.separator).append(Build.MODEL)
                .append(GTConfig.separator).append(Build.VERSION.SDK_INT)
                .append(GTConfig.separator).append(Build.VERSION.RELEASE)
                .toString();
    }

    public static class RemoteClient extends IRemoteClient.Stub {
        @Override
        public boolean isAlive() throws RemoteException {
            return true;
        }

        @Override
        public void onDisconnected() throws RemoteException {
            GTRLog.d(TAG, "on remote client disconnected");
            handler.sendEmptyMessage(MSG_SERVER_DISCONNECTED);
        }
    }

    /**
     * A remote client representation that is registered into
     * GT service. When the client crashes or is killed, the
     * service will know it and automatically unregisters
     * the died client.
     */
    private static RemoteClient remoteClient = new RemoteClient();
}
