package com.tencent.wstt.gt.service;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.tencent.wstt.gt.IGTR;
import com.tencent.wstt.gt.IRemoteClient;
import com.tencent.wstt.gt.GTRParam;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;

/**
 * GTRBinder实现通信接口
 * Created by p_hongjcong on 2017/7/11.
 */
public class GTRBinder extends IGTR.Stub {
    private static final String TAG = "GTRBinder";

    private static final int MSG_UNREGISTER = 1;

    private Context context;

    private static GTRService service;

    GTRBinder(GTRService service) {
        GTRBinder.service = service;
        this.context = service.getApplicationContext();
    }

    /**
     * 异步进行数据处理：
     */
    static HandlerThread handlerThread;
    static Handler handler;
    static Handler getHandler() {
        if (handlerThread == null || handler == null) {
            handlerThread = new HandlerThread("GTRBinderHandlerThread");
            handlerThread.start();

            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_UNREGISTER:
                            if (service != null) {
                                service.unregister(null, (String)msg.obj);
                            }

                            break;
                    }
                }
            };
        }
        return handler;
    }

    /**
     * 数据筛选：
     */
    private static String packageName;

    public static void setPackageName(String pkg) {
        packageName = pkg;
    }

    public static void unregisterClient() {
        final String name = packageName;
        Message message = Message.obtain(getHandler(), MSG_UNREGISTER, name);
        getHandler().sendMessage(message);
    }

    @Override
    public void pushData(final String packageName, final long startTestTime, final int pid, final String data) throws RemoteException {
        // Log.i(TAG, "Received: " + packageName);
        // GTR-related app info was set when the service was created and returned.
        // So if the client successfully holds a handle to the service, there
        // must be a valid application name assigned.
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (GTRBinder.packageName != null && GTRBinder.packageName.equals(packageName)) {
                    GTRAnalysis.analysis(packageName, startTestTime, pid, data);

                    try {
                        GTRServerSave.saveData(packageName, startTestTime, pid, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int register(IRemoteClient client, String cookie) throws RemoteException {
        if (service != null) {
            return service.register(client, cookie);
        }
        return GTRService.REG_FAIL;
    }

    @Override
    public void startGTRAnalysis(final String packageName, final int pid) throws RemoteException {
        getHandler().post(new Runnable() {
            public void run() {
                Log.i(TAG, "start analysis: " + packageName + " " + pid);
                GTRAnalysis.start(context, pid, packageName);
            }
        });
    }

    @Override
    public void stopGTRAnalysis() throws RemoteException {
        getHandler().post(new Runnable() {
            public void run() {
                GTRAnalysis.stop();
            }
        });
    }

    @Override
    public GTRParam pullInPara() throws RemoteException {
        return new GTRParam();
    }


}
