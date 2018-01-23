package com.tencent.wstt.gt.controller;

import android.content.Context;

import com.tencent.wstt.gt.Env;
import com.tencent.wstt.gt.collector.GTRCollector;

import java.io.File;

public class GTRController {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    /** 模式有两种
     /* 1.启动应用时开启采集并保存数据（缺点：无法准确获取启动时长）（默认）
     /* 2.启动应用时不开启采集，接受到广播时再启动采集并保存数据（缺点：开启之前的数据会被遗漏）
     * @param context
     * */
	public static void init(Context context) {
        sContext = context.getApplicationContext();

        // 读取模式：
        boolean startCollector = true;
        File gtrDir = new File(Env.GTR_CONFIG_PATH);
        if (gtrDir.exists()) {
            File[] files = gtrDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().equals(sContext.getPackageName() + ".txt")) {
                        startCollector = false;
                    }
                }
            }
        }

        if (startCollector) {
            GTRCollector.init(sContext);
        }

        // 注册广播控制器
        GTRBroadcastReceiver.start(context);
    }
}
