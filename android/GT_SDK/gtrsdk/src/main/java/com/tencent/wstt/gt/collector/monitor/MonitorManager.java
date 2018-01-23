package com.tencent.wstt.gt.collector.monitor;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by p_xcli on 2017/12/24.
 */

public class MonitorManager {
    public static final String CHORE_MONITOR = "choreographer";

    public static final String HOOK_MONITOR = "hook";

    public static final String LOGCAT_MONITOR = "logcat";

    public static final String NORMAL_MONITOR = "normal";

    public static final String SCREEN_MONITOR = "screen";

    private static boolean hasHooked;

    private HashMap<String, AbsMonitor> monitorMap = new HashMap();

    private static MonitorManager Instance;

    public static MonitorManager getInstance() {
        if (Instance == null) {
            Instance = new MonitorManager();
        }
        return Instance;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    private MonitorManager() {

    }

    public static AbsMonitor getMonitor(String name) {
        return getInstance().createMonitor(name);
    }

    public static void startMonitors() {
        if (getInstance().context != null) {
            startMonitors(getInstance().context);
        }
    }

    public static void startMonitors(Context context) {
        if (!hasHooked) {
            System.out.println(LogcatMonitor.generateLogFinishTag());

            // start only once, and doesn't need a context and to be stopped
            getMonitor(HOOK_MONITOR).start();
            hasHooked = true;
        }

        getMonitor(LOGCAT_MONITOR).start(context);
        getMonitor(CHORE_MONITOR).start();
        getMonitor(NORMAL_MONITOR).start(context);
        getMonitor(SCREEN_MONITOR).start(context);
    }

    private AbsMonitor createMonitor(String name) {
        AbsMonitor monitor;
        if (monitorMap.containsKey(name)) {
            monitor = monitorMap.get(name);
        } else if ((monitor = create(name)) != null) {
            monitorMap.put(name, monitor);
        }

        return monitor;
    }

    private AbsMonitor create(String name) {
        switch (name) {
            case CHORE_MONITOR:
                return new ChoreographerMonitor();
            case HOOK_MONITOR:
                return new HookMonitor();
            case LOGCAT_MONITOR:
                return new LogcatMonitor();
            case NORMAL_MONITOR:
                return new NormalMonitor();
            case SCREEN_MONITOR:
                return new ScreenMonitor();
            default:
                return null;
        }
    }

    public static void stopAllMonitors() {
        getMonitor(LOGCAT_MONITOR).stop();
        getMonitor(CHORE_MONITOR).stop();
        getMonitor(NORMAL_MONITOR).stop();
        getMonitor(SCREEN_MONITOR).stop(getInstance().context);
    }
}
