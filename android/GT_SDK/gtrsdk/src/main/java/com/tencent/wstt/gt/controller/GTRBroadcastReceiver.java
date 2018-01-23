package com.tencent.wstt.gt.controller;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;
import com.tencent.wstt.gt.collector.monitor.MonitorManager;

import java.util.ArrayList;

public class GTRBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = "GTRBroadcastReceiver";

	public static final String ACTION_GTR_BROADCAST = "GTR.GTRBroadcastReceiver";

    private static final String KEY_BEHAVIOR = "behavior";
    private static final String KEY_PARAM_1 = "param_1";

    private static GTRBroadcastReceiver receiver;

	public static void start(Context context) {
        GTRBroadcastReceiver receiver = new GTRBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GTR_BROADCAST);
        context.registerReceiver(receiver, intentFilter);

        ((Application)(context.getApplicationContext()))
                .registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
	}

	private static void stop(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        ((Application)(context.getApplicationContext()))
                .unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

	@Override
	public void onReceive(Context context, Intent intent) {
        if (intent == null || !intent.getAction().equals(ACTION_GTR_BROADCAST)) {
            return;
        }

        String behavior = intent.getStringExtra(KEY_BEHAVIOR);

        switch (behavior) {
            case "kill":
                GTRLog.i(TAG, "Current app: " + context.getPackageName());
                String killPackageName = intent.getStringExtra(KEY_PARAM_1);
                GTRLog.i(TAG, "App to be killed: " + killPackageName);

                if (killPackageName != null && killPackageName.equals(context.getPackageName())) {
                    while (activities.size() > 0) {
                        Activity activity = activities.get(activities.size() - 1);
                        activities.remove(activity);
                        activity.finish();
                    }

                    MonitorManager.stopAllMonitors();
                    stop(context);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                return;
            case "connect":
                GTRLog.d(TAG, "action received=" + intent.getAction());
                Handler handler = GTRClient.getHandler();
                handler.sendMessage(Message.obtain(handler, GTRClient.MSG_CLIENT_OP, intent));
                break;
        }
	}

	// Activity回退栈：
	static ArrayList<Activity> activities = new ArrayList<>();
	static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			activities.add(activity);
		}

		@Override
		public void onActivityStarted(Activity activity) {

		}

		@Override
		public void onActivityResumed(Activity activity) {

		}

		@Override
		public void onActivityPaused(Activity activity) {

		}

		@Override
		public void onActivityStopped(Activity activity) {

		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			activities.remove(activity);
		}
	};
}
