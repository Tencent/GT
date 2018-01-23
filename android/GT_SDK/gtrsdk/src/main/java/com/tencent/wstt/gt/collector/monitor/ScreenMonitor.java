package com.tencent.wstt.gt.collector.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.client.GTRClient;
import com.tencent.wstt.gt.GTRLog;

/**
 * Created by p_hongjcong on 2017/7/13.
 */
public class ScreenMonitor extends AbsMonitor {
    private static final String TAG = "ScreenMonitor";

    @Override
	public void start(Context context) {
        // 判断是否为亮屏状态
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

        // sdk<20时使用pm.isScreenOn();
        // GTR目前仅支持sdk>=21(5.0)
        GTRClient.pushData(new StringBuilder()
                .append("screenCollect")
                .append(GTConfig.separator)
                .append(pm.isInteractive())
                .append(GTConfig.separator)
                .append(System.currentTimeMillis())
                .toString());

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(mBatInfoReceiver, filter);

        GTRLog.d(TAG, "monitor started");
        started = true;
    }

    @Override
    public void stop(Context context) {
        if (!started) {
            return;
        }

        context.unregisterReceiver(mBatInfoReceiver);
        GTRLog.d(TAG, "monitor stopped");
        started = false;
    }

    private static final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();

            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                GTRLog.d(TAG, "-----------------screen is on...");
                GTRClient.pushData(new StringBuilder()
                        .append("screenCollect")
                        .append(GTConfig.separator)
                        .append(true)
                        .append(GTConfig.separator)
                        .append(System.currentTimeMillis())
                        .toString());
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                GTRLog.d(TAG, "----------------- screen is off...");
                GTRClient.pushData(new StringBuilder()
                        .append("screenCollect")
                        .append(GTConfig.separator)
                        .append(false)
                        .append(GTConfig.separator)
                        .append(System.currentTimeMillis())
                        .toString());
            }
        }
    };
}
