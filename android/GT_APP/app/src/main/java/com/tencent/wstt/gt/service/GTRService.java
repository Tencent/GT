package com.tencent.wstt.gt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.tencent.wstt.gt.IRemoteClient;

/**
 * GTRService：GTR的服务进程
 * Created by p_hongjcong on 2017/7/11.
 */
public class GTRService extends Service {
    private static final String TAG = "TAG";

    private static final int CLIENT_MAX = 1;

    public static final int REG_SUCCESS = 1;

    public static final int REG_EXIST = 2;

    public static final int REG_FAIL = 3;

    private RemoteCallbackList<IRemoteClient> remoteClients
            = new RemoteCallbackList();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new GTRBinder(this);
    }

    public int register(IRemoteClient client, String cookie) {
        if (client == null) {
            Log.i(TAG, "client is null");
            return REG_FAIL;
        }

        try {
            int calls = remoteClients.beginBroadcast();
            Log.i(TAG, calls + " calls registered.");
            boolean registered = false;
            if (calls < CLIENT_MAX) {
                for (int i = 0; i < calls; i++) {
                    if (remoteClients.getBroadcastCookie(i).equals(cookie)) {
                        registered = true;
                    }
                }

                if (registered) {
                    Log.i(TAG, "register exists: " + cookie);
                    return REG_EXIST;
                } else {
                    if (remoteClients.register(client, cookie)) {
                        Log.i(TAG, "register success: " + cookie);
                        return REG_SUCCESS;
                    } else {
                        Log.i(TAG, "register fail: " + cookie);
                        return REG_FAIL;
                    }
                }
            } else {
                return REG_FAIL;
            }
        } finally {
            remoteClients.finishBroadcast();
        }
    }

    public void unregister(IRemoteClient client, String cookie) {
        IRemoteClient target = client;

        if (target == null && cookie != null) {
            int calls = remoteClients.beginBroadcast();
            for (int i = 0; i < calls; i++) {
                if (remoteClients.getBroadcastCookie(i).equals(cookie)) {
                    target = remoteClients.getBroadcastItem(i);
                    break;
                }
            }

            remoteClients.finishBroadcast();
        }

        if (target != null) {
            try {
                target.onDisconnected();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            remoteClients.unregister(target);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        remoteClients.kill();
    }
}
